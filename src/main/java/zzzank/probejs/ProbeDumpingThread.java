package zzzank.probejs;

import lombok.val;
import net.minecraft.network.chat.Component;
import zzzank.probejs.features.kubejs.SpecialData;
import zzzank.probejs.utils.GameUtils;

import java.util.function.Consumer;

/**
 * @author ZZZank
 */
public class ProbeDumpingThread extends Thread {

    public static ProbeDumpingThread INSTANCE;
    private final Consumer<Component> messageSender;

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
        super("ProbeDumpingThread");
        this.messageSender = messageSender;
    }

    @Override
    public void run() {
        SpecialData.refresh();
        val probeDump = new ProbeDump();
        probeDump.defaultScripts();
        try {
            probeDump.trigger(messageSender);
        } catch (Throwable e) {
            GameUtils.logThrowable(e);
        }
    }
}
