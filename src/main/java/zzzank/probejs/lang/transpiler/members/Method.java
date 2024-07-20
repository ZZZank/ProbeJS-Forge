package zzzank.probejs.lang.transpiler.members;

import zzzank.probejs.lang.java.clazz.members.MethodInfo;
import zzzank.probejs.lang.java.type.impl.VariableType;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.code.member.MethodDecl;
import zzzank.probejs.lang.typescript.code.type.TSVariableType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Method extends Converter<MethodInfo, MethodDecl> {
    private final Param param;

    public Method(TypeConverter converter) {
        super(converter);
        this.param = new Param(converter);
    }

    @Override
    public MethodDecl transpile(MethodInfo input) {
        List<TSVariableType> variableTypes = new ArrayList<>();
        for (VariableType variableType : input.variableTypes) {
            variableTypes.add((TSVariableType) converter.convertType(variableType));
        }
        MethodDecl decl = new MethodDecl(
                input.name,
                variableTypes,
                input.params.stream().map(this.param::transpile).collect(Collectors.toList()),
                converter.convertType(input.returnType)
        );
        decl.isAbstract = input.attributes.isAbstract;
        decl.isStatic = input.attributes.isStatic;

        return decl;
    }
}
