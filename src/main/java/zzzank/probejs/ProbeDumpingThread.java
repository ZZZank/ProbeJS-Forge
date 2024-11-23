package zzzank.probejs;

import dev.latvian.kubejs.bindings.TextWrapper;
import lombok.val;
import net.minecraft.network.chat.Component;
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
            messageSender.accept(TextWrapper.translate("probejs.rhizo_missing").red().component());
            messageSender.accept(TextWrapper
                .translate("probejs.download_rhizo_help")
                .append(TextWrapper.string("CurseForge")
                    .aqua()
                    .underlined()
                    .click("https://www.curseforge.com/minecraft/mc-mods/rhizo/files"))
                .append(" / ")
                .append(TextWrapper.string("Github")
                    .aqua()
                    .underlined()
                    .click("https://github.com/ZZZank/Rhizo/releases/latest"))
                .component()
            );
        }

        RegistryInfos.refresh();
        val probeDump = new ProbeDump();
        probeDump.defaultScripts();
        try {
            probeDump.trigger(messageSender);
        } catch (Throwable e) {
            messageSender.accept(TextWrapper.translate("probejs.error").red().component());
            GameUtils.logThrowable(e);
        }
    }
}
