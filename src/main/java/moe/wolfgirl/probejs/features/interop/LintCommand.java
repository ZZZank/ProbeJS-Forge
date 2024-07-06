package moe.wolfgirl.probejs.features.interop;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import moe.wolfgirl.probejs.ProbeJS;
import moe.wolfgirl.probejs.features.bridge.Command;
import moe.wolfgirl.probejs.lang.linter.Linter;
import moe.wolfgirl.probejs.lang.linter.LintingWarning;

import java.io.IOException;

public class LintCommand extends Command {
    @Override
    public String identifier() {
        return "lint";
    }

    @Override
    public JsonElement handle(JsonObject payload) {
        String scriptType = payload.get("script_type").getAsString();
        Linter linter;
        if (scriptType.equals("client")) {
            linter = Linter.CLIENT_SCRIPT.get();
        } else if (scriptType.equals("server")) {
            linter = Linter.SERVER_SCRIPT.get();
        } else if (scriptType.equals("startup")) {
            linter = Linter.STARTUP_SCRIPT.get();
        } else {
            throw new RuntimeException(String.format("Unknown script type %s",scriptType));
        }

        try {
            var warnings = linter.lint();
            var result = new JsonArray();
            for (LintingWarning warning : warnings) {
                result.add(ProbeJS.GSON.toJsonTree(warning));
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
