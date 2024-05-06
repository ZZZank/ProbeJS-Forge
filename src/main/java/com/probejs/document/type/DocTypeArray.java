package com.probejs.document.type;

import lombok.Getter;

import java.util.function.BiFunction;

@Getter
public class DocTypeArray implements IDocType {
    private final IDocType component;

    public DocTypeArray(IDocType component) {
        this.component = component;
    }

    @Override
    public String getTypeName() {
        return component.getTypeName() + "[]";
    }

    @Override
    public String transform(BiFunction<IDocType, String, String> transformer) {
        return transformer.apply(this, component.transform(transformer) + "[]");
    }
}
