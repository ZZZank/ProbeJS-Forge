package zzzank.probejs.lang.typescript.code.ts;

import zzzank.probejs.lang.typescript.code.member.ClassDecl;

public interface Statements {
    static FunctionDeclaration.Builder func(String name) {
        return new FunctionDeclaration.Builder(name);
    }

    static ClassDecl.Builder clazz(String name) {
        return new ClassDecl.Builder(name);
    }
}
