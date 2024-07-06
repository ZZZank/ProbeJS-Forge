package moe.wolfgirl.probejs.utils.registry;

import java.util.Set;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.StaticTagHelper;
import net.minecraft.tags.StaticTags;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

public class RegistryInfo {

    private final Registry<?> raw;
    private final ForgeRegistry<? extends IForgeRegistryEntry<?>> forgeRaw;
    private final ResourceKey<? extends Registry<?>> resKey;
    private final ResourceLocation parentId;
    private final ResourceLocation id;
    private final Set<ResourceLocation> names;
    @Nullable
    private final StaticTagHelper<?> tagHelper;

    public RegistryInfo(ForgeRegistry<? extends IForgeRegistryEntry<?>> forgeRegistry) {
        this.forgeRaw = forgeRegistry;
        this.resKey = forgeRaw.getRegistryKey();
        this.parentId = resKey.getRegistryName();
        this.id = resKey.location();
        this.names = forgeRaw.getKeys();
        this.tagHelper = StaticTags.get(this.id);

        this.raw = Registry.REGISTRY.get(id);
    }

    public RegistryInfo(Registry<?> registry) {
        this.raw = registry;
        this.forgeRaw = null;
        this.resKey = raw.key();
        this.parentId = resKey.getRegistryName();
        this.id = resKey.location();
        this.names = raw.keySet();
        this.tagHelper = StaticTags.get(this.id);
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

    public StaticTagHelper<?> tagHelper() {
        return tagHelper;
    }
}