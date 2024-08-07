package zzzank.probejs;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.bindings.TextWrapper;
import dev.latvian.kubejs.text.Text;
import lombok.val;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import zzzank.probejs.features.bridge.ProbeServer;
import zzzank.probejs.features.kubejs.SpecialData;
import zzzank.probejs.lang.linter.Linter;
import zzzank.probejs.utils.GameUtils;

import java.net.UnknownHostException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.minecraft.Util.NIL_UUID;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class GameEvents {
    private static final int MOD_LIMIT = 300;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void playerJoined(ClientPlayerNetworkEvent.LoggedInEvent event) {
        val player = event.getPlayer();
        if (player == null) {
            return;
        }

        ProbeConfig config = ProbeJS.CONFIG;
        if (!config.enabled.get()) {
            return;
        }

        final Consumer<Component> sendMsg = msg -> player.sendMessage(msg, NIL_UUID);
        SpecialData.refresh();

        if (config.modHash.get() == -1) {
            sendMsg.accept(TextWrapper.translate("probejs.hello").gold().component());
        }
        if (config.registryHash.get() != GameUtils.registryHash()) {
            if (!ProbeDumpingThread.exists()) {
                ProbeDumpingThread.create(sendMsg).start();
            }
        } else {
            sendMsg.accept(
                TextWrapper
                    .translate("probejs.enabled_warning")
                    .append(TextWrapper.string("/probejs disable").click("command:/probejs disable").aqua())
                    .component()
            );
            if (ModList.get().size() >= MOD_LIMIT && ProbeJS.CONFIG.complete.get()) {
                sendMsg.accept(
                    TextWrapper.translate("probejs.performance", ModList.get().size()).component()
                );
            }
        }
        sendMsg.accept(
            TextWrapper.translate("probejs.wiki")
                .append(TextWrapper.string("Wiki Page")
                    .aqua()
                    .underlined()
                    .click("https://kubejs.com/wiki/addons/third-party/probejs")
                    .hover("https://kubejs.com/wiki/addons/third-party/probejs"))
                .component());

        if (config.interactive.get() && GlobalStates.SERVER == null) {
            try {
                GlobalStates.SERVER = new ProbeServer(config.interactivePort.get());
                GlobalStates.SERVER.start();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            sendMsg.accept(
                TextWrapper.translate("probejs.interactive", config.interactivePort.get()).component()
            );
        }
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        val dispatcher = event.getDispatcher();
        BiConsumer<CommandContext<CommandSourceStack>, Component> sendMsg =
            (context, text) -> context.getSource().sendSuccess(text, true);
        dispatcher.register(
            Commands.literal("probejs")
                .then(Commands.literal("dump")
                    .requires(source -> ProbeJS.CONFIG.enabled.get() && source.hasPermission(2))
                    .executes(context -> {
                        if (ProbeDumpingThread.exists()) {
                            sendMsg.accept(context, TextWrapper.translate("probejs.already_running").red().component());
                            return Command.SINGLE_SUCCESS;
                        }
                        KubeJS.PROXY.reloadClientInternal();
                        ProbeDumpingThread.create(msg -> sendMsg.accept(context, msg)).start();
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("disable")
                    .requires(source -> ProbeJS.CONFIG.enabled.get() && source.hasPermission(2))
                    .executes(context -> {
                        ProbeJS.CONFIG.enabled.set(false);
                        sendMsg.accept(context, TextWrapper.translate("probejs.bye_bye").gold().component());
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("enable")
                    .requires(source -> source.hasPermission(2))
                    .executes(context -> {
                        ProbeJS.CONFIG.enabled.set(true);
                        sendMsg.accept(context, TextWrapper.translate("probejs.hello_again").aqua().component());
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("scope_isolation")
                    .requires(source -> ProbeJS.CONFIG.enabled.get() && source.hasPermission(2))
                    .executes(context -> {
                        boolean flag = !ProbeJS.CONFIG.isolatedScopes.get();
                        ProbeJS.CONFIG.isolatedScopes.set(flag);
                        sendMsg.accept(
                            context,
                            TextWrapper.translate(flag ? "probejs.isolation" : "probejs.no_isolation").aqua().component()
                        );
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("lint")
                    .requires(source -> ProbeJS.CONFIG.enabled.get() && source.hasPermission(2))
                    .executes(context -> {
                        Linter.defaultLint(msg -> sendMsg.accept(context, Text.of(msg).component()));
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("complete_dump")
                    .requires(source -> ProbeJS.CONFIG.enabled.get() && source.hasPermission(2))
                    .executes(context -> {
                        boolean flag = !ProbeJS.CONFIG.complete.get();
                        ProbeJS.CONFIG.complete.set(flag);
                        sendMsg.accept(
                            context,
                            TextWrapper.translate(flag ? "probejs.complete" : "probejs.no_complete").component()
                        );
                        return Command.SINGLE_SUCCESS;
                    })
                )
        );
    }

    @SubscribeEvent
    public static void rightClickedBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getSide() == LogicalSide.SERVER) {
            GlobalStates.LAST_RIGHTCLICKED = event.getPos();
        }
    }

    @SubscribeEvent
    public static void rightClickedEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide() == LogicalSide.SERVER) {
            GlobalStates.LAST_ENTITY = event.getTarget();
        }
    }

    @SubscribeEvent
    public static void changedDimension(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof Player player && !(player instanceof FakePlayer)) {
            GlobalStates.LAST_RIGHTCLICKED = null;
            GlobalStates.LAST_ENTITY = null;
        }
    }
}

