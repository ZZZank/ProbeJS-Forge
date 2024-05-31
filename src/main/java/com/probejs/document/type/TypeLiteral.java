package com.probejs.document.type;

/**
 * string literal
 * @author ZZZank
 */
public class TypeLiteral implements DocType {

    private final String literal;

    public TypeLiteral(String literal) {
        this.literal = literal;
    }

    public String getRawTypeName() {
        return literal;
    }

    @Override
    public String getTypeName() {
        return literal;
    }
}
