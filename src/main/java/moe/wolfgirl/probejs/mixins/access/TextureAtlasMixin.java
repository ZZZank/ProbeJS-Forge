package moe.wolfgirl.probejs.mixins.access;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * @author ZZZank
 */
@Mixin(TextureAtlas.class)
public interface TextureAtlasMixin {

    @Accessor("texturesByName")
    Map<ResourceLocation, TextureAtlasSprite> texturesByName();
}
