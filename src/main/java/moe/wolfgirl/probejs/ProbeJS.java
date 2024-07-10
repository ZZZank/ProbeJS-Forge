package moe.wolfgirl.probejs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.architectury.platform.forge.EventBuses;
import moe.wolfgirl.probejs.utils.JsonUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

@Mod(ProbeJS.MOD_ID)
public class ProbeJS {
    public static final String MOD_ID = "probejs";
    public static final Logger LOGGER = LogManager.getLogger("probejs");
    public static final Gson GSON = new GsonBuilder()
        .serializeSpecialFloatingPointValues()
        .setLenient()
        .disableHtmlEscaping()
        .registerTypeHierarchyAdapter(Path.class, new JsonUtils.PathConverter())
        .create();
    public static final Gson GSON_WRITER = new GsonBuilder()
        .setPrettyPrinting()
        .disableHtmlEscaping()
        .create();

    public ProbeJS() {
        EventBuses.registerModEventBus(ProbeJS.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
    }
}
