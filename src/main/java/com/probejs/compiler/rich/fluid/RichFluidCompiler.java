package com.probejs.compiler.rich.fluid;

import com.google.gson.JsonArray;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import net.minecraft.core.Registry;

public class RichFluidCompiler {

    public static void compile() throws IOException {
        JsonArray fluidArray = new JsonArray();
        Registry.FLUID
            .entrySet()
            .stream()
            .map(Map.Entry::getValue)
            .map(FluidAttribute::new)
            .map(FluidAttribute::serialize)
            .forEach(fluidArray::add);
        Path richFile = ProbePaths.WORKSPACE.resolve("fluid-attributes.json");
        BufferedWriter writer = Files.newBufferedWriter(richFile);
        ProbeJS.GSON.toJson(fluidArray, writer);
        writer.close();
    }
}
