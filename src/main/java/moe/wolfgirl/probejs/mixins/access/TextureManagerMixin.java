package moe.wolfgirl.probejs.mixins.access;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * @author ZZZank
 */
@Mixin(TextureManager.class)
public interface TextureManagerMixin {

    @Accessor("byPath")
    @Contract(" -> _")
    default Map<ResourceLocation, AbstractTexture> byPath() {
        throw new AssertionError();
    }
}
