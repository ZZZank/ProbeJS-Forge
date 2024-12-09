package zzzank.probejs.lang.java.clazz;

import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import zzzank.probejs.features.rhizo.RemapperBridge;
import zzzank.probejs.lang.java.clazz.members.ConstructorInfo;
import zzzank.probejs.lang.java.clazz.members.FieldInfo;
import zzzank.probejs.lang.java.clazz.members.MethodInfo;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
public class MemberCollector {

    private final Set<String> names = new HashSet<>();

    public Stream<? extends ConstructorInfo> constructors(Class<?> from, Constructor<?>[] constructors) {
        return Arrays.stream(constructors)
            .filter(ctor -> !ctor.isAnnotationPresent(HideFromJS.class))
            .map(ConstructorInfo::new);
    }

    public Stream<? extends MethodInfo> methods(Class<?> from, Method[] methods) {
        return Arrays.stream(methods)
            .peek(m -> names.add(RemapperBridge.remapMethod(from, m)))
            .filter(m -> !m.isSynthetic()
                && !m.isAnnotationPresent(HideFromJS.class)
                && !hasIdenticalParentMethod(m, from)
            )
            .map(method -> new MethodInfo(
                from,
                method,
                getGenericTypeReplacementForParentInterfaceMethods(from, method)
            ));
    }

    public Stream<? extends FieldInfo> fields(Class<?> from, Field[] fields) {
        return Arrays.stream(fields)
            .filter(f -> !names.contains(RemapperBridge.remapField(from, f))
                && !f.isAnnotationPresent(HideFromJS.class))
            .map(f -> new FieldInfo(from, f));
    }

    /**
     * hasIdenticalParentMethodAndEnsureNotDirectlyImplementsInterfaceSinceTypeScriptDoesNotHaveInterfaceAtRuntimeInTypeDeclarationFilesJustBecauseItSucks
     */
    static boolean hasIdenticalParentMethod(Method method, Class<?> clazz) {
        Class<?> parent = clazz.getSuperclass();
        if (parent == null) {
            return false;
        }
        while (parent != null && !parent.isInterface()) {
            try {
                val parentMethod = parent.getMethod(method.getName(), method.getParameterTypes());
                // Check if the generic return type is the same
                return parentMethod.equals(method);
            } catch (NoSuchMethodException e) {
                parent = parent.getSuperclass();
            }
        }
        return false;
    }

    /**
     * getGenericTypeReplacementForParentInterfaceMethodsJustBecauseJavaDoNotKnowToReplaceThemWithGenericArgumentsOfThisClass
     */
    static Map<TypeVariable<?>, Type> getGenericTypeReplacementForParentInterfaceMethods(
        Class<?> thisClass,
        Method thatMethod
    ) {
        Class<?> targetClass = thatMethod.getDeclaringClass();

        Map<TypeVariable<?>, Type> replacement = new HashMap<>();
        if (Arrays.asList(thisClass.getInterfaces()).contains(targetClass)) {
            return getInterfaceRemap(thisClass, targetClass);
        }
        val superInterface = Arrays
            .stream(thisClass.getInterfaces())
            .filter(targetClass::isAssignableFrom)
            .findFirst()
            .orElse(null);
        if (superInterface == null) {
            return Collections.emptyMap();
        }
        val parentType = getGenericTypeReplacementForParentInterfaceMethods(superInterface, thatMethod);
        val parentReplacement = getInterfaceRemap(thisClass, superInterface);

        for (val entry : parentType.entrySet()) {
            val variable = entry.getKey();
            val type = entry.getValue();

            replacement.put(variable,
                type instanceof TypeVariable<?> typeVariable
                    ? parentReplacement.getOrDefault(typeVariable, typeVariable)
                    : type
            );
        }
        return replacement;
    }

    static Map<TypeVariable<?>, Type> getInterfaceRemap(Class<?> thisClass, Class<?> thatInterface) {
        Map<TypeVariable<?>, Type> replacement = new HashMap<>();
        int indexOfInterface = -1;
        for (Type type : thisClass.getGenericInterfaces()) {
            if (type instanceof ParameterizedType parameterizedType) {
                if (parameterizedType.getRawType().equals(thatInterface)) {
                    indexOfInterface = 0;
                    for (TypeVariable<?> typeVariable : thatInterface.getTypeParameters()) {
                        replacement.put(typeVariable, parameterizedType.getActualTypeArguments()[indexOfInterface]);
                        indexOfInterface++;
                    }
                }
            } else if (type instanceof Class<?> clazz) {
                if (clazz.equals(thatInterface)) {
                    indexOfInterface = 0;
                    for (TypeVariable<?> typeVariable : thatInterface.getTypeParameters()) {
                        // Raw use of parameterized type, so we fill with Object.class
                        // Very bad programming practice, but we have to prepare for random people coding their stuffs bad
                        replacement.put(typeVariable, Object.class);
                    }
                }
            }
        }

        if (indexOfInterface == -1) {
            // throw new IllegalArgumentException("The class does not implement the target interface");
            return Collections.emptyMap();
        }

        return replacement;
    }
}
