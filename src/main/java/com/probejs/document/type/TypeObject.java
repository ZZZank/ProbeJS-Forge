package com.probejs.document.type;

import com.probejs.util.StringUtil;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * {@code {value: number, mapping: Map<string,string>, [x in string]: string}}
 * @author ZZZank
 */
@AllArgsConstructor
public class TypeObject implements DocType {

    public static boolean test(String type) {
        return type.startsWith("{") && type.endsWith("}");
    }

    private final Map<String, DocType> raw;

    public TypeObject(String typeStr) {
        if (!test(typeStr)) {
            throw new IllegalArgumentException();
        }
        this.raw = new HashMap<>();
        StringUtil.splitLayer(typeStr.substring(1, typeStr.length() - 1), ",")
            .stream()
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(s -> StringUtil.splitFirst(s, ":"))
            .forEach(p -> raw.put(p.first().trim(), DocTypeResolver.resolve(p.second())));
    }

    @Override
    public String getTypeName() {
        return transform(dummyTransformer);
    }

    @Override
    public String transform(BiFunction<DocType, String, String> transformer) {
        return "{" + raw.entrySet()
            .stream()
            .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue().transform(transformer)))
            .collect(Collectors.joining("; ")) + "}";
    }
}
