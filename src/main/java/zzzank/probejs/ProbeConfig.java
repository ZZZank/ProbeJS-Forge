package zzzank.probejs;

import zzzank.probejs.utils.config.ConfigEntry;
import zzzank.probejs.utils.config.ConfigEntrySerde;
import zzzank.probejs.utils.config.ConfigImpl;

import static zzzank.probejs.utils.config.ConfigEntryBuilder.of;

/**
 * @author ZZZank
 */
public interface ProbeConfig {

    ConfigImpl INSTANCE = new ConfigImpl(ProbePaths.SETTINGS_JSON, ProbeJS.MOD_ID);

    ConfigEntry<Integer> configVersion = INSTANCE.addConfig(of("configVersion", 3).comments(
        "welcome to ProbeJS Legacy config file",
        "names of each config entry are in `{namespace}.{name}` form, e.g. 'probejs.version'",
        "each name is mapped to a config entry, where default value, current value, and possibly comments, are provided",
        "",
        String.format(
            "sub-entry and keys: comments->'%s', current values->'%s', default values->'%s'",
            ConfigEntrySerde.COMMENTS_KEY,
            ConfigEntrySerde.VALUE_KEY,
            ConfigEntrySerde.DEFAULT_VALUE_KEY
        ),
        "",
        "comments and default values are provided, but not modifiable, changes to them will not be kept",
        String.format("for changing certain config value, change sub-entry whose key is '%s'", ConfigEntrySerde.VALUE_KEY)
    ));
    ConfigEntry<Boolean> enabled = INSTANCE.addConfig(of("enabled", true).comments(
        "enable or disable ProbeJS Legacy",
        "note that `require()` function in script are always available"
    ));
    ConfigEntry<Boolean> interactive = INSTANCE.addConfig(of("interactive", false).comments(
        "use with ProbeJS VSCode Extension." ,
        "Currently broken due to many breaking changes from KubeJS/ProbeJS from higher version"
    ));
    ConfigEntry<Integer> interactivePort = INSTANCE.addConfig(of("interactivePort", 7796).comment(
        "use with ProbeJS VSCode Extension."
    ));
    ConfigEntry<Long> modHash = INSTANCE.addConfig(of("modHash", -1L).comment(
        "internal config, used for tracking mod update and modlist change"
    ));
    ConfigEntry<Long> registryHash = INSTANCE.addConfig(of("registryHash", -1L).comment(
        "internal config, used for tracking registry change"
    ));
    ConfigEntry<Boolean> isolatedScopes = INSTANCE.addConfig(of("isolatedScope", false).comments(
        "isolate scripts from different script file with certain exposure,",
        "used for making scripts actual running situation more in line with your coding"
    ));
    ConfigEntry<Boolean> complete = INSTANCE.addConfig(of("complete", true).comments(
        "attach all registry names of each registry type to related JS types, for better code completion",
        "disabling this can help with performance of your code editor",
        "snippets for registry names are always available, regardless of this option"
    ));
    ConfigEntry<Boolean> publicClassOnly = INSTANCE.addConfig(of("publicClassOnly", false).comment(
        "prevent classes that are not public and not referenced from being scanned"
    ));
    ConfigEntry<Boolean> resolveGlobal = INSTANCE.addConfig(of("resolveGlobal", true).comment(
        "resolve defined values in `global`"
    ));
    ConfigEntry<Boolean> fullScan = INSTANCE.addConfig(of("fullScan", false).comments(
        "apply no filter on class scanning process if true",
        "disabling this will restrict class scanner to only scan ProbeJS captured classes and forge event classes"
    ));
}
