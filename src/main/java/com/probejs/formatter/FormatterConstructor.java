package com.probejs.formatter;

import com.probejs.formatter.api.MultiFormatter;
import com.probejs.formatter.resolver.NameResolver;
import com.probejs.info.clazz.ClassInfo;
import com.probejs.info.clazz.ConstructorInfo;
import com.probejs.info.clazz.MethodInfo;
import com.probejs.info.type.IType;
import com.probejs.info.type.TypeClass;
import com.probejs.util.PUtil;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FormatterConstructor implements MultiFormatter {

    private final ConstructorInfo constructor;

    public FormatterConstructor(ConstructorInfo constructor) {
        this.constructor = constructor;
    }

    private String formatTypeParameterized(IType info) {
        val sb = new StringBuilder(FormatterType.of(info).format());
        if (info instanceof TypeClass) {
            val clazz = (TypeClass) info;
            val classInfo = ClassInfo.ofCache(clazz.getResolvedClass());
            if (!classInfo.getTypeParameters().isEmpty()) {
                sb.append('<');
                sb.append(
                    classInfo
                        .getTypeParameters()
                        .stream()
                        .map(IType::getTypeName)
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
    public List<String> formatLines(int indent, int stepIndent) {
        List<String> lines = new ArrayList<>();
        lines.add(String.format("%sconstructor(%s);", PUtil.indent(indent), formatParams()));
        return lines;
    }
}
