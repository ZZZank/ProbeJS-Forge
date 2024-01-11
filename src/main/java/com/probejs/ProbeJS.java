package com.probejs;

import com.probejs.plugin.ForgeEventListener;
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
@Mod("probejs")
public class ProbeJS {

    public static final Logger LOGGER = LogManager.getLogger("probejs");

    public ProbeJS() {
        CommandRegistrationEvent.EVENT.register(
            ((dispatcher, selection) -> ProbeCommands.register(dispatcher))
        );
        if (!ProbeConfig.INSTANCE.disabled) {
            ProbeJS.LOGGER.info("Listening to EVERY forge event. ");
            MinecraftForge.EVENT_BUS.addListener(
                EventPriority.NORMAL,
                true,
                Event.class,
                ForgeEventListener::onEvent
            );
        }
    }
}
