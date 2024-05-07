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
        langKeywords.addAll(Arrays.asList("function", "debugger", "in", "with", "java"));
        //pre-resolve primitive
        val ANY = new ClazzPath(Collections.emptyList(), "any", false);
        resolvePrimitive(Object.class, ANY);
        val STR = new ClazzPath(Collections.emptyList(), "string", false);
        resolvePrimitive(String.class, STR);
        resolvePrimitive(Character.class, STR);
        resolvePrimitive(Character.TYPE, STR);
        val VOID = new ClazzPath(Collections.emptyList(), "void", false);
        resolvePrimitive(Void.class, VOID);
        resolvePrimitive(Void.TYPE, VOID);
        val NUM = new ClazzPath(Collections.emptyList(), "number", false);
        resolvePrimitive(Long.class, NUM);
        resolvePrimitive(Long.TYPE, NUM);
        resolvePrimitive(Integer.class, NUM);
        resolvePrimitive(Integer.TYPE, NUM);
        resolvePrimitive(Short.class, NUM);
        resolvePrimitive(Short.TYPE, NUM);
        resolvePrimitive(Byte.class, NUM);
        resolvePrimitive(Byte.TYPE, NUM);
        resolvePrimitive(Double.class, NUM);
        resolvePrimitive(Double.TYPE, NUM);
        resolvePrimitive(Float.class, NUM);
        resolvePrimitive(Float.TYPE, NUM);
        val BOOL = new ClazzPath(Collections.emptyList(), "boolean", false);
        resolvePrimitive(Boolean.class, BOOL);
        resolvePrimitive(Boolean.TYPE, BOOL);
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
