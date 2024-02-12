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

public class Manager {

    public static Map<String, List<DocumentClass>> classDocuments = new HashMap<>();
    public static Map<String, List<IType>> typesAssignable = new HashMap<>();
    public static Map<String, List<DocumentClass>> classAdditions = new HashMap<>();
    public static List<String> rawTSDoc = new ArrayList<>();
    public static List<DocumentType> typeDocuments = new ArrayList<>();

    public static void fromPath(Document document) throws IOException {
        File[] files = ProbePaths.DOCS.toFile().listFiles();
        if (files == null) {
            return;
        }
        List<File> filesSorted = Arrays
            .stream(files)
            .sorted(Comparator.comparing(File::getName))
            .collect(Collectors.toList());
        for (File f : filesSorted) {
            if (!f.getName().endsWith(".d.ts") || f.isDirectory()) {
                continue;
                //return?
            }
            BufferedReader reader = Files.newBufferedReader(f.toPath());
            if (!f.getName().startsWith("!")) {
                reader.lines().forEach(document::step);
            } else {
                reader.lines().forEach(rawTSDoc::add);
            }
            reader.close();
        }
    }

    public static void fromFiles(Document document) throws IOException {
        for (Mod mod : Platform.getMods()) {
            Path filePath = mod.getFilePath();
            if (
                // doc appearently should be readable regular file
                !Files.isRegularFile(filePath) ||
                (
                    // let's assume docs are only inside jar/zip
                    !filePath.getFileName().toString().endsWith(".jar") &&
                    !filePath.getFileName().toString().endsWith(".zip")
                )
            ) {
                continue;
            }
            ZipFile file = new ZipFile(filePath.toFile());
            ZipEntry entry = file.getEntry("probejs.documents.txt");
            if (entry == null) {
                continue;
            }
            ProbeJS.LOGGER.info("Found documents list from {}", mod.getName());
            InputStream stream = file.getInputStream(entry);
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(new BufferedInputStream(stream), StandardCharsets.UTF_8)
            );
            List<String> docNames = reader.lines().collect(Collectors.toList());
            for (String docName : docNames) {
                if (docName.startsWith("!")) {
                    docName = docName.substring(1);
                    int i = docName.indexOf(" ");
                    if (i != -1) {
                        if (!Platform.isModLoaded(docName.substring(0, i))) {
                            continue;
                        }
                        docName = docName.substring(i + 1);
                    }
                    ZipEntry docEntry = file.getEntry(docName);
                    if (docEntry != null) {
                        ProbeJS.LOGGER.info("Loading document inside jar - {}", docName);
                        InputStream docStream = file.getInputStream(docEntry);
                        BufferedReader docReader = new BufferedReader(
                            new InputStreamReader(new BufferedInputStream(docStream), StandardCharsets.UTF_8)
                        );
                        docReader.lines().forEach(rawTSDoc::add);
                    } else {
                        ProbeJS.LOGGER.warn("Document from file not found - {}", docName);
                    }
                } else {
                    ZipEntry docEntry = file.getEntry(docName);
                    if (docEntry == null) {
                        ProbeJS.LOGGER.warn("Document from file not found - {}", docName);
                        continue;
                    }
                    ProbeJS.LOGGER.info("Loading document inside jar - {}", docName);
                    InputStream docStream = file.getInputStream(docEntry);
                    BufferedReader docReader = new BufferedReader(
                        new InputStreamReader(new BufferedInputStream(docStream), StandardCharsets.UTF_8)
                    );
                    docReader.lines().forEach(document::step);
                }
            }
            file.close();
        }
    }

    public static void init() {
        Document documentState = new Document();
        rawTSDoc.clear();
        try {
            fromFiles(documentState);
            fromPath(documentState);
        } catch (IOException e) {
            e.printStackTrace();
        }

        classDocuments.clear();
        classAdditions.clear();
        typeDocuments.clear();
        typesAssignable.clear();

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
                        List<CommentAssign> assignable = comment.getSpecialComments(CommentAssign.class);
                        typesAssignable
                            .computeIfAbsent(target.getTargetName(), s -> new ArrayList<>())
                            .addAll(
                                assignable.stream().map(CommentAssign::getType).collect(Collectors.toList())
                            );
                        continue;
                    }
                }
                classAdditions.computeIfAbsent(classDoc.getName(), s -> new ArrayList<>()).add(classDoc);
            }

            if (doc instanceof DocumentType) {
                DocumentType typeDoc = (DocumentType) doc;
                if (CommentUtil.isLoaded(typeDoc.getComment())) {
                    typeDocuments.add(typeDoc);
                }
            }
        }
    }
}
