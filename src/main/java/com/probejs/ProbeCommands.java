package com.probejs;

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
import com.probejs.formatter.resolver.ClazzFilter;
import com.probejs.formatter.resolver.NameResolver;
import com.probejs.info.SpecialData;
import com.probejs.util.PText;
import com.probejs.util.RemapperBridge;
import dev.latvian.kubejs.KubeJSPaths;

import java.nio.file.Files;
import lombok.val;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import static com.probejs.util.PUtil.sendSuccess;

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
                        .executes(ProbeCommands::dump)
                )
                .then(
                    Commands
                        .literal("clear_cache")
                        .requires(source -> source.getServer().isSingleplayer())
                        .executes(ProbeCommands::clearCache)
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
                                        String.format(
                                            "Keep method while beaning set to: %s",
                                            ProbeJS.CONFIG.keepBeaned
                                        ), context
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
                                        String.format("Dump trimming set to: %s", ProbeJS.CONFIG.trimming), context
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
                                        String.format(
                                            "Event listening set to: %s. Changes will be applied next time you start the game",
                                            ProbeJS.CONFIG.enabled ? "enabled" : "disabled"
                                        ), context
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
                                        "Export class name to snippets set to: " + ProbeJS.CONFIG.exportClassNames,
                                        context
                                    );
                                })
                        )
                )
        );
    }

    private static int dump(CommandContext<CommandSourceStack> context) {
        if (!ProbeJS.RHIZO_LOADED) {
            sendSuccess(PText.translatable("probejs.rhizo_missing").withStyle(ChatFormatting.RED), context);
            sendSuccess(PText.translatable("probejs.download_rhizo_help")
                .append(PText.url("CurseForge",
                    "https://www.curseforge.com/minecraft/mc-mods/rhizo/files/all?page=1&pageSize=20"
                ))
                .append(" / ")
                .append(PText.url("Github", "https://github.com/ZZZank/Rhizo/releases/latest")), context);
        }
        try {
            sendSuccess("ProbeJS initializing...", context);
            RemapperBridge.refreshRemapper();
            DocumentProviderManager.init();
            CommentHandler.init();
            DocManager.init();
            ClazzFilter.init();
            NameResolver.init();
            SpecialData.refresh();
            sendSuccess("Generating docs...", context);
            TypingCompiler.compile();
            sendSuccess("Generating code snippets...", context);
            SnippetCompiler.compile();
            sendSuccess("Generating rich display information...", context);
            RichFluidCompiler.compile();
            RichItemCompiler.compile();
            RichLangCompiler.compile();
        } catch (Exception e) {
            e.printStackTrace();
            sendSuccess(PText.literal("[ERROR]Uncaught exception happened, terminating typing generation...")
                .withStyle(ChatFormatting.RED), context);
            sendSuccess(PText.literal("Please report this error to ")
                .append(PText.url("ProbeJS Github", "https://github.com/ZZZank/ProbeJS-Forge/issues"))
                .append(" with complete latest.log."), context);
        }
        return sendSuccess("ProbeJS typing generation finished.", context);
    }

    private static int clearCache(CommandContext<CommandSourceStack> context) {
        String[] cacheNames = new String[]{
            EventCompiler.EVENT_CACHE_NAME,
            EventCompiler.FORGE_EVENT_CACHE_NAME,
        };
        for (String cacheName : cacheNames) {
            val wrapped = String.format("Cache file '%s'", cacheName);
            val path = KubeJSPaths.EXPORTED.resolve(cacheName);
            if (!Files.exists(path)) {
                sendSuccess(wrapped + " not found, skipping. ", context);
                continue;
            }
            val deleted = path.toFile().delete();
            if (!deleted) {
                sendSuccess(wrapped + " unable to delete. ", context);
                continue;
            }
            sendSuccess(wrapped + " deleted. ", context);
        }
        return sendSuccess("Cache files removing process finished.", context);
    }
}
