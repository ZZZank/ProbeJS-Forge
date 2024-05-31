package com.probejs.compiler.special;

import com.probejs.ProbeJS;
import com.probejs.formatter.FormatterNamespace;
import com.probejs.formatter.FormatterRaw;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.probejs.util.PUtil;
import lombok.val;
import me.shedaniel.architectury.platform.Platform;

public class PlatformDataCompiler {

    public static void compile(BufferedWriter writer) throws IOException {
        val lines = new ArrayList<String>();
        //modids
        val modids = Platform
            .getModIds()
            .stream()
            .map(ProbeJS.GSON::toJson)
            .collect(Collectors.joining("|"));
        lines.add(String.format("type modids = %s;", modids));
        //more?

        PUtil.writeLines(
            writer,
            new FormatterNamespace("platform", new FormatterRaw(lines, false)).formatLines(0, 4)
        );
        writer.write('\n');
    }
}
