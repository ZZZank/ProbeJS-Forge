package com.probejs.document.parser.processor;

import com.probejs.document.DocumentField;
import com.probejs.document.DocumentMethod;
import com.probejs.document.DocumentType;

public class DocumentProviderHandler {

    public static void init() {
        DocumentHandler.handlers.clear();
        ProviderClass.handlers.clear();

        //doc
        DocumentHandler.addMultiHandler(
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
        DocumentHandler.addMultiHandler(
            c -> c.trim().startsWith("/**"),
            (s, d) -> {
                ProviderComment comment = new ProviderComment();
                d.addElement(comment);
                return comment;
            }
        );
        DocumentHandler.addSingleHandler(
            c -> c.trim().startsWith("type "),
            (s, d) -> d.addElement(DocumentType.of(s))
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
