package com.probejs.integration;

import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.ModList;

import static com.probejs.util.PUtil.classExist;

/**
 * @author ZZZank
 */
public interface RhizoState {

    Lazy<Boolean> MOD = Lazy.of(() -> ModList.get().isLoaded("rhizo"));
    boolean ENUM_TYPE_WRAPPER = classExist("dev.latvian.mods.rhino.util.wrap.EnumTypeWrapper");
    boolean REMAPPER = classExist("dev.latvian.mods.rhino.util.remapper.RemapperManager");
    boolean GENERIC_ANNOTATION = classExist("dev.latvian.mods.rhino.annotations.typing.Generics");
    boolean INFO_ANNOTATION = classExist("dev.latvian.mods.rhino.annotations.typing.JSInfo");
}