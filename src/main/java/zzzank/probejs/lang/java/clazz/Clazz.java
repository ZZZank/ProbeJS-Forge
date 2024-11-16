package zzzank.probejs.lang.java.clazz;

import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import zzzank.probejs.features.rhizo.RemapperBridge;
import zzzank.probejs.lang.java.base.TypeVariableHolder;
import zzzank.probejs.lang.java.clazz.members.ConstructorInfo;
import zzzank.probejs.lang.java.clazz.members.FieldInfo;
import zzzank.probejs.lang.java.clazz.members.MethodInfo;
import zzzank.probejs.lang.java.type.TypeAdapter;
import zzzank.probejs.lang.java.type.TypeDescriptor;
import zzzank.probejs.utils.CollectUtils;
import zzzank.probejs.utils.ReflectUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class Clazz extends TypeVariableHolder {

    @HideFromJS
    public final Class<?> original;
    public final ClassPath classPath;
    public final List<ConstructorInfo> constructors;
    public final List<FieldInfo> fields;
    public final List<MethodInfo> methods;
    @Nullable
    public final TypeDescriptor superClass;
    public final List<TypeDescriptor> interfaces;
    public final ClassAttribute attribute;

    public Clazz(Class<?> clazz) {
        super(clazz.getTypeParameters(), clazz.getAnnotations());

        this.original = clazz;
        this.classPath = ClassPath.fromJava(original);
        this.constructors = Arrays.stream(ReflectUtils.constructorsSafe(original))
            .filter(ctor -> !ctor.isAnnotationPresent(HideFromJS.class))
            .map(ConstructorInfo::new)
            .collect(Collectors.toList());
        Set<String> names = new HashSet<>();
        this.methods = Arrays.stream(ReflectUtils.methodsSafe(original))
            .peek(m -> names.add(RemapperBridge.remapMethod(original, m)))
            .filter(m -> !m.isSynthetic()
                && !m.isAnnotationPresent(HideFromJS.class)
                && !hasIdenticalParentMethod(m, clazz)
            )
            .map(method -> new MethodInfo(
                original,
                method,
                getGenericTypeReplacementForParentInterfaceMethods(clazz, method)
            ))
            .collect(Collectors.toList());
        this.fields = Arrays.stream(ReflectUtils.fieldsSafe(original))
            .filter(f -> !names.contains(RemapperBridge.remapField(original, f))
                && !f.isAnnotationPresent(HideFromJS.class))
            .map(f -> new FieldInfo(original, f))
            .collect(Collectors.toList());

        this.superClass = clazz.getSuperclass() == Object.class
            ? null
            : TypeAdapter.getTypeDescription(clazz.getAnnotatedSuperclass());

        this.interfaces = CollectUtils.mapToList(
            clazz.getAnnotatedInterfaces(),
            TypeAdapter::getTypeDescription
        );
        this.attribute = new ClassAttribute(clazz);
    }

    @Override
    public int hashCode() {
        return classPath.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Clazz clazz = (Clazz) o;
        return Objects.equals(classPath, clazz.classPath);
    }

    /**
     * hasIdenticalParentMethodAndEnsureNotDirectlyImplementsInterfaceSinceTypeScriptDoesNotHaveInterfaceAtRuntimeInTypeDeclarationFilesJustBecauseItSucks
     */
    private static boolean hasIdenticalParentMethod(Method method, Class<?> clazz) {
        Class<?> parent = clazz.getSuperclass();
        if (parent == null) {
            return false;
        }
        while (parent != null && !parent.isInterface()) {
            try {
                Method parentMethod = parent.getMethod(method.getName(), method.getParameterTypes());
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
    private static Map<TypeVariable<?>, Type> getGenericTypeReplacementForParentInterfaceMethods(
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

    private static Map<TypeVariable<?>, Type> getInterfaceRemap(Class<?> thisClass, Class<?> thatInterface) {
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

    public enum ClassType {
        INTERFACE,
        ENUM,
        RECORD,
        CLASS
    }

    public static class ClassAttribute {

        public final ClassType type;
        public final boolean isAbstract;
        public final boolean isInterface;
        public final Class<?> raw;


        public ClassAttribute(Class<?> clazz) {
            if (clazz.isInterface()) {
                this.type = ClassType.INTERFACE;
            } else if (clazz.isEnum()) {
                this.type = ClassType.ENUM;
//            } else if (clazz.isRecord()) {
//                this.type = ClassType.RECORD;
            } else {
                this.type = ClassType.CLASS;
            }

            int modifiers = clazz.getModifiers();
            this.isAbstract = Modifier.isAbstract(modifiers);
            this.isInterface = type == ClassType.INTERFACE;
            this.raw = clazz;
        }
    }
}
