package com.probejs.rewrite;

import lombok.val;

import java.util.*;

public abstract class PathResolver {
    public static final HashMap<String, ClazzPath> resolved = new HashMap<>();
    public static final HashSet<String> lastNames = new HashSet<>();
    public static final HashSet<Class<?>> primitives = new HashSet<>();
    /**
     * language keywords used by typescript
     */
    public static final Set<String> langKeywords = new HashSet<>();

    static {
        //keywords
        langKeywords.addAll(Arrays.asList(
            "abstract",
            "arguments",
            "boolean",
            "break",
            "byte",
            "case",
            "catch",
            "char",
            "const",
            "continue",
            "constructor",
            "debugger",
            "default",
            "delete",
            "do",
            "double",
            "else",
            "eval",
            "false",
            "final",
            "finally",
            "float",
            "for",
            "function",
            "goto",
            "if",
            "implements",
            "in",
            "instanceof",
            "int",
            "interface",
            "let",
            "long",
            "native",
            "new",
            "null",
            "package",
            "private",
            "protected",
            "public",
            "return",
            "short",
            "static",
            "switch",
            "synchronized",
            "this",
            "throw",
            "throws",
            "transient",
            "true",
            "try",
            "typeof",
            "var",
            "void",
            "volatile",
            "while",
            "with",
            "yield",
            "export"
        ));
        //pre-resolve primitive
        val ANY = new ClazzPath(Collections.emptyList(), "any", false);
        resolvePrimitive(Object.class, ANY);
        val STRING = new ClazzPath(Collections.emptyList(), "string", false);
        resolvePrimitive(String.class, STRING);
        resolvePrimitive(Character.class, STRING);
        resolvePrimitive(Character.TYPE, STRING);
        val VOID = new ClazzPath(Collections.emptyList(), "void", false);
        resolvePrimitive(Void.class, VOID);
        resolvePrimitive(Void.TYPE, VOID);
        val NUMBER = new ClazzPath(Collections.emptyList(), "number", false);
        resolvePrimitive(Long.class, NUMBER);
        resolvePrimitive(Long.TYPE, NUMBER);
        resolvePrimitive(Integer.class, NUMBER);
        resolvePrimitive(Integer.TYPE, NUMBER);
        resolvePrimitive(Short.class, NUMBER);
        resolvePrimitive(Short.TYPE, NUMBER);
        resolvePrimitive(Byte.class, NUMBER);
        resolvePrimitive(Byte.TYPE, NUMBER);
        resolvePrimitive(Double.class, NUMBER);
        resolvePrimitive(Double.TYPE, NUMBER);
        resolvePrimitive(Float.class, NUMBER);
        resolvePrimitive(Float.TYPE, NUMBER);
        val BOOLEAN = new ClazzPath(Collections.emptyList(), "boolean", false);
        resolvePrimitive(Boolean.class, BOOLEAN);
        resolvePrimitive(Boolean.TYPE, BOOLEAN);
    }

    public static void resolvePrimitive(Class<?> clazz, ClazzPath resolvedPath) {
        resolveManually(clazz, resolvedPath);
        primitives.add(clazz);
    }

    public static ClazzPath resolve(Class<?> clazz) {
        return resolve(clazz.getName());
    }

    public static ClazzPath resolve(String clazzName) {
        ClazzPath path = resolved.get(clazzName);
        if (path == null) {
            path = new ClazzPath(clazzName);
            path.setInternal(!lastNames.contains(path.getName()));
            resolveManually(clazzName, path);
        }
        return path;
    }

    public static ClazzPath resolveManually(String name, ClazzPath resolvedPath) {
        resolved.put(name, resolvedPath);
        lastNames.add(resolvedPath.getName());
        return resolvedPath;
    }

    public static ClazzPath resolveManually(Class<?> clazz, ClazzPath resolvedPath) {
        return resolveManually(clazz.getName(), resolvedPath);
    }

    public static ClazzPath resolveManually(Class<?> clazz, String resolvedPath) {
        return resolveManually(clazz.getName(), new ClazzPath(resolvedPath));
    }

    public static ClazzPath get(String name) {
        return resolved.getOrDefault(name, ClazzPath.UNRESOLVED);
    }

    public static boolean isNameSafe(String name) {
        return langKeywords.contains(name);
    }

    public static String getNameSafe(String name) {
        if (!isNameSafe(name)) {
            name = name + "_";
        }
        return name;
    }
}
