package zzzank.probejs;

import lombok.val;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import zzzank.probejs.utils.ProbeText;
import zzzank.probejs.utils.registry.RegistryInfos;
import zzzank.probejs.features.rhizo.RhizoState;
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

    public static ProbeDumpingThread create(final Consumer<Component> messageSender) {
        if (exists()) {
            throw new IllegalStateException("There's already a thread running");
        }
        INSTANCE = new ProbeDumpingThread(messageSender);
        return INSTANCE;
    }

    private ProbeDumpingThread(final Consumer<Component> messageSender) {
        super("ProbeDumpingThread");
        this.messageSender = messageSender;
    }

    @Override
    public void run() {
        if (!RhizoState.MOD.get()) {
            messageSender.accept(ProbeText.pjs("rhizo_missing").red().unwrap());
            messageSender.accept(ProbeText
                .pjs("download_rhizo_help")
                .append(ProbeText.literal("CurseForge")
                    .aqua()
                    .underlined(true)
                    .click(ClickEvent.Action.OPEN_URL, "https://www.curseforge.com/minecraft/mc-mods/rhizo/files"))
                .append(" / ")
                .append(ProbeText.literal("Github")
                    .aqua()
                    .underlined(true)
                    .click(ClickEvent.Action.OPEN_URL, "https://github.com/ZZZank/Rhizo/releases/latest"))
                .unwrap()
            );
        }

        RegistryInfos.refresh();
        val probeDump = new ProbeDump();
        probeDump.defaultScripts();
        try {
            probeDump.trigger(messageSender);
        } catch (Throwable e) {
            messageSender.accept(ProbeText.pjs("error").red().unwrap());
            GameUtils.logThrowable(e);
        }
    }
}
