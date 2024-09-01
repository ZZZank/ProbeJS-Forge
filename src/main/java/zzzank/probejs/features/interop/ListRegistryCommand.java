package zzzank.probejs.features.interop;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.val;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.StaticTags;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import zzzank.probejs.features.bridge.Command;
import zzzank.probejs.utils.registry.RegistryInfos;

import java.util.stream.Stream;

public abstract class ListRegistryCommand extends Command {

    protected abstract Stream<ResourceLocation> getItems(Registry<?> registry);

    @Override
    public JsonElement handle(JsonObject payload) {
        String registryKey = payload.get("registry").getAsString();
        MinecraftServer currentServer = ServerLifecycleHooks.getCurrentServer();
        if (currentServer == null) {
            return new JsonArray();
        }

        for (val info : RegistryInfos.infos.values()) {
            val key = info.resKey;
            val registryName = key.location().getNamespace().equals("minecraft") ?
                key.location().getPath() :
                key.location().toString();
            if (!registryKey.equals(registryName)) {
                continue;
            }
            if (info.raw == null) {
                break;
            }

            val result = new JsonArray();
            getItems(info.raw)
                .map(ResourceLocation::toString)
                .map(JsonPrimitive::new)
                .forEach(result::add);
            return result;
        }
        return new JsonArray();
    }

    public static class Objects extends ListRegistryCommand {

        @Override
        public String identifier() {
            return "list_registry_items";
        }

        @Override
        protected Stream<ResourceLocation> getItems(Registry<?> registry) {
            return registry.keySet().stream();
        }
    }

    public static class Tags extends ListRegistryCommand {

        @Override
        public String identifier() {
            return "list_registry_tags";
        }

        @Override
        protected Stream<ResourceLocation> getItems(Registry<?> registry) {
            val tagHelper = StaticTags.get(registry.key().location());
            return tagHelper == null ? Stream.of() : tagHelper.getAllTags().getAvailableTags().stream();
        }
    }
}
