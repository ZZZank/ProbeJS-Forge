package zzzank.probejs;

import zzzank.probejs.utils.config.ConfigEntry;
import zzzank.probejs.utils.config.ConfigImpl;

import static zzzank.probejs.utils.config.ConfigEntryBuilder.of;

/**
 * @author ZZZank
 */
public interface ProbeConfig {

    ConfigImpl INSTANCE = new ConfigImpl(ProbePaths.SETTINGS_JSON, ProbeJS.MOD_ID);

    ConfigEntry<Boolean> enabled = INSTANCE.addConfig(of("enabled", true));
    ConfigEntry<Boolean> interactive = INSTANCE.addConfig(of("interactive", false));
    ConfigEntry<Integer> interactivePort = INSTANCE.addConfig(of("interactivePort", 7796));
    ConfigEntry<Long> modHash = INSTANCE.addConfig(of("modHash", -1L));
    ConfigEntry<Long> registryHash = INSTANCE.addConfig(of("registryHash", -1L));
    ConfigEntry<Boolean> isolatedScopes = INSTANCE.addConfig(of("isolatedScope", false));
    ConfigEntry<Boolean> complete = INSTANCE.addConfig(of("complete", true));
    ConfigEntry<Boolean> publicClassOnly = INSTANCE.addConfig(
        of("publicClassOnly", false).comment(
            "prevent classes that are not public and not referenced from being scanned"
        )
    );
}
