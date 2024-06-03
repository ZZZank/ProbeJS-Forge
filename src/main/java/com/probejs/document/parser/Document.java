package com.probejs.document.parser;

import com.probejs.document.parser.handler.AbstractStackedMachine;
import com.probejs.document.parser.processor.DocumentHandler;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;

@Getter
public class Document extends AbstractStackedMachine<String> {

    private final DocumentHandler document;
    private final ArrayList<String> rawDocs;

    public Document() {
        this.rawDocs = new ArrayList<>();
        document = new DocumentHandler();
        this.stack.add(document);
    }

    public DocReader reader() {
        return new DocReader(this);
    }

    public DocParser parser() {
        return new DocParser(this);
    }

    @Override
    public void step(String element) {
        super.step(element);
    }

    public void stepAll(Collection<String> lines) {
        for (String line : lines) {
            super.step(line);
        }
    }
}
