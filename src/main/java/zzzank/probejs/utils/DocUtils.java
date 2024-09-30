package zzzank.probejs.utils;

import lombok.val;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.TypeScriptFile;
import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.member.MethodDecl;
import zzzank.probejs.lang.typescript.code.member.ParamDecl;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.refer.ImportInfo;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class DocUtils {
    public static void applyParam(TypeScriptFile file, Predicate<MethodDecl> test, int index, Consumer<ParamDecl> effect) {
        if (file == null) {
            return;
        }
        val code = file.findCode(ClassDecl.class);
        if (code.isPresent()) {
            for (MethodDecl method : code.get().methods) {
                if (test.test(method)) {
                    effect.accept(method.params.get(index));
                }
            }
        }
    }

    public static void replaceParamType(TypeScriptFile file, Predicate<MethodDecl> test, int index, BaseType toReplace) {
        applyParam(file, test, index, decl -> decl.type = toReplace);
        for (val info : toReplace.getImportInfos()) {
            file.declaration.addImport(info);
        }
    }
}
