package zzzank.probejs;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.text.Text;
import lombok.val;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
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
import zzzank.probejs.utils.ProbeText;
import zzzank.probejs.utils.registry.RegistryInfos;
import zzzank.probejs.lang.linter.Linter;
import zzzank.probejs.utils.GameUtils;

import java.net.UnknownHostException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static net.minecraft.Util.NIL_UUID;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class GameEvents {
    private static final int MOD_LIMIT = 200;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void playerJoined(ClientPlayerNetworkEvent.LoggedInEvent event) {
        val player = event.getPlayer();
        if (player == null) {
            return;
        }

        if (!ProbeConfig.enabled.get()) {
            return;
        }

        val sendMsg = (Consumer<Component>) msg -> player.sendMessage(msg, NIL_UUID);
        RegistryInfos.refresh();

        if (ProbeConfig.modHash.get() == -1) {
            sendMsg.accept(ProbeText.translatable("probejs.hello").color(ChatFormatting.GOLD));
        }
        if (ProbeConfig.registryHash.get() != GameUtils.registryHash()) {
            if (!ProbeDumpingThread.exists()) {
                ProbeDumpingThread.create(sendMsg).start();
            }
        } else {

            sendMsg.accept(
                ProbeText
                    .translatable("probejs.enabled_warning")
                    .append(ProbeText.literal("/probejs disable")
                        .click(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/probejs disable"))
                        .color(ChatFormatting.AQUA))
            );
            if (ModList.get().size() >= MOD_LIMIT) {
                if (ProbeConfig.complete.get()) {
                    sendMsg.accept(
                        ProbeText.translatable("probejs.performance", ModList.get().size())
                    );
                }
            }
        }
        sendMsg.accept(
            ProbeText.translatable("probejs.wiki")
                .append(ProbeText.literal("Wiki Page")
                    .color(ChatFormatting.AQUA)
                    .underlined(true)
                    .click(new ClickEvent(
                        ClickEvent.Action.OPEN_URL,
                        "https://kubejs.com/wiki/addons/third-party/probejs"
                    ))
                    .hover(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new TextComponent("https://kubejs.com/wiki/addons/third-party/probejs")
                    )))
        );

        if (ProbeConfig.interactive.get() && GlobalStates.SERVER == null) {
            try {
                GlobalStates.SERVER = new ProbeServer(ProbeConfig.interactivePort.get());
                GlobalStates.SERVER.start();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            sendMsg.accept(
                ProbeText.translatable("probejs.interactive", ProbeConfig.interactivePort.get())
            );
        }
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        val spOrOp = (Predicate<CommandSourceStack>)
            (source) -> source.hasPermission(2) || source.getServer().isSingleplayer();
        val pjsEnabled = (Predicate<CommandSourceStack>) (source) -> ProbeConfig.enabled.get();
        val sendMsg = (BiConsumer<CommandContext<CommandSourceStack>, Component>)
            (context, text) -> context.getSource().sendSuccess(text, true);

        event.getDispatcher().register(
            Commands.literal("probejs")
                .then(Commands.literal("dump")
                    .requires(pjsEnabled.and(spOrOp))
                    .executes(context -> {
                        if (ProbeDumpingThread.exists()) {
                            sendMsg.accept(
                                context,
                                ProbeText.translatable("probejs.already_running").color(ChatFormatting.RED)
                            );
                            return Command.SINGLE_SUCCESS;
                        }
                        KubeJS.PROXY.reloadClientInternal();
                        ProbeDumpingThread.create(msg -> sendMsg.accept(context, msg)).start();
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("disable")
                    .requires(pjsEnabled.and(spOrOp))
                    .executes(context -> {
                        ProbeConfig.enabled.set(false);
                        sendMsg.accept(
                            context,
                            ProbeText.translatable("probejs.bye_bye").color(ChatFormatting.GOLD)
                        );
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("enable")
                    .requires(spOrOp)
                    .executes(context -> {
                        ProbeConfig.enabled.set(true);
                        sendMsg.accept(
                            context,
                            ProbeText.translatable("probejs.hello_again").color(ChatFormatting.AQUA)
                        );
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("refresh_config")
                    .requires(pjsEnabled.and(spOrOp))
                    .executes(context -> {
                        ProbeConfig.refresh();
                        sendMsg.accept(context, ProbeText.translatable("probejs.config_refreshed"));
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("scope_isolation")
                    .requires(pjsEnabled.and(spOrOp))
                    .executes(context -> {
                        boolean flag = !ProbeConfig.isolatedScopes.get();
                        ProbeConfig.isolatedScopes.set(flag);
                        sendMsg.accept(
                            context,
                            ProbeText.translatable(flag ? "probejs.isolation" : "probejs.no_isolation")
                                .color(ChatFormatting.AQUA)
                        );
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("lint")
                    .requires(pjsEnabled.and(spOrOp))
                    .executes(context -> {
                        Linter.defaultLint(msg -> sendMsg.accept(context, Text.of(msg).component()));
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("complete_dump")
                    .requires(pjsEnabled.and(spOrOp))
                    .executes(context -> {
                        boolean flag = !ProbeConfig.complete.get();
                        ProbeConfig.complete.set(flag);
                        sendMsg.accept(
                            context,
                            ProbeText.translatable(flag ? "probejs.complete" : "probejs.no_complete")
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

