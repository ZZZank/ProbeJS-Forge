package com.probejs.info.clazz;

import com.probejs.ProbeJS;
import com.probejs.formatter.resolver.ClazzFilter;
import com.probejs.formatter.resolver.NameResolver;
import com.probejs.formatter.FormatterMethod;
import com.probejs.info.type.IType;
import com.probejs.info.type.TypeParameterized;
import com.probejs.info.type.TypeVariable;
import com.probejs.info.type.TypeResolver;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
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
        ClassInfo cInfo = ALL.get(clazz);
        if (cInfo != null) {
            return cInfo;
        }
        cInfo = new ClassInfo(clazz);
        ALL.put(clazz, cInfo);
        return cInfo;
    }

    private final Class<?> raw;
    private final String name;
    private final int modifiers;
    private final boolean isInterface;
    private final boolean isFunctionalInterface;
    private final List<TypeVariable> typeParameters;
    private final List<MethodInfo> methodInfos;
    private final List<FieldInfo> fieldInfos;
    private final List<ConstructorInfo> constructors;
    private final ClassInfo superClass;
    private final IType superType;
    private final List<IType> interfaces;

    private ClassInfo(Class<?> clazz) {
        this.raw = clazz;
        this.name = raw.getName();
        this.modifiers = raw.getModifiers();
        this.isInterface = raw.isInterface();
        this.superClass = ofCache(raw.getSuperclass());
        this.superType = TypeResolver.resolveType(clazz.getGenericSuperclass());

        this.interfaces = new ArrayList<>(0);
        this.constructors = new ArrayList<>(0);
        this.typeParameters = new ArrayList<>(0);
        this.methodInfos = new ArrayList<>(0);
        this.fieldInfos = new ArrayList<>(0);
        try {
            interfaces.addAll(
                Arrays
                    .stream(clazz.getGenericInterfaces())
                    .map(TypeResolver::resolveType)
                    .collect(Collectors.toList())
            );
            constructors.addAll(
                Arrays
                    .stream(raw.getConstructors())
                    .map(ConstructorInfo::new)
                    .collect(Collectors.toList())
            );
            typeParameters.addAll(
                Arrays
                    .stream(raw.getTypeParameters())
                    .map(TypeVariable::new)
                    .collect(Collectors.toList())
            );
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
                .forEach(methodInfos::add);
            //fields
            Arrays
                .stream(raw.getFields())
                .map(f -> new FieldInfo(f, clazz))
                .filter(fInfo -> !ProbeJS.CONFIG.trimming || fInfo.getRaw().getDeclaringClass() == raw)
                .filter(f -> ClazzFilter.acceptField(f.getName()))
                .filter(f -> !f.isShouldHide())
                .forEach(fieldInfos::add);
        } catch (NoClassDefFoundError e) {
            // https://github.com/ZZZank/ProbeJS-Forge/issues/2
            ProbeJS.LOGGER.error("Unable to fetch infos for class '{}'", raw.getName());
            ProbeJS.LOGGER.error(e);
        }
        //Resolve types - rollback everything till Object
        applySuperGenerics(methodInfos, fieldInfos);
        //Functional Interfaces
        List<MethodInfo> abstracts =
            this.methodInfos.stream().filter(MethodInfo::isAbstract).collect(Collectors.toList());
        this.isFunctionalInterface = isInterface && abstracts.size() == 1;
        if (this.isFunctionalInterface) {
            NameResolver.addSpecialAssignments(this.raw, () -> {
                FormatterMethod formatterLambda = new FormatterMethod(abstracts.get(0));
                String lambdaStr = String.format("((%s)=>%s)",
                    formatterLambda.formatParams(Collections.emptyMap(), true),
                    formatterLambda.formatReturn()
                );
                return Collections.singletonList(lambdaStr);
            });
        }
    }

    private static Map<String, IType> resolveTypeOverrides(IType typeInfo) {
        Map<String, IType> caughtTypes = new HashMap<>();
        if (typeInfo instanceof TypeParameterized) {
            val parType = (TypeParameterized) typeInfo;
            val rawClassNames = Arrays
                .stream(parType.getResolvedClass().getTypeParameters())
                .map(TypeVariable::new)
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
                .map(TypeResolver::resolveType)
                .map(ClassInfo::resolveTypeOverrides)
                .forEach(m -> applyGenerics(m, methodsToMutate, fieldsToMutate));
            //Step to next level
            superClass.applySuperGenerics(methodsToMutate, fieldsToMutate);
            //Rewind
            applyGenerics(internalGenericMap, methodsToMutate, fieldsToMutate);
            Arrays
                .stream(raw.getGenericInterfaces())
                .map(TypeResolver::resolveType)
                .map(ClassInfo::resolveTypeOverrides)
                .forEach(m -> applyGenerics(m, methodsToMutate, fieldsToMutate));
        }
        applyInterfaceGenerics(methodsToMutate, fieldsToMutate);
    }

    private void applyInterfaceGenerics(List<MethodInfo> methodsToMutate, List<FieldInfo> fieldsToMutate) {
        //Apply current level changes
        Arrays
            .stream(raw.getGenericInterfaces())
            .map(TypeResolver::resolveType)
            .map(ClassInfo::resolveTypeOverrides)
            .forEach(m -> applyGenerics(m, methodsToMutate, fieldsToMutate));
        //Step to next level
        interfaces.forEach(i -> ofCache(i.getResolvedClass()).applyInterfaceGenerics(methodsToMutate, fieldsToMutate));
        //Rewind
        Arrays
            .stream(raw.getGenericInterfaces())
            .map(TypeResolver::resolveType)
            .map(ClassInfo::resolveTypeOverrides)
            .forEach(m -> applyGenerics(m, methodsToMutate, fieldsToMutate));
    }

    private static void applyGenerics(
        Map<String, IType> internalGenericMap,
        List<MethodInfo> methodInfo,
        List<FieldInfo> fieldInfo
    ) {
        for (MethodInfo method : methodInfo) {
            Map<String, IType> maskedNames = new HashMap<>();
            method
                .getTypeVariables()
                .stream()
                .filter(i -> i instanceof TypeVariable)
                .map(i -> (TypeVariable) i)
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
