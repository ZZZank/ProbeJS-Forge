package com.probejs.document.type;

import lombok.Getter;

import java.util.function.BiFunction;

@Getter
public class TypeArray implements IType {
    private final IType component;

    public TypeArray(IType component) {
        this.component = component;
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
