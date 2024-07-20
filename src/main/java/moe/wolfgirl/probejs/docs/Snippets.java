package moe.wolfgirl.probejs.docs;

import dev.latvian.kubejs.util.UtilsJS;
import moe.wolfgirl.probejs.GlobalStates;
import moe.wolfgirl.probejs.plugin.ProbeJSPlugin;
import moe.wolfgirl.probejs.lang.snippet.SnippetDump;
import moe.wolfgirl.probejs.lang.snippet.parts.Variable;
import net.minecraft.core.Registry;

import java.util.Arrays;

public class Snippets extends ProbeJSPlugin {
    @Override
    public void addVSCodeSnippets(SnippetDump dump) {
        dump.snippet("uuid")
            .prefix("#uuid")
            .description("Generates a random version 4 UUID.")
            .literal("\"")
            .variable(Variable.UUID)
            .literal("\"");

        dump.snippet("recipes")
            .prefix("#recipes")
            .literal("ServerEvents.recipes(event => {")
            .newline()
            .literal("    const { ")
            .tabStop(1)
            .literal(" } = event.recipes")
            .newline()
            .literal("    ")
            .tabStop(0)
            .newline()
            .literal("})");

        defineHeader(dump, "priority", "0");
        defineHeader(dump, "packmode", null);

        dump.snippet("ignored")
            .prefix("#ignored")
            .description("Creates the file header for `ignored`.")
            .literal("// ignored: ")
            .choices(Arrays.asList("true", "false"));

        dump.snippet("requires")
            .prefix("#requires")
            .description("Creates the file header for `requires`.")
            .literal("// requires: ")
            .choices(GlobalStates.MODS.get());

        dump.snippet("itemstack")
            .prefix("#itemstack")
            .description("Creates a `nx item string.")
            .literal("\"")
            .tabStop(1, "1")
            .literal("x ")
            .registry(UtilsJS.cast(Registry.ITEM.key()))
            .literal("\"");
    }

    private static void defineHeader(SnippetDump dump, String symbol, String defaultValue) {
        dump.snippet(symbol)
            .prefix("#" + symbol)
            .description(String.format("Creates the file header for `%s`.", symbol))
            .literal(String.format("// %s: ", symbol))
            .tabStop(0, defaultValue);
    }
}
