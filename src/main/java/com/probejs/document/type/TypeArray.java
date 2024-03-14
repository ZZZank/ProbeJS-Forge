package com.probejs.document.type;

import java.util.function.BiFunction;

public class TypeArray implements IType {
    private final IType component;

    public TypeArray(IType component) {
        this.component = component;
    }

    public IType getComponent() {
        return component;
    }

    @Override
    public String getTypeName() {
        return component.getTypeName() + "[]";
    }

    @Override
    public String transform(BiFunction<IType, String, String> transformer) {
        return transformer.apply(this, component.transform(transformer) + "[]");
    }
}
