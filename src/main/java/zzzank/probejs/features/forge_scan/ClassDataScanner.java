package zzzank.probejs.features.forge_scan;

import lombok.val;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;
import zzzank.probejs.mixins.AccessClassData;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
public interface ClassDataScanner {
    ClassDataScanner FULL_SCAN = (dataStream) -> dataStream
        .map(data -> (AccessClassData) data)
        .map(AccessClassData::pjs$clazz)
        .map(Type::getClassName)
        .collect(Collectors.toList());
    ClassDataScanner EVENT_SUBCLASS_ONLY = (dataStream) -> {
        val names = new HashSet<String>();
        names.add(Event.class.getName());
        val dataAll = dataStream.collect(Collectors.toList());
        while (true) {
            boolean changed = false;
            for (val data : dataAll) {
                val access = (AccessClassData) data;
                if (access.pjs$parent() != null
                    && names.contains(access.pjs$parent().getClassName())
                ) {
                    names.add(access.pjs$clazz().getClassName());
                    changed = true;
                }
            }
            if (!changed) {
                break;
            }
        }
        return names;
    };

    /**
     * stream of class data -> class name
     */
    Collection<String> scan(Stream<ModFileScanData.ClassData> dataAll);
}
