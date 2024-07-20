package zzzank.probejs.features.rhizo;

import static zzzank.probejs.utils.ReflectUtils.classExist;

/**
 * @author ZZZank
 */
public interface RhizoState {

    boolean REMAPPER = classExist("dev.latvian.mods.rhino.util.remapper.RemapperManager");
    boolean GENERIC_ANNOTATION = classExist("dev.latvian.mods.rhino.annotations.typing.Generics");
    boolean INFO_ANNOTATION = classExist("dev.latvian.mods.rhino.annotations.typing.JSInfo");
}
