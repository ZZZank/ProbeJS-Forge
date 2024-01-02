package com.probejs;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.probejs.typings.KubeCompiler;
import com.probejs.typings.ProbeCompiler;
import com.probejs.typings.SpecialFormatters;
import dev.latvian.kubejs.KubeJSPaths;
import dev.latvian.kubejs.server.ServerSettings;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.WorldData;

public class ProbeCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands
                .literal("probejs")
                .then(
                    Commands
                        .literal("dump")
                        .requires(source -> source.getServer().isSingleplayer() || source.hasPermission(2))
                        .executes(context -> dumpCommandHandler(context))
                )
                .then(
                    Commands
                        .literal("clear_cache")
                        .requires(source -> source.getServer().isSingleplayer() || source.hasPermission(2))
                        .executes(context -> clearCacheCommandHandler(context))
                )
                .then(
                    Commands
                        .literal("config")
                        .then(
                            Commands
                                .literal("toggle_bean")
                                .executes(context -> {
                                    ProbeConfig.INSTANCE.dumpMethod = !ProbeConfig.INSTANCE.dumpMethod;
                                    ProbeConfig.INSTANCE.save();
                                    return sendSuccess(
                                        context,
                                        String.format(
                                            "Keep method while beaning set to: %s",
                                            ProbeConfig.INSTANCE.dumpMethod
                                        ),
                                        false
                                    );
                                })
                        )
                        .then(
                            Commands
                                .literal("toggle_mixin")
                                .executes(context -> {
                                    ProbeConfig.INSTANCE.disabled = !ProbeConfig.INSTANCE.disabled;
                                    ProbeConfig.INSTANCE.save();
                                    return sendSuccess(
                                        context,
                                        String.format(
                                            "OnEvent mixin wrapper set to: %s. Changes will be applied next time you start the game",
                                            ProbeConfig.INSTANCE.disabled ? "disabled" : "enabled"
                                        ),
                                        false
                                    );
                                })
                        )
                        .then(
                            Commands
                                .literal("toggle_snippet_order")
                                .executes(context -> {
                                    ProbeConfig.INSTANCE.vanillaOrder = !ProbeConfig.INSTANCE.vanillaOrder;
                                    ProbeConfig.INSTANCE.save();
                                    return sendSuccess(
                                        context,
                                        String.format(
                                            "In snippets, which will appear first: %s",
                                            ProbeConfig.INSTANCE.vanillaOrder ? "mod_id" : "member_type"
                                        ),
                                        false
                                    );
                                })
                        )
                        .then(
                            Commands
                                .literal("toggle_classname_snippets")
                                .executes(context -> {
                                    ProbeConfig.INSTANCE.exportClassNames =
                                        !ProbeConfig.INSTANCE.exportClassNames;
                                    ProbeConfig.INSTANCE.save();
                                    return sendSuccess(
                                        context,
                                        String.format(
                                            "Export class name as snippets set to: %s",
                                            ProbeConfig.INSTANCE.exportClassNames
                                        ),
                                        false
                                    );
                                })
                        )
                        .then(
                            Commands
                                .literal("toggle_server_dump")
                                .executes(context -> {
                                    ProbeConfig.INSTANCE.dumpExport = !ProbeConfig.INSTANCE.dumpExport;
                                    ProbeConfig.INSTANCE.save();
                                    return sendSuccess(
                                        context,
                                        String.format(
                                            "Create dump.json set to: %s",
                                            ProbeConfig.INSTANCE.dumpExport
                                        ),
                                        false
                                    );
                                })
                        )
                        .then(
                            Commands
                                .literal("toggle_autoexport")
                                .executes(context -> {
                                    ProbeConfig.INSTANCE.autoExport = !ProbeConfig.INSTANCE.autoExport;
                                    ProbeConfig.INSTANCE.save();
                                    return sendSuccess(
                                        context,
                                        String.format(
                                            "Auto-export for KubeJS set to: %s",
                                            ProbeConfig.INSTANCE.autoExport
                                        ),
                                        false
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

    private static int clearCacheCommandHandler(CommandContext<CommandSourceStack> context) {
        Path path = KubeJSPaths.EXPORTED.resolve("cachedEvents.json");
        if (!Files.exists(path)) {
            return sendSuccess(context, "No cached files to be cleared.", false);
        }
        boolean deleted = path.toFile().delete();
        if (!deleted) {
            return sendSuccess(context, "Failed to remove cache files.", false);
        }
        return sendSuccess(context, "Cache files removed.", false);
    }

    private static int dumpCommandHandler(CommandContext<CommandSourceStack> context) {
        try {
            export(context.getSource());
            KubeCompiler.fromKubeDump();
            sendSuccess(context, "KubeJS registry snippets generated.", false);
            SpecialFormatters.init();
            ProbeCompiler.compileDeclarations();
        } catch (Exception e) {
            e.printStackTrace();
            context
                .getSource()
                .sendFailure(
                    new TextComponent(
                        "Uncaught exception happened in wrapper, please report to Github with complete latest.log."
                    )
                );
        }
        return sendSuccess(context, "ProbeJS typing generation finished.", false);
    }

    private static void export(CommandSourceStack source) {
        if (ServerSettings.dataExport != null) {
            return;
        }

        ServerSettings.source = source;
        ServerSettings.dataExport = new JsonObject();
        source.sendSuccess(new TextComponent("Reloading server and exporting data..."), false);

        MinecraftServer minecraftServer = source.getServer();
        PackRepository packRepository = minecraftServer.getPackRepository();
        WorldData worldData = minecraftServer.getWorldData();
        Collection<String> collection = packRepository.getSelectedIds();
        packRepository.reload();
        Collection<String> collection2 = Lists.newArrayList(collection);
        Collection<String> disabledDatapacks = worldData.getDataPackConfig().getDisabled();

        for (String string : packRepository.getAvailableIds()) {
            if (!disabledDatapacks.contains(string) && !collection2.contains(string)) {
                collection2.add(string);
            }
        }

        ReloadCommand.reloadPacks(collection2, source);
    }
}
