package com.probejs.compiler.rich.fluid;

import com.google.gson.JsonObject;
import com.probejs.util.json.JObject;
import com.probejs.util.json.JPrimitive;
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
        String fluidName = "Unknown Fluid";
        try {
            fluidName = fluidStack.getName().getString();
        } catch (Exception e) {
            try {
                fluidName = FluidStackJS.of(fluidStack).getId();
            } catch (Exception ignored) {}
        }
        return JObject
            .of()
            .add("id", Objects.requireNonNull(Registry.FLUID.getKey(fluid)).toString())
            .add("localized", fluidName)
            .add("hasBucket", JPrimitive.of(fluid.getBucket() != Items.AIR))
            .add("bucketItem", Objects.requireNonNull(Registry.ITEM.getKey(fluid.getBucket())).toString())
            .add("hasBlock", JPrimitive.of(fluid.defaultFluidState().createLegacyBlock().getBlock() != Blocks.AIR))
            .build();
    }
}
