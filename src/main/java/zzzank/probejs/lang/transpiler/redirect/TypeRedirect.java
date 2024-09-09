package zzzank.probejs.lang.transpiler.redirect;

import zzzank.probejs.lang.java.type.TypeDescriptor;
import zzzank.probejs.lang.typescript.code.type.BaseType;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author ZZZank
 */
public interface TypeRedirect
    extends Predicate<TypeDescriptor>, Function<TypeDescriptor, BaseType> {
}
