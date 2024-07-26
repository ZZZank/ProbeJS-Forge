package zzzank.probejs.docs.assignments;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.kubejs.item.ItemStackJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.kubejs.text.Text;
import dev.latvian.mods.rhino.util.unit.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import zzzank.probejs.docs.Primitives;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSTupleType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.*;

public class JavaPrimitives extends ProbeJSPlugin {

    private static JSTupleType xyzOf(BaseType baseType) {
        return Types.tuple()
            .member("x", baseType)
            .member("y", baseType)
            .member("z", baseType)
            .build();
    }

    private static JSTupleType minMaxOf(BaseType baseType) {
        return Types.tuple()
            .member("min", baseType)
            .member("max", baseType)
            .build();
    }

    @Override
    public void assignType(ScriptDump scriptDump) {
        scriptDump.assignType(List.class, Types.generic("E").asArray());
        scriptDump.assignType(Map.class, Types.primitive("{[key: string]: V}"));
        scriptDump.assignType(Iterable.class, Types.generic("T").asArray());
        scriptDump.assignType(Collection.class, Types.generic("E").asArray());
        scriptDump.assignType(Set.class, Types.generic("E").asArray());
        scriptDump.assignType(UUID.class, Types.STRING);
        scriptDump.assignType(JsonObject.class, Types.OBJECT);
        scriptDump.assignType(JsonArray.class, Types.ANY.asArray());
        scriptDump.assignType(JsonPrimitive.class, Types.NUMBER);
        scriptDump.assignType(JsonPrimitive.class, Types.STRING);
        scriptDump.assignType(JsonPrimitive.class, Types.BOOLEAN);
        scriptDump.assignType(JsonPrimitive.class, Types.NULL);
        scriptDump.assignType(JsonElement.class, Types.type(JsonObject.class));
        scriptDump.assignType(JsonElement.class, Types.type(JsonArray.class));
        scriptDump.assignType(JsonElement.class, Types.type(JsonPrimitive.class));
        scriptDump.assignType(Path.class, Types.STRING);
        scriptDump.assignType(File.class, Types.type(Path.class));
        scriptDump.assignType(Unit.class, Types.STRING);
        scriptDump.assignType(Unit.class, Types.NUMBER);
        scriptDump.assignType(TemporalAmount.class, Types.STRING);
        scriptDump.assignType(TemporalAmount.class, Types.NUMBER);
        scriptDump.assignType(Duration.class, Types.type(TemporalAmount.class));
        scriptDump.assignType(ResourceLocation.class, Types.STRING);
        scriptDump.assignType(CompoundTag.class, Types.OBJECT);
        scriptDump.assignType(CollectionTag.class, Types.ANY.asArray());
        scriptDump.assignType(ListTag.class, Types.ANY.asArray());
        scriptDump.assignType(Tag.class, Types.STRING);
        scriptDump.assignType(Tag.class, Types.NUMBER);
        scriptDump.assignType(Tag.class, Types.BOOLEAN);
        scriptDump.assignType(Tag.class, Types.OBJECT);
        scriptDump.assignType(Tag.class, Types.ANY.asArray());
        scriptDump.assignType(BlockPos.class, xyzOf(Primitives.INTEGER));
        scriptDump.assignType(Vec3.class, xyzOf(Primitives.DOUBLE));
        scriptDump.assignType(Vec3i.class, xyzOf(Primitives.INTEGER));
        scriptDump.assignType(MobCategory.class, Types.STRING);
        scriptDump.assignType(AABB.class, Types.EMPTY_ARRAY);
        scriptDump.assignType(AABB.class, xyzOf(Primitives.DOUBLE));
        scriptDump.assignType(AABB.class, Types.tuple()
            .member("x1", Primitives.DOUBLE)
            .member("y1", Primitives.DOUBLE)
            .member("z1", Primitives.DOUBLE)
            .member("x2", Primitives.DOUBLE)
            .member("y2", Primitives.DOUBLE)
            .member("z2", Primitives.DOUBLE)
            .build());
        //kube wrapper -> raw
        scriptDump.assignType(Component.class, Types.type(Text.class).contextShield(BaseType.FormatType.INPUT));
        scriptDump.assignType(Ingredient.class, Types.type(IngredientJS.class));
        scriptDump.assignType(ItemStack.class, Types.type(ItemStackJS.class));
    }
}
