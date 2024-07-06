package moe.wolfgirl.probejs.lang.decompiler;

import dev.latvian.mods.rhino.mod.remapper.RhizoRemapper;
import lombok.val;
import org.jetbrains.java.decompiler.main.extern.IIdentifierRenamer;

/**
 * @author ZZZank
 */
public class ProbeRemapper implements IIdentifierRenamer {
    @Override
    public boolean toBeRenamed(Type elementType, String className, String element, String descriptor) {
        val clazz = RhizoRemapper.instance().getClazzMappingView().get(className);
        return switch (elementType) {
            case ELEMENT_CLASS -> clazz != null;
            case ELEMENT_FIELD -> clazz.fields().containsKey(element);
            case ELEMENT_METHOD -> {
                if (descriptor.startsWith("()")) {
                    yield clazz.noArgMethods().containsKey(element);
                }
                yield clazz.nArgMethods().containsKey(element);
            }
        };
    }

    @Override
    public String getNextClassName(String fullName, String shortName) {
        val parts = RhizoRemapper
            .instance()
            .getClazzMappingView()
            .get(fullName.replace('/', '.'))
            .remapped()
            .split("\\.");
        return parts[parts.length - 1];
    }

    @Override
    public String getNextFieldName(String className, String field, String descriptor) {
        return RhizoRemapper
            .instance()
            .getClazzMappingView()
            .get(className.replace('/', '.'))
            .fields()
            .get(field)
            .remapped();
    }

    @Override
    public String getNextMethodName(String className, String method, String descriptor) {
        val clazz = RhizoRemapper
            .instance()
            .getClazzMappingView()
            .get(className.replace('/', '.'));
        if (descriptor.startsWith("()")) {
            return clazz.noArgMethods().get(method).remapped();
        }
        return clazz.nArgMethods().get(method).get(0).remapped();
    }
}
