package zzzank.probejs.docs;

import lombok.val;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.java.type.TypeDescriptor;
import zzzank.probejs.lang.java.type.impl.ClassType;
import zzzank.probejs.lang.java.type.impl.ParamType;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.transpiler.redirect.TypeRedirect;
import zzzank.probejs.lang.typescript.code.type.*;
import zzzank.probejs.lang.typescript.code.type.js.JSPrimitiveType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ZZZank
 */
public class ClassWrapping extends ProbeJSPlugin {

    public static final Set<Class<?>> CONVERTIBLES = new HashSet<>();

    static {
        CONVERTIBLES.add(Class.class);
    }

    @Override
    public void addPredefinedTypes(TypeConverter converter) {
        converter.addTypeRedirect(new ClassWrapperRedirect());
    }

    public static class ClassWrapperRedirect implements TypeRedirect {

        @Override
        public BaseType apply(TypeDescriptor typeDescriptor, TypeConverter converter) {
            val converted = converter.convertTypeExcluding(typeDescriptor, this);
            if (!(converted instanceof TSParamType paramType)) {
                ProbeJS.LOGGER.error("a ParamType type desc is converted to a not-param doc type, skipping modification");
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
                && CONVERTIBLES.contains(base.clazz);
        }
    }
}
