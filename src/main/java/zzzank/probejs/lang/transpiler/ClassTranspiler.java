package zzzank.probejs.lang.transpiler;

import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.java.clazz.members.ConstructorInfo;
import zzzank.probejs.lang.java.clazz.members.FieldInfo;
import zzzank.probejs.lang.java.clazz.members.MethodInfo;
import zzzank.probejs.lang.java.type.impl.VariableType;
import zzzank.probejs.lang.transpiler.members.Constructor;
import zzzank.probejs.lang.transpiler.members.Converter;
import zzzank.probejs.lang.transpiler.members.Field;
import zzzank.probejs.lang.transpiler.members.Method;
import zzzank.probejs.lang.transpiler.transformation.ClassTransformer;
import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.member.InterfaceDecl;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.TSVariableType;
import zzzank.probejs.lang.typescript.code.type.Types;

import java.util.ArrayList;
import java.util.List;
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
        List<TSVariableType> variableTypes = new ArrayList<>();
        for (VariableType variableType : input.variableTypes) {
            variableTypes.add((TSVariableType) converter.convertType(variableType));
        }
        BaseType superClass = input.superClass == null ? null : converter.convertType(input.superClass);
        ClassDecl decl = input.attribute.isInterface ?
            new InterfaceDecl(
                input.classPath.getName(),
                superClass == Types.ANY ? null : superClass,
                input.interfaces.stream()
                    .map(converter::convertType)
                    .filter(t -> t != Types.ANY)
                    .collect(Collectors.toList()),
                variableTypes
            ) :
            new ClassDecl(
                input.classPath.getName(),
                superClass == Types.ANY ? null : superClass,
                input.interfaces.stream()
                    .map(converter::convertType)
                    .filter(t -> t != Types.ANY)
                    .collect(Collectors.toList()),
                variableTypes
            );

        for (FieldInfo fieldInfo : input.fields) {
            var fieldDecl = field.transpile(fieldInfo);
            ClassTransformer.transformFields(fieldInfo, fieldDecl);
            decl.fields.add(fieldDecl);
        }

        for (MethodInfo methodInfo : input.methods) {
            var methodDecl = method.transpile(methodInfo);
            ClassTransformer.transformMethods(input, methodInfo, methodDecl);
            decl.methods.add(methodDecl);
        }

        for (ConstructorInfo constructorInfo : input.constructors) {
            var constructorDecl = constructor.transpile(constructorInfo);
            ClassTransformer.transformConstructors(constructorInfo, constructorDecl);
            decl.constructors.add(constructorDecl);
        }
        return decl;
    }
}
