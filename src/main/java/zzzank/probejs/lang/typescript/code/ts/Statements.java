package zzzank.probejs.lang.typescript.code.ts;

import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.member.ConstructorDecl;
import zzzank.probejs.lang.typescript.code.member.MethodDecl;

public interface Statements {
    static FunctionDeclaration.Builder func(String name) {
        return new FunctionDeclaration.Builder(name);
    }

    static ClassDecl.Builder clazz(String name) {
        return new ClassDecl.Builder(name);
    }

    static ConstructorDecl.Builder ctor() {
        return new ConstructorDecl.Builder();
    }

    static MethodDecl.Builder method(String name) {
        return new MethodDecl.Builder(name);
    }
}
