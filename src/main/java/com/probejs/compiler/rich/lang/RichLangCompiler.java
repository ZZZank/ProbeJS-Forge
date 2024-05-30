package com.probejs.compiler.rich.lang;

import com.google.gson.JsonArray;
import com.probejs.ProbeJS;
import com.probejs.ProbePaths;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import com.probejs.util.Pair;
import com.probejs.util.json.JObject;
import com.probejs.util.json.JPrimitive;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageInfo;

public class RichLangCompiler {

    public static void compile() throws IOException {
        val langKeyArray = new JsonArray();

        val langManager = Minecraft.getInstance().getLanguageManager();

        val selected = langManager.getSelected().getCode();
        val codeRegion = selected.contains("_") ? selected.split("_")[0] : selected.substring(0, 2);
        val sameRegionLang = langManager
            .getLanguages()
            .stream()
            .map(LanguageInfo::getCode)
            .filter(code -> code.startsWith(codeRegion))
            .collect(Collectors.toList());
        val langCodes = new HashSet<String>();
        langCodes.add(FormatterLang.DEFAULT_LANGUAGE.getCode());
        langCodes.add(langManager.getSelected().getCode());
        langCodes.addAll(sameRegionLang);

        Map<String, Map<String, String>> storage = new HashMap<>();
        for (val langCode : langCodes) {
            val lang = langManager.getLanguage(langCode);
            FormatterLang.getLangKeys(lang).forEach(entry ->
                storage
                    .computeIfAbsent(entry.getKey(), key -> new HashMap<>())
                    .put(lang.getName(), entry.getValue()));
        }

        storage
            .entrySet()
            .stream()
            .map(entry -> JObject.of()
                .add("key", entry.getKey())
                .add("languages", JObject.of().addAll(entry
                    .getValue()
                    .entrySet()
                    .stream()
                    .map(e -> new Pair<>(e.getKey(), JPrimitive.of(e.getValue())))
                ))
                .add("selected", langManager.getLanguage(selected).getName())
                .build()
            )
            .forEach(langKeyArray::add);
        Path richFile = ProbePaths.WORKSPACE.resolve("lang-keys.json");
        BufferedWriter writer = Files.newBufferedWriter(richFile);
        ProbeJS.GSON.toJson(langKeyArray, writer);
        writer.close();
    }
}
