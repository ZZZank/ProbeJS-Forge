package com.probejs.compiler;

import com.probejs.ProbePaths;
import com.probejs.compiler.special.RecipeHoldersComplier;
import com.probejs.compiler.special.RegistryCompiler;
import com.probejs.compiler.special.TagComplier;
import com.probejs.info.SpecialData;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public abstract class SpecialComplier {

    public static final Path PATH = ProbePaths.GENERATED.resolve("special.d.ts");

    public static void init(Map<ResourceLocation, RecipeTypeJS> recipeHandlers) {
        SpecialData data = SpecialData.fetch();

        RegistryCompiler.init(data);
        TagComplier.init(data);
        RecipeHoldersComplier.init(recipeHandlers);
    }

    public static void compile() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(PATH);

        RegistryCompiler.compileRegistries(writer);
        RecipeHoldersComplier.compileRecipeHolder(writer);

        writer.close();
    }
}
