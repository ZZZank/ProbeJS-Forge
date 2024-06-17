package com.probejs.formatter.resolver;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ZZZank
 */
@EqualsAndHashCode
@ToString
public class ClassPath {

    public static final ClassPath UNRESOLVED = new ClassPath(Collections.singletonList("Unresolved"));
    private final List<String> names;

    ClassPath(List<String> names) {
        this.names = names.stream().map(PathResolver::getNameSafe).collect(Collectors.toList());
    }

    public String fullPath() {
        return String.join(".", names);
    }

    public String namespace() {
        return String.join(".", names.subList(0, names.size() - 1));
    }

    public String name() {
        return names.get(names.size() - 1);
    }

    public String slashFullPath() {
        return String.join("/", names);
    }

    public String slashNamespace() {
        return String.join("/", names.subList(0, names.size() - 1));
    }
}
