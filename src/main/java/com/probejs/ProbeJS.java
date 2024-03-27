package com.probejs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.probejs.plugin.CapturedClasses;
import me.shedaniel.architectury.event.events.CommandRegistrationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Prunoideae
 */
@Mod(ProbeJS.MOD_ID)
public class ProbeJS {

    public static final String MOD_ID = "probejs";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static final Gson GSON = new GsonBuilder()
        .serializeSpecialFloatingPointValues()
        .disableHtmlEscaping()
        .create();
    public static final ProbeConfig CONFIG = ProbeConfig.getInstance();
    public static final boolean ENABLED = !CONFIG.disabled;

    public ProbeJS() {
        CommandRegistrationEvent.EVENT.register(ProbeCommands::register);

        if (ENABLED) {
            ProbeJS.LOGGER.info("Listening to EVERY forge event. ");
            MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                true,
                Event.class,
                event -> {
                    CapturedClasses.capturedRawEvents.put(event.getClass().getName(), event.getClass());
                }
            );
        }
    }
}
