package zzzank.probejs.features.forge_scan;

import com.mojang.datafixers.util.Pair;
import lombok.val;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.forgespi.language.ModFileScanData;
import zzzank.probejs.ProbeJS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
public interface ClassDataScanner {
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
    ClassDataScanner EVENT_SUBCLASS_ONLY = (dataStream) -> {
        val names = new HashSet<String>();
        names.add(Event.class.getName());
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
