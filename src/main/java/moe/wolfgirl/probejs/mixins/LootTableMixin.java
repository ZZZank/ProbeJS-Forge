package moe.wolfgirl.probejs.mixins;

import com.google.gson.JsonElement;
import lombok.val;
import moe.wolfgirl.probejs.GlobalStates;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.LootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LootTables.class)
public class LootTableMixin {
    @Inject(method = "apply*", at = @At("RETURN"))
    public void apply(Map<ResourceLocation, JsonElement> object,
        ResourceManager resourceManager,
        ProfilerFiller profiler,
        CallbackInfo ci) {
        for (val resourceLocation : object.keySet()) {
            GlobalStates.LOOT_TABLES.add(resourceLocation.toString());
        }
    }
}
