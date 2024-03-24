package com.probejs.compiler;

import com.probejs.ProbePaths;
import com.probejs.compiler.special.RecipeHoldersComplier;
import com.probejs.compiler.special.RegistryCompiler;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class SpecialComplier {

    public static final Path PATH = ProbePaths.GENERATED.resolve("special.d.ts");

    public static void compile() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(PATH);

        RegistryCompiler.compileRegistries(writer);
        RecipeHoldersComplier.compileRecipeHolder(writer);
    }
}
