package zzzank.probejs.features.rhizo;

import net.minecraftforge.fml.ModList;
import zzzank.probejs.utils.Lazy;

import static zzzank.probejs.utils.ReflectUtils.classExist;

/**
 * @author ZZZank
 */
public interface RhizoState {

    Lazy<Boolean> MOD = Lazy.of(() -> ModList.get().isLoaded("rhizo"));
    boolean REMAPPER = classExist("dev.latvian.mods.rhino.util.remapper.RemapperManager");
    boolean GENERIC_ANNOTATION = classExist("dev.latvian.mods.rhino.annotations.typing.Generics");
    boolean INFO_ANNOTATION = classExist("dev.latvian.mods.rhino.annotations.typing.JSInfo");
}
