package com.probejs.formatter;

import com.probejs.formatter.api.MultiFormatter;
import com.probejs.formatter.resolver.NameResolver;
import com.probejs.formatter.resolver.SpecialTypes;
import com.probejs.info.clazz.ConstructorInfo;
import com.probejs.info.type.IType;
import com.probejs.info.type.TypeClass;
import com.probejs.util.PUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormatterConstructor implements MultiFormatter {

    private final ConstructorInfo constructor;

    public FormatterConstructor(ConstructorInfo constructor) {
        this.constructor = constructor;
    }

    private String formatType(IType info) {
        return FormatterType.of(info)
            .format()
            .concat(info instanceof TypeClass clazz ? SpecialTypes.attachedTypeVar(clazz) : "");
    }

    private String formatParams() {
        return constructor
            .getParams()
            .stream()
            .map(param -> String.format("%s: %s",
                NameResolver.getNameSafe(param.getName()),
                formatType(param.getType())
            ))
            .collect(Collectors.joining(", "));
    }

    @Override
    public List<String> formatLines(int indent, int stepIndent) {
        List<String> lines = new ArrayList<>();
        lines.add(String.format("%sconstructor(%s);", PUtil.indent(indent), formatParams()));
        return lines;
    }
}
