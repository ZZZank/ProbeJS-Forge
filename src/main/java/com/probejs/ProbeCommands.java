package com.probejs;

import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.probejs.compiler.RegistryCompiler;
import com.probejs.compiler.SnippetCompiler;
import com.probejs.compiler.TypingCompiler;
import com.probejs.document.DocManager;
import com.probejs.document.comment.CommentHandler;
import com.probejs.document.parser.processor.DocumentProviderHandler;
import com.probejs.formatter.ClassResolver;
import com.probejs.formatter.NameResolver;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.server.ServerSettings;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.WorldData;

public class ProbeCommands {

    public static void register(
        CommandDispatcher<CommandSourceStack> dispatcher,
        net.minecraft.commands.Commands.CommandSelection selection
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
                                if (ProbeJS.CONFIG.autoExport) {
                                    export(context.getSource());
                                }
                                DocumentProviderHandler.init();
                                CommentHandler.init();
                                DocManager.init();
                                ClassResolver.init();
                                NameResolver.init();
                                RegistryCompiler.init();

                                SnippetCompiler.compile();
                                TypingCompiler.compile();
                            } catch (Exception e) {
                                e.printStackTrace();
                                sendSuccess(
                                    context,
                                    "Uncaught exception happened in wrapper, please report to the Github issue with complete latest.log."
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
                                            "OnEvent mixin wrapper set to: %s. Changes will be applied next time you start the game",
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
                                            "Export class name as snippets set to: %s",
                                            ProbeJS.CONFIG.exportClassNames
                                        )
                                    );
                                })
                        )
                        .then(
                            Commands
                                .literal("toggle_autoexport")
                                .executes(context -> {
                                    ProbeJS.CONFIG.autoExport = !ProbeJS.CONFIG.autoExport;
                                    ProbeJS.CONFIG.save();
                                    return sendSuccess(
                                        context,
                                        String.format(
                                            "Auto-export for KubeJS set to: %s",
                                            ProbeJS.CONFIG.autoExport
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

    private static void export(CommandSourceStack source) {
        if (ServerSettings.dataExport != null) {
            return;
        }

        ServerSettings.source = source;
        ServerSettings.dataExport = new JsonObject();
        source.sendSuccess(new TextComponent("Reloading server and exporting data..."), false);

        MinecraftServer server = source.getServer();
        PackRepository packRepository = server.getPackRepository();
        WorldData worldData = server.getWorldData();
        Collection<String> selectedPackIds = packRepository.getSelectedIds();
        packRepository.reload();
        Collection<String> disabledDatapacks = worldData.getDataPackConfig().getDisabled();

        Collection<String> selected = new ArrayList<>(selectedPackIds);
        for (String string : packRepository.getAvailableIds()) {
            if (!disabledDatapacks.contains(string) && !selected.contains(string)) {
                selected.add(string);
            }
        }

        ReloadCommand.reloadPacks(selected, source);
    }
}
