package moe.wolfgirl.probejs.mixins;

import dev.latvian.kubejs.script.ScriptType;
import dev.latvian.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.RhinoException;
import moe.wolfgirl.probejs.GlobalStates;
import moe.wolfgirl.probejs.lang.linter.LintingWarning;
import moe.wolfgirl.probejs.utils.FileUtils;
import moe.wolfgirl.probejs.utils.JsonUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.regex.Pattern;

@Mixin(value = ConsoleJS.class, remap = false)
public class ConsoleMixin {

    @Shadow
    @Final
    private ScriptType type;

    @Inject(
        method = "error(Ljava/lang/String;Ljava/lang/Throwable;Ljava/util/regex/Pattern;)V",
        remap = false,
        at = @At("HEAD")
    )
    public void reportError(String message, Throwable error, Pattern exitPattern, CallbackInfo ci) {
        if (GlobalStates.SERVER != null) {
            if (error instanceof RhinoException rhinoException) {
                Path path = FileUtils.parseSourcePath(rhinoException.sourceName());
                if (path == null) return;
                LintingWarning warning = new LintingWarning(path, LintingWarning.Level.ERROR,
                        rhinoException.lineNumber(), rhinoException.columnNumber(),
                        rhinoException.details()
                );
                GlobalStates.SERVER.broadcast("accept_error", warning.asPayload());
            } else {
                // No flooding
                if (System.currentTimeMillis() - GlobalStates.ERROR_TIMESTAMP > 2000) {
                    GlobalStates.ERROR_TIMESTAMP = System.currentTimeMillis();
                    GlobalStates.SERVER.broadcast("accept_error_no_line", JsonUtils.errorAsPayload(error));
                }
            }
        }
    }

//    @Inject(
//        method = "log(Ljava/util/function/Consumer;Ljava/lang/String;Ljava/lang/Object;)V",
////        method = "log(Ldev/latvian/mods/kubejs/util/LogType;Ljava/lang/Throwable;Ljava/lang/Object;)Ldev/latvian/mods/kubejs/script/ConsoleLine;",
//            remap = false,
//            at = @At("RETURN"))
//    public void reportWarning(Consumer<String> logFunction, String type, Object message, CallbackInfo ci) {
//        if (!(type == LogType.WARN || type == LogType.ERROR || type == LogType.INFO) || GlobalStates.SERVER == null) {
//            return;
//        }
//        if (error instanceof RhinoException) return;
//        String scriptType = switch (this.type) {
//            case STARTUP -> "startup_scripts";
//            case SERVER -> "server_scripts";
//            case CLIENT -> "client_scripts";
//        };
//
//        LintingWarning.Level level = switch (type) {
//            case WARN -> LintingWarning.Level.WARNING;
//            case ERROR -> LintingWarning.Level.ERROR;
//            default -> LintingWarning.Level.INFO;
//        };
//
//        var sourceLine = line.sourceLines.stream().findFirst().orElse(null);
//        if (sourceLine == null) {
//            return;
//        }
//        Path path = FileUtils.parseSourcePath("%s:%s".formatted(scriptType, sourceLine.source()));
//        if (path != null && path.toString().endsWith(".js")) {
//            GlobalStates.SERVER.broadcast("accept_error", (new LintingWarning(
//                    path, level,
//                    sourceLine.line(), 0,
//                    line.message
//            )).asPayload());
//        }
//    }
}
