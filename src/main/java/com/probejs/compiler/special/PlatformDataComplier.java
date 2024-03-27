package com.probejs.compiler.special;

import com.probejs.ProbeJS;
import com.probejs.formatter.formatter.FormatterNamespace;
import com.probejs.formatter.formatter.FormatterRaw;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import me.shedaniel.architectury.platform.Platform;

public class PlatformDataComplier {

    public static void compile(BufferedWriter writer) throws IOException {
        List<String> lines = new ArrayList<>();
        //modids
        String modids = Platform
            .getModIds()
            .stream()
            .map(ProbeJS.GSON::toJson)
            .collect(Collectors.joining("|"));
        lines.add(String.format("type modids = %s;", modids));
        //empty line to seperate different namespaces in dumped docs
        lines.add("");

        new FormatterNamespace("platform", new FormatterRaw(lines));
    }
}
