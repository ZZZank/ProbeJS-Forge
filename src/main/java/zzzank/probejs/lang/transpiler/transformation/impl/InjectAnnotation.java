package zzzank.probejs.lang.transpiler.transformation.impl;

import dev.latvian.mods.rhino.annotations.typing.JSInfo;
import lombok.val;
import zzzank.probejs.features.rhizo.RhizoState;
import zzzank.probejs.lang.java.base.AnnotationHolder;
import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.java.clazz.members.ConstructorInfo;
import zzzank.probejs.lang.java.clazz.members.FieldInfo;
import zzzank.probejs.lang.java.clazz.members.MethodInfo;
import zzzank.probejs.lang.transpiler.transformation.ClassTransformer;
import zzzank.probejs.lang.typescript.code.member.*;

import java.util.stream.Collectors;

public class InjectAnnotation implements ClassTransformer {

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

        if (RhizoState.INFO_ANNOTATION) {
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
    public void transformField(Clazz clazz, FieldInfo fieldInfo, FieldDecl decl) {
        applyInfo(fieldInfo, decl);
        if (fieldInfo.hasAnnotation(Deprecated.class)) {
            decl.newline("@deprecated");
        }
    }

    @Override
    public void transformConstructor(Clazz clazz, ConstructorInfo constructorInfo, ConstructorDecl decl) {
        applyInfo(constructorInfo, decl);
        if (constructorInfo.hasAnnotation(Deprecated.class)) {
            decl.newline("@deprecated");
        }
    }

    public void applyInfo(AnnotationHolder info, CommentableCode decl) {
        if (!RhizoState.INFO_ANNOTATION) {
            return;
        }
        for (JSInfo annotation : info.getAnnotations(JSInfo.class)) {
            decl.addComment(annotation.value());
        }
    }
}
