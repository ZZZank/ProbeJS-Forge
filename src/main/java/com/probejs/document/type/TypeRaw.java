package com.probejs.document.type;

public class TypeRaw implements IType {

    private final String typeName;

    public TypeRaw(String typeName) {
        this.typeName = typeName;
    }

    public String getRawTypeName() {
        return typeName;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }
}
