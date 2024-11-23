package zzzank.probejs.lang.transpiler.redirect;

import dev.latvian.mods.rhino.annotations.typing.Generics;
import lombok.val;
import zzzank.probejs.features.rhizo.RhizoState;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.java.type.TypeDescriptor;
import zzzank.probejs.lang.java.type.impl.ParamType;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.ts.TSClassType;
import zzzank.probejs.lang.typescript.code.type.ts.TSParamType;
import zzzank.probejs.utils.CollectUtils;

/**
 * @author ZZZank
 */
public class RhizoGenericRedirect implements TypeRedirect {

    @Override
    public boolean test(TypeDescriptor typeDescriptor, TypeConverter converter) {
        return RhizoState.GENERIC_ANNOTATION
            && typeDescriptor instanceof ParamType
            && typeDescriptor.hasAnnotation(Generics.class);
    }

    @Override
    public BaseType apply(TypeDescriptor typeDescriptor, TypeConverter converter) {
        val paramType = (ParamType) typeDescriptor;
        val annot = typeDescriptor.getAnnotation(Generics.class);
        val baseType = annot.base() == Object.class
            ? converter.convertType(paramType.base)
            : Types.type(annot.base());
        val params = CollectUtils.mapToList(
            annot.value(),
            converter::convertType
        );
        return new TSParamType(baseType, params);
    }
}
