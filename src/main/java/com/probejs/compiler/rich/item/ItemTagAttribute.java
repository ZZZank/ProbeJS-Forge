package com.probejs.compiler.rich.item;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;

import com.probejs.util.json.JArray;
import com.probejs.util.json.JObject;
import com.probejs.util.json.JPrimitive;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

public class ItemTagAttribute {

    private final String id;
    private final List<Item> itemsOfTag;

    public ItemTagAttribute(Map.Entry<ResourceLocation, Tag<Item>> entry) {
        this.id = entry.getKey().toString();
        this.itemsOfTag = entry.getValue().getValues();
    }

    /*
    public ItemTagAttribute(Tag<Item> itemTag) {
        this.id = itemTag.location().toString();
        this.itemsOfTag = new ArrayList<>();
        Tags.items().getTag(this.id);
        for (Holder<Item> holder : RegistryInfo.ITEM.getVanillaRegistry().getTagOrEmpty(itemTag)) {
            itemsOfTag.add(holder.value());
        }
    }
     */

    public JsonObject serialize() {
        return JObject.of()
            .add("id", id)
            .add("items",
                JArray.of()
                    .addAll(itemsOfTag.stream()
                        .map(Registry.ITEM::getKey)
                        .map(ResourceLocation::toString)
                        .map(JPrimitive::of)
                    )
            )
            .build();
    }
}
