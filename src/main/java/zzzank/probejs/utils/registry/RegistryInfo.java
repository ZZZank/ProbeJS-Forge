package zzzank.probejs.utils.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.StaticTagHelper;
import net.minecraft.tags.StaticTags;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class RegistryInfo implements Comparable<RegistryInfo> {

    public final Registry<?> raw;
    public final ForgeRegistry<? extends IForgeRegistryEntry<?>> forgeRaw;
    public final ResourceKey<? extends Registry<?>> resKey;
    public final ResourceLocation parentId;
    public final ResourceLocation id;
    public final Set<ResourceLocation> names;
    @Nullable
    public final StaticTagHelper<?> tagHelper;

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

    @Override
    public int compareTo(@NotNull RegistryInfo o) {
        return resKey.compareTo(o.resKey);
    }
}