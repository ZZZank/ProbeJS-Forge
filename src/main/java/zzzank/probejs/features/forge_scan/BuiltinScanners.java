package zzzank.probejs.features.forge_scan;

import com.mojang.datafixers.util.Pair;
import lombok.val;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.forgespi.language.ModFileScanData;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.CollectUtils;

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
public enum BuiltinScanners implements ClassDataScanner {
    NONE {
        @Override
        public Collection<String> scan(Stream<ModFileScanData.ClassData> dataStream) {
            return Collections.emptyList();
        }
    },
    FULL {
        @Override
        public Collection<String> scan(Stream<ModFileScanData.ClassData> dataStream) {
            val collected = dataStream
                .map(AccessClassData::new)
                .map(AccessClassData::className)
                .collect(Collectors.toList());
            ProbeJS.LOGGER.debug("FullScan collected {} class names", collected.size());
            return collected;
        }
    },
    EVENTS {
        @Override
        public Collection<String> scan(Stream<ModFileScanData.ClassData> dataStream) {
            val names = new HashSet<>(PREDEFINED_BASECLASS);
            final Pair<String, String>[] dataNames = dataStream
                .map(AccessClassData::new)
                .map(access -> new Pair<>(access.parentClassName(), access.className()))
                .toArray((IntFunction<Pair<String, String>[]>) Pair[]::new);
            boolean changed = true;
            while (changed) {
                changed = false;
                for (val data : dataNames) {
                    if (names.contains(data.getFirst())) {
                        changed |= names.add(data.getSecond());
                    }
                }
            }
            ProbeJS.LOGGER.debug("ForgeEventSubclassOnly collected {} class names", names.size());
            return names;
        }
    };

    /**
     * will only be used by {@link BuiltinScanners#EVENTS}
     */
    public static final List<String> PREDEFINED_BASECLASS = CollectUtils.ofList(
        Event.class.getName()
    );
}
