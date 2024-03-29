package com.probejs.compiler.rich.lang;

import com.probejs.ProbeJS;
import com.probejs.formatter.formatter.IFormatter;
import com.probejs.util.PUtil;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.locale.Language;

public class FormatterLang implements IFormatter {

    public static final LanguageInfo DEFAULT_LANGUAGE = new LanguageInfo("en_us", "US", "English", false);
    private static final Set<String> ALL_KEYS = new HashSet<>();

    public static synchronized void modifyKeys(Consumer<Set<String>> access) {
        synchronized (ALL_KEYS) {
            access.accept(ALL_KEYS);
        }
    }

    public static Set<String> getAllKeys() {
        HashSet<String> keys;
        synchronized (ALL_KEYS) {
            keys = new HashSet<>(ALL_KEYS);
        }
        return keys;
    }

    @Override
    public List<String> format(int indent, int stepIndent) {
        if (!(Language.getInstance() instanceof ClientLanguage)) {
            return new ArrayList<>(0);
        }
        return Arrays.asList(
            String.format(
                "%stype LangKey = %s",
                PUtil.indent(indent),
                getLangKeys(FormatterLang.DEFAULT_LANGUAGE.getCode())
                    .map(Map.Entry::getKey)
                    .map(ProbeJS.GSON::toJson)
                    .collect(Collectors.joining(" | "))
            )
        );
    }

    public static Stream<Map.Entry<String, String>> getLangKeys(String language) {
        LanguageManager manager = Minecraft.getInstance().getLanguageManager();
        return getLangKeys(manager.getLanguage(language));
    }

    public static Stream<Map.Entry<String, String>> getLangKeys(LanguageInfo language) {
        Minecraft mc = Minecraft.getInstance();
        LanguageManager manager = mc.getLanguageManager();
        LanguageInfo english = manager.getLanguage(FormatterLang.DEFAULT_LANGUAGE.getCode());
        List<LanguageInfo> languages = language.equals(english)
            ? Arrays.asList(english)
            : Arrays.asList(english, language);

        // HashMap<LanguageInfo, String> reversedMap = new HashMap<>();
        // manager.getLanguages().forEach(lang -> reversedMap.put(lang, lang.getCode()));
        // // manager.getLanguages().forEach((key, value) -> reversedMap.put(value, key));
        // List<String> langFiles = languages
        //     .stream()
        //     .map(reversedMap::get)
        //     .filter(Objects::nonNull)
        //     .collect(Collectors.toList());

        // ClientLanguage clientLanguage = ClientLanguage.loadFrom(
        //     mc.getResourceManager(),
        //     langFiles,
        //     english.isBidirectional()
        // );
        ClientLanguage clientLanguage = ClientLanguage.loadFrom(mc.getResourceManager(), languages);
        Map<String, String> storage = clientLanguage.getLanguageData();

        if (!ALL_KEYS.isEmpty()) {
            storage = new HashMap<>(storage);
            for (String key : getAllKeys()) {
                if (!storage.containsKey(key)) {
                    storage.put(key, key);
                }
            }
        }

        return storage.entrySet().stream();
    }
}
