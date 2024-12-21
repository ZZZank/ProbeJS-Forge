package zzzank.probejs.lang.linter;

import com.github.bsideup.jabel.Desugar;
import com.google.gson.JsonElement;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import net.minecraft.network.chat.Component;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.utils.ProbeText;

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

        return ProbeText.literal("[")
            .append(ProbeText.literal(level().name()).color(level().color.createTextColorKJS()))
            .append(ProbeText.literal("] "))
            .append(ProbeText.literal(stripped.toString()))
            .append(ProbeText.literal(String.format(":%d:%d: %s", line, column, message)))
            .unwrap();
    }

    public JsonElement asPayload() {
        return ProbeJS.GSON.toJsonTree(this);
    }
}
