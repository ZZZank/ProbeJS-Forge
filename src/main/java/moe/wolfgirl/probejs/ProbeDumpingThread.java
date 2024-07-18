package moe.wolfgirl.probejs;

import lombok.val;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * @author ZZZank
 */
public class ProbeDumpingThread extends Thread {

    public static ProbeDumpingThread INSTANCE;

    public static boolean exists() {
        return INSTANCE != null && INSTANCE.isAlive();
    }

    static ProbeDumpingThread create(final Consumer<Component> messageSender) {
        if (exists()) {
            throw new IllegalStateException("There's already a thread running");
        }
        ProbeDumpingThread thread = new ProbeDumpingThread(messageSender);
        INSTANCE = thread;
        return thread;
    }

    private ProbeDumpingThread(final Consumer<Component> messageSender) {
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
            "ProbeDumpingThread"
        );
    }
}
