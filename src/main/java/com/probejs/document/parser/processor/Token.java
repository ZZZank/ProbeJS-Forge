package com.probejs.document.parser.processor;

/**
 * @author ZZZank
 */
public enum Token {
    NONE,
    ERROR,
    EOF,

    DECLARE,

    CONST,
    LET,
    VAR,

    CLASS,
    INTERFACE,
    TYPE,

    EXTENDS,
    IMPLEMENTS,

    SEMICOLON,

    TOKEN_END;

    public boolean is(Token another) {
        return this.ordinal() == another.ordinal();
    }
}
