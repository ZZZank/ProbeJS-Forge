package com.probejs.rewrite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ClazzPath {
    public static final List<String> NAMESPACE_INTERNAL = Collections.singletonList("Internal");
    public static final List<String> NAMESPACE_NONE = Collections.emptyList();
    public static final String NAME_UNRESOLVED = "Unresolved";
    public static final ClazzPath UNRESOLVED = new ClazzPath(NAMESPACE_NONE, NAME_UNRESOLVED);

    private final List<String> namespace;
    private final String name;
    private final String raw;

    ClazzPath(String clazzName) {
        raw = clazzName;
        val path = Arrays
            .stream(clazzName.split("\\."))
            .map(PathResolver::getNameSafe)
            .collect(Collectors.toList());
        this.namespace = path.subList(0, path.size() - 1);
        this.name = path.get(path.size() - 1);
    }

    ClazzPath(List<String> namespace, String name) {
        this.namespace = namespace;
        this.name = name;
        this.raw = dotPath();
    }

    public String name() {
        return name;
    }

    public String joinPath(String delimiter) {
        return String.join(delimiter, this.joinNamespace(delimiter), this.name);
    }

    public String joinNamespace(String delimiter) {
        return String.join(delimiter, this.getNamespace());
    }

    public String dotPath() {
        return joinPath(".");
    }

    public String slashPath() {
        return joinPath("/");
    }

    public String dotNamespace() {
        return this.joinNamespace(".");
    }

    public String slashNamespace() {
        return this.joinNamespace("/");
    }
}
