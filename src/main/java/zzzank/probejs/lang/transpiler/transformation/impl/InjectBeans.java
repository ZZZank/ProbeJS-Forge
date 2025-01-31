package zzzank.probejs.lang.transpiler.transformation.impl;

import lombok.val;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.transpiler.transformation.ClassTransformer;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.refer.ImportInfos;
import zzzank.probejs.utils.NameUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InjectBeans implements ClassTransformer {
    @Override
    public void transform(Clazz clazz, ClassDecl classDecl) {
        Set<String> names = new HashSet<>();
        for (val method : classDecl.methods) {
            names.add(method.name);
        }
        for (val method : classDecl.methods) {
            if (method.isStatic) {
                continue;
            }
            if (method.name.startsWith("set") && method.params.size() == 1) {
                if (method.name.length() == 3) {
                    continue;
                }
                val beanName = NameUtils.firstLower(method.name.substring(3));
                if (names.contains(beanName)) {
                    continue;
                }
                classDecl.bodyCode.add(new BeanDecl(
                    "set %s(value: %s)",
                    beanName,
                    Types.contextShield(method.params.get(0).type, BaseType.FormatType.INPUT)
                ));
            } else if (method.params.isEmpty()) {
                if (method.name.startsWith("get")) {
                    if (method.name.length() == 3) {
                        continue;
                    }
                    val beanName = NameUtils.firstLower(method.name.substring(3));
                    if (names.contains(beanName)) {
                        continue;
                    }
                    classDecl.bodyCode.add(new BeanDecl("get %s(): %s", beanName, method.returnType));
                } else if (method.name.startsWith("is")) {
                    if (method.name.length() == 2) {
                        continue;
                    }
                    val beanName = NameUtils.firstLower(method.name.substring(2));
                    if (names.contains(beanName)) {
                        continue;
                    }
                    classDecl.bodyCode.add(new BeanDecl("get %s(): %s", beanName, Types.BOOLEAN));
                }
            }
        }
    }

    public static class BeanDecl extends Code {
        public String format;
        public String name;
        public BaseType type;

        BeanDecl(String format, String name, BaseType type) {
            this.format = format;
            this.name = name;
            this.type = type;
        }

        @Override
        public ImportInfos getImportInfos() {
            return type.getImportInfos(BaseType.FormatType.RETURN);
        }

        @Override
        public List<String> format(Declaration declaration) {
            return Collections.singletonList(String.format(
                format,
                ProbeJS.GSON.toJson(name),
                type.line(declaration, BaseType.FormatType.RETURN)
            ));
        }
    }
}
