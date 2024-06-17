package com.probejs.document.parser.processor;

import com.probejs.document.DocComment;
import com.probejs.document.parser.handler.IStateHandler;
import java.util.ArrayList;
import java.util.List;

public class ProviderComment implements IStateHandler<String>, IDocumentProvider<DocComment> {

    private final List<String> comments = new ArrayList<>();

    @Override
    public void trial(String element, List<IStateHandler<String>> stack) {
        comments.add(element);
        if (element.trim().endsWith("*/")) {
            stack.remove(this);
        }
    }

    @Override
    public DocComment provide() {
        return new DocComment(comments);
    }
}
