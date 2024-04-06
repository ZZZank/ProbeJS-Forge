package com.probejs;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.probejs.compiler.EventCompiler;
import com.probejs.compiler.SnippetCompiler;
import com.probejs.compiler.TypingCompiler;
import com.probejs.compiler.rich.fluid.RichFluidCompiler;
import com.probejs.compiler.rich.item.RichItemCompiler;
import com.probejs.compiler.rich.lang.RichLangCompiler;
import com.probejs.document.DocManager;
import com.probejs.document.comment.CommentHandler;
import com.probejs.document.parser.processor.DocumentProviderManager;
import com.probejs.formatter.ClassResolver;
import com.probejs.formatter.NameResolver;
import dev.latvian.kubejs.KubeJSPaths;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class ProbeCommands {

    public static void register(
        CommandDispatcher<CommandSourceStack> dispatcher,
        Commands.CommandSelection selection
    ) {
        dispatcher.register(
            Commands
                .literal("probejs")
                .then(
                    Commands
                        .literal("dump")
                        .requires(source -> source.getServer().isSingleplayer())
                        .executes(context -> {
                            try {
                                sendSuccess(context, "ProbeJS initializing...");
                                DocumentProviderManager.init();
                                CommentHandler.init();
                                DocManager.init();
                                ClassResolver.init();
                                NameResolver.init();
                                sendSuccess(context, "Generating docs...");
                                TypingCompiler.compile();
                                sendSuccess(context, "Generating code snippets...");
                                SnippetCompiler.compile();
                                sendSuccess(context, "Generating rich display informations...");
                                RichFluidCompiler.compile();
                                RichItemCompiler.compile();
                                RichLangCompiler.compile();
                            } catch (Exception e) {
                                e.printStackTrace();
                                context
                                    .getSource()
                                    .sendSuccess(
                                        new TextComponent(
                                            "[ERROR]Uncaught exception happened, terminating typing generation..."
                                        )
                                            .withStyle(ChatFormatting.RED),
                                        true
                                    );
                                MutableComponent githubLink = new TextComponent("ProbeJS Github")
                                    .withStyle(
                                        Style.EMPTY
                                            .withUnderlined(true)
                                            .withClickEvent(
                                                new ClickEvent(
                                                    ClickEvent.Action.OPEN_URL,
                                                    "https://github.com/ZZZank/ProbeJS-Forge/issues"
                                                )
                                            )
                                    );
                                context
                                    .getSource()
                                    .sendSuccess(
                                        new TextComponent("Please report this error to ")
                                            .append(githubLink)
                                            .append(" with complete latest.log."),
                                        true
                                    );
                            }
                            return sendSuccess(context, "ProbeJS typing generation finished.");
                        })
                )
                .then(
                    Commands
                        .literal("clear_cache")
                        .requires(source -> source.getServer().isSingleplayer())
                        .executes(context -> {
                            String[] cacheNames = new String[] {
                                EventCompiler.EVENT_CACHE_NAME,
                                EventCompiler.FORGE_EVENT_CACHE_NAME,
                            };
                            for (String cacheName : cacheNames) {
                                String wrapped = String.format("Cache file '%s'", cacheName);
                                Path path = KubeJSPaths.EXPORTED.resolve(cacheName);
                                if (!Files.exists(path)) {
                                    sendSuccess(context, wrapped + " not found, skipping. ");
                                    continue;
                                }
                                boolean deleted = path.toFile().delete();
                                if (!deleted) {
                                    sendSuccess(context, wrapped + " unable to delete. ");
                                    continue;
                                }
                                sendSuccess(context, wrapped + " deleted. ");
                            }
                            return sendSuccess(context, "Cache files removing process finished.");
                        })
                )
                .then(
                    Commands
                        .literal("config")
                        .then(
                            Commands
                                .literal("toggle_bean")
                                .executes(context -> {
                                    ProbeJS.CONFIG.keepBeaned = !ProbeJS.CONFIG.keepBeaned;
                                    ProbeJS.CONFIG.save();
                                    return sendSuccess(
                                        context,
                                        String.format(
                                            "Keep method while beaning set to: %s",
                                            ProbeJS.CONFIG.keepBeaned
                                        )
                                    );
                                })
                        )
                        .then(
                            Commands
                                .literal("toggle_triming")
                                .executes(context -> {
                                    ProbeJS.CONFIG.trimming = !ProbeJS.CONFIG.trimming;
                                    ProbeJS.CONFIG.save();
                                    return sendSuccess(
                                        context,
                                        String.format("Dump trimming set to: %s", ProbeJS.CONFIG.trimming)
                                    );
                                })
                        )
                        .then(
                            Commands
                                .literal("toggle_mixin")
                                .executes(context -> {
                                    ProbeJS.CONFIG.enabled = !ProbeJS.CONFIG.enabled;
                                    ProbeJS.CONFIG.save();
                                    return sendSuccess(
                                        context,
                                        String.format(
                                            "Event listening set to: %s. Changes will be applied next time you start the game",
                                            ProbeJS.CONFIG.enabled ? "enabled" : "disabled"
                                        )
                                    );
                                })
                        )
                        .then(
                            Commands
                                .literal("toggle_snippet_order")
                                .executes(context -> {
                                    ProbeJS.CONFIG.vanillaOrder = !ProbeJS.CONFIG.vanillaOrder;
                                    ProbeJS.CONFIG.save();
                                    return sendSuccess(
                                        context,
                                        String.format(
                                            "In snippets, which will appear first: %s",
                                            ProbeJS.CONFIG.vanillaOrder ? "mod_id" : "member_type"
                                        )
                                    );
                                })
                        )
                        .then(
                            Commands
                                .literal("toggle_classname_snippets")
                                .executes(context -> {
                                    ProbeJS.CONFIG.exportClassNames = !ProbeJS.CONFIG.exportClassNames;
                                    ProbeJS.CONFIG.save();
                                    return sendSuccess(
                                        context,
                                        String.format(
                                            "Export class name to snippets set to: %s",
                                            ProbeJS.CONFIG.exportClassNames
                                        )
                                    );
                                })
                        )
                )
        );
    }

    /**
     * @param source The command source, usually avaliable in Command.executes() callback,
     * via {@code Command.executes(context -> context.getSource())}
     * @param message The message you want to send
     * @param allowLogging
     * @return Will always be `Command.SINGLE_SUCCESS`
     */
    private static int sendSuccess(CommandSourceStack source, String message, boolean allowLogging) {
        source.sendSuccess(new TextComponent(message), allowLogging);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * @param context The command context, usually avaliable in Command.executes() callback
     * @param message The message you want to send
     * @return Will always be `Command.SINGLE_SUCCESS`
     */
    private static int sendSuccess(CommandContext<CommandSourceStack> context, String message) {
        return sendSuccess(context.getSource(), message, true);
    }
}
