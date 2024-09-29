package zzzank.probejs.features.forge_scan;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import lombok.val;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.forgespi.language.ModFileScanData;
import zzzank.probejs.ProbeJS;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
public interface ClassDataScanner {
    /**
     * will only be used by {@link ClassDataScanner#SUBCLASSES}
     */
    ImmutableList<String> PREDEFINED_BASECLASS = ImmutableList.of(
        Event.class.getName()
    );
    ClassDataScanner NONE = dataStream -> Collections.emptySet();
    ClassDataScanner FULL_SCAN = (dataStream) -> {
        val dataAll = dataStream.collect(Collectors.toList());
        val names = new ArrayList<String>(dataAll.size());
        for (val data : dataAll) {
            val access = new AccessClassData(data);
            names.add(access.pjs$clazz().getClassName());
        }
        ProbeJS.LOGGER.debug("FullScan collected {} class names", names.size());
        return names;
    };
    ClassDataScanner SUBCLASSES = (dataStream) -> {
        val names = new HashSet<>(PREDEFINED_BASECLASS);
        val dataNames = dataStream
            .map(AccessClassData::new)
            .map(access -> access.pjs$parent() == null
                ? new Pair<String, String>(null, access.pjs$clazz().getClassName())
                : new Pair<>(access.pjs$parent().getClassName(), access.pjs$clazz().getClassName())
            )
            .toArray((size) -> (Pair<String, String>[]) new Pair[size]);
        while (true) {
            val oldSize = names.size();
            for (val data : dataNames) {
                if (names.contains(data.getFirst())) {
                    names.add(data.getSecond());
                }
            }
            if (oldSize == names.size()) {
                break;
            }
        }
        ProbeJS.LOGGER.debug("ForgeEventSubclassOnly collected {} class names", names.size());
        return names;
    };

    /**
     * stream of class data -> class name
     */
    Collection<String> scan(Stream<ModFileScanData.ClassData> dataAll);
}
