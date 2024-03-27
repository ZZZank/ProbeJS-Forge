package com.probejs;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.probejs.compiler.SnippetCompiler;
import com.probejs.compiler.TypingCompiler;
import com.probejs.document.DocManager;
import com.probejs.document.comment.CommentHandler;
import com.probejs.document.parser.processor.DocumentProviderHandler;
import com.probejs.formatter.ClassResolver;
import com.probejs.formatter.NameResolver;
import dev.latvian.kubejs.KubeJSPaths;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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
                                DocumentProviderHandler.init();
                                CommentHandler.init();
                                DocManager.init();
                                ClassResolver.init();
                                NameResolver.init();
                                sendSuccess(context, "Generating docs...");
                                TypingCompiler.compile();
                                sendSuccess(context, "Generating code snippets...");
                                SnippetCompiler.compile();
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendSuccess(
                                    context,
                                    "[ERROR]Uncaught exception happened, terminating typing generation..."
                                );
                                sendSuccess(
                                    context,
                                    "Please report this error to ProbeJS Github with complete latest.log."
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
                            Path path = KubeJSPaths.EXPORTED.resolve("cachedEvents.json");
                            if (!Files.exists(path)) {
                                return sendSuccess(context, "No cached files to be cleared.");
                            }
                            boolean deleted = path.toFile().delete();
                            if (!deleted) {
                                return sendSuccess(context, "Failed to remove cache files.");
                            }
                            return sendSuccess(context, "Cache files removed.");
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
                                    ProbeJS.CONFIG.disabled = !ProbeJS.CONFIG.disabled;
                                    ProbeJS.CONFIG.save();
                                    return sendSuccess(
                                        context,
                                        String.format(
                                            "Event listening set to: %s. Changes will be applied next time you start the game",
                                            ProbeJS.CONFIG.disabled ? "disabled" : "enabled"
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
     * @param context The command context, usually avaliable in Command.executes() callback
     * @param message The message you want to send
     * @param wantToInformAdmin
     * @return Will always be `Command.SINGLE_SUCCESS`
     */
    private static int sendSuccess(
        CommandContext<CommandSourceStack> context,
        String message,
        boolean wantToInformAdmin
    ) {
        context.getSource().sendSuccess(new TextComponent(message), wantToInformAdmin);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * @param context The command context, usually avaliable in Command.executes() callback
     * @param message The message you want to send
     * @return Will always be `Command.SINGLE_SUCCESS`
     */
    private static int sendSuccess(CommandContext<CommandSourceStack> context, String message) {
        return sendSuccess(context, message, false);
    }
}
