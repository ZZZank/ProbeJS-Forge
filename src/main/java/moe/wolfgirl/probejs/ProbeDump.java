package moe.wolfgirl.probejs;

import dev.latvian.kubejs.text.Text;
import moe.wolfgirl.probejs.lang.decompiler.ProbeDecompiler;
import moe.wolfgirl.probejs.lang.java.ClassRegistry;
import moe.wolfgirl.probejs.lang.schema.SchemaDump;
import moe.wolfgirl.probejs.lang.snippet.SnippetDump;
import moe.wolfgirl.probejs.lang.typescript.ScriptDump;
import moe.wolfgirl.probejs.utils.FileUtils;
import moe.wolfgirl.probejs.utils.GameUtils;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static dev.latvian.kubejs.bindings.TextWrapper.string;
import static dev.latvian.kubejs.bindings.TextWrapper.translate;

public class ProbeDump {
    public static final Path SNIPPET_PATH = ProbePaths.WORKSPACE_SETTINGS.resolve("probe.code-snippets");
    public static final Path CLASS_CACHE = ProbePaths.PROBE.resolve("classes.txt");

    final SchemaDump schemaDump = new SchemaDump();
    final SnippetDump snippetDump = new SnippetDump();
    final Collection<ScriptDump> scriptDumps = new ArrayList<>();
    final ProbeDecompiler decompiler = new ProbeDecompiler();
    private Consumer<Component> progressReport;

    public void addScript(ScriptDump dump) {
        if (dump != null) scriptDumps.add(dump);
    }

    public void defaultScripts() {
        addScript(ScriptDump.CLIENT_DUMP.get());
        addScript(ScriptDump.SERVER_DUMP.get());
        addScript(ScriptDump.STARTUP_DUMP.get());
    }

    private void onModChange() throws IOException {
        // Decompile stuffs - here we scan mod classes even if we don't decompile
        // So we have all classes without needing to decompile
        decompiler.fromMods();
        ClassRegistry.REGISTRY.fromClasses(decompiler.scanner.getScannedClasses());

        report(translate("probejs.dump.cleaning"));
        for (ScriptDump scriptDump : scriptDumps) {
            scriptDump.removeClasses();
            report(translate("probejs.removed_script", scriptDump.manager.type.toString()));
        }

//        SchemaDownloader downloader = new SchemaDownloader();
//        try (var zipStream = downloader.openSchemaStream()) {
//            downloader.processFile(zipStream);
//        } catch (Throwable err) {
//            ProbeJS.LOGGER.error(err.getMessage());
//        }
    }

    private void onRegistryChange() throws IOException {

    }

    private void report(Component component) {
        if (progressReport == null) {
            return;
        }
        progressReport.accept(component);
    }

    private void report(Text text) {
        report(text.component());
    }

    public void trigger(Consumer<Component> p) throws IOException {
        progressReport = p;
        report(translate("probejs.dump.start").green());

        // Create the snippets
        snippetDump.fromDocs();
        snippetDump.writeTo(SNIPPET_PATH);

        // And schemas
        schemaDump.fromDocs();
        schemaDump.writeTo(ProbePaths.WORKSPACE_SETTINGS);
        writeVSCodeConfig();
        appendGitIgnore();

        report(translate("probejs.dump.snippets_generated"));

        if (GameUtils.modHash() != ProbeConfig.INSTANCE.modHash.get()) {
            report(translate("probejs.dump.mod_changed").aqua());
            onModChange();
            ProbeConfig.INSTANCE.modHash.set(GameUtils.modHash());
        }

        if (GameUtils.registryHash() != ProbeConfig.INSTANCE.registryHash.get()) {
            onRegistryChange();
            ProbeConfig.INSTANCE.registryHash.set(GameUtils.registryHash());
        }

        // Fetch classes that will be used in the dump
        ClassRegistry.REGISTRY.loadFrom(CLASS_CACHE);

        decompiler.fromMods();
        ClassRegistry.REGISTRY.fromClasses(decompiler.scanner.getScannedClasses());
        for (ScriptDump scriptDump : scriptDumps) {
            ClassRegistry.REGISTRY.fromClasses(scriptDump.retrieveClasses());
        }

        ClassRegistry.REGISTRY.discoverClasses();
        ClassRegistry.REGISTRY.writeTo(CLASS_CACHE);
        report(translate("probejs.dump.class_discovered", ClassRegistry.REGISTRY.foundClasses.keySet().size()));

        // Spawn a thread for each dump
        List<Thread> dumpThreads = new ArrayList<>();
        for (ScriptDump scriptDump : scriptDumps) {
            Thread t = new Thread(
                () -> {
                    scriptDump.acceptClasses(ClassRegistry.REGISTRY.getFoundClasses());
                    try {
                        scriptDump.dump();
                        report(translate("probejs.dump.dump_finished", scriptDump.manager.type.toString()).green());
                    } catch (Throwable e) {
                        report(translate("probejs.dump.dump_error", scriptDump.manager.type.toString()).red());
                        throw new RuntimeException(e);
                    }
                },
                String.format("ProbeDumpingThread-%s", scriptDump.manager.type.name)
            );
            dumpThreads.add(t);
            t.start();
        }

        Thread reportingThread = new Thread(
            () -> {
                while (true) {
                    try {
                        Thread.sleep(3000);
                        String dumpProgress = scriptDumps.stream()
                            .filter(sd -> sd.total != 0)
                            .map(sd -> String.format("%s/%s", sd.dumped, sd.total))
                            .collect(Collectors.joining(", "));
                        report(translate("probejs.dump.report_progress").append(string(dumpProgress).blue()));
                        if (dumpThreads.stream().noneMatch(Thread::isAlive)) {
                            return;
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            },
            "ProbeDumpingThread-report"
        );
        reportingThread.start();
    }

    private void writeVSCodeConfig() throws IOException {
        FileUtils.writeMergedConfig(ProbePaths.VSCODE_JSON, """
                {
                    "json.schemas": [
                        {
                            "fileMatch": [
                                "/recipe_schemas/*.json"
                            ],
                            "url": "./.vscode/recipe.json"
                        }
                    ]
                }
                """);
    }

    private void appendGitIgnore() throws IOException {
        boolean shouldAppend;

        try (var reader = Files.newBufferedReader(ProbePaths.GIT_IGNORE)) {
            shouldAppend = reader.lines().noneMatch(s -> s.equals(".probe"));
        } catch (IOException ignore) {
            shouldAppend = true;
        }

        try (var writer = Files.newBufferedWriter(ProbePaths.GIT_IGNORE, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            if (shouldAppend) {
                writer.write("\n.probe\n");
            }
        }
    }
}
