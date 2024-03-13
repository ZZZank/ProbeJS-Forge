package com.probejs.recipe;

import com.probejs.formatter.NameResolver;
import com.probejs.util.PUtil;
import com.probejs.util.Pair;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;

public abstract class RecipeHolders {

    static Map<String, List<Pair<String, String>>> namespace2Method = new HashMap<>();

    public static void init(Map<ResourceLocation, RecipeTypeJS> recipeHandlers) {
        namespace2Method.clear();
        recipeHandlers.forEach((key, value) -> {
            String namespace = key.getNamespace();
            String invoke = key.getPath();
            String recipeJSName =
                "Internal." +
                NameResolver.getResolvedName(value.factory.get().getClass().getName()).getLastName();

            namespace2Method
                .computeIfAbsent(namespace, k -> new ArrayList<Pair<String, String>>())
                .add(new Pair<>(invoke, recipeJSName));
        });
    }

    public static List<String> format(int indent, int stepIndent) {
        List<String> formatted = new ArrayList<>();
        // head
        formatted.add(PUtil.indent(indent) + "declare namespace stub.probejs {");

        indent += stepIndent;
        // a base class for indexing other classes
        formatted.add(PUtil.indent(indent) + "class RecipeHolder {");
        for (String namespace : namespace2Method.keySet()) {
            formatted.add(
                PUtil.indent(indent + stepIndent) +
                String.format("readonly %s: stub.probejs.%s", namespace, namespace)
            );
        }
        formatted.add(PUtil.indent(indent) + "}");

        // recipeHolder classes
        for (Entry<String, List<Pair<String, String>>> entry : namespace2Method.entrySet()) {
            formatted.add(String.format(PUtil.indent(indent) + "class %s {", entry.getKey()));
            // methods inside recipeHolder classes
            for (Pair<String, String> pair : entry.getValue()) {
                // we dont know how a Recipe Serializer actually works, so only `...args`
                formatted.add(
                    PUtil.indent(indent + stepIndent) +
                    String.format("%s(...args: object): %s", pair.getFirst(), pair.getSecond())
                );
            }
            formatted.add(PUtil.indent(indent) + "}");
        }
        indent -= stepIndent;

        // end
        formatted.add(PUtil.indent(indent) + "}");
        return formatted;
    }
}
