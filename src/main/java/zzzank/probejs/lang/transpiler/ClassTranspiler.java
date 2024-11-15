package zzzank.probejs.lang.transpiler;

import lombok.val;
import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.transpiler.members.Constructor;
import zzzank.probejs.lang.transpiler.members.Converter;
import zzzank.probejs.lang.transpiler.members.Field;
import zzzank.probejs.lang.transpiler.members.Method;
import zzzank.probejs.lang.transpiler.transformation.ClassTransformer;
import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.member.InterfaceDecl;
import zzzank.probejs.lang.typescript.code.type.ts.TSVariableType;
import zzzank.probejs.lang.typescript.code.type.Types;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ClassTranspiler extends Converter<Clazz, ClassDecl> {

    private final Method method;
    private final Field field;
    private final Constructor constructor;

    public ClassTranspiler(TypeConverter converter) {
        super(converter);
        this.method = new Method(converter);
        this.field = new Field(converter);
        this.constructor = new Constructor(converter);
    }

    @Override
    public ClassDecl transpile(Clazz input) {
        val variableTypes = new ArrayList<TSVariableType>(input.variableTypes.size());
        for (val variableType : input.variableTypes) {
            variableTypes.add((TSVariableType) converter.convertType(variableType));
        }
        val superClass = input.superClass == null ? null : converter.convertType(input.superClass);
        val interfaces = input.interfaces.stream()
            .map(converter::convertType)
            .filter(t -> t != Types.ANY)
            .collect(Collectors.toList());
        val decl = input.attribute.isInterface
            ? new InterfaceDecl(
            input.classPath.getName(),
            superClass == Types.ANY ? null : superClass,
            interfaces,
            variableTypes
        )
            : new ClassDecl(
                input.classPath.getName(),
                superClass == Types.ANY ? null : superClass,
                interfaces,
                variableTypes
            );

        for (val fieldInfo : input.fields) {
            val fieldDecl = field.transpile(fieldInfo);
            ClassTransformer.transformFields(fieldInfo, fieldDecl);
            decl.fields.add(fieldDecl);
        }

        for (val methodInfo : input.methods) {
            val methodDecl = method.transpile(methodInfo);
            ClassTransformer.transformMethods(input, methodInfo, methodDecl);
            decl.methods.add(methodDecl);
        }

        for (val constructorInfo : input.constructors) {
            val constructorDecl = constructor.transpile(constructorInfo);
            ClassTransformer.transformConstructors(constructorInfo, constructorDecl);
            decl.constructors.add(constructorDecl);
        }
        return decl;
    }
}
