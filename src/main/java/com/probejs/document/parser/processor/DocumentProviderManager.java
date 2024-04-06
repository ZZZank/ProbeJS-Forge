package com.probejs.document.parser.processor;

import com.probejs.document.DocumentField;
import com.probejs.document.DocumentMethod;
import com.probejs.document.DocumentType;

public class DocumentProviderManager {

    public static void init() {
        DocumentHandler.handlerCandidates.clear();
        ProviderClass.handlers.clear();

        //doc
        DocumentHandler.addHandlerCandidate(line -> line.trim().isEmpty(), (line, doc) -> null);
        DocumentHandler.addHandlerCandidate(
            c -> {
                String cs = c.trim();
                return cs.startsWith("class ") && cs.endsWith("{");
            },
            (s, d) -> {
                ProviderClass clazz = new ProviderClass();
                d.addElement(clazz);
                return clazz;
            }
        );
        DocumentHandler.addHandlerCandidate(
            c -> c.trim().startsWith("/**"),
            (s, d) -> {
                ProviderComment comment = new ProviderComment();
                d.addElement(comment);
                return comment;
            }
        );
        DocumentHandler.addHandlerCandidate(
            c -> c.trim().startsWith("type "),
            (s, d) -> {
                d.addElement(DocumentType.of(s));
                return null;
            }
        );

        //class
        ProviderClass.addMultiHandler(
            s -> s.trim().startsWith("/**"),
            (s, d) -> {
                ProviderComment comment = new ProviderComment();
                d.addElement(comment);
                return comment;
            }
        );
        ProviderClass.addSingleHandler(
            s -> s.contains(":") && !s.contains("("),
            (s, d) -> {
                d.addElement(new DocumentField(s));
            }
        );
        ProviderClass.addSingleHandler(
            s -> s.contains("("),
            (s, d) -> {
                d.addElement(new DocumentMethod(s));
            }
        );
    }
}
