package zzzank.probejs.lang.transpiler.transformation;

import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.java.clazz.members.ConstructorInfo;
import zzzank.probejs.lang.java.clazz.members.FieldInfo;
import zzzank.probejs.lang.java.clazz.members.MethodInfo;
import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.member.ConstructorDecl;
import zzzank.probejs.lang.typescript.code.member.FieldDecl;
import zzzank.probejs.lang.typescript.code.member.MethodDecl;

/**
 * Accepts a Clazz and a transpiled TS file, modify the
 * file to respect some stuffs.
 */
public interface ClassTransformer {
    ClassTransformer[] CLASS_TRANSFORMERS = new ClassTransformer[]{
            new InjectAnnotation(),
            new InjectArray(),
            new InjectBeans(),
//            new InjectSelf(),
            new InjectSpecialType(),
    };

    static void transformClass(Clazz clazz, ClassDecl classDecl) {
        for (ClassTransformer classTransformer : CLASS_TRANSFORMERS) {
            classTransformer.transform(clazz, classDecl);
        }
    }

    static void transformMethods(Clazz clazz, MethodInfo methodInfo, MethodDecl methodDecl) {
        for (ClassTransformer classTransformer : CLASS_TRANSFORMERS) {
            classTransformer.transformMethod(clazz, methodInfo, methodDecl);
        }
    }

    static void transformConstructors(ConstructorInfo constructorInfo, ConstructorDecl constructorDecl) {
        for (ClassTransformer classTransformer : CLASS_TRANSFORMERS) {
            classTransformer.transformConstructor(constructorInfo, constructorDecl);
        }
    }

    static void transformFields(FieldInfo fieldInfo, FieldDecl fieldDecl) {
        for (ClassTransformer classTransformer : CLASS_TRANSFORMERS) {
            classTransformer.transformField(fieldInfo, fieldDecl);
        }
    }

    default void transform(Clazz clazz, ClassDecl classDecl) {
    }

    default void transformMethod(Clazz clazz, MethodInfo methodInfo, MethodDecl methodDecl) {

    }

    default void transformConstructor(ConstructorInfo constructorInfo, ConstructorDecl constructorDecl) {

    }

    default void transformField(FieldInfo fieldInfo, FieldDecl fieldDecl) {

    }
}
