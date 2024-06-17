package com.probejs.formatter.resolver;

import com.probejs.ProbeJS;
import com.probejs.info.type.JavaType;
import dev.latvian.kubejs.block.MaterialJS;
import dev.latvian.kubejs.block.MaterialListJS;
import dev.latvian.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.NativeObject;
import dev.latvian.mods.rhino.Scriptable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.val;
import net.minecraft.world.damagesource.DamageSource;

public class PathResolver {

    public static final HashMap<String, ClassPath> resolved = new HashMap<>();
    public static final HashMap<Class<?>, Function<JavaType, String>> specialTypeFormatters = new HashMap<>();
    public static final HashMap<Class<?>, Function<Object, String>> specialValueFormatters = new HashMap<>();
    public static final HashMap<Class<?>, Supplier<List<String>>> specialClassAssigner = new HashMap<>();
    public static final HashMap<Class<?>, Boolean> specialTypeGuards = new HashMap<>();

    public static final Set<String> langKeywords = new HashSet<>();
    public static final Set<String> resolvedPrimitives = new HashSet<>();

    /**
     * @see PathResolver#resolveManually(String, ClassPath)
     */
    public static ClassPath resolveManually(String className, String resolvedName) {
        return resolveManually(className, new ClassPath(Arrays.asList(resolvedName.split("\\."))));
    }

    /**
     * works like {@code resolvedNames::putIfAbsent}, only puts {@code resolvedName} in when
     * the specified className is NOT resolved
     * @param className key
     * @param resolvedName value
     * @return Current value if className is already resolved, otherwise new value(
     * {@code resolvedName}), so returned value will always be not-null, as long as
     * provided {@code resolvedName} is not null
     */
    public static ClassPath resolveManually(String className, ClassPath resolvedName) {
        ClassPath curr = resolved.putIfAbsent(className, resolvedName);
        if (curr != null) {
            return curr;
        }
        return resolved.get(className);
    }

    /**
     * @see PathResolver#resolveManually(String, ClassPath)
     */
    public static ClassPath resolveManually(Class<?> clazz, ClassPath resolvedName) {
        return resolveManually(clazz.getName(), resolvedName);
    }

    /**
     * @see PathResolver#resolveManually(String, ClassPath)
     */
    public static ClassPath resolveManually(Class<?> clazz, String resolvedName) {
        return resolveManually(clazz.getName(), new ClassPath(Arrays.asList(resolvedName.split("\\."))));
    }

    /**
     * resolve full-name of a class into ones used by ProbeJS
     * @param className Full class name, like "java.lang.String"
     * @return Resolved name, or {@code ResolvedName.UNRESOLVED} if unable to resolve
     */
    public static ClassPath getResolvedName(String className) {
        return resolved.getOrDefault(className, ClassPath.UNRESOLVED);
    }

    public static void putTypeFormatter(Class<?> className, Function<JavaType, String> formatter) {
        specialTypeFormatters.put(className, formatter);
    }

