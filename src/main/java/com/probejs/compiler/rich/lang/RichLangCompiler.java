package com.probejs.compiler.rich.lang;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;

public class RichLangCompiler {

    public static void compile() throws IOException {
        JsonArray langKeyArray = new JsonArray();

        LanguageManager languageManager = Minecraft.getInstance().getLanguageManager();

        String selected = languageManager.getSelected().getCode();
        String codeRegion = selected.contains("_") ? selected.split("_")[0] : selected.substring(0, 2);
        List<LanguageInfo> sameRegionLang = languageManager
            .getLanguages()
            .stream()
            .filter(entry -> entry.getCode().startsWith(codeRegion))
            .collect(Collectors.toList());

        Map<String, Map<String, String>> storage = new HashMap<>();

        FormatterLang
            .getLangKeys(FormatterLang.DEFAULT_LANGUAGE.getCode())
            .forEach(entry ->
                storage
                    .computeIfAbsent(entry.getKey(), key -> new HashMap<>())
                    .put(
                        languageManager.getLanguage(FormatterLang.DEFAULT_LANGUAGE.getCode()).getName(),
                        entry.getValue()
                    )
            );

        if (!selected.equals(FormatterLang.DEFAULT_LANGUAGE.getCode())) {
            FormatterLang
                .getLangKeys(selected)
                .forEach(entry ->
                    storage
                        .computeIfAbsent(entry.getKey(), key -> new HashMap<>())
                        .put(languageManager.getLanguage(selected).getName(), entry.getValue())
                );
        }

        for (LanguageInfo lang : sameRegionLang) {
            FormatterLang
                .getLangKeys(lang)
                .forEach(entry ->
                    storage
                        .computeIfAbsent(entry.getKey(), key -> new HashMap<>())
                        .put(lang.getName(), entry.getValue())
                );
        }

        storage
            .entrySet()
            .stream()
            .map(entry -> {
                JsonObject langObj = new JsonObject();
                langObj.addProperty("key", entry.getKey());
                JsonObject langs = new JsonObject();
                entry
                    .getValue()
                    .entrySet()
                    .stream()
                    .forEach(e -> langs.addProperty(e.getKey(), e.getValue()));
                langObj.add("languages", langs);
                langObj.addProperty("selected", languageManager.getLanguage(selected).getName());
                return langObj;
            })
            .forEach(langKeyArray::add);
        Path richFile = ProbePaths.WORKSPACE.resolve("lang-keys.json");
        BufferedWriter writer = Files.newBufferedWriter(richFile);
        writer.write(ProbeJS.GSON.toJson(langKeyArray));
        writer.close();
    }
}
