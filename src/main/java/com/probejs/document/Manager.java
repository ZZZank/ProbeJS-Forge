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
import java.util.stream.Collector;
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
        List<File> filesSorted = files == null
            ? new ArrayList<>()
            : new ArrayList<>(Arrays.stream(files).collect(Collectors.toList()));
        filesSorted.sort(Comparator.comparing(File::getName));
        for (File f : filesSorted) {
            if (!f.getName().endsWith(".d.ts") || f.isDirectory()) {
                return;
            }
            BufferedReader reader = Files.newBufferedReader(f.toPath());
            if (!f.getName().startsWith("!")) reader.lines().forEach(document::step); else reader
                .lines()
                .forEach(rawTSDoc::add);
        }
    }

    public static void fromFiles(Document document) throws IOException {
        for (Mod mod : Platform.getMods()) {
            Path filePath = mod.getFilePath();
            if (
                Files.isRegularFile(filePath) &&
                (
                    filePath.getFileName().toString().endsWith(".jar") ||
                    filePath.getFileName().toString().endsWith(".zip")
                )
            ) {
                ZipFile file = new ZipFile(filePath.toFile());
                ZipEntry entry = file.getEntry("probejs.documents.txt");
                if (entry != null) {
                    ProbeJS.LOGGER.info(String.format("Found documents list from %s", mod.getName()));
                    InputStream stream = file.getInputStream(entry);
                    BufferedReader reader = new BufferedReader(
                        new InputStreamReader(new BufferedInputStream(stream), StandardCharsets.UTF_8)
                    );
                    List<String> list = reader.lines().collect(Collectors.toList());
                    for (String subEntry : list) {
                        if (subEntry.startsWith("!")) {
                            subEntry = subEntry.substring(1);
                            int i = subEntry.indexOf(" ");
                            if (i != -1) {
                                if (!Platform.isModLoaded(subEntry.substring(0, i))) {
                                    continue;
                                }
                                subEntry = subEntry.substring(i + 1);
                            }
                            ZipEntry docEntry = file.getEntry(subEntry);
                            if (docEntry != null) {
                                ProbeJS.LOGGER.info(
                                    String.format("Loading document inside jar - %s", subEntry)
                                );
                                InputStream docStream = file.getInputStream(docEntry);
                                BufferedReader docReader = new BufferedReader(
                                    new InputStreamReader(
                                        new BufferedInputStream(docStream),
                                        StandardCharsets.UTF_8
                                    )
                                );
                                docReader.lines().forEach(rawTSDoc::add);
                            } else {
                                ProbeJS.LOGGER.warn(
                                    String.format("Document from file is not found - %s", subEntry)
                                );
                            }
                        } else {
                            ZipEntry docEntry = file.getEntry(subEntry);
                            if (docEntry != null) {
                                ProbeJS.LOGGER.info(
                                    String.format("Loading document inside jar - %s", subEntry)
                                );
                                InputStream docStream = file.getInputStream(docEntry);
                                BufferedReader docReader = new BufferedReader(
                                    new InputStreamReader(
                                        new BufferedInputStream(docStream),
                                        StandardCharsets.UTF_8
                                    )
                                );
                                docReader.lines().forEach(document::step);
                            } else {
                                ProbeJS.LOGGER.warn(
                                    String.format("Document from file is not found - %s", subEntry)
                                );
                            }
                        }
                    }
                }
            }
        }
    }

    public static void init() {
        Document documentState = new Document();
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
        rawTSDoc.clear();

        for (IDocument doc : documentState.getDocument().getDocuments()) {
            if (doc instanceof DocumentClass) {
                DocumentClass classDoc = (DocumentClass) doc;
                if (CommentUtil.isLoaded(classDoc.getComment())) {
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
                                    assignable
                                        .stream()
                                        .map(CommentAssign::getType)
                                        .collect(Collectors.toList())
                                );
                            continue;
                        }
                    }
                    classAdditions.computeIfAbsent(classDoc.getName(), s -> new ArrayList<>()).add(classDoc);
                }
            }

            if (doc instanceof DocumentType) {
                if (CommentUtil.isLoaded(((DocumentType) doc).getComment())) typeDocuments.add(
                    (DocumentType) doc
                );
            }
        }
    }
}
