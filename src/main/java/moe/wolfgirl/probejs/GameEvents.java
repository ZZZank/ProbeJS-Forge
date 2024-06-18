package moe.wolfgirl.probejs;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.bindings.TextWrapper;
import dev.latvian.kubejs.text.Text;
import lombok.val;
import moe.wolfgirl.probejs.lang.linter.Linter;
import moe.wolfgirl.probejs.utils.GameUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.minecraft.Util.NIL_UUID;

@Mod.EventBusSubscriber
public class GameEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void playerJoined(ClientPlayerNetworkEvent.LoggedInEvent event) {
        val player = event.getPlayer();
        if (player == null) {
            return;
        }
        Consumer<Component> sendMsg = msg -> player.sendMessage(msg, NIL_UUID);
        val config = ProbeConfig.INSTANCE;

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
                        Linter.defaultLint(sendMsg);
                    } catch (Throwable e) {
                        ProbeJS.LOGGER.error(e.getMessage());
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
                Linter.defaultLint(sendMsg);
            }
            sendMsg.accept(
                TextWrapper.translate("probejs.wiki")
                    .append(TextWrapper.string("Github Page")
                        .aqua()
                        .underlined()
                        .click("https://kubejs.com/wiki/addons/third-party/probejs")
                        .hover("https://kubejs.com/wiki/addons/third-party/probejs"))
                    .component()
            );
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
                        try {
                            dump.trigger(msg -> sendMsg.accept(context, TextWrapper.of(msg)));
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
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
                    .requires(source -> source.hasPermission(2))
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
        );
    }
}

