package moe.wolfgirl.probejs.utils;

import com.probejs.info.RegistryInfo;
import com.probejs.info.SpecialData;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

import java.util.Collection;
import java.util.stream.Collectors;

public class RegistryUtils {

    public static Collection<ResourceKey<? extends Registry<?>>> getRegistries(RegistryAccess access) {
        return SpecialData.instance().registries().stream().map(RegistryInfo::resKey).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public static <T> ResourceKey<Registry<T>> castKey(ResourceKey<?> key) {
        return (ResourceKey<Registry<T>>) key;
    }
}
