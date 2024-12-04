package zzzank.probejs;

import zzzank.probejs.utils.config.ConfigEntry;
import zzzank.probejs.utils.config.ConfigEntrySerde;
import zzzank.probejs.utils.config.ConfigImpl;

/**
 * @author ZZZank
 */
public interface ProbeConfig {

    ConfigImpl INSTANCE = new ConfigImpl(ProbePaths.SETTINGS_JSON, ProbeJS.MOD_ID);

    static void refresh() {
        INSTANCE.readFromFile();
        INSTANCE.save();
    }

    ConfigEntry<Integer> configVersion = INSTANCE.define("configVersion")
        .setDefaultValue(3)
        .comment(String.format("""
            welcome to ProbeJS Legacy config file
            remember to use '/probejs refresh_config' to refresh your config after changing config values
            sub-entry and keys: comments->'%s', current values->'%s', default values->'%s'
            
            comments and default values are provided, but not modifiable, changes to them will not be kept
            for changing certain config value, change sub-entry whose key is '%s'""",
            ConfigEntrySerde.COMMENTS_KEY, ConfigEntrySerde.VALUE_KEY, ConfigEntrySerde.DEFAULT_VALUE_KEY,
            ConfigEntrySerde.VALUE_KEY
        ))
        .build();
    ConfigEntry<Boolean> enabled = INSTANCE.define("enabled")
        .setDefaultValue(true)
        .comments("""
            enable or disable ProbeJS Legacy
            note that `require()` function in script are always available""")
        .build();
    ConfigEntry<Boolean> interactive = INSTANCE.define("interactive")
        .setDefaultValue(false)
        .comments("""
            use with ProbeJS VSCode Extension.
            Currently broken due to many breaking changes from KubeJS/ProbeJS from higher version""")
        .build();
    ConfigEntry<Integer> interactivePort = INSTANCE.define("interactivePort")
        .setDefaultValue(7796)
        .comment("""
            use with ProbeJS VSCode Extension.
            Currently broken due to many breaking changes from KubeJS/ProbeJS from higher version""")
        .build();
    ConfigEntry<Long> modHash = INSTANCE.define("modHash")
        .setDefaultValue(-1L)
        .comment("""
            internal config, used for tracking mod update and modlist change""")
        .build();
    ConfigEntry<Long> registryHash = INSTANCE.define("registryHash")
        .setDefaultValue(-1L)
        .comment("""
            internal config, used for tracking registry change""")
        .build();
    ConfigEntry<Boolean> isolatedScopes = INSTANCE.define("isolatedScopes")
        .setDefaultValue(false)
        .comment("""
            isolate scripts from different script file with certain exposure,
            used for making scripts actual running situation more in line with your coding""")
        .build();
    ConfigEntry<Boolean> complete = INSTANCE.define("complete")
        .setDefaultValue(true)
        .comment("""
            attach all registry names of each registry type to related JS types, for better code completion
            disabling this can help with performance of your code editor
            snippets for registry names are always available, regardless of this option""")
        .build();
    ConfigEntry<Boolean> publicClassOnly = INSTANCE.define("publicClassOnly")
        .setDefaultValue(false)
        .comment("""
            prevent classes that are not public and not referenced from being scanned""")
        .build();
    ConfigEntry<Boolean> resolveGlobal = INSTANCE.define("resolveGlobal")
        .setDefaultValue(true)
        .comment("""
            resolve defined values in `global`""")
        .build();
    ConfigEntry<Boolean> fullScan = INSTANCE.define("fullScan")
        .setDefaultValue(false)
        .comment("""
            apply no filter on class scanning process if true
            disabling this will restrict class scanner to only scan ProbeJS captured classes and forge event classes""")
        .build();
    ConfigEntry<Boolean> dumpCustomRecipeGenerator = INSTANCE.define("dumpCustomRecipeGenerator")
        .setDefaultValue(false)
        .comment("""
            KubeJS will generate custom recipe creation method in recipe event, these methods only accept one Json as its arg
            enabling this will allow ProbeJS to dump syntax these JsonSerializer-based recipe creating functions""")
        .build();
}
