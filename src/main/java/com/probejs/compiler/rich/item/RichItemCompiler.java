package com.probejs.compiler.rich.item;

import com.google.gson.JsonArray;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import dev.latvian.kubejs.util.Tags;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

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

    /*
    public static void render(List<Pair<ItemStack, Path>> items) throws IOException {
        RenderTarget frameBuffer = ImageHelper.init();
        for (Pair<ItemStack, Path> pair : items) {
            NativeImage image = ImageHelper.getFromItem(pair.getFirst(), frameBuffer);
            image.writeToFile(pair.getSecond());
            image.close();
            frameBuffer.clear(false);
        }
        frameBuffer.destroyBuffers();
    }

    public static List<Pair<ItemStack, Path>> resolve() {
        ArrayList<Pair<ItemStack, Path>> items = new ArrayList<>();
        for (ItemStack itemStack : RegistryInfo.ITEM
            .entrySet()
            .stream()
            .map(Map.Entry::getValue)
            .map(Item::getDefaultInstance)
            .toList()) {
            Path path = ProbePaths.RICH_ITEM.resolve(itemStack.kjs$getIdLocation().getNamespace());
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            String name = itemStack.kjs$getIdLocation().getPath().replace("/", "_");
            if (path.resolve(name + ".png").toFile().exists()) {
                continue;
            }
            items.add(Pair.of(itemStack, path.resolve(name + ".png")));
        }
        return items;
    }
     */
}
