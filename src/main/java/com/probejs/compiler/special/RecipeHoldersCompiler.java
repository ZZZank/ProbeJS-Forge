package com.probejs.compiler.special;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.probejs.formatter.resolver.PathResolver;
import com.probejs.formatter.FormatterNamespace;
import com.probejs.formatter.FormatterRaw;
import com.probejs.formatter.api.IFormatter;
import com.probejs.util.PUtil;
import com.probejs.util.Pair;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import lombok.val;
import net.minecraft.resources.ResourceLocation;

public abstract class RecipeHoldersCompiler {

    /**
     * mod -> {functionName, returnName}
     */
    private static final Multimap<String, Pair<String, String>> namespace2Method = ArrayListMultimap.create();

    public static void init(Map<ResourceLocation, RecipeTypeJS> recipeHandlers) {
        namespace2Method.clear();
        recipeHandlers.forEach((key, value) -> {
            val namespace = key.getNamespace();
            val invoke = key.getPath();
            val recipeJSName = PathResolver.resolveName(value.factory.get().getClass()).fullPath();

            namespace2Method.put(namespace, new Pair<>(invoke, recipeJSName));
        });
    }

    public static List<String> format(int indent, int stepIndent) {
        val namespecedFmtr = new ArrayList<IFormatter>();
        val step = PUtil.indent(stepIndent);

        {
            List<String> base = new ArrayList<>();
            base.add("class RecipeHolder {");
            namespace2Method
                .keySet()
                .stream()
                .map(namespace -> String.format("%sreadonly %s: stub.probejs.%s", step, namespace, namespace))
                .forEach(base::add);
            base.add("}");
            namespecedFmtr.add(new FormatterRaw(base, false));
        }

        for (Entry<String, Collection<Pair<String, String>>> entry : namespace2Method.asMap().entrySet()) {
            val name = entry.getKey();
            List<String> lines = new ArrayList<>();
            //name
            lines.add(String.format("class %s {", name));
            //methods inside recipeHolder classes
            entry
                .getValue()
                .stream()
                // we dont know how a Recipe Serializer actually works, so only `...args`
                .map(p -> String.format("%s%s(...args: any): %s;", step, p.first(), p.second()))
                .forEach(lines::add);
            //close
            lines.add("}");
            namespecedFmtr.add(new FormatterRaw(lines, false));
        }

        return new FormatterNamespace("stub.probejs", namespecedFmtr).formatLines(indent, stepIndent);
    }

    public static void compile(BufferedWriter writer) throws IOException {
        PUtil.writeLines(writer, format(0, 4));
        writer.write('\n');
        namespace2Method.clear();
    }
}
