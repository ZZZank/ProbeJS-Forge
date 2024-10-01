package zzzank.probejs.mixins;

import dev.latvian.kubejs.recipe.RecipeEventJS;
import dev.latvian.kubejs.recipe.RecipeTypeJS;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzzank.probejs.features.kubejs.RecipeTypesHolder;

import java.util.Map;

/**
 * @author ZZZank
 */
@Mixin(value = RecipeEventJS.class, remap = false)
public abstract class MixinRecipeEventJS implements RecipeTypesHolder {

    @Unique
    private Map<ResourceLocation, RecipeTypeJS> pjs$capturedTypes;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void pjs$captureRecipeTypes(Map<ResourceLocation, RecipeTypeJS> t, CallbackInfo ci) {
        pjs$capturedTypes = t;
    }

    @Override
    public Map<ResourceLocation, RecipeTypeJS> pjs$recipeTypes() {
        return pjs$capturedTypes;
    }
}
