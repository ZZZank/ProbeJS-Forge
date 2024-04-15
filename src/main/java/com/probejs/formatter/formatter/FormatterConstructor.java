package com.probejs.formatter.formatter;

import com.probejs.formatter.NameResolver;
import com.probejs.info.ClassInfo;
import com.probejs.info.ConstructorInfo;
import com.probejs.info.MethodInfo;
import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeInfoClass;
import com.probejs.util.PUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormatterConstructor implements IFormatter {

    private final ConstructorInfo constructor;

    public FormatterConstructor(ConstructorInfo constructor) {
        this.constructor = constructor;
    }

    private String formatTypeParameterized(ITypeInfo info) {
        StringBuilder sb = new StringBuilder(new FormatterType(info).format());
        if (info instanceof TypeInfoClass) {
            TypeInfoClass clazz = (TypeInfoClass) info;
            ClassInfo classInfo = ClassInfo.ofCache(clazz.getResolvedClass());
            if (!classInfo.getTypeParameters().isEmpty()) {
                sb.append('<');
                sb.append(
                    classInfo
                        .getTypeParameters()
                        .stream()
                        .map(ITypeInfo::getTypeName)
                        .map(NameResolver::getResolvedName)
                        .map(NameResolver.ResolvedName::getFullName)
                        .collect(Collectors.joining(","))
                );
                sb.append('>');
            }
        }
        return sb.toString();
    }

    private String formatParams() {
        List<MethodInfo.ParamInfo> params = constructor.getParams();
        List<String> paramStrings = new ArrayList<>();
        for (MethodInfo.ParamInfo param : params) {
            paramStrings.add(
                String.format(
                    "%s: %s",
                    NameResolver.getNameSafe(param.getName()),
                    formatTypeParameterized(param.getType())
                )
            );
        }
        return String.join(", ", paramStrings);
    }

    @Override
    public List<String> format(int indent, int stepIndent) {
        List<String> lines = new ArrayList<>();
        lines.add(String.format("%sconstructor(%s);", PUtil.indent(indent), formatParams()));
        return lines;
    }
}
