package com.probejs.compiler.rich.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
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
        JsonObject o = new JsonObject();
        //tag id
        o.addProperty("id", id);
        //items in tag
        JsonArray tags = new JsonArray();
        itemsOfTag.stream().map(Registry.ITEM::getKey).map(ResourceLocation::toString).forEach(tags::add);
        o.add("items", tags);
        return o;
    }
}
