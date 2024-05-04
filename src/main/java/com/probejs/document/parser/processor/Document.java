package com.probejs.document.parser.processor;

import com.probejs.document.parser.handler.AbstractStackedMachine;
import lombok.Getter;

import java.util.Collection;

@Getter
public class Document extends AbstractStackedMachine<String> {

    private final DocumentHandler document;

    public Document() {
        document = new DocumentHandler();
        this.stack.add(document);
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
