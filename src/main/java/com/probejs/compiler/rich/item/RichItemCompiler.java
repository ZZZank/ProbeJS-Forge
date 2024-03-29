package com.probejs.compiler.rich.item;

import com.google.gson.JsonArray;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import dev.latvian.kubejs.util.Tags;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

public class RichItemCompiler {

    public static void compile() throws IOException {
        JsonArray itemArray = new JsonArray();
        Registry.ITEM
            .entrySet()
            .stream()
            .map(Map.Entry::getValue)
            .map(Item::getDefaultInstance)
            .map(ItemAttribute::new)
            .map(ItemAttribute::serialize)
            .forEach(itemArray::add);

        Path richFile = ProbePaths.WORKSPACE.resolve("item-attributes.json");
        BufferedWriter writer = Files.newBufferedWriter(richFile);
        writer.write(ProbeJS.GSON.toJson(itemArray));
        writer.close();

        JsonArray tagArray = new JsonArray();
        Tags
            .items()
            .getAllTags()
            .entrySet()
            .stream()
            .map(ItemTagAttribute::new)
            .map(ItemTagAttribute::serialize)
            .forEach(tagArray::add);

        Path richTagFile = ProbePaths.WORKSPACE.resolve("item-tag-attributes.json");
        BufferedWriter tagWriter = Files.newBufferedWriter(richTagFile);
        tagWriter.write(ProbeJS.GSON.toJson(tagArray));
        tagWriter.close();
    }
}
