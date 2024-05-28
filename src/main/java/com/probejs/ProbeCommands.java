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
import com.probejs.util.PUtil;
import com.probejs.util.RemapperBridge;
import dev.latvian.kubejs.KubeJSPaths;

import java.nio.file.Files;
import lombok.val;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

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
                                    return PUtil.sendSuccess(
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
                                    return PUtil.sendSuccess(
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
                                    return PUtil.sendSuccess(
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
                                .literal("toggle_classname_snippets")
                                .executes(context -> {
                                    ProbeJS.CONFIG.exportClassNames = !ProbeJS.CONFIG.exportClassNames;
                                    ProbeJS.CONFIG.save();
                                    return PUtil.sendSuccess(
                                        context,
                                        "Export class name to snippets set to: " + ProbeJS.CONFIG.exportClassNames
                                    );
                                })
                        )
                )
        );
    }

    private static int dump(CommandContext<CommandSourceStack> context) {
        if (!ProbeJS.RHIZO_LOADED) {
            context.getSource()
                .sendSuccess(PText.translatable("probejs.rhizo_missing").withStyle(ChatFormatting.RED), true);
            context.getSource()
                .sendSuccess(PText.translatable("probejs.download_rhizo")
                    .append(PText.url("CurseForge", "https://www.curseforge.com/minecraft/mc-mods/rhizo/files/all?page=1&pageSize=20"))
                    .append(" / ")
                    .append(PText.url("Github", "https://github.com/ZZZank/Rhizo/releases/latest")), true);
        }
        try {
            PUtil.sendSuccess(context, "ProbeJS initializing...");
            RemapperBridge.refreshRemapper();
            DocumentProviderManager.init();
            CommentHandler.init();
            DocManager.init();
            ClazzFilter.init();
            NameResolver.init();
            SpecialData.refresh();
            PUtil.sendSuccess(context, "Generating docs...");
            TypingCompiler.compile();
            PUtil.sendSuccess(context, "Generating code snippets...");
            SnippetCompiler.compile();
            PUtil.sendSuccess(context, "Generating rich display information...");
            RichFluidCompiler.compile();
            RichItemCompiler.compile();
            RichLangCompiler.compile();
        } catch (Exception e) {
            e.printStackTrace();
            context.getSource()
                .sendSuccess(PText.literal("[ERROR]Uncaught exception happened, terminating typing generation...").withStyle(
                    ChatFormatting.RED), true);
            val githubLink = PText.url("ProbeJS Github", "https://github.com/ZZZank/ProbeJS-Forge/issues");
            context.getSource()
                .sendSuccess(PText.literal("Please report this error to ").append(githubLink)
                    .append(" with complete latest.log."), true);
        }
        return PUtil.sendSuccess(context, "ProbeJS typing generation finished.");
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
                PUtil.sendSuccess(context, wrapped + " not found, skipping. ");
                continue;
            }
            val deleted = path.toFile().delete();
            if (!deleted) {
                PUtil.sendSuccess(context, wrapped + " unable to delete. ");
                continue;
            }
            PUtil.sendSuccess(context, wrapped + " deleted. ");
        }
        return PUtil.sendSuccess(context, "Cache files removing process finished.");
    }
}
