package com.probejs.formatter.formatter;

import com.probejs.formatter.NameResolver;
import com.probejs.formatter.NameResolver.ResolvedName;
import com.probejs.info.type.*;

import java.util.function.BiFunction;
import java.util.function.Function;
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

    public String format() {
        if (useSpecial) {
            Class<?> rawClass = typeInfo.getResolvedClass();
            Function<ITypeInfo, String> special = NameResolver.specialTypeFormatters.get(rawClass);
            if (special != null) {
                return special.apply(this.typeInfo);
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
                new FormatterType(typeInfo.getBaseType(), useSpecial, transformer).format()
            );
        }
        if (typeInfo instanceof TypeInfoVariable) {
            TypeInfoVariable vInfo = (TypeInfoVariable) typeInfo;
            String bounds = vInfo.getBounds()
                .stream()
                .map(FormatterType::new)
                .map(FormatterType::format)
                .filter(str -> !str.equals("any"))
                .collect(Collectors.joining(","));
            String name = vInfo.getTypeName();
            if (!bounds.isEmpty()) {
                name = String.format("%s extends %s", name, bounds);
            }
            return transformer.apply(typeInfo, name);
        }
        if (typeInfo instanceof TypeInfoArray) {
            return transformer.apply(
                typeInfo,
                new FormatterType(typeInfo.getBaseType(), useSpecial, transformer)
                    .format() +
                    "[]"
            );
        }
        if (typeInfo instanceof TypeInfoParameterized) {
            TypeInfoParameterized parType = (TypeInfoParameterized) typeInfo;
            String raw = new FormatterType(parType.getBaseType(), useSpecial, transformer).format();
            if (raw.equals("any")) {
                return transformer.apply(typeInfo, "any");
            }
            return transformer.apply(
                typeInfo,
                String.format(
                    "%s<%s>",
                    raw,
                    parType
                        .getParamTypes()
                        .stream()
                        .map(p -> new FormatterType(p, useSpecial, transformer).format())
                        .collect(Collectors.joining(", "))
                )
            );
        }
        return ResolvedName.UNRESOLVED.getFullName();
    }
}
