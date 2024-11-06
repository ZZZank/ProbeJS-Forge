package zzzank.probejs.lang.transpiler.redirect;

import lombok.val;
import zzzank.probejs.docs.ClassWrapping;
import zzzank.probejs.lang.java.type.TypeDescriptor;
import zzzank.probejs.lang.java.type.impl.ClassType;
import zzzank.probejs.lang.java.type.impl.ParamType;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.TSParamType;
import zzzank.probejs.lang.typescript.code.type.TSVariableType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSPrimitiveType;

/**
 * @author ZZZank
 */
public class ClassRedirect implements TypeRedirect {

    @Override
    public BaseType apply(TypeDescriptor typeDescriptor, TypeConverter converter) {
        val converted = converter.convertTypeExcluding(typeDescriptor, this);
        if (!(converted instanceof TSParamType paramType)) {
            return converted;
        }
        val param = paramType.params.get(0);
        if (param instanceof JSPrimitiveType || param instanceof TSVariableType) {
            return converted;
        }
        return Types.and(
            converted,
            Types.typeOf(param)
        );
    }

    @Override
    public boolean test(TypeDescriptor typeDescriptor, TypeConverter converter) {
        return typeDescriptor instanceof ParamType paramType
            && paramType.params.size() == 1
            && paramType.base instanceof ClassType base
            && ClassWrapping.CONVERTIBLES.contains(base.clazz);
    }
}
