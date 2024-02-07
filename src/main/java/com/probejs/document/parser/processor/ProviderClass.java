package com.probejs.document.parser.processor;

import com.probejs.ProbeJS;
import com.probejs.document.DocumentClass;
import com.probejs.document.IConcrete;
import com.probejs.document.IDecorative;
import com.probejs.document.IDocument;
import com.probejs.document.parser.handler.IStateHandler;
import com.probejs.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProviderClass implements IStateHandler<String>, IDocumentProvider<DocumentClass> {

    public static List<Pair<Predicate<String>, BiFunction<String, ProviderClass, IStateHandler<String>>>> handlers = new ArrayList<>();
    private final List<IDocumentProvider<?>> elements = new ArrayList<>();
    private String name;
    private String superClass;

    public static void addMultiHandler(
        Predicate<String> condition,
        BiFunction<String, ProviderClass, IStateHandler<String>> handler
    ) {
        handlers.add(new Pair<>(condition, handler));
    }

    public static void addSingleHandler(
        Predicate<String> condition,
        BiConsumer<String, ProviderClass> handler
    ) {
        handlers.add(
            new Pair<>(
                condition,
                (s, documentHandler) -> {
                    handler.accept(s, documentHandler);
                    return null;
                }
            )
        );
    }

    public void addElement(IDocumentProvider<?> element) {
        this.elements.add(element);
    }

    @Override
    public void trial(String element, List<IStateHandler<String>> stack) {
        element = element.trim();
        if (element.startsWith("class ") && element.endsWith("{")) {
            int start = "class ".length();
            int end = element.length() - 1; // `-1` because we dont need "{"
            int indexExtd = element.indexOf(" extends ");
            int indexImpl = element.indexOf(" implements ");
            if (indexImpl != -1) {
                ProbeJS.LOGGER.error("'implements' not supported yet!");
            }
            if (indexExtd != -1) {
                superClass = element.substring(indexExtd + " extends ".length(), end).trim();
                name = element.substring(start, indexExtd).trim();
            } else {
                superClass = null;
                name = element.substring(start, end).trim();
            }
        } else if (element.equals("}")) {
            stack.remove(this);
        }

        for (Pair<Predicate<String>, BiFunction<String, ProviderClass, IStateHandler<String>>> multiHandler : handlers) {
            if (multiHandler.getFirst().test(element)) {
                IStateHandler<String> layer = multiHandler.getSecond().apply(element, this);
                if (layer != null) {
                    layer.trial(element, stack);
                    stack.add(layer);
                }
                return;
            }
        }
        ProbeJS.LOGGER.error("Cannot handle document string: {}", element);
    }

    @Override
    public DocumentClass provide() {
        DocumentClass document = new DocumentClass();
        document.setName(name);
        if (this.superClass != null) {
            document.setSuperClass(this.superClass);
        }
        List<IDecorative> decos = new ArrayList<>();
        for (IDocumentProvider<?> provider : elements) {
            IDocument doc = provider.provide();
            if (doc instanceof IDecorative) {
                decos.add((IDecorative) doc);
            } else {
                if (doc instanceof IConcrete) {
                    ((IConcrete) doc).acceptDeco(decos.stream().collect(Collectors.toList()));
                }
                decos.clear();
                document.acceptProperty(doc);
            }
        }
        return document;
    }

    public String getName() {
        return name;
    }
}
