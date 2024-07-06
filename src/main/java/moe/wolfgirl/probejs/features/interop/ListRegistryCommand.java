package moe.wolfgirl.probejs.features.interop;

import com.google.gson.*;
import dev.latvian.kubejs.util.UtilsJS;
import lombok.val;
import moe.wolfgirl.probejs.features.bridge.Command;
import moe.wolfgirl.probejs.utils.RegistryUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.StaticTags;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

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
        RegistryAccess access = currentServer.registryAccess();

        for (ResourceKey<? extends Registry<?>> key : RegistryUtils.getRegistries(access)) {
            String registryName = key.location().getNamespace().equals("minecraft") ?
                key.location().getPath() :
                key.location().toString();
            if (!registryKey.equals(registryName)) {
                continue;
            }

            Registry<?> registry = access.registry(UtilsJS.cast(key)).orElse(null);
            if (registry == null) {
                break;
            }

            var result = new JsonArray();
            getItems(registry)
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
