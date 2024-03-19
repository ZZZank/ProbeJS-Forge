package com.probejs.info;

import java.util.Set;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RegistryInfo {

    public final Registry<?> raw;
    public final ForgeRegistry<? extends IForgeRegistryEntry<?>> forgeRaw;
    public final ResourceKey<? extends Registry<?>> resKey;
    public final ResourceLocation parentId;
    public final ResourceLocation id;
    public final Set<ResourceLocation> names;

    public RegistryInfo(ForgeRegistry<? extends IForgeRegistryEntry<?>> forgeRegistry) {
        this.raw = null;
        this.forgeRaw = forgeRegistry;
        this.resKey = forgeRaw.getRegistryKey();
        this.parentId = forgeRaw.getDefaultKey();
        this.id = forgeRaw.getRegistryName();
        this.names = forgeRaw.getKeys();
    }

    public RegistryInfo(Registry<?> registry) {
        this.raw = registry;
        this.forgeRaw = null;
        this.resKey = raw.key();
        this.parentId = resKey.getRegistryName();
        this.id = resKey.location();
        this.names = raw.keySet();
    }

    public Registry<?> raw() {
        return this.raw;
    }

    public ForgeRegistry<? extends IForgeRegistryEntry<?>> forgeRaw() {
        return this.forgeRaw;
    }

    public ResourceKey<? extends Registry<?>> resKey() {
        return this.resKey;
    }

    public ResourceLocation parentId() {
        return this.parentId;
    }

    public ResourceLocation id() {
        return this.id;
    }

    public Set<ResourceLocation> names() {
        return this.names;
    }
}