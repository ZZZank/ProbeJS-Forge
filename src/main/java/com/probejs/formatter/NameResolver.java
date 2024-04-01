package com.probejs.formatter;

import com.probejs.ProbeJS;
import com.probejs.info.type.ITypeInfo;
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
import net.minecraft.world.damagesource.DamageSource;

public class NameResolver {

    public static class ResolvedName {

        public static final ResolvedName UNRESOLVED = new ResolvedName(Arrays.asList("any"));
        private final List<String> names;

        private ResolvedName(List<String> names) {
            this.names = names.stream().map(NameResolver::getNameSafe).collect(Collectors.toList());
        }

        public String getFullName() {
            return String.join(".", names);
        }

        public String getNamespace() {
            return String.join(".", names.subList(0, names.size() - 1));
        }

        public String getLastName() {
            return names.get(names.size() - 1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ResolvedName that = (ResolvedName) o;
            return names.equals(that.names);
        }

        @Override
        public int hashCode() {
            return Objects.hash(names);
        }

        @Override
        public String toString() {
            return "ResolvedName{" + "names=" + names + '}';
        }
    }

    public static final HashMap<String, ResolvedName> resolvedNames = new HashMap<>();
    public static final HashMap<Class<?>, Function<ITypeInfo, String>> specialTypeFormatters = new HashMap<>();
    public static final HashMap<Class<?>, Function<Object, String>> specialValueFormatters = new HashMap<>();
    public static final HashMap<Class<?>, Supplier<List<String>>> specialClassAssigner = new HashMap<>();
    public static final HashMap<Class<?>, Boolean> specialTypeGuards = new HashMap<>();

    public static final Set<String> keywords = new HashSet<>();
    public static final Set<String> resolvedPrimitives = new HashSet<>();

    /**
     * @see com.probejs.formatter.NameResolver#putResolvedName(String, ResolvedName)
     */
    public static ResolvedName putResolvedName(String className, String resolvedName) {
        return putResolvedName(className, new ResolvedName(Arrays.asList(resolvedName.split("\\."))));
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
    public static ResolvedName putResolvedName(String className, ResolvedName resolvedName) {
        ResolvedName curr = resolvedNames.putIfAbsent(className, resolvedName);
        if (curr != null) {
            return curr;
        }
        return resolvedNames.get(className);
    }

    /**
     * @see com.probejs.formatter.NameResolver#putResolvedName(String, ResolvedName)
     */
    public static ResolvedName putResolvedName(Class<?> clazz, ResolvedName resolvedName) {
        return putResolvedName(clazz.getName(), resolvedName);
    }

    /**
     * @see com.probejs.formatter.NameResolver#putResolvedName(String, ResolvedName)
     */
    public static ResolvedName putResolvedName(Class<?> clazz, String resolvedName) {
        return putResolvedName(clazz, new ResolvedName(Arrays.asList(resolvedName.split("\\."))));
    }

    /**
     * resolve full-name of a class into ones used by ProbeJS
     * @param className Full class name, like "java.lang.String"
     * @return Resolved name, or {@code ResolvedName.UNRESOLVED} if unable to resolve
     */
    public static ResolvedName getResolvedName(String className) {
        return resolvedNames.getOrDefault(className, ResolvedName.UNRESOLVED);
    }

    public static void putTypeFormatter(Class<?> className, Function<ITypeInfo, String> formatter) {
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
        if (specialValueFormatters.containsKey(object.getClass())) {
            return specialValueFormatters.get(object.getClass()).apply(object);
        }
        for (Map.Entry<Class<?>, Function<Object, String>> entry : specialValueFormatters.entrySet()) {
            if (entry.getKey().isAssignableFrom(object.getClass())) {
                return entry.getValue().apply(object);
            }
        }
        return null;
    }

    public static ResolvedName resolveName(Class<?> clazz) {
        // String remappedName = MethodInfo.RUNTIME.getMappedClass(clazz);
        // ResolvedName resolved = new ResolvedName(Arrays.asList(remappedName.split("\\.")));
        final ResolvedName resolved = new ResolvedName(Arrays.asList(clazz.getName().split("\\.")));
        final ResolvedName internal = new ResolvedName(Arrays.asList("Internal", resolved.getLastName()));
        if (resolvedNames.containsValue(internal)) {
            return putResolvedName(clazz.getName(), resolved);
        } else {
            return putResolvedName(clazz.getName(), internal);
        }
    }

    public static void resolveNames(Collection<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            resolveName(clazz);
        }
    }

    public static void addKeyword(String kw) {
        keywords.add(kw);
    }

    public static String getNameSafe(String kw) {
        return keywords.contains(kw) ? kw + "_" : kw;
    }

    public static void putResolvedPrimitive(Class<?> clazz, String resolvedName) {
        putResolvedName(clazz, resolvedName);
        resolvedPrimitives.add(clazz.getName());
    }

    public static void putSpecialAssignments(Class<?> clazz, Supplier<List<String>> assigns) {
        specialClassAssigner.put(clazz, assigns);
    }

    public static List<String> getClassAssignments(Class<?> clazz) {
        return specialClassAssigner.getOrDefault(clazz, ArrayList::new).get();
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

        putSpecialAssignments(
            DamageSource.class,
            () -> {
                List<String> result = new ArrayList<>();
                try {
                    for (Field field : DamageSource.class.getDeclaredFields()) {
                        if (
                            !Modifier.isStatic(field.getModifiers()) ||
                            field.getType() != DamageSource.class ||
                            !Modifier.isPublic(field.getModifiers())
                        ) {
                            continue;
                        }
                        field.setAccessible(true);
                        String id = ((DamageSource) field.get(null)).getMsgId();
                        result.add(ProbeJS.GSON.toJson(id));
                    }
                } catch (Exception ignored) {}
                return result;
            }
        );

        // putTypeGuard(true, Class.class, ClassWrapper.class);
        putTypeGuard(true, Class.class);
        putTypeGuard(false, IngredientJS.class);

        addKeyword("function");
        addKeyword("debugger");
        addKeyword("in");
        addKeyword("with");
        addKeyword("java");
    }
}
