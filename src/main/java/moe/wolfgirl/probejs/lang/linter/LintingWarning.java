package moe.wolfgirl.probejs.lang.linter;

import com.github.bsideup.jabel.Desugar;
import com.google.gson.JsonElement;
import dev.latvian.kubejs.bindings.TextWrapper;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import moe.wolfgirl.probejs.ProbeJS;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;

@Desugar
public record LintingWarning(Path file, Level level, int line, int column, String message) {
    public enum Level {
        INFO(ColorWrapper.BLUE),
        WARNING(ColorWrapper.GOLD),
        ERROR(ColorWrapper.RED);

        public final Color color;

        Level(Color color) {
            this.color = color;
        }
    }

    public Component defaultFormatting(Path relativeBase) {
        Path stripped = relativeBase.getParent().relativize(file);

        return TextWrapper.string("[")
            .append(TextWrapper.string(level().name()).color(level().color))
            .append(TextWrapper.string("] "))
            .append(TextWrapper.string(stripped.toString()))
            .append(TextWrapper.string(String.format(":%d:%d: %s", line, column, message)))
            .component();
    }

    public JsonElement asPayload() {
        return ProbeJS.GSON.toJsonTree(this);
    }
}
