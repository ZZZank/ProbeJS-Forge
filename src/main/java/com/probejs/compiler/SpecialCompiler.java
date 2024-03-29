package com.probejs.compiler;

import com.probejs.ProbePaths;
import com.probejs.compiler.special.PlatformDataCompiler;
import com.probejs.compiler.special.RecipeHoldersCompiler;
import com.probejs.compiler.special.RegistryCompiler;
import com.probejs.compiler.special.TagCompiler;
import com.probejs.info.SpecialData;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public abstract class SpecialCompiler {

    public static final Path PATH = ProbePaths.GENERATED.resolve("special.d.ts");

    public static void init(Map<ResourceLocation, RecipeTypeJS> recipeHandlers) {
        final SpecialData data = SpecialData.fetch();

        SnippetCompiler.init(data);
        RegistryCompiler.init(data.registries);
        TagCompiler.init(data.tags);
        RecipeHoldersCompiler.init(recipeHandlers);
    }

    public static void compile() throws IOException {
        final BufferedWriter writer = Files.newBufferedWriter(PATH);

        RegistryCompiler.compile(writer);
        TagCompiler.compile(writer);
        RecipeHoldersCompiler.compile(writer);
        PlatformDataCompiler.compile(writer);

        writer.close();
    }
}
