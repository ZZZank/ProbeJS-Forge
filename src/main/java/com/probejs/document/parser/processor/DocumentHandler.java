package com.probejs.document.parser.processor;

import com.probejs.document.IConcrete;
import com.probejs.document.IDecorative;
import com.probejs.document.IDocument;
import com.probejs.document.parser.handler.IMultiHandler;
import com.probejs.document.parser.handler.IStateHandler;
import com.probejs.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class DocumentHandler implements IStateHandler<String> {

    /**
     * a list of {condition, handler} pair
     * <p>
     * "condition" will be called sequentually, and will stop once one condition is met. 
     * Then cooresponding "handler" will be called
     */
    public static final List<Pair<Predicate<String>, IMultiHandler>> handlerCandidates = new ArrayList<>();
    private final List<IDocumentProvider<?>> elements = new ArrayList<>();

    public static void addHandlerCandidate(Predicate<String> condition, IMultiHandler handler) {
        handlerCandidates.add(new Pair<>(condition, handler));
    }

    public void addElement(IDocumentProvider<?> element) {
        this.elements.add(element);
    }

    public List<IDocument> getDocuments() {
        List<IDecorative> decos = new ArrayList<>();
        List<IDocument> elements = new ArrayList<>();
        for (IDocumentProvider<?> document : this.elements) {
            IDocument doc = document.provide();
            if (doc instanceof IDecorative) {
                decos.add((IDecorative) doc);
            } else {
                if (doc instanceof IConcrete) {
                    ((IConcrete) doc).acceptDeco(decos);
                }
                decos.clear();
                elements.add(doc);
            }
        }
        return elements;
    }

    @Override
    public void trial(String line, List<IStateHandler<String>> stack) {
        line = line.trim();
        for (Pair<Predicate<String>, IMultiHandler> handler : handlerCandidates) {
            if (!handler.first().test(line)) {
                //skip if condition not met
                continue;
            }
            IStateHandler<String> layer = handler.second().apply(line, this);
            if (layer != null) {
                layer.trial(line, stack);
                stack.add(layer);
            }
            //because trial once is enough
            return;
        }
    }
}
