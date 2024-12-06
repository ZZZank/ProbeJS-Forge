package zzzank.probejs.utils.registry;

import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryManager;
import zzzank.probejs.mixins.AccessForgeRegistryManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZZZank
 */
public final class RegistryInfos {
    /**
     * not using {@link net.minecraft.resources.ResourceKey} as key, because ResourceKey for registries
     * will always use {@link net.minecraft.core.Registry#ROOT_REGISTRY_NAME} as its parent
     */
    public static final Map<ResourceLocation, RegistryInfo> ALL = new HashMap<>();

    public static void refresh() {
        ALL.clear();
        for (val entry : ((AccessForgeRegistryManager) RegistryManager.FROZEN).getRegistries().entrySet()) {
            ALL.put(entry.getKey(), new RegistryInfo(entry.getValue()));
        }
        for (val entry : ((AccessForgeRegistryManager) RegistryManager.ACTIVE).getRegistries().entrySet()) {
            ALL.put(entry.getKey(), new RegistryInfo(entry.getValue()));
        }
    }

    private RegistryInfos() {}
}
