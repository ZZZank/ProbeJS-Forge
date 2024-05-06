package com.probejs.document.type;

public class DocTypeRaw implements IDocType {

    private final String typeName;

    public DocTypeRaw(String typeName) {
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
