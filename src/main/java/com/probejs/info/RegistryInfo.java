package com.probejs.info;

import java.util.Set;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class RegistryInfo {

    public final Registry<?> raw;
    public final ResourceKey<? extends Registry<?>> resKey;
    public final ResourceLocation parentId;
    public final ResourceLocation id;
    public final Set<ResourceLocation> names;

    public RegistryInfo(Registry<?> registry) {
        this.raw = registry;
        this.resKey = raw.key();
        this.parentId = resKey.getRegistryName();
        this.id = resKey.location();
        this.names = raw.keySet();
    }

    public Registry<?> raw() {
        return this.raw;
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