package zzzank.probejs.lang.transpiler.members;

import zzzank.probejs.lang.java.clazz.members.ConstructorInfo;
import zzzank.probejs.lang.java.type.impl.VariableType;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.code.member.ConstructorDecl;
import zzzank.probejs.lang.typescript.code.type.ts.TSVariableType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Constructor extends Converter<ConstructorInfo, ConstructorDecl> {
    private final Param param;

    public Constructor(TypeConverter converter) {
        super(converter);
        this.param = new Param(converter);
    }

    @Override
    public ConstructorDecl transpile(ConstructorInfo input) {
        List<TSVariableType> variableTypes = new ArrayList<>();
        for (VariableType variableType : input.variableTypes) {
            variableTypes.add((TSVariableType) converter.convertType(variableType));
        }
        return new ConstructorDecl(
                variableTypes,
                input.params.stream().map(param::transpile).collect(Collectors.toList())
        );
    }
}
