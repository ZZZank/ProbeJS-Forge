package zzzank.probejs.lang.transpiler.redirect;

import zzzank.probejs.lang.java.type.TypeDescriptor;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.code.type.BaseType;

import java.util.function.Function;

/**
 * @author ZZZank
 */
public interface TypeRedirect {

    boolean test(TypeDescriptor typeDescriptor, TypeConverter converter);

    BaseType apply(TypeDescriptor typeDescriptor, TypeConverter adapter);
}
