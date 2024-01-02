package com.probejs.formatter.formatter;

import com.probejs.formatter.NameResolver;
import com.probejs.info.ClassInfo;
import com.probejs.info.ConstructorInfo;
import com.probejs.info.MethodInfo;
import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeInfoClass;
import com.probejs.util.PUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FormatterConstructor implements IFormatter {

    private final ConstructorInfo constructor;

    public FormatterConstructor(ConstructorInfo constructor) {
        this.constructor = constructor;
    }

    private String formatTypeParameterized(ITypeInfo info) {
        StringBuilder sb = new StringBuilder(new FormatterType(info).format(0, 0));
        if (info instanceof TypeInfoClass) {
            TypeInfoClass clazz = (TypeInfoClass) info;
            ClassInfo classInfo = ClassInfo.getOrCache(clazz.getResolvedClass());
            if (classInfo.getParameters().size() != 0) sb.append(
                String.format(
                    "<%s>",
                    String.join(", ", Collections.nCopies(classInfo.getParameters().size(), "any"))
                )
            );
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
    public List<String> format(Integer indent, Integer stepIndent) {
        List<String> formatted = new ArrayList<>();
        formatted.add(PUtil.indent(indent) + String.format("constructor(%s);", formatParams()));
        return formatted;
    }
}
