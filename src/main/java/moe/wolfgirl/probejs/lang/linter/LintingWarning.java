package moe.wolfgirl.probejs.lang.linter;

import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import moe.wolfgirl.probejs.utils.PText;
import net.minecraft.network.chat.Component;

import java.nio.file.Path;

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

        return PText.literal("[")
//            .append(PText.literal(level().name()).kjs$color(level().color))
                .append(PText.literal(level().name()))
                .append(PText.literal("] "))
                .append(PText.literal(stripped.toString()))
                .append(PText.literal(":%d:%d: %s".formatted(line, column, message)));
    }
}
