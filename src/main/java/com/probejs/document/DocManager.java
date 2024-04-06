package com.probejs.document;

import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.document.comment.CommentUtil;
import com.probejs.document.comment.special.CommentAssign;
import com.probejs.document.comment.special.CommentTarget;
import com.probejs.document.parser.processor.Document;
import com.probejs.document.type.IType;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import me.shedaniel.architectury.platform.Mod;
import me.shedaniel.architectury.platform.Platform;

public class DocManager {

    public static final Map<String, List<DocumentClass>> classDocuments = new HashMap<>();
    public static final Map<String, List<IType>> typesAssignable = new HashMap<>();
    public static final Map<String, List<DocumentClass>> classAdditions = new HashMap<>();
    public static final List<String> rawTSDoc = new ArrayList<>();
    public static final List<DocumentType> typeDocuments = new ArrayList<>();

    public static void init() {
        Document documentState = new Document();

        rawTSDoc.clear();
        classDocuments.clear();
        classAdditions.clear();
        typeDocuments.clear();
        typesAssignable.clear();

        try {
            // fromFilesJson(documentState);
            fromFiles(documentState);
            fromPath(documentState);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (IDocument doc : documentState.getDocument().getDocuments()) {
            if (doc instanceof DocumentClass) {
                DocumentClass classDoc = (DocumentClass) doc;
                if (!CommentUtil.isLoaded(classDoc.getComment())) {
                    continue;
                }
                DocumentComment comment = classDoc.getComment();
                if (comment != null) {
                    CommentTarget target = comment.getSpecialComment(CommentTarget.class);
                    if (target != null) {
                        classDocuments
                            .computeIfAbsent(target.getTargetName(), s -> new ArrayList<>())
                            .add(classDoc);
                        comment
                            .getSpecialComments(CommentAssign.class)
                            .stream()
                            .map(CommentAssign::getType)
                            .forEach(type -> {
                                addAssignable(target.getTargetName(), type);
                            });
                        continue;
                    }
                }
                addAdditions(classDoc.getName(), classDoc);
            } else if (doc instanceof DocumentType) {
                DocumentType typeDoc = (DocumentType) doc;
                if (CommentUtil.isLoaded(typeDoc.getComment())) {
                    typeDocuments.add(typeDoc);
                }
            } else {
                //maybe we can add more doc type
            }
        }
    }

    public static final void addAssignable(String className, IType type) {
        DocManager.typesAssignable.computeIfAbsent(className, k -> new ArrayList<>()).add(type);
    }

    public static final void addAdditions(String className, DocumentClass addition) {
        DocManager.classAdditions.computeIfAbsent(className, k -> new ArrayList<>()).add(addition);
    }

    public static void fromPath(Document document) throws IOException {
        File[] files = ProbePaths.DOCS.toFile().listFiles();
        if (files == null) {
            return;
        }
        List<File> filesSorted = Arrays
            .stream(files)
            .filter(f -> f.getName().endsWith(".d.ts") && !f.isDirectory())
            .sorted(Comparator.comparing(File::getName))
            .collect(Collectors.toList());
        for (File f : filesSorted) {
            BufferedReader reader = Files.newBufferedReader(f.toPath());
            if (f.getName().startsWith("!")) {
                reader.lines().forEach(rawTSDoc::add);
            } else {
                reader.lines().forEach(document::step);
            }
            reader.close();
        }
    }

    public static void fromFiles(Document document) throws IOException {
        List<ZipFile> validFiles = Platform
            .getMods()
            .stream()
            .map(Mod::getFilePath)
            .filter(path -> {
                String pathName = path.getFileName().toString();
                return Files.isRegularFile(path) && (pathName.endsWith(".jar") || pathName.endsWith(".zip"));
            })
            .map(Path::toFile)
            .map(file -> {
                try {
                    return new ZipFile(file);
                } catch (IOException ignored) {
                    ProbeJS.LOGGER.error("Unable to open file - {}", file.getName());
                }
                return null;
            })
            .filter(zipFile -> zipFile != null)
            .collect(Collectors.toList());
        for (ZipFile file : validFiles) {
            ZipEntry entry = file.getEntry("probejs.documents.txt");
            if (entry == null) {
                continue;
            }
            ProbeJS.LOGGER.info("Found documents list from {}", file.getName());
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                    new BufferedInputStream(file.getInputStream(entry)),
                    StandardCharsets.UTF_8
                )
            );
            List<String> docNames = reader.lines().collect(Collectors.toList());
            for (String docName : docNames) {
                boolean isRawDoc = docName.startsWith("!");
                if (isRawDoc) {
                    //remove "raw" indicator
                    docName = docName.substring(1);
                }
                //check mod installing condition
                int i = docName.indexOf(" ");
                if (
                    i != -1 &&
                    !Arrays
                        .stream(docName.substring(0, i).split("&"))
                        .allMatch(modid -> Platform.isModLoaded(modid))
                ) {
                    continue;
                }
                //read doc
                ZipEntry docEntry = file.getEntry(docName);
                if (docEntry == null) {
                    ProbeJS.LOGGER.warn("Document from file not found - {}", docName);
                    continue;
                }
                ProbeJS.LOGGER.info("Loading document inside jar - {}", docName);
                BufferedReader docReader = new BufferedReader(
                    new InputStreamReader(
                        new BufferedInputStream(file.getInputStream(docEntry)),
                        StandardCharsets.UTF_8
                    )
                );
                if (isRawDoc) {
                    rawTSDoc.addAll(docReader.lines().collect(Collectors.toList()));
                } else {
                    docReader.lines().forEach(document::step);
                }
            }
            file.close();
        }
    }
}
