package com.probejs.document.parser.processor;

import com.probejs.document.DocumentField;
import com.probejs.document.DocumentMethod;
import com.probejs.document.DocumentType;
import com.probejs.util.StringUtil;

public class DocumentProviderManager {

    public static void init() {
        DocumentHandler.handlerCandidates.clear();
        ProviderClass.handlers.clear();

        //doc
        DocumentHandler.addHandlerCandidate(
            //empty line or single-line comment
            line -> {
                line = line.trim();
                return line.isEmpty() || line.startsWith("//");
            },
            (line, doc) -> null
        );
        DocumentHandler.addHandlerCandidate(
            line -> line.trim().startsWith("class "),
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
        ProviderClass.addSingleHandler(
            //empty line or single-line comment
            line -> {
                line = line.trim();
                return line.isEmpty() || line.startsWith("//");
            },
            (line, doc) -> {}
        );
        ProviderClass.addMultiHandler(
            line -> line.trim().startsWith("/**"),
            (line, doc) -> {
                ProviderComment comment = new ProviderComment();
                doc.addElement(comment);
                return comment;
            }
        );
        ProviderClass.addSingleHandler(
            //field, like: "static event: {raw: Internal.Class, lmbda: (a: number)=>boolean}"
            line -> {
                if (!line.contains(":")) {
                    return false;
                }
                if (!line.contains("(")) {
                    return true;
                }
                return StringUtil.indexLayer(line, "(") > StringUtil.indexLayer(line, ":");
            },
            (line, doc) -> {
                doc.addElement(new DocumentField(line));
            }
        );
        ProviderClass.addSingleHandler(
            //methods, like: "static get(a: number, b: string): string;"
            line -> {
                if (!line.contains(":")) {
                    return false;
                }
                return StringUtil.indexLayer("(", line) < StringUtil.indexLayer(":", line);
            },
            (line, doc) -> {
                doc.addElement(new DocumentMethod(line));
            }
        );
    }
}
