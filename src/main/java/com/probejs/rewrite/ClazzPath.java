package com.probejs.rewrite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class ClazzPath {
    public static final List<String> NAMESPACE_INTERNAL = Collections.singletonList("Internal");
    public static final List<String> NAMESPACE_NONE = Collections.emptyList();
    public static final String NAME_UNRESOLVED = "Unresolved";
    public static final ClazzPath UNRESOLVED = new ClazzPath(NAMESPACE_NONE, NAME_UNRESOLVED, false);

    private final List<String> namespace;
    private final String name;
    private boolean isInternal;

    ClazzPath(String clazzName) {
        val path = Arrays
            .stream(clazzName.split("\\."))
            .map(PathResolver::getNameSafe)
            .collect(Collectors.toList());
        this.namespace = path.subList(1, path.size());
        this.name = path.get(0);
        this.isInternal = false;
    }

    public String asStrPath(String delimiter) {
        return String.join(delimiter, this.asStrNamespace(delimiter), this.name);
    }

    public String asStrNamespace(String delimiter) {
        return String.join(delimiter, this.getNamespace());
    }

    public String dotPath() {
        return asStrPath(".");
    }

    public String slashPath() {
        return asStrPath("/");
    }

    public String dotNamespace() {
        return this.asStrNamespace(".");
    }

    public String slashNamespace() {
        return this.asStrNamespace("/");
    }
}
