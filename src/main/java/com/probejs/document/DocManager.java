package com.probejs.document;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.probejs.document.comment.CommentUtil;
import com.probejs.document.comment.special.CommentAssign;
import com.probejs.document.comment.special.CommentTarget;
import com.probejs.document.parser.Document;
import com.probejs.document.type.DocType;
import lombok.val;

import java.util.*;

public class DocManager {

    public static final Multimap<String, DocumentClass> classDocuments = ArrayListMultimap.create();
    public static final Map<String, List<DocType>> typesAssignable = new HashMap<>();
    public static final Map<String, List<DocumentClass>> classAdditions = new HashMap<>();
    public static final List<String> rawTSDoc = new ArrayList<>();
    public static final List<DocumentType> typeDocuments = new ArrayList<>();

    public static void init() {
        Document document = new Document();

        rawTSDoc.clear();
        classDocuments.clear();
        classAdditions.clear();
        typeDocuments.clear();
        typesAssignable.clear();

        document.reader().defaultSetup().read();

        rawTSDoc.addAll(document.getRawDocs());

        for (val doc : document.getDocument().getDocuments()) {
            if (doc instanceof DocumentClass classDoc) {
                if (!CommentUtil.isLoaded(classDoc.getComment())) {
                    continue;
                }
                DocumentComment comment = classDoc.getComment();
                if (comment != null) {
                    CommentTarget target = comment.getSpecialComment(CommentTarget.class);
                    if (target != null) {
                        classDocuments.put(target.getTargetName(), classDoc);
                        comment
                            .getSpecialComments(CommentAssign.class)
                            .stream()
                            .map(CommentAssign::getType)
                            .forEach(type -> addAssignable(target.getTargetName(), type));
                        continue;
                    }
                }
                addAdditions(classDoc.getName(), classDoc);
            } else if (doc instanceof DocumentType typeDoc) {
                if (CommentUtil.isLoaded(typeDoc.getComment())) {
                    typeDocuments.add(typeDoc);
                }
            } else {
                //maybe we can add more doc type
            }
        }
    }

    public static void addAssignable(String className, DocType type) {
        DocManager.typesAssignable.computeIfAbsent(className, k -> new ArrayList<>()).add(type);
    }

    public static void addAdditions(String className, DocumentClass addition) {
        DocManager.classAdditions.computeIfAbsent(className, k -> new ArrayList<>()).add(addition);
    }
}
