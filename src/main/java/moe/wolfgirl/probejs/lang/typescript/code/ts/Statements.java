package moe.wolfgirl.probejs.lang.typescript.code.ts;

import moe.wolfgirl.probejs.lang.typescript.code.member.ClassDecl;

public interface Statements {
    static FunctionDeclaration.Builder function(String name) {
        return new FunctionDeclaration.Builder(name);
    }

    static ClassDecl.Builder clazz(String name) {
        return new ClassDecl.Builder(name);
    }
}
