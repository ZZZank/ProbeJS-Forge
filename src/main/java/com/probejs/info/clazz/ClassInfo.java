package com.probejs.info.clazz;

import com.probejs.ProbeJS;
import com.probejs.formatter.resolver.ClazzFilter;
import com.probejs.formatter.resolver.PathResolver;
import com.probejs.formatter.FormatterMethod;
import com.probejs.info.type.JavaType;
import com.probejs.info.type.JavaTypeParameterized;
import com.probejs.info.type.JavaTypeVariable;
import com.probejs.info.type.TypeResolver;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Getter
public class ClassInfo implements Comparable<ClassInfo> {

    public static final Map<Class<?>, ClassInfo> ALL = new HashMap<>();

    public static ClassInfo ofCache(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        //No computeIfAbsent because new ClassInfo will call ofCache for superclass lookup
        //This will cause a CME because multiple updates occurred in one computeIfAbsent
        val cInfo = ALL.get(clazz);
        if (cInfo != null) {
            return cInfo;
        }
        return new ClassInfo(clazz, true);
    }

    private final Class<?> raw;
    private final String name;
    private final int modifiers;
    private final List<Annotation> annotations;
    private final boolean isInterface;
    private final boolean isFunctionalInterface;
    private final List<JavaTypeVariable> typeParameters;
    private final List<MethodInfo> methods;
    private final List<FieldInfo> fields;
    private final List<ConstructorInfo> constructors;
    private final ClassInfo superClass;
    private final JavaType superType;
    private final List<JavaType> interfaces;

