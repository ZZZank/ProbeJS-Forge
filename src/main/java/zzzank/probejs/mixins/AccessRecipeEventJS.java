package zzzank.probejs.mixins;

import com.google.gson.JsonObject;
import dev.latvian.kubejs.recipe.RecipeEventJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zzzank.probejs.docs.events.RecipeEvents;

import java.util.Map;

/**
 * @author ZZZank
 */
@Mixin(value = RecipeEventJS.class, remap = false)
public interface AccessRecipeEventJS {

    @Shadow
    Map<String, Object> getRecipes();

    @Inject(method = "post", at = @At("HEAD"))
    default void captureSelf(RecipeManager recipeManager, Map<ResourceLocation, JsonObject> jsonMap, CallbackInfo ci) {
        RecipeEvents.captureRecipes(this.getRecipes());
    }
}
