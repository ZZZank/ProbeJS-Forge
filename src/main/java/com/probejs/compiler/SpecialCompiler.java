package com.probejs.compiler;

import com.probejs.ProbePaths;
import com.probejs.compiler.rich.fluid.RichFluidCompiler;
import com.probejs.compiler.rich.item.RichItemCompiler;
import com.probejs.compiler.rich.lang.RichLangCompiler;
import com.probejs.compiler.special.PlatformDataComplier;
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

        RegistryCompiler.compileRegistries(writer);
        TagCompiler.compile(writer);
        RecipeHoldersCompiler.compileRecipeHolder(writer);
        PlatformDataComplier.compile(writer);

        RichFluidCompiler.compile();
        RichItemCompiler.compile();
        RichLangCompiler.compile();

        writer.close();
    }
}
