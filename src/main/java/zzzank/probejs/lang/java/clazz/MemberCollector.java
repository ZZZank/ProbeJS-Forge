package zzzank.probejs.lang.java.clazz;

import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import zzzank.probejs.features.rhizo.RemapperBridge;
import zzzank.probejs.lang.java.clazz.members.ConstructorInfo;
import zzzank.probejs.lang.java.clazz.members.FieldInfo;
import zzzank.probejs.lang.java.clazz.members.MethodInfo;
import zzzank.probejs.utils.ReflectUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
public class MemberCollector {

    private final Set<String> names = new HashSet<>();

    public Stream<? extends ConstructorInfo> constructors(Class<?> from) {
        return Arrays.stream(ReflectUtils.constructorsSafe(from))
            .filter(MemberCollector::notHideFromJS)
            .map(ConstructorInfo::new);
    }

    public Stream<? extends MethodInfo> methods(Class<?> from) {
        return Arrays.stream(ReflectUtils.methodsSafe(from))
            .peek(m -> names.add(RemapperBridge.remapMethod(from, m)))
            .filter(MemberCollector::notHideFromJS)
            .filter(m -> !m.isSynthetic() && !hasIdenticalParentMethod(m, from))
            .sorted(Comparator.comparing(Method::getName))
            .map(method -> new MethodInfo(
                from,
                method,
                getGenericTypeReplacement(from, method)
            ));
    }

    public Stream<? extends FieldInfo> fields(Class<?> from) {
        return Arrays.stream(ReflectUtils.fieldsSafe(from))
            .filter(MemberCollector::notHideFromJS)
            .filter(f -> !names.contains(RemapperBridge.remapField(from, f)))
            .sorted(Comparator.comparing(Field::getName))
            .map(f -> new FieldInfo(from, f));
    }

    public static boolean notHideFromJS(AnnotatedElement element) {
        return !element.isAnnotationPresent(HideFromJS.class);
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
    static Map<TypeVariable<?>, Type> getGenericTypeReplacement(
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
        val parentType = getGenericTypeReplacement(superInterface, thatMethod);
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
