package moe.wolfgirl.probejs.features.interop;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import moe.wolfgirl.probejs.features.bridge.Command;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class ReloadCommand extends Command {
    @Override
    public String identifier() {
        return "reload";
    }

    @Override
    public JsonElement handle(JsonObject payload) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) throw new RuntimeException("No current server found.");

        switch (payload.get("scriptType").getAsString()) {
            case "server_scripts" -> runCommand(server, "kubejs reload server-scripts");
            case "startup_scripts" -> runCommand(server, "kubejs reload startup-scripts");
            case "client_scripts" -> runCommand(server, "kubejs reload client-scripts");
            case "reload" -> runCommand(server, "reload");
        }

        return JsonNull.INSTANCE;
    }

    public static void runCommand(MinecraftServer server, String command) {
        server.getCommands().performCommand(server.createCommandSourceStack(), command);
    }
}
