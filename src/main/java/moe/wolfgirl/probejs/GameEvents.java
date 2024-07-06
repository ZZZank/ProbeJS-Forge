package moe.wolfgirl.probejs;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.bindings.TextWrapper;
import dev.latvian.kubejs.text.Text;
import lombok.val;
import moe.wolfgirl.probejs.features.bridge.ProbeServer;
import moe.wolfgirl.probejs.lang.linter.Linter;
import moe.wolfgirl.probejs.utils.GameUtils;
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

import java.net.UnknownHostException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.minecraft.Util.NIL_UUID;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class GameEvents {
    private static final int MOD_LIMIT = 350;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void playerJoined(ClientPlayerNetworkEvent.LoggedInEvent event) {
        val player = event.getPlayer();
        if (player == null) {
            return;
        }
        ProbeConfig config = ProbeConfig.INSTANCE;
        final Consumer<Component> sendMsg = msg -> player.sendMessage(msg, NIL_UUID);

        if (config.enabled.get()) {
            if (config.modHash.get() == -1) {
                sendMsg.accept(TextWrapper.translate("probejs.hello").gold().component());
            }
            if (config.registryHash.get() != GameUtils.registryHash()) {
                new Thread(() -> {  // Don't stall the client
                    val dump = new ProbeDump();
                    dump.defaultScripts();
                    try {
                        dump.trigger(sendMsg);
                    } catch (Throwable e) {
                        ProbeJS.LOGGER.error(e.getMessage());
                        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                            ProbeJS.LOGGER.error(stackTraceElement.toString());
                        }
                        throw new RuntimeException(e);
                    }
                }).start();
            } else {
                sendMsg.accept(
                    TextWrapper
                        .translate("probejs.enabled_warning")
                        .append(TextWrapper.string("/probejs disable").click("command:/probejs disable").aqua())
                        .component()
                );
                if (ModList.get().size() >= MOD_LIMIT && ProbeConfig.INSTANCE.complete.get()) {
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
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        val dispatcher = event.getDispatcher();
        BiConsumer<CommandContext<CommandSourceStack>, Text> sendMsg = (context, text) -> {
            context.getSource().sendSuccess(text.component(), true);
        };
        dispatcher.register(
            Commands.literal("probejs")
                .then(Commands.literal("dump")
                    .requires(source -> ProbeConfig.INSTANCE.enabled.get() && source.hasPermission(2))
                    .executes(context -> {
                        KubeJS.PROXY.reloadClientInternal();
                        ProbeDump dump = new ProbeDump();
                        dump.defaultScripts();
                        new Thread(() -> {
                            try {
                                dump.trigger(msg -> sendMsg.accept(context, TextWrapper.of(msg)));
                            } catch (Throwable e) {
                                ProbeJS.LOGGER.error(e.getMessage());
                                for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                                    ProbeJS.LOGGER.error(stackTraceElement.toString());
                                }
                                throw new RuntimeException(e);
                            }
                        }).start();
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("disable")
                    .requires(source -> ProbeConfig.INSTANCE.enabled.get() && source.hasPermission(2))
                    .executes(context -> {
                        ProbeConfig.INSTANCE.enabled.set(false);
                        sendMsg.accept(context, TextWrapper.translate("probejs.bye_bye").gold());
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("enable")
                    .requires(source -> source.hasPermission(2))
                    .executes(context -> {
                        ProbeConfig.INSTANCE.enabled.set(true);
                        sendMsg.accept(context, TextWrapper.translate("probejs.hello_again").aqua());
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("scope_isolation")
                    .requires(source -> ProbeConfig.INSTANCE.enabled.get() && source.hasPermission(2))
                    .executes(context -> {
                        boolean flag = !ProbeConfig.INSTANCE.isolatedScopes.get();
                        ProbeConfig.INSTANCE.isolatedScopes.set(flag);
                        sendMsg.accept(
                            context,
                            TextWrapper.translate(flag ? "probejs.isolation" : "probejs.no_isolation").aqua()
                        );
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("lint")
                    .requires(source -> ProbeConfig.INSTANCE.enabled.get() && source.hasPermission(2))
                    .executes(context -> {
                        Linter.defaultLint(msg -> sendMsg.accept(context, Text.of(msg)));
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("complete_dump")
                    .requires(source -> ProbeConfig.INSTANCE.enabled.get() && source.hasPermission(2))
                    .executes(context -> {
                        boolean flag = !ProbeConfig.INSTANCE.complete.get();
                        ProbeConfig.INSTANCE.complete.set(flag);
                        sendMsg.accept(
                            context,
                            TextWrapper.translate(flag ? "probejs.complete" : "probejs.no_complete")
                        );
                        return Command.SINGLE_SUCCESS;
                    })
                )
                .then(Commands.literal("decompile")
                    .requires(source -> ProbeConfig.INSTANCE.enabled.get() && source.hasPermission(2))
                    .executes(context -> {
                        boolean flag = !ProbeConfig.INSTANCE.enableDecompiler.get();
                        ProbeConfig.INSTANCE.enableDecompiler.set(flag);
                        sendMsg.accept(
                            context,
                            TextWrapper.translate(flag ? "probejs.decompile" : "probejs.no_decompile")
                        );
                        if (flag) {
                            ProbeConfig.INSTANCE.modHash.set(-2L);
                        }
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

