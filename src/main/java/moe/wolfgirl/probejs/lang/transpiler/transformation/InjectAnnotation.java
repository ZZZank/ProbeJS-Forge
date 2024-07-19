package moe.wolfgirl.probejs.lang.transpiler.transformation;

import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import lombok.val;
import moe.wolfgirl.probejs.lang.java.base.AnnotationHolder;
import moe.wolfgirl.probejs.lang.java.clazz.Clazz;
import moe.wolfgirl.probejs.lang.java.clazz.members.ConstructorInfo;
import moe.wolfgirl.probejs.lang.java.clazz.members.FieldInfo;
import moe.wolfgirl.probejs.lang.java.clazz.members.MethodInfo;
import moe.wolfgirl.probejs.lang.typescript.code.member.*;
import moe.wolfgirl.probejs.utils.ReflectUtils;

import java.util.stream.Collectors;

public class InjectAnnotation implements ClassTransformer {

    public static final boolean RHIZO_AVAILABLE = ReflectUtils.classExist("dev.latvian.mods.rhino.annotations.typing.JSInfo");

    @Override
    public void transform(Clazz clazz, ClassDecl classDecl) {
        applyInfo(clazz, classDecl);
        if (clazz.hasAnnotation(Deprecated.class)) {
            classDecl.newline("@deprecated");
        }
    }

    @Override
    public void transformMethod(Clazz clazz, MethodInfo methodInfo, MethodDecl decl) {
        applyInfo(methodInfo, decl);
        if (methodInfo.hasAnnotation(Deprecated.class)) {
            decl.newline("@deprecated");
        }

        if (RHIZO_AVAILABLE) {
            val paramLines = methodInfo.params.stream()
                .filter(p -> p.hasAnnotation(JSInfo.class))
                .map(p -> String.format("@param %s - %s", p.name, p.getAnnotation(JSInfo.class).value()))
                .collect(Collectors.toList());
            if (!paramLines.isEmpty()) {
                decl.linebreak();
                for (String line : paramLines) {
                    decl.addComment(line);
                }
            }
        }
    }

    @Override
    public void transformField(FieldInfo fieldInfo, FieldDecl decl) {
        applyInfo(fieldInfo, decl);
        if (fieldInfo.hasAnnotation(Deprecated.class)) {
            decl.newline("@deprecated");
        }
    }

    @Override
    public void transformConstructor(ConstructorInfo constructorInfo, ConstructorDecl decl) {
        applyInfo(constructorInfo, decl);
        if (constructorInfo.hasAnnotation(Deprecated.class)) {
            decl.newline("@deprecated");
        }
    }

    public void applyInfo(AnnotationHolder info, CommentableCode decl) {
        if (!RHIZO_AVAILABLE) {
            return;
        }
        for (JSInfo annotation : info.getAnnotations(JSInfo.class)) {
            decl.addComment(annotation.value());
        }
    }
}
