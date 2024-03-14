package com.probejs.info;

import com.probejs.ProbeConfig;
import com.probejs.formatter.ClassResolver;
import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.InfoTypeResolver;
import com.probejs.info.type.TypeInfoParameterized;
import com.probejs.info.type.TypeInfoVariable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassInfo {

    public static final Map<Class<?>, ClassInfo> CLASS_CACHE = new HashMap<>();

    public static ClassInfo ofCache(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        //No computeIfAbsent because new ClassInfo will call ofCache for superclass lookup
        //This will cause a CME because multiple updates occurred in one computeIfAbsent
        if (CLASS_CACHE.containsKey(clazz)) {
            return CLASS_CACHE.get(clazz);
        }
        ClassInfo info = new ClassInfo(clazz);
        CLASS_CACHE.put(clazz, info);
        return info;
    }

    public static ClassInfo of(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return new ClassInfo(clazz);
    }

    private final Class<?> clazzRaw;
    private final String name;
    private final int modifiers;
    private final boolean isInterface;
    private final boolean isFunctionalInterface;
    private final List<ITypeInfo> parameters;
    private final List<MethodInfo> methodInfo;
    private final List<FieldInfo> fieldInfo;
    private final List<ConstructorInfo> constructorInfo;
    private final ClassInfo superClass;
    private final List<ClassInfo> interfaces;

    private ClassInfo(Class<?> clazz) {
        clazzRaw = clazz;
        name = clazzRaw.getName();
        modifiers = clazzRaw.getModifiers();
        isInterface = clazzRaw.isInterface();
        isFunctionalInterface = clazzRaw.isAnnotationPresent(FunctionalInterface.class);
        constructorInfo =
            Arrays.stream(clazzRaw.getConstructors()).map(ConstructorInfo::new).collect(Collectors.toList());
        superClass = ofCache(clazzRaw.getSuperclass());
        interfaces =
            Arrays.stream(clazzRaw.getInterfaces()).map(ClassInfo::ofCache).collect(Collectors.toList());

        parameters =
            Arrays
                .stream(clazzRaw.getTypeParameters())
                .map(InfoTypeResolver::resolveType)
                .collect(Collectors.toList());

        // declared methods include public/protected/private methods, but exclude inherited ones
        Set<Method> declaredMethods = new HashSet<>();
        if (ProbeConfig.INSTANCE.trimming) {
            declaredMethods.addAll(Arrays.asList(clazzRaw.getDeclaredMethods()));
        }
        methodInfo =
            Arrays
                .stream(clazzRaw.getMethods())
                .filter(method -> !ProbeConfig.INSTANCE.trimming || declaredMethods.contains(method))
                .map(m -> new MethodInfo(m, clazz))
                .filter(m -> ClassResolver.acceptMethod(m.getName()))
                .filter(m -> !m.shouldHide())
                .collect(Collectors.toList());

        Set<Field> declaredFields = new HashSet<>();
        if (ProbeConfig.INSTANCE.trimming) {
            declaredFields.addAll(Arrays.asList(clazzRaw.getDeclaredFields()));
        }
        fieldInfo =
            Arrays
                .stream(clazzRaw.getFields())
                .filter(field -> !ProbeConfig.INSTANCE.trimming || declaredFields.contains(field))
                .map(FieldInfo::new)
                .filter(f -> ClassResolver.acceptField(f.getName()))
                .filter(f -> !f.shouldHide())
                .collect(Collectors.toList());

        //Resolve types - rollback everything till Object
        applySuperGenerics(methodInfo, fieldInfo);
    }

    private static Map<String, ITypeInfo> resolveTypeOverrides(ITypeInfo typeInfo) {
        Map<String, ITypeInfo> caughtTypes = new HashMap<>();
        if (typeInfo instanceof TypeInfoParameterized) {
            TypeInfoParameterized parType = (TypeInfoParameterized) typeInfo;
            List<ITypeInfo> rawClassNames = Arrays
                .stream(parType.getResolvedClass().getTypeParameters())
                .map(InfoTypeResolver::resolveType)
                .collect(Collectors.toList());
            List<ITypeInfo> parTypeNames = parType.getParamTypes();
            for (int i = 0; i < parTypeNames.size(); i++) {
                caughtTypes.put(rawClassNames.get(i).getTypeName(), parTypeNames.get(i));
            }
        }
        return caughtTypes;
    }

    private void applySuperGenerics(List<MethodInfo> methodsToMutate, List<FieldInfo> fieldsToMutate) {
        if (superClass != null) {
            //Apply current level changes
            ITypeInfo typeInfo = InfoTypeResolver.resolveType(clazzRaw.getGenericSuperclass());
            Map<String, ITypeInfo> internalGenericMap = resolveTypeOverrides(typeInfo);
            applyGenerics(internalGenericMap, methodsToMutate, fieldsToMutate);
            Arrays
                .stream(clazzRaw.getGenericInterfaces())
                .map(InfoTypeResolver::resolveType)
                .map(ClassInfo::resolveTypeOverrides)
                .forEach(m -> applyGenerics(m, methodsToMutate, fieldsToMutate));
            //Step to next level
            superClass.applySuperGenerics(methodsToMutate, fieldsToMutate);
            //Rewind
            applyGenerics(internalGenericMap, methodsToMutate, fieldsToMutate);
            Arrays
                .stream(clazzRaw.getGenericInterfaces())
                .map(InfoTypeResolver::resolveType)
                .map(ClassInfo::resolveTypeOverrides)
                .forEach(m -> applyGenerics(m, methodsToMutate, fieldsToMutate));
        }
        applyInterfaceGenerics(methodsToMutate, fieldsToMutate);
    }

    private void applyInterfaceGenerics(List<MethodInfo> methodsToMutate, List<FieldInfo> fieldsToMutate) {
        //Apply current level changes
        Arrays
            .stream(clazzRaw.getGenericInterfaces())
            .map(InfoTypeResolver::resolveType)
            .map(ClassInfo::resolveTypeOverrides)
            .forEach(m -> applyGenerics(m, methodsToMutate, fieldsToMutate));
        //Step to next level
        interfaces.forEach(i -> i.applyInterfaceGenerics(methodsToMutate, fieldsToMutate));
        //Rewind
        Arrays
            .stream(clazzRaw.getGenericInterfaces())
            .map(InfoTypeResolver::resolveType)
            .map(ClassInfo::resolveTypeOverrides)
            .forEach(m -> applyGenerics(m, methodsToMutate, fieldsToMutate));
    }

    private static void applyGenerics(
        Map<String, ITypeInfo> internalGenericMap,
        List<MethodInfo> methodInfo,
        List<FieldInfo> fieldInfo
    ) {
        for (MethodInfo method : methodInfo) {
            Map<String, ITypeInfo> maskedNames = new HashMap<>();
            method
                .getTypeVariables()
                .stream()
                .filter(i -> i instanceof TypeInfoVariable)
                .map(i -> (TypeInfoVariable) i)
                .forEach(v -> {
                    maskedNames.put(v.getTypeName(), v);
                    v.setUnderscored(true);
                });

            method.setReturnType(InfoTypeResolver.mutateTypeMap(method.getReturnType(), maskedNames));
            method
                .getParams()
                .forEach(p -> p.setTypeInfo(InfoTypeResolver.mutateTypeMap(p.getType(), maskedNames)));

            method.setReturnType(InfoTypeResolver.mutateTypeMap(method.getReturnType(), internalGenericMap));
            method
                .getParams()
                .forEach(p -> p.setTypeInfo(InfoTypeResolver.mutateTypeMap(p.getType(), internalGenericMap)));
        }
        for (FieldInfo field : fieldInfo) {
            field.setTypeInfo(InfoTypeResolver.mutateTypeMap(field.getType(), internalGenericMap));
        }
    }

    public boolean isInterface() {
        return isInterface;
    }

    public boolean isFunctionalInterface() {
        return isFunctionalInterface;
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(modifiers);
    }

    public ClassInfo getSuperClass() {
        return superClass;
    }

    public List<ClassInfo> getInterfaces() {
        return interfaces;
    }

    public List<FieldInfo> getFieldInfo() {
        return fieldInfo;
    }

    public List<ConstructorInfo> getConstructorInfo() {
        return constructorInfo;
    }

    public List<MethodInfo> getMethodInfo() {
        return methodInfo;
    }

    public List<ITypeInfo> getParameters() {
        return parameters;
    }

    public boolean isEnum() {
        return clazzRaw.isEnum();
    }

    public Class<?> getClazzRaw() {
        return clazzRaw;
    }

    public String getName() {
        return name;
    }
}
