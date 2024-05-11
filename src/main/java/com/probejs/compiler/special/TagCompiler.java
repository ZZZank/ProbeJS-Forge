package com.probejs.compiler.special;

import com.probejs.ProbeJS;
import com.probejs.formatter.FormatterNamespace;
import com.probejs.formatter.FormatterRaw;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import lombok.val;
import net.minecraft.resources.ResourceLocation;

public abstract class TagCompiler {

    private static Map<ResourceLocation, Collection<ResourceLocation>> tags;

    public static void init(Map<ResourceLocation, Collection<ResourceLocation>> tags) {
        TagCompiler.tags = tags;
    }

    public static List<String> format(int indent, int stepIndent) {
        val lines = new ArrayList<String>();
        val duped = new HashSet<String>();
        tags.forEach((id, entriesRl) -> {
            //name
            String name = id.getPath();
            if (duped.contains(name)) {
                name = name + "_" + id.getNamespace();
            }
            duped.add(name);
            //entries
            String entries = entriesRl.stream()
                .map(ResourceLocation::toString)
                .map(ProbeJS.GSON::toJson)
                .collect(Collectors.joining("|"));
            if (entries.isEmpty()) {
                entries = "never";
            }
            //link together
            lines.add(String.format("type %s = %s;", name, entries));
        });
        return new FormatterNamespace("Tag", new FormatterRaw(lines, false))
            .formatLines(indent, stepIndent);
    }

    public static void compile(BufferedWriter writer) throws IOException {
        for (String line : TagCompiler.format(0, 4)) {
            writer.write(line);
            writer.write('\n');
        }
        writer.write('\n');
        tags = null;
    }
}
