package zzzank.probejs.utils;

import lombok.val;
import me.shedaniel.architectury.platform.Mod;
import me.shedaniel.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.features.kubejs.SpecialData;
import zzzank.probejs.utils.registry.RegistryInfo;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class GameUtils {
    public static long modHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (Mod mod : Platform.getMods()) {
                digest.update((mod.getModId() + mod.getVersion()).getBytes());
            }
            return ByteBuffer.wrap(digest.digest()).getLong();
        } catch (NoSuchAlgorithmException e) {
            return -1;
        }
    }

    public static long registryHash() {
        try {
            val digest = MessageDigest.getInstance("SHA-256");
            val server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                return -1;
            }

            SpecialData.instance().registries()
                .stream()
                .map(RegistryInfo::names)
                .flatMap(Set::stream)
                .map(ResourceLocation::toString)
                .sorted()
                .forEach(s -> digest.update(s.getBytes()));

            ByteBuffer buffer = ByteBuffer.wrap(digest.digest());
            return buffer.getLong();
        } catch (NoSuchAlgorithmException e) {
            return -1;
        }
    }

    public static void logThrowable(Throwable t) {
        val trace = t.getStackTrace();
        val lines = new ArrayList<>(1 + trace.length);
        lines.add(t);
        lines.addAll(Arrays.asList(trace));
        ProbeJS.LOGGER.error(lines.stream().map(Object::toString).collect(Collectors.joining("\n")));
    }
}
