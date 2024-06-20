package moe.wolfgirl.probejs.lang.java.clazz;

import moe.wolfgirl.probejs.lang.java.base.ClassPathProvider;
import moe.wolfgirl.probejs.lang.java.base.TypeVariableHolder;
import moe.wolfgirl.probejs.lang.java.clazz.members.ConstructorInfo;
import moe.wolfgirl.probejs.lang.java.clazz.members.FieldInfo;
import moe.wolfgirl.probejs.lang.java.clazz.members.MethodInfo;
import moe.wolfgirl.probejs.lang.java.clazz.members.ParamInfo;
import moe.wolfgirl.probejs.lang.java.type.TypeAdapter;
import moe.wolfgirl.probejs.lang.java.type.TypeDescriptor;
import moe.wolfgirl.probejs.lang.java.type.impl.VariableType;
import moe.wolfgirl.probejs.utils.RemapperUtils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class Clazz extends TypeVariableHolder implements ClassPathProvider {
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
        this.classPath = new ClassPath(clazz);
        this.constructors = RemapperUtils.getConstructors(clazz)
                .stream()
                .map(ConstructorInfo::new)
                .collect(Collectors.toList());
        this.fields = RemapperUtils.getFields(clazz)
                .stream()
                .map(FieldInfo::new)
                .collect(Collectors.toList());
        this.methods = RemapperUtils.getMethods(clazz)
                .stream()
                .filter(m -> !m.method.isSynthetic())
                .filter(m -> !hasIdenticalParentMethodAndEnsureNotDirectlyImplementsInterface(m.method, clazz))
                .map(method -> {
                    Map<TypeVariable<?>, Type> replacement = getGenericTypeReplacementForParentInterfaceMethods(clazz, method.method);
                    return new MethodInfo(method, replacement);
                })
                .collect(Collectors.toList());

        if (clazz.getSuperclass() != Object.class) {
            this.superClass = TypeAdapter.getTypeDescription(clazz.getAnnotatedSuperclass());
        } else {
            this.superClass = null;
        }
        this.interfaces = Arrays.stream(clazz.getAnnotatedInterfaces())
                .map(TypeAdapter::getTypeDescription)
                .collect(Collectors.toList());
        this.attribute = new ClassAttribute(clazz);
    }

    @Override
    public Collection<ClassPath> getClassPaths() {
        Set<ClassPath> paths = new HashSet<>();
        for (ConstructorInfo constructor : constructors) {
            paths.addAll(constructor.getClassPaths());
        }
        for (FieldInfo field : fields) {
            paths.addAll(field.getClassPaths());
        }
        for (MethodInfo method : methods) {
            paths.addAll(method.getClassPaths());
        }
        if (superClass != null) paths.addAll(superClass.getClassPaths());
        for (TypeDescriptor i : interfaces) {
            paths.addAll(i.getClassPaths());
        }
        for (VariableType variableType : variableTypes) {
            paths.addAll(variableType.getClassPaths());
        }
        return paths;
    }

    @Override
    public int hashCode() {
        return classPath.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clazz clazz = (Clazz) o;
        return Objects.equals(classPath, clazz.classPath);
    }

    public Set<ClassPath> getUsedClasses() {
        Set<ClassPath> used = new HashSet<>();

        for (MethodInfo method : methods) {
            used.addAll(method.returnType.getClassPaths());
            for (ParamInfo param : method.params) {
                used.addAll(param.type.getClassPaths());
            }
        }

        for (FieldInfo field : fields) {
            used.addAll(field.type.getClassPaths());
        }

        for (ConstructorInfo constructor : constructors) {
            for (ParamInfo param : constructor.params) {
                used.addAll(param.type.getClassPaths());
            }
        }

        if (superClass != null) used.addAll(superClass.getClassPaths());
        for (TypeDescriptor i : interfaces) {
            used.addAll(i.getClassPaths());
        }

        for (VariableType variableType : variableTypes) {
            used.addAll(variableType.getClassPaths());
        }

        return used;
    }

    /**
     * 天生万物以养民，民无一善可报天。
     * 不知蝗蠹遍天下，苦尽苍生尽王臣。
     * 人之生矣有贵贱，贵人长为天恩眷。
     * 人生富贵总由天，草民之穷由天谴。
     * 忽有狂徒夜磨刀，帝星飘摇荧惑高。
     * 翻天覆地从今始，杀人何须惜手劳。
     * 不忠之人曰可杀！不孝之人曰可杀！
     * 不仁之人曰可杀！不义之人曰可杀！
     * 不礼不智不信人，大西王曰杀杀杀！
     * 我生不为逐鹿来，都门懒筑黄金台，
     * 状元百官都如狗，总是刀下觳觫材。
     * 传令麾下四王子，破城不须封刀匕。
     * 山头代天树此碑，逆天之人立死跪亦死！
     */
    private static boolean hasIdenticalParentMethodAndEnsureNotDirectlyImplementsInterface(Method method, Class<?> clazz) {
        Class<?> parent = clazz.getSuperclass();
        if (parent == null)
            return false;
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

    private static Map<TypeVariable<?>, Type> getGenericTypeReplacementForParentInterfaceMethods(Class<?> thisClass, Method thatMethod) {
        Class<?> targetClass = thatMethod.getDeclaringClass();

        Map<TypeVariable<?>, Type> replacement = new HashMap<>();
        if (Arrays.stream(thisClass.getInterfaces()).noneMatch(c -> c.equals(targetClass))) {
            Class<?> superInterface = Arrays.stream(thisClass.getInterfaces()).filter(targetClass::isAssignableFrom).findFirst().orElse(null);
            if (superInterface == null) return Map.of();
            Map<TypeVariable<?>, Type> parentType = getGenericTypeReplacementForParentInterfaceMethods(superInterface, thatMethod);
            Map<TypeVariable<?>, Type> parentReplacement = getInterfaceRemap(thisClass, superInterface);

            for (Map.Entry<TypeVariable<?>, Type> entry : parentType.entrySet()) {
                TypeVariable<?> variable = entry.getKey();
                Type type = entry.getValue();

                replacement.put(variable,
                        type instanceof TypeVariable<?> typeVariable ? parentReplacement.getOrDefault(typeVariable, typeVariable) : type
                );
            }
        } else {
            return getInterfaceRemap(thisClass, targetClass);
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
            return Map.of();
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
            } else if (clazz.isRecord()) {
                this.type = ClassType.RECORD;
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
