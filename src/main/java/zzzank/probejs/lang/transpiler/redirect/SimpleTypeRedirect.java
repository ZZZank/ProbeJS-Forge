package zzzank.probejs.lang.transpiler.redirect;

import com.google.common.collect.ImmutableSet;
import zzzank.probejs.lang.java.type.TypeDescriptor;
import zzzank.probejs.lang.java.type.impl.ClassType;
import zzzank.probejs.lang.typescript.code.type.BaseType;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

/**
 * only redirects {@link ClassType}
 *
 * @author ZZZank
 */
public class SimpleTypeRedirect implements TypeRedirect {

    public final ImmutableSet<Class<?>> targets;
    public final Function<ClassType, BaseType> mapper;

    public SimpleTypeRedirect(Class<?> target, Function<ClassType, BaseType> mapper) {
        targets = ImmutableSet.of(target);
        this.mapper = Objects.requireNonNull(mapper);
    }

    public SimpleTypeRedirect(Collection<Class<?>> targets, Function<ClassType, BaseType> mapper) {
        this.targets = ImmutableSet.copyOf(targets);
        this.mapper = mapper;
    }

    @Override
    public BaseType apply(TypeDescriptor typeDesc) {
        return mapper.apply((ClassType) typeDesc);
    }

    @Override
    public boolean test(TypeDescriptor typeDescriptor) {
        return typeDescriptor instanceof ClassType classType && targets.contains(classType.clazz);
    }
}
