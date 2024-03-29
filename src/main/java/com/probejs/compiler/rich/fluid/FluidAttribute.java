package com.probejs.compiler.rich.fluid;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.fluid.FluidStackJS;
import java.util.Objects;
import me.shedaniel.architectury.fluid.FluidStack;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;

public class FluidAttribute {

    private final Fluid fluid;
    private final FluidStack fluidStack;

    public FluidAttribute(Fluid fluid) {
        this.fluid = fluid;
        this.fluidStack = FluidStack.create(fluid, FluidStack.bucketAmount());
    }

    public JsonObject serialize() {
        JsonObject serialized = new JsonObject();
        //id
        serialized.addProperty("id", Objects.requireNonNull(Registry.FLUID.getKey(fluid)).toString());
        //name
        String fluidName = "Unknown Fluid";
        try {
            fluidName = fluidStack.getName().getString();
        } catch (Exception e) {
            try {
                fluidName = FluidStackJS.of(fluidStack).getId();
            } catch (Exception ignored) {}
        }
        serialized.addProperty("localized", fluidName);
        //bucket or not
        serialized.addProperty("hasBucket", fluid.getBucket() != Items.AIR);
        serialized.addProperty(
            "bucketItem",
            Objects.requireNonNull(Registry.ITEM.getKey(fluid.getBucket())).toString()
        );
        //block or not
        serialized.addProperty(
            "hasBlock",
            fluid.defaultFluidState().createLegacyBlock().getBlock() != Blocks.AIR
        );
        return serialized;
    }
}
