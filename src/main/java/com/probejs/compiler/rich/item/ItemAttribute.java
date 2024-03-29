package com.probejs.compiler.rich.item;

import com.google.gson.JsonObject;

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
        JsonObject prop;
        //basic
        Item itemRepr = item.getItem();
        try {
            prop = new JsonObject();
            prop.addProperty("id", Registry.ITEM.getKey(itemRepr).toString());
            prop.addProperty("localized", item.getHoverName().getString());
            prop.addProperty("maxDamage", item.getMaxDamage());
            prop.addProperty("maxStackSize", item.getMaxStackSize());
        } catch (Throwable ignored) {
            return null;
        }
        //tool
        String toolType = determineToolType();
        if (toolType != null) {
            prop.addProperty("toolType", toolType);
        }
        //food
        FoodProperties food = itemRepr.getFoodProperties();
        if (food != null) {
            JsonObject foodProp = new JsonObject();
            foodProp.addProperty("nutrition", food.getNutrition());
            foodProp.addProperty("saturation", food.getSaturationModifier());
            foodProp.addProperty("alwaysEdible", food.canAlwaysEat());
            prop.add("food", foodProp);
        }
        //block
        if (itemRepr instanceof BlockItem) {
            BlockItem blockItem = (BlockItem) itemRepr;
            JsonObject blockProp = new JsonObject();
            blockProp.addProperty("crop", blockItem.getBlock() instanceof CropBlock);
            prop.add("block", blockProp);
        }
        return prop;
    }
}
