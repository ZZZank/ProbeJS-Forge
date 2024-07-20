package zzzank.probejs.utils;

import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.TypeScriptFile;
import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.member.MethodDecl;
import zzzank.probejs.lang.typescript.code.member.ParamDecl;
import zzzank.probejs.lang.typescript.code.type.BaseType;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class DocUtils {
    public static void applyParam(TypeScriptFile file, Predicate<MethodDecl> test, int index, Consumer<ParamDecl> effect) {
        if (file == null) {
            return;
        }
        file.findCode(ClassDecl.class).ifPresent(classDecl -> {
            for (MethodDecl method : classDecl.methods) {
                if (test.test(method)) {
                    effect.accept(method.params.get(index));
                }
            }
        });
    }

    public static void replaceParamType(TypeScriptFile file, Predicate<MethodDecl> test, int index, BaseType toReplace) {
        applyParam(file, test, index, decl -> decl.type = toReplace);
        for (ClassPath usedClassPath : toReplace.getUsedClassPaths()) {
            file.declaration.addClass(usedClassPath);
        }
    }
}
