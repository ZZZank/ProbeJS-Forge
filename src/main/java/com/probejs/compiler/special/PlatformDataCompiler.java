package com.probejs.compiler.special;

import com.probejs.ProbeJS;
import com.probejs.formatter.FormatterNamespace;
import com.probejs.formatter.FormatterRaw;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.shedaniel.architectury.platform.Platform;

public class PlatformDataCompiler {

    public static void compile(BufferedWriter writer) throws IOException {
        final List<String> lines = new ArrayList<>();
        //modids
        final String modids = Platform
            .getModIds()
            .stream()
            .map(ProbeJS.GSON::toJson)
            .collect(Collectors.joining("|"));
        lines.add(String.format("type modids = %s;", modids));
        //more?

        for (final String line : new FormatterNamespace("platform", new FormatterRaw(lines, false))
            .formatLines(0, 4)) {
            writer.write(line);
            writer.write('\n');
        }
        writer.write('\n');
    }
}
