package moe.wolfgirl.probejs.features.interop;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.kubejs.KubeJS;
import dev.latvian.kubejs.server.ServerScriptManager;
import lombok.val;
import moe.wolfgirl.probejs.features.bridge.Command;

public class EvaluateCommand extends Command {
    @Override
    public String identifier() {
        return "evaluate";
    }

    @Override
    public JsonElement handle(JsonObject payload) {
        val scriptType = payload.get("scriptType").getAsString();
        val content = payload.get("content").getAsString();

        val scriptManager = switch (scriptType) {
            case "startup_scripts" -> KubeJS.startupScriptManager;
            case "client_scripts" -> KubeJS.clientScriptManager;
            case "server_scripts" -> ServerScriptManager.instance.scriptManager;
            case null, default -> null;
        };

        if (scriptManager == null) {
            throw new RuntimeException("Unable to get script manager.");
        }
        throw new RuntimeException("Unsupported operation");
//        Context context = scriptManager.contextFactory.enter();
//        Object result = context.evaluateString(context.topLevelScope, content, "probejsEvaluator", 1, null);
//        if (result instanceof NativeJavaObject nativeJavaObject) {
//            result = nativeJavaObject.unwrap();
//        }
//        JsonElement jsonElement = JsonUtils.parseObject(result);
//        if (jsonElement == JsonNull.INSTANCE && result != null) {
//            jsonElement = new JsonPrimitive(result.toString());
//        }
//        return jsonElement;
    }
}
