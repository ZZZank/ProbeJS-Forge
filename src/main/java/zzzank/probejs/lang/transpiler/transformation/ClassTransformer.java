package zzzank.probejs.lang.transpiler.transformation;

import lombok.val;
import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.java.clazz.members.ConstructorInfo;
import zzzank.probejs.lang.java.clazz.members.FieldInfo;
import zzzank.probejs.lang.java.clazz.members.MethodInfo;
import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.member.ConstructorDecl;
import zzzank.probejs.lang.typescript.code.member.FieldDecl;
import zzzank.probejs.lang.typescript.code.member.MethodDecl;
import zzzank.probejs.plugin.ProbeJSPlugins;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Accepts a Clazz and a transpiled TS file, modify the
 * file to respect some stuffs.
 */
public interface ClassTransformer {

    static ClassTransformer fromPlugin() {
        val transformers = new ArrayList<ClassTransformer>();
        Consumer<ClassTransformer> registration = t -> transformers.add(Objects.requireNonNull(t));
        ProbeJSPlugins.forEachPlugin(p -> p.registerClassTransformer(registration));
        return new TransformerSequence(transformers.toArray(new ClassTransformer[0]));
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
