package zzzank.probejs.features.kubejs;

import com.github.bsideup.jabel.Desugar;
import lombok.val;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryManager;
import zzzank.probejs.mixins.AccessForgeRegistryManager;
import zzzank.probejs.utils.registry.RegistryInfo;

import javax.annotation.Nonnull;
import java.util.*;

@Desugar
public record SpecialData(Map<ResourceLocation, Collection<ResourceLocation>> tags,
                          Collection<RegistryInfo> registries) {

    private static SpecialData INSTANCE = null;

    @Nonnull
    public static SpecialData instance() {
        return INSTANCE;
    }

    public static void refresh() {
        val rInfos = fetchRegistries();
        SpecialData.INSTANCE = new SpecialData(extractTagsFrom(rInfos.values()), rInfos.values());
    }

    private static Map<ResourceLocation, Collection<ResourceLocation>> extractTagsFrom(Collection<RegistryInfo> registries) {
        val tags = new HashMap<ResourceLocation, Collection<ResourceLocation>>();
        for (val rInfo : registries) {
            val tagHelper = rInfo.tagHelper();
            if (tagHelper == null) {
                continue;
            }
            val names = tagHelper.getAllTags().getAvailableTags();
            val id = rInfo.id();
            tags.put(id, names);
        }
        return tags;
    }

    private static Map<ResourceLocation, RegistryInfo> fetchRegistries() {
        val registries = new HashMap<ResourceLocation, RegistryInfo>();
        for (val entry : ((AccessForgeRegistryManager) RegistryManager.ACTIVE).getRegistries().entrySet()) {
            registries.put(entry.getKey(), new RegistryInfo(entry.getValue()));
        }
        return registries;
    }
}
