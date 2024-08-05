package zzzank.probejs.mixins;

import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author ZZZank
 */
@Mixin(LanguageManager.class)
public interface AccessLanguageManager {

    @Accessor("DEFAULT_LANGUAGE")
    @Contract(" -> _")
    static LanguageInfo getDefault() {
        throw new AssertionError();
    }
}
