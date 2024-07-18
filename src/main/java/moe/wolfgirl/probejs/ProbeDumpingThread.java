package moe.wolfgirl.probejs;

import lombok.val;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * @author ZZZank
 */
public class ProbeDumpingThread extends Thread {

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
                    e.printStackTrace();
                }
            },
            name
        );
    }
}
