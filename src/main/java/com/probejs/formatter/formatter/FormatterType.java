package com.probejs.formatter.formatter;

import com.probejs.formatter.NameResolver;
import com.probejs.formatter.NameResolver.ResolvedName;
import com.probejs.info.type.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class FormatterType {

    private final ITypeInfo typeInfo;
    private final boolean useSpecial;
    private final BiFunction<ITypeInfo, String, String> transformer;

    public FormatterType(
        ITypeInfo typeInfo,
        boolean useSpecial,
        BiFunction<ITypeInfo, String, String> transformer
    ) {
        this.typeInfo = typeInfo;
        this.useSpecial = useSpecial;
        this.transformer = transformer;
    }

    public FormatterType(ITypeInfo typeInfo, boolean useSpecial) {
        this(typeInfo, useSpecial, (t, s) -> s);
    }

    public FormatterType(ITypeInfo typeInfo) {
        this(typeInfo, true);
    }

    public String format(int indent, int stepIndent) {
        if (useSpecial) {
            Class<?> rawClass = typeInfo.getResolvedClass();
            if (NameResolver.specialTypeFormatters.containsKey(rawClass)) {
                return NameResolver.specialTypeFormatters.get(rawClass).apply(this.typeInfo);
            }
        }

        if (typeInfo instanceof TypeInfoClass) {
            return transformer.apply(
                typeInfo,
                NameResolver.getResolvedName(typeInfo.getTypeName()).getFullName()
            );
        }
        if (typeInfo instanceof TypeInfoWildcard) {
            return transformer.apply(
                typeInfo,
                new FormatterType(typeInfo.getBaseType(), useSpecial, transformer).format(indent, stepIndent)
            );
        }
        if (typeInfo instanceof TypeInfoVariable) {
            return transformer.apply(typeInfo, typeInfo.getTypeName());
        }
        if (typeInfo instanceof TypeInfoArray) {
            return transformer.apply(
                typeInfo,
                new FormatterType(typeInfo.getBaseType(), useSpecial, transformer)
                    .format(indent, stepIndent) +
                "[]"
            );
        }
        if (typeInfo instanceof TypeInfoParameterized) {
            TypeInfoParameterized parType = (TypeInfoParameterized) typeInfo;
            if (
                new FormatterType(parType.getBaseType(), useSpecial, transformer).format(0, 0).equals("any")
            ) {
                return transformer.apply(typeInfo, NameResolver.ResolvedName.UNRESOLVED.getFullName());
            }
            return transformer.apply(
                typeInfo,
                String.format(
                    "%s<%s>",
                    new FormatterType(parType.getBaseType(), useSpecial, transformer)
                        .format(indent, stepIndent),
                    parType
                        .getParamTypes()
                        .stream()
                        .map(p -> new FormatterType(p, useSpecial, transformer).format(indent, stepIndent))
                        .collect(Collectors.joining(", "))
                )
            );
        }
        return "any";
    }

    /**
     * similar to {@code new FormatterType(info,false).format(0,4)}, but with additional
     * processing for TypeInfoClass. If its getTypeVariables() is not returning an empty
     * list, a {@code <any,any,...>} style type variable representation will be added to
     * the end of formatted string
     */
    public static String formatParameterized(ITypeInfo info) {
        StringBuilder sb = new StringBuilder(new FormatterType(info, false).format(0, 0));
        if (info instanceof TypeInfoClass) {
            TypeInfoClass clazz = (TypeInfoClass) info;
            if (clazz.getTypeVariables().size() != 0) {
                sb.append(
                    String.format(
                        "<%s>",
                        clazz
                            .getTypeVariables()
                            .stream()
                            .map(ITypeInfo::getTypeName)
                            .map(NameResolver::getResolvedName)
                            .map(ResolvedName::getFullName)
                            .collect(Collectors.joining(","))
                    )
                );
            }
        }
        return sb.toString();
    }
}
