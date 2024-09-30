package zzzank.probejs.docs;

import lombok.val;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.java.type.TypeDescriptor;
import zzzank.probejs.lang.java.type.impl.ClassType;
import zzzank.probejs.lang.java.type.impl.ParamType;
import zzzank.probejs.lang.java.type.impl.VariableType;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.transpiler.redirect.TypeRedirect;
import zzzank.probejs.lang.typescript.code.type.*;
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
        converter.addTypeRedirect(new ClassWrapperRedirect(converter));
    }

    public static class ClassWrapperRedirect implements TypeRedirect {

        private final TypeConverter converter;

        public ClassWrapperRedirect(TypeConverter converter) {
            this.converter = converter;
        }

        @Override
        public BaseType apply(TypeDescriptor typeDescriptor) {
            val paramType = ((ParamType) typeDescriptor);
            val param1 = paramType.params.get(0);

            val convertedParam = converter.convertType(param1);
            val raw = Types.parameterized(
                converter.convertType(paramType.base),
                convertedParam
            );
            if (convertedParam instanceof TSVariableType) {
                //should be blocked by test(), why happening
                return raw;
            }
            val typeOf = Types.typeOf(convertedParam);
            return new CustomType(
                (declaration, formatType) -> {
                    val t = switch (formatType) {
                        case INPUT -> raw.or(typeOf);
                        case VARIABLE -> raw;
                        case RETURN -> raw.and(typeOf);
                    };
                    return t.line(declaration, formatType);
                },
                paramType.getClassPaths().toArray(new ClassPath[0])
            );
        }

        @Override
        public boolean test(TypeDescriptor typeDescriptor) {
            return typeDescriptor instanceof ParamType paramType
                && paramType.params.size() == 1
                && !(paramType.params.get(0) instanceof VariableType)
                && paramType.base instanceof ClassType base
                && CONVERTIBLES.contains(base.clazz);
        }
    }
}
