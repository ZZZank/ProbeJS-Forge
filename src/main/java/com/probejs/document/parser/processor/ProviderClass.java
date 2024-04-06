package com.probejs.document.parser.processor;

import com.probejs.document.DocumentClass;
import com.probejs.document.IConcrete;
import com.probejs.document.IDecorative;
import com.probejs.document.IDocument;
import com.probejs.document.parser.handler.IStateHandler;
import com.probejs.util.Pair;
import java.util.ArrayList;
import java.util.Arrays;
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
    private List<String> interfaces;

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
    public void trial(String line, List<IStateHandler<String>> stack) {
        // "    class A implements List, Wow extends B {"
        line = line.trim();
        if (line.startsWith("class ")) {
            final int indexExtd = line.indexOf(" extends ");
            final int indexImpl = line.indexOf(" implements ");
            /**
             * holds some critical indexes that marks start/end of certain parts
             */
            List<Integer> anchors = new ArrayList<>();
            anchors.add("class ".length()); //start
            anchors.add(line.length() - (line.endsWith("{") ? 1 : 0)); //end
            if (indexExtd != -1) {
                anchors.add(indexExtd);
                anchors.add(indexExtd + " extends ".length());
            }
            if (indexImpl != -1) {
                anchors.add(indexImpl);
                anchors.add(indexImpl + " implements ".length());
            }
            anchors.sort(null);
            if (indexImpl != -1) {
                int i = anchors.indexOf(indexImpl);
                //i==impl, i+1==end of impl, i+2==start of next part
                this.interfaces =
                    Arrays
                        .stream(line.substring(anchors.get(i + 1), anchors.get(i + 2)).split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
            }
            if (indexExtd != -1) {
                int i = anchors.indexOf(indexExtd);
                this.superClass = line.substring(anchors.get(i + 1), anchors.get(i + 2)).trim();
            }
        } else if (line.equals("}")) {
            stack.remove(this);
        }

        for (Pair<Predicate<String>, BiFunction<String, ProviderClass, IStateHandler<String>>> multiHandler : handlers) {
            if (multiHandler.first().test(line)) {
                IStateHandler<String> layer = multiHandler.second().apply(line, this);
                if (layer != null) {
                    layer.trial(line, stack);
                    stack.add(layer);
                }
                return;
            }
        }
        // ProbeJS.LOGGER.error("Cannot handle document string: {}", element);
    }

    @Override
    public DocumentClass provide() {
        DocumentClass document = new DocumentClass();
        document.setName(name);
        if (this.superClass != null) {
            document.setSuperClass(this.superClass);
            document.setInterfaces(this.interfaces);
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
