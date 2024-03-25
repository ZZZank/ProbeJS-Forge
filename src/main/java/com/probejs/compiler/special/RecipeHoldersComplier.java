package com.probejs.compiler.special;

import com.probejs.formatter.NameResolver;
import com.probejs.formatter.formatter.FormatterNamespace;
import com.probejs.formatter.formatter.FormatterRaw;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.util.PUtil;
import com.probejs.util.Pair;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.ResourceLocation;

public abstract class RecipeHoldersComplier {

    private static Map<String, List<Pair<String, String>>> namespace2Method = new HashMap<>();

    public static void init(Map<ResourceLocation, RecipeTypeJS> recipeHandlers) {
        namespace2Method.clear();
        recipeHandlers.forEach((key, value) -> {
            String namespace = key.getNamespace();
            String invoke = key.getPath();
            String recipeJSName = NameResolver.resolveName(value.factory.get().getClass()).getFullName();

            namespace2Method
                .computeIfAbsent(namespace, k -> new ArrayList<Pair<String, String>>())
                .add(new Pair<>(invoke, recipeJSName));
        });
    }

    public static List<String> format(int indent, int stepIndent) {
        List<IFormatter> namespecedFmtr = new ArrayList<>();
        String step = PUtil.indent(stepIndent);

        {
            List<String> base = new ArrayList<>();
            base.add("class RecipeHolder {");
            for (String namespace : namespace2Method.keySet()) {
                base.add(String.format("%sreadonly %s: stub.probejs.%s", step, namespace, namespace));
            }
            base.add("}");
            namespecedFmtr.add(new FormatterRaw(base, false));
        }

        for (Entry<String, List<Pair<String, String>>> entry : namespace2Method.entrySet()) {
            String name = entry.getKey();
            List<String> lines = new ArrayList<>();
            //name
            lines.add(String.format("class %s {", name));
            //methods inside recipeHolder classes
            for (Pair<String, String> pair : entry.getValue()) {
                // we dont know how a Recipe Serializer actually works, so only `...args`
                lines.add(String.format("%s%s(...args: any): %s;", step, pair.first, pair.second));
            }
            //close
            lines.add("}");
            namespecedFmtr.add(new FormatterRaw(lines, false));
        }

        return new FormatterNamespace("stub.probejs", namespecedFmtr).format(indent, stepIndent);
    }

    public static void compileRecipeHolder(BufferedWriter writer) throws IOException {
        for (String line : format(0, 4)) {
            writer.write(line);
            writer.write('\n');
        }
        writer.write('\n');
    }
}
