package com.probejs.features.rich.fluid;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import com.probejs.features.rich.ImageHelper;
import com.probejs.util.json.JArray;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RichFluidCompiler {
    public static void compile() throws IOException {
        JArray fluidArray = JArray.create()
                .addAll(RegistryInfo.FLUID.entrySet().stream()
                        .map(Map.Entry::getValue)
                        .map(FluidAttribute::new)
                        .map(FluidAttribute::serialize));
        Path richFile = ProbePaths.WORKSPACE_SETTINGS.resolve("fluid-attributes.json");
        BufferedWriter writer = Files.newBufferedWriter(richFile);
        writer.write(ProbeJS.GSON.toJson(fluidArray.serialize()));
        writer.close();
    }
}
