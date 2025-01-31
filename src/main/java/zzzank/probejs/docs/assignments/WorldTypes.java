package zzzank.probejs.docs.assignments;

import dev.latvian.kubejs.block.BlockStatePredicate;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.stats.Stat;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import zzzank.probejs.docs.Primitives;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;

public class WorldTypes implements ProbeJSPlugin {
    @Override
    public void assignType(ScriptDump scriptDump) {
        scriptDump.assignType(BlockStatePredicate.class, Types.type(BlockStatePredicate.class).asArray());
        scriptDump.assignType(
            BlockStatePredicate.class,
            "BlockStatePredicateObject",
            Types.object()
                .member("or", true, Types.type(BlockStatePredicate.class))
                .member("not", true, Types.type(BlockStatePredicate.class))
                .build()
        );
        scriptDump.assignType(BlockStatePredicate.class, Types.type(Block.class));
        scriptDump.assignType(BlockStatePredicate.class, Types.type(BlockState.class));
        scriptDump.assignType(BlockStatePredicate.class, Types.primitive("Special.BlockTag"));
        scriptDump.assignType(BlockStatePredicate.class, Types.primitive("RegExp"));
        scriptDump.assignType(BlockStatePredicate.class, Types.literal("*"));
        scriptDump.assignType(BlockStatePredicate.class, Types.literal("-"));
        scriptDump.assignType(
            RuleTest.class,
            Types.type(BlockStatePredicate.class).contextShield(BaseType.FormatType.RETURN)
        );
        scriptDump.assignType(RuleTest.class, Types.type(CompoundTag.class));
        scriptDump.assignType(MobCategory.class, Types.STRING);
        scriptDump.assignType(LootContext.EntityTarget.class, Types.STRING);
        scriptDump.assignType(CopyNameFunction.NameSource.class, Types.STRING);
//        scriptDump.assignType(BiomeFilter.class, Types.primitive("Special.Biome"));
//        scriptDump.assignType(BiomeFilter.class, Types.primitive("RegExp"));
//        scriptDump.assignType(BiomeFilter.class, Types.type(BiomeFilter.class).asArray());
//        scriptDump.assignType(BiomeFilter.class, "BiomeFilterObject", Types.object()
//            .member("or", true, Types.type(BiomeFilter.class))
//            .member("not", true, Types.type(BiomeFilter.class))
//            .member("id", true, Types.primitive("Special.Biome"))
//            .member("type", true, Types.primitive("Special.Biome"))
//            .member("tag", Types.primitive("Special.BiomeTag"))
//            .build());
        scriptDump.assignType(Tier.class, Types.STRING);
        scriptDump.assignType(ArmorMaterial.class, Types.STRING);
//        scriptDump.assignType(PlayerSelector.class, Types.STRING);
        scriptDump.assignType(EntitySelector.class, Types.STRING);
//        scriptDump.assignType(ReplacementMatch.class, Types.type(Ingredient.class));
        scriptDump.assignType(Stat.class, Types.STRING);
//        scriptDump.assignType(MapColorHelper.class, Types.STRING);
//        scriptDump.assignType(MapColorHelper.class, Types.NUMBER);
        scriptDump.assignType(SoundType.class, Types.STRING);
        scriptDump.assignType(ParticleOptions.class, Types.STRING);
//        scriptDump.assignType(ItemTintFunction.class, Types.type(ItemTintFunction.class).asArray());
//        scriptDump.assignType(ItemTintFunction.class, Types.STRING);
//        scriptDump.assignType(ItemTintFunction.class, Types.lambda()
//            .param("stack", Types.type(ItemStack.class))
//            .param("index", Primitives.INTEGER)
//            .build());
//        scriptDump.assignType(BlockTintFunction.class, Types.type(BlockTintFunction.class).asArray());
//        scriptDump.assignType(BlockTintFunction.class, Types.STRING);
//        scriptDump.assignType(BlockTintFunction.class, Types.lambda()
//            .param("state", Types.type(BlockState.class))
//            .param("level", Types.type(BlockAndTintGetter.class))
//            .param("pos", Types.type(BlockPos.class))
//            .param("index", Primitives.INTEGER)
//            .returnType(Types.type(Color.class))
//            .build());

        BaseType[] predefinedColors = ColorWrapper.MAP.keySet()
            .stream()
            .map(String::toLowerCase)
            .distinct()
            .map(Types::literal)
            .toArray(BaseType[]::new);
        scriptDump.assignType(Color.class, Types.or(predefinedColors));
        scriptDump.assignType(Color.class, Types.primitive("`#${string}`"));
        scriptDump.assignType(Color.class, Primitives.INTEGER);

        scriptDump.assignType(TextColor.class, Types.or(predefinedColors));
        scriptDump.assignType(TextColor.class, Types.primitive("`#${string}`"));
        scriptDump.assignType(TextColor.class, Primitives.INTEGER);

        BaseType[] actions = new BaseType[]{
            Types.literal("open_url"),
            Types.literal("open_file"),
            Types.literal("run_command"),
            Types.literal("suggest_command"),
            Types.literal("change_page"),
            Types.literal("copy_to_clipboard"),
        };
        scriptDump.assignType(ClickEvent.class, Types.object()
            .member("action", Types.or(actions))
            .member("value", Types.STRING)
            .build());
        scriptDump.assignType(ClickEvent.class, Types.STRING);

//        scriptDump.assignType(DataComponentMap.class, Types.STRING);
//        scriptDump.assignType(ItemEnchantments.class, Types.primitive("{[key in Special.Enchantment]?: integer}"));
    }
}
