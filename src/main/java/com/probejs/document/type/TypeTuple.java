package com.probejs.document.type;

import com.probejs.util.StringUtil;
import lombok.Getter;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * "[int,int,int]"
 *
 * @author ZZZank
 */
@Getter
public class TypeTuple implements DocType {

    private final List<DocType> elements;

    public TypeTuple(String typeStr) {
        if (!test(typeStr)) {
            throw new IllegalArgumentException();
        }
        typeStr = typeStr.substring(1, typeStr.length() - 1);
        elements = StringUtil.splitLayer(typeStr, ",")
            .stream()
            .map(String::trim)
            .map(DocTypeResolver::resolve)
            .collect(Collectors.toList());
    }

    public static boolean test(String typeStr) {
        return typeStr.startsWith("[") && typeStr.endsWith("]");
    }

    @Override
    public String getTypeName() {
        return "[" + elements.stream().map(DocType::getTypeName).collect(Collectors.joining(",")) + "]";
    }

    @Override
    public String transform(BiFunction<DocType, String, String> transformer) {
        return "[" + elements.stream().map(type -> type.transform(transformer)).collect(Collectors.joining(",")) + "]";
    }
}
