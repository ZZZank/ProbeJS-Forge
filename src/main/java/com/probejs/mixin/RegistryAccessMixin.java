package com.probejs.mixin;

import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryAccess.RegistryData;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RegistryAccess.class)
public interface RegistryAccessMixin {
    @Accessor("REGISTRIES")
    public Map<ResourceKey<? extends Registry<?>>, RegistryData<?>> GET_REGISTRIES();
}
