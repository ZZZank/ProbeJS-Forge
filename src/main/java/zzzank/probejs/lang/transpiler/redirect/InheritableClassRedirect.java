package zzzank.probejs.lang.transpiler.redirect;

import com.google.common.collect.ImmutableSet;
import zzzank.probejs.lang.java.type.TypeDescriptor;
import zzzank.probejs.lang.java.type.impl.ClassType;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.code.type.BaseType;

import java.util.*;
import java.util.function.Function;

/**
 * @author ZZZank
 */
public final class InheritableClassRedirect implements TypeRedirect {

    private final Set<Class<?>> targets;
    private final Function<ClassType, BaseType> mapper;

    public InheritableClassRedirect(Class<?> target, Function<ClassType, BaseType> mapper) {
        targets = Collections.singleton(target);
        this.mapper = Objects.requireNonNull(mapper);
    }

    public InheritableClassRedirect(Collection<Class<?>> targets, Function<ClassType, BaseType> mapper) {
        this.targets = ImmutableSet.copyOf(targets);
        this.mapper = mapper;
    }

    @Override
    public BaseType apply(TypeDescriptor typeDesc, TypeConverter converter) {
        return mapper.apply((ClassType) typeDesc);
    }

    @Override
    public boolean test(TypeDescriptor typeDescriptor, TypeConverter converter) {
        if (typeDescriptor instanceof ClassType classType) {
            Class<?> c = classType.clazz;
            while (c != null) {
                if (targets.contains(c)) {
                    return true;
                }
                c = c.getSuperclass();
            }
        }
        return false;
    }
}