package com.probejs.compiler.rich.item;

import com.google.gson.JsonObject;

import com.probejs.util.json.JObject;
import lombok.val;
import net.minecraft.core.Registry;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.CropBlock;
import org.jetbrains.annotations.Nullable;

public class ItemAttribute {

    private final ItemStack item;

    public ItemAttribute(ItemStack item) {
        this.item = item;
    }

    private String determineToolType() {
        Item itemRepr = item.getItem();
        if (itemRepr instanceof SwordItem) {
            return "sword";
        } else if (itemRepr instanceof PickaxeItem) {
            return "pickaxe";
        } else if (itemRepr instanceof ShovelItem) {
            return "shovel";
        } else if (itemRepr instanceof AxeItem) {
            return "axe";
        } else if (itemRepr instanceof HoeItem) {
            return "hoe";
        } else if (itemRepr instanceof ShearsItem) {
            return "shears";
        } else if (itemRepr instanceof TridentItem) {
            return "trident";
        } else if (itemRepr instanceof BowItem) {
            return "bow";
        } else if (itemRepr instanceof CrossbowItem) {
            return "crossbow";
        } else if (itemRepr instanceof ShieldItem) {
            return "shield";
        } else if (itemRepr instanceof ArmorItem) {
            return "armor";
        }
        return null;
    }

    @Nullable
    public JsonObject serialize() {
        val prop = JObject.of();
        //basic
        Item itemRepr = item.getItem();
        try {
            prop.add("id", Registry.ITEM.getKey(itemRepr).toString())
                .add("localized", item.getHoverName().getString())
                .add("maxDamage", item.getMaxDamage())
                .add("maxStackSize", item.getMaxStackSize());
        } catch (Throwable ignored) {
            return null;
        }
        //tool
        String toolType = determineToolType();
        if (toolType != null) {
            prop.add("toolType", toolType);
        }
        //food
        FoodProperties food = itemRepr.getFoodProperties();
        if (food != null) {
            prop.add("food", JObject.of()
                .add("nutrition", food.getNutrition())
                .add("saturation", food.getSaturationModifier())
                .add("alwaysEdible", food.canAlwaysEat()));
        }
        //block
        if (itemRepr instanceof BlockItem) {
            BlockItem blockItem = (BlockItem) itemRepr;
            prop.add("block", JObject.of().add("crop", blockItem.getBlock() instanceof CropBlock));
        }
        return prop.build();
    }
}
