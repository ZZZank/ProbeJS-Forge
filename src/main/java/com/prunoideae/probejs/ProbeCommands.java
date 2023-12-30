package com.prunoideae.probejs;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.prunoideae.probejs.typings.KubeCompiler;
import com.prunoideae.probejs.typings.ProbeCompiler;
import com.prunoideae.probejs.typings.SpecialFormatters;
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
                        // .then(
                        //     Commands
                        //         .literal("bean_method")
                        //         .executes(context -> beaningToggleCommandHandler(context))
                        // )
                        .then(
                            Commands
                                .literal("dump_export")
                                .executes(context -> dumpToggleCommandHandler(context))
                        )
                )
        );
    }

    private static void sendSuccess(
        CommandContext<CommandSourceStack> context,
        String message,
        boolean wantToInformAdmin
    ) {
        context.getSource().sendSuccess(new TextComponent(message), wantToInformAdmin);
    }

    private static int beaningToggleCommandHandler(CommandContext<CommandSourceStack> context) {
        ProbeConfig.dumpMethod = !ProbeConfig.dumpMethod;
        sendSuccess(
            context,
            String.format("Keep method while beaning set to: %s", ProbeConfig.dumpMethod),
            false
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int dumpToggleCommandHandler(CommandContext<CommandSourceStack> context) {
        ProbeConfig.dumpExport = !ProbeConfig.dumpExport;
        sendSuccess(context, String.format("Create dump.js set to: %s", ProbeConfig.dumpExport), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int clearCacheCommandHandler(CommandContext<CommandSourceStack> context) {
        Path path = KubeJSPaths.EXPORTED.resolve("cachedEvents.json");
        if (!Files.exists(path)) {
            context.getSource().sendSuccess(new TextComponent("No cached files to be cleared."), false);
            return Command.SINGLE_SUCCESS;
        }
        boolean deleted = path.toFile().delete();
        if (deleted) {
            sendSuccess(context, "Cache files removed.", false);
        } else {
            sendSuccess(context, "Failed to remove cache files.", false);
        }
        return Command.SINGLE_SUCCESS;
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
        sendSuccess(context, "ProbeJS typing generation finished.", false);
        return Command.SINGLE_SUCCESS;
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