    private ClassInfo(Class<?> clazz, boolean putInCache) {
        if (putInCache) {
            ALL.put(clazz, this);
        }
        this.raw = clazz;
        this.name = raw.getName();
        this.modifiers = raw.getModifiers();
        this.isInterface = raw.isInterface();
        this.superClass = ofCache(raw.getSuperclass());
        this.superType = TypeResolver.resolve(clazz.getGenericSuperclass());
        this.interfaces = Arrays
            .stream(clazz.getGenericInterfaces())
            .map(TypeResolver::resolve)
            .collect(Collectors.toList());
        this.typeParameters = Arrays
            .stream(raw.getTypeParameters())
            .map(JavaTypeVariable::new)
            .collect(Collectors.toList());
        this.annotations = Arrays.asList(raw.getAnnotations());

        this.constructors = new ArrayList<>(0);
        this.methods = new ArrayList<>(0);
        this.fields = new ArrayList<>(0);
        try {
            //constructors
            for (val constructor : raw.getConstructors()) {
                constructors.add(new ConstructorInfo(constructor));
            }
            //methods
            Arrays
                .stream(raw.getMethods())
                .map(m -> new MethodInfo(m, clazz))
                .filter(mInfo -> {
                    if (!ProbeJS.CONFIG.trimming) {
                        return true;
                    }
                    return !hasIdenticalParentMethod(mInfo.getRaw(), clazz);
                })
                .filter(m -> ClazzFilter.acceptMethod(m.getName()))
                .filter(m -> !m.isShouldHide())
                .forEach(methods::add);
            //fields
            Arrays
                .stream(raw.getFields())
                .map(f -> new FieldInfo(f, clazz))
                .filter(fInfo -> !ProbeJS.CONFIG.trimming || fInfo.getRaw().getDeclaringClass() == raw)
                .filter(f -> ClazzFilter.acceptField(f.getName()))
                .filter(f -> !f.isShouldHide())
                .forEach(fields::add);
        } catch (NoClassDefFoundError e) {
            // https://github.com/ZZZank/ProbeJS-Forge/issues/2
            ProbeJS.LOGGER.error("Unable to fetch infos for class '{}'", raw.getName());
            e.printStackTrace();
        }
        //Resolve types - rollback everything till Object
        applySuperGenerics(methods, fields);
        //Functional Interfaces
        val abstracts =
            this.methods.stream().filter(MethodInfo::isAbstract).collect(Collectors.toList());
        this.isFunctionalInterface = isInterface && abstracts.size() == 1;
        //type alias: Functional Interfaces
        if (this.isFunctionalInterface) {
            PathResolver.addSpecialAssignments(this.raw, () -> {
                val formatterLambda = new FormatterMethod(abstracts.get(0));
                val lambdaStr = String.format("((%s)=>%s)",
                    formatterLambda.formatParams(Collections.emptyMap(), true),
                    formatterLambda.formatReturn()
                );
                return Collections.singletonList(lambdaStr);
            });
        }
        //type alias: Enum
        if (isEnum()) {
            Supplier<List<String>> assign = () -> {
                try {
                    val values = clazz.getMethod("values");
                    values.setAccessible(true);
                    val enumValues = (Object[]) values.invoke(null);
                    //Use the name() method here so won't be affected by overrides
                    val name = Enum.class.getMethod("name");
                    return Arrays.stream(enumValues)
                        .map(obj -> {
                            try {
                                return name.invoke(obj);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .map(String::toLowerCase)
                        .map(ProbeJS.GSON::toJson)
                        .collect(Collectors.toList());
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                return Collections.emptyList();
            };
            PathResolver.addSpecialAssignments(raw, assign);
        }
    }

    private static Map<String, JavaType> resolveTypeOverrides(JavaType typeInfo) {
        Map<String, JavaType> caughtTypes = new HashMap<>();
        if (typeInfo instanceof JavaTypeParameterized parType) {
            val rawClassNames = Arrays
                .stream(parType.getResolvedClass().getTypeParameters())
                .map(JavaTypeVariable::new)
                .collect(Collectors.toList());
            val paramTypes = parType.getParamTypes();
            for (int i = 0; i < paramTypes.size(); i++) {
                caughtTypes.put(rawClassNames.get(i).getTypeName(), paramTypes.get(i));
            }
        }
        return caughtTypes;
    }

    private void applySuperGenerics(List<MethodInfo> methodsToMutate, List<FieldInfo> fieldsToMutate) {
        if (superClass != null) {
            //Apply current level changes
            val internalGenericMap = resolveTypeOverrides(this.superType);
            applyGenerics(internalGenericMap, methodsToMutate, fieldsToMutate);
            Arrays
                .stream(raw.getGenericInterfaces())
                .map(TypeResolver::resolve)
                .map(ClassInfo::resolveTypeOverrides)
                .forEach(m -> applyGenerics(m, methodsToMutate, fieldsToMutate));
            //Step to next level
            superClass.applySuperGenerics(methodsToMutate, fieldsToMutate);
            //Rewind
            applyGenerics(internalGenericMap, methodsToMutate, fieldsToMutate);
            Arrays
                .stream(raw.getGenericInterfaces())
                .map(TypeResolver::resolve)
                .map(ClassInfo::resolveTypeOverrides)
                .forEach(m -> applyGenerics(m, methodsToMutate, fieldsToMutate));
        }
        applyInterfaceGenerics(methodsToMutate, fieldsToMutate);
    }

    private void applyInterfaceGenerics(List<MethodInfo> methodsToMutate, List<FieldInfo> fieldsToMutate) {
        //Apply current level changes
        Arrays
            .stream(raw.getGenericInterfaces())
            .map(TypeResolver::resolve)
            .map(ClassInfo::resolveTypeOverrides)
            .forEach(m -> applyGenerics(m, methodsToMutate, fieldsToMutate));
        //Step to next level
        interfaces.forEach(i -> ofCache(i.getResolvedClass()).applyInterfaceGenerics(methodsToMutate, fieldsToMutate));
        //Rewind
        Arrays
            .stream(raw.getGenericInterfaces())
            .map(TypeResolver::resolve)
            .map(ClassInfo::resolveTypeOverrides)
            .forEach(m -> applyGenerics(m, methodsToMutate, fieldsToMutate));
    }

    private static void applyGenerics(
        Map<String, JavaType> internalGenericMap,
        List<MethodInfo> methodInfo,
        List<FieldInfo> fieldInfo
    ) {
        for (MethodInfo method : methodInfo) {
            Map<String, JavaType> maskedNames = new HashMap<>();
            method
                .getTypeVariables()
                .stream()
                .filter(i -> i instanceof JavaTypeVariable)
                .map(i -> (JavaTypeVariable) i)
                .forEach(v -> maskedNames.put(v.getTypeName(), v));

            method.setType(TypeResolver.mutateTypeMap(method.getType(), maskedNames));
            method
                .getParams()
                .forEach(p -> p.setType(TypeResolver.mutateTypeMap(p.getType(), maskedNames)));

            method.setType(TypeResolver.mutateTypeMap(method.getType(), internalGenericMap));
            method
                .getParams()
                .forEach(p -> p.setType(TypeResolver.mutateTypeMap(p.getType(), internalGenericMap)));
        }
        for (FieldInfo field : fieldInfo) {
            field.setType(TypeResolver.mutateTypeMap(field.getType(), internalGenericMap));
        }
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(modifiers);
    }

    public boolean isEnum() {
        return raw.isEnum();
    }

    /**
     * seems not working for parameterized interfaces
     */
    private static boolean hasIdenticalParentMethod(Method method, Class<?> clazz) {
        if (method.isDefault()) {
            return false;
        }
        for (Class<?> parent = clazz.getSuperclass(); parent != null; parent = parent.getSuperclass()) {
            try {
                Method parentMethod = parent.getMethod(method.getName(), method.getParameterTypes());
                // seems not working for interfaces, e.g. RecipeFilter
                return parentMethod.getGenericReturnType().equals(method.getGenericReturnType());
            } catch (NoSuchMethodException ignored) {}
        }
        return false;
    }

    @Override
    public int compareTo(@NotNull ClassInfo o) {
        return this.name.compareTo(o.name);
    }
}
