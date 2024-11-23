package zzzank.probejs.lang.transpiler.members;

import lombok.val;
import zzzank.probejs.lang.java.clazz.members.ConstructorInfo;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.typescript.code.member.ConstructorDecl;
import zzzank.probejs.lang.typescript.code.type.ts.TSVariableType;
import zzzank.probejs.utils.CollectUtils;

import java.util.ArrayList;

public class Constructor extends Converter<ConstructorInfo, ConstructorDecl> {
    private final Param param;

    public Constructor(TypeConverter converter) {
        super(converter);
        this.param = new Param(converter);
    }

    @Override
    public ConstructorDecl transpile(ConstructorInfo input) {
        val variableTypes = new ArrayList<TSVariableType>();
        for (val variableType : input.variableTypes) {
            variableTypes.add((TSVariableType) converter.convertType(variableType));
        }
        return new ConstructorDecl(
            variableTypes,
            CollectUtils.mapToList(input.params, param::transpile)
        );
    }
}
