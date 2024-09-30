package zzzank.probejs.lang.transpiler.transformation;

import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.member.MethodDecl;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.refer.ImportInfos;
import zzzank.probejs.utils.NameUtils;

import java.util.*;

public class InjectBeans implements ClassTransformer {
    @Override
    public void transform(Clazz clazz, ClassDecl classDecl) {
        Set<String> names = new HashSet<>();
        for (MethodDecl method : classDecl.methods) {
            names.add(method.name);
        }
        for (MethodDecl method : classDecl.methods) {
            if (method.isStatic) continue;
            if (method.name.startsWith("set") && method.params.size() == 1) {
                if (method.name.length() == 3) continue;
                String beanName = NameUtils.firstLower(method.name.substring(3));
                if (names.contains(beanName)) continue;
                classDecl.bodyCode.add(new BeanDecl(
                    "set %s(value: %s)",
                    beanName,
                    Types.ignoreContext(method.params.get(0).type, BaseType.FormatType.INPUT)
                ));
            } else if (method.params.isEmpty()) {
                if (method.name.startsWith("get")) {
                    if (method.name.length() == 3) continue;
                    String beanName = NameUtils.firstLower(method.name.substring(3));
                    if (names.contains(beanName)) continue;
                    classDecl.bodyCode.add(new BeanDecl("get %s(): %s", beanName, method.returnType));
                } else if (method.name.startsWith("is")) {
                    if (method.name.length() == 2) continue;
                    String beanName = NameUtils.firstLower(method.name.substring(2));
                    if (names.contains(beanName)) continue;
                    classDecl.bodyCode.add(new BeanDecl("get %s(): %s", beanName, Types.BOOLEAN));
                }
            }
        }
    }

    public static class BeanDecl extends Code {
        public String formattingString;
        public String name;
        public BaseType baseType;

        BeanDecl(String formattingString, String name, BaseType baseType) {
            this.formattingString = formattingString;
            this.name = name;
            this.baseType = baseType;
        }

        @Override
        public ImportInfos getImportInfos() {
            return baseType.getImportInfos();
        }

        @Override
        public List<String> format(Declaration declaration) {
            return Collections.singletonList(String.format(formattingString, ProbeJS.GSON.toJson(name), baseType.line(declaration)));
        }
    }
}
