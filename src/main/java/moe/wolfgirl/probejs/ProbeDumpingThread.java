package moe.wolfgirl.probejs;

import lombok.val;
import moe.wolfgirl.probejs.utils.InMemoryLib;
import net.minecraft.network.chat.Component;

import java.net.URLClassLoader;
import java.util.function.Consumer;

/**
 * @author ZZZank
 */
public class ProbeDumpingThread extends Thread implements AutoCloseable {

    private InMemoryLib lib;

    ProbeDumpingThread(final Consumer<Component> messageSender) {
        this(messageSender, "probe dumping thread");
    }

    ProbeDumpingThread(final Consumer<Component> messageSender, String name) {
        super(
            () -> {  // Don't stall the client
                val dump = new ProbeDump();
                dump.defaultScripts();
                try {
                    dump.trigger(messageSender);
                } catch (Throwable e) {
                    ProbeJS.LOGGER.error(e.getMessage());
                    for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                        ProbeJS.LOGGER.error(stackTraceElement.toString());
                    }
                    throw new RuntimeException(e);
                }
            },
            name
        );
        try {
            lib = new InMemoryLib();
            val urls = lib.getURLs();
            this.setContextClassLoader(new URLClassLoader(
                urls,
                this.getContextClassLoader()
            ));
        } catch (Exception e) {
            ProbeJS.LOGGER.error("Error when loading Non Mod Library", e);
        }
    }

    @Override
    public void close() throws Exception {
        lib.close();
    }
}
