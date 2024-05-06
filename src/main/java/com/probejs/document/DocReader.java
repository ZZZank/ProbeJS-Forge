package com.probejs.document;

import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.document.parser.processor.Document;
import lombok.Setter;
import lombok.val;
import me.shedaniel.architectury.platform.Mod;
import me.shedaniel.architectury.platform.Platform;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class DocReader {

    @Setter
    private List<Path> paths;
    @Setter
    private boolean readModJars;
    private final Document documentTarget;
    private final List<String> rawDocTarget;

    public DocReader(Document documentTarget, List<String> rawDocTarget) {
        this.paths = new ArrayList<>();
        this.readModJars = false;
        this.documentTarget = documentTarget;
        this.rawDocTarget = rawDocTarget;
    }

    public DocReader defaultSetup() {
        this.paths = Collections.singletonList(ProbePaths.DOCS);
        this.readModJars = true;
        return this;
    }

    public void read(){
        for (val path : this.paths) {
            readFromPath(path);
        }
        if (this.readModJars) {
            for (val mod : Platform.getMods()) {
                try {
                    readFromModJar(mod);
                } catch (IOException e) {
                    ProbeJS.LOGGER.error("Cannot read doc from mod '{}'", mod.getModId());
                }
            }
        }
    }

    private void readFromModJar(Mod mod) throws IOException {
        //make sure it's regular mod file
        val path = mod.getFilePath();
        val fName = path.getFileName();
        if (!Files.isRegularFile(path) || !(fName.endsWith(".jar") && fName.endsWith(".zip"))) {
            return;
        }
        //open it, fetch document list

        val zip = new ZipFile(path.toFile());
        val target = zip.getEntry("probejs.documents.txt");
        if (target == null) {
            return;
        }
        ProbeJS.LOGGER.info("Found document list from '{}'", mod.getModId());
        val docs = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            zip.getInputStream(target),
            StandardCharsets.UTF_8
        ))) {
            reader.lines().forEach(docs::add);
        }
        //read each doc
        for (String docName : docs) {
            val isRawDoc = docName.startsWith("!");
            if (isRawDoc) {
                //remove "raw" indicator
                docName = docName.substring(1);
            }
            //check mod installing condition
            if (!modConditionMet(docName)) {
                continue;
            }
            //read doc
            val docEntry = zip.getEntry(docName);
            if (docEntry == null) {
                ProbeJS.LOGGER.warn("Document from file not found - {}", docName);
                continue;
            }
            ProbeJS.LOGGER.info("Loading document inside jar - {}", docName);
            BufferedReader docReader = new BufferedReader(
                new InputStreamReader(
                    new BufferedInputStream(zip.getInputStream(docEntry)),
                    StandardCharsets.UTF_8
                )
            );
            readOnce(isRawDoc, docReader);
        }
        zip.close();
    }

    private static boolean modConditionMet(String name) {
        val i = name.indexOf(" ");
        if (i < 0) {
            return true;
        }
        for (val modId : name.substring(0, i).split("&")) {
            if (!Platform.isModLoaded(modId)) {
                return false;
            }
        }
        return true;
    }

    private void readFromPath(Path path) {
        val files = path.toFile().listFiles();
        if (files == null) {
            return;
        }
        Arrays.stream(files)
            .filter(f -> f.getName().endsWith(".d.ts") && !f.isDirectory())
            .sorted(Comparator.comparing(File::getName))
            .forEach(this::readOneFile);
    }

    private void readOneFile(File f) {
        String fName = f.getName();
        val isRawDoc = fName.startsWith("!");
        if (isRawDoc) {
            fName = fName.substring(1);
        }
        if (!modConditionMet(fName)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(f.toPath())) {
            readOnce(isRawDoc, reader);
        } catch (Exception e) {
            ProbeJS.LOGGER.error("Cannot read {}", f.getPath());
        }
    }

    private void readOnce(boolean isRawDoc, BufferedReader reader) {
        if (isRawDoc) {
            reader.lines().forEach(this.rawDocTarget::add);
        } else {
            reader.lines().forEach(this.documentTarget::step);
        }
    }
}
