package zzzank.probejs.docs.recipes;

import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.js.JSLambdaType;

/**
 * @author ZZZank
 */
public class RecipeLambdaBuilder extends JSLambdaType.Builder {

    @Override
    public RecipeLambdaBuilder param(String symbol, BaseType type, boolean isOptional) {
        return (RecipeLambdaBuilder) super.param(symbol, type, isOptional);
    }

    @Override
    public RecipeLambdaBuilder param(String symbol, BaseType type, boolean isOptional, boolean isVarArg) {
        return (RecipeLambdaBuilder) super.param(symbol, type, isOptional, isVarArg);
    }

    @Override
    public RecipeLambdaBuilder param(String symbol, BaseType type) {
        return (RecipeLambdaBuilder) super.param(symbol, type);
    }

    public RecipeLambdaBuilder input(BaseType type) {
        return param("input", type);
    }

    public RecipeLambdaBuilder inputs(BaseType type) {
        return param("inputs", type);
    }

    public RecipeLambdaBuilder output(BaseType type) {
        return param("output", type);
    }

    public RecipeLambdaBuilder outputs(BaseType type) {
        return param("outputs", type);
    }
}