    public static void putTypeGuard(boolean isSafe, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            specialTypeGuards.put(clazz, isSafe);
        }
    }

    public static boolean isTypeSpecial(Class<?> clazz) {
        return specialTypeFormatters.containsKey(clazz);
    }

    public static void putValueFormatter(Function<Object, String> transformer, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            specialValueFormatters.put(clazz, transformer);
        }
    }

    public static String formatValue(Object object) {
        if (object == null) {
            return null;
        }
        val clazz = object.getClass();
        val direct = specialValueFormatters.get(clazz);
        if (direct != null) {
            return direct.apply(object);
        }
        for (val entry : specialValueFormatters.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                return entry.getValue().apply(object);
            }
        }
        return null;
    }

    public static ClassPath resolveName(Class<?> clazz) {
        // String remappedName = MethodInfo.RUNTIME.getMappedClass(clazz);
        // ResolvedName resolved = new ResolvedName(Arrays.asList(remappedName.split("\\.")));
        val resolved = new ClassPath(Arrays.asList(clazz.getName().split("\\.")));
        val internal = new ClassPath(Arrays.asList("Internal", resolved.name()));
        if (PathResolver.resolved.containsValue(internal)) {
            return resolveManually(clazz.getName(), resolved);
        } else {
            return resolveManually(clazz.getName(), internal);
        }
    }

    public static void resolveNames(Collection<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            resolveName(clazz);
        }
    }

    public static void addKeyword(String kw) {
        langKeywords.add(kw);
    }

    public static String getNameSafe(String kw) {
        return langKeywords.contains(kw) ? kw + "_" : kw;
    }

    public static void putResolvedPrimitive(Class<?> clazz, String resolvedName) {
        resolveManually(clazz.getName(), resolvedName);
        resolvedPrimitives.add(clazz.getName());
    }

    /**
     * note: this will not overwrite original assignments, if you want naw assigns to overwrite
     * existed assigns, call specialClassAssigner#put instead
     */
    public static void addSpecialAssignments(Class<?> clazz, Supplier<List<String>> assigns) {
        val concat = specialClassAssigner.containsKey(clazz)
            ? new Supplier<List<String>>() {
                private final Supplier<List<String>> lastSupplier = specialClassAssigner.get(clazz);
                private final Supplier<List<String>> thenSupplier = assigns;

                @Override
                public List<String> get() {
                    val last = lastSupplier.get();
                    last.addAll(thenSupplier.get());
                    return last;
                }
            }
            : assigns;
        specialClassAssigner.put(clazz, concat);
    }

    public static List<String> getClassAssignments(Class<?> clazz) {
        return specialClassAssigner.getOrDefault(clazz, Collections::emptyList).get();
    }

    public static void init() {
        putResolvedPrimitive(Object.class, "any");
        putResolvedPrimitive(String.class, "string");
        putResolvedPrimitive(Character.class, "string");
        putResolvedPrimitive(Character.TYPE, "string");

        putResolvedPrimitive(Void.class, "void");
        putResolvedPrimitive(Void.TYPE, "void");

        putResolvedPrimitive(Long.class, "number");
        putResolvedPrimitive(Long.TYPE, "number");
        putResolvedPrimitive(Integer.class, "number");
        putResolvedPrimitive(Integer.TYPE, "number");
        putResolvedPrimitive(Short.class, "number");
        putResolvedPrimitive(Short.TYPE, "number");
        putResolvedPrimitive(Byte.class, "number");
        putResolvedPrimitive(Byte.TYPE, "number");

        putResolvedPrimitive(Double.class, "number");
        putResolvedPrimitive(Double.TYPE, "number");
        putResolvedPrimitive(Float.class, "number");
        putResolvedPrimitive(Float.TYPE, "number");

        putResolvedPrimitive(Boolean.class, "boolean");
        putResolvedPrimitive(Boolean.TYPE, "boolean");

        putValueFormatter(
            ProbeJS.GSON::toJson,
            String.class,
            Character.class,
            Character.TYPE,
            Long.class,
            Long.TYPE,
            Integer.class,
            Integer.TYPE,
            Short.class,
            Short.TYPE,
            Byte.class,
            Byte.TYPE,
            Double.class,
            Double.TYPE,
            Float.class,
            Float.TYPE,
            Boolean.class,
            Boolean.TYPE
        );
        putValueFormatter(SpecialTypes::formatMap, Map.class);
        putValueFormatter(SpecialTypes::formatList, List.class);
        putValueFormatter(SpecialTypes::formatScriptable, NativeObject.class);
        putValueFormatter(SpecialTypes::formatFunction, BaseFunction.class);
        putValueFormatter(SpecialTypes::formatNJO, NativeJavaObject.class);
        putValueFormatter(SpecialTypes::formatScriptable, Scriptable.class);

        addSpecialAssignments(
            MaterialJS.class,
            () ->
                MaterialListJS.INSTANCE.map
                    .keySet()
                    .stream()
                    .map(ProbeJS.GSON::toJson)
                    .collect(Collectors.toList())
        );

        addSpecialAssignments(DamageSource.class, () -> {
            List<String> result = new ArrayList<>();
            try {
                for (Field field : DamageSource.class.getDeclaredFields()) {
                    if (!Modifier.isStatic(field.getModifiers())
                        || field.getType() != DamageSource.class
                        || !Modifier.isPublic(field.getModifiers())) {
                        continue;
                    }
                    field.setAccessible(true);
                    String id = ((DamageSource) field.get(null)).getMsgId();
                    result.add(ProbeJS.GSON.toJson(id));
                }
            } catch (Exception ignored) {}
            return result;
        });

        // putTypeGuard(true, Class.class, ClassWrapper.class);
        putTypeGuard(true, Class.class);
        putTypeGuard(false, IngredientJS.class);

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
    }
}
