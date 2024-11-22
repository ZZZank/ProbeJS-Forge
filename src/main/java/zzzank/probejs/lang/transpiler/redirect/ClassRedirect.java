package zzzank.probejs.lang.transpiler.redirect;

import lombok.val;
import zzzank.probejs.docs.GlobalClasses;
import zzzank.probejs.lang.java.type.TypeDescriptor;
import zzzank.probejs.lang.java.type.impl.ClassType;
import zzzank.probejs.lang.java.type.impl.ParamType;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.ts.TSParamType;
import zzzank.probejs.lang.typescript.code.type.ts.TSVariableType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSPrimitiveType;
import zzzank.probejs.lang.typescript.refer.ImportInfo;

import java.util.Set;
import java.util.function.Function;

/**
 * @author ZZZank
 */
public class ClassRedirect implements TypeRedirect {

    private final Set<Class<?>> convertibles;

    public ClassRedirect(Set<Class<?>> convertibles) {
        this.convertibles = convertibles;
    }

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
        val selector = (Function<BaseType.FormatType, BaseType>) formatType -> switch (formatType) {
            case INPUT -> Types.or(converted, Types.parameterized(GlobalClasses.J_CLASS, param));
            case VARIABLE -> converted;
            case RETURN -> Types.and(converted, Types.typeOf(param));
        };
        return Types.custom(
            (declaration, formatType) -> selector.apply(formatType).line(declaration, formatType),
            (type) -> selector.apply(type).getImportInfos(type)
        );
    }

    @Override
    public boolean test(TypeDescriptor typeDescriptor, TypeConverter converter) {
        return typeDescriptor instanceof ParamType paramType
            && paramType.params.size() == 1
            && paramType.base instanceof ClassType base
            && convertibles.contains(base.clazz);
    }
}
