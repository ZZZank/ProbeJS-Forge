package zzzank.probejs.lang.transpiler.transformation;

import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.member.ClassDecl;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.TSParamType;

import java.util.*;

/**
 * Inject [Symbol.iterator](): IterableIterator<T>; for Iterable.
 * <br>
 * Inject [index: number]: T; for List<T>.
 * <br>
 * Inject [index: string | number]: V; for Map<K, V>.
 */
public class InjectArray implements ClassTransformer {

    static class FormattedLine extends Code {
        private final String line;
        private final BaseType type;

        FormattedLine(String line, BaseType type) {
            this.line = line;
            this.type = type;
        }

        @Override
        public Collection<ClassPath> getUsedClassPaths() {
            return type.getUsedClassPaths();
        }

        @Override
        public List<String> format(Declaration declaration) {
            return Collections.singletonList(String.format(line, type.line(declaration, BaseType.FormatType.RETURN)));
        }
    }

    @Override
    public void transform(Clazz clazz, ClassDecl classDecl) {
        if (isDirectlyImplementing(clazz.original, Iterable.class)) {
            BaseType iterType = classDecl.methods.stream()
                .filter(m -> m.name.equals("iterator"))
                .filter(m -> m.returnType instanceof TSParamType)
                .map(m -> ((TSParamType) m.returnType).params.get(0))
                .findFirst()
                .orElse(null);
            if (iterType == null) {
                return;
            }

            classDecl.bodyCode.add(new FormattedLine("[Symbol.iterator](): IterableIterator<%s>;", iterType));
        }

        // AbstractCollection is not a List, and AbstractList is not directly implementing Iterable
        if (isDirectlyImplementing(clazz.original, List.class)) {
            BaseType iterType = classDecl.methods.stream()
                .filter(m -> m.name.equals("iterator") && m.params.isEmpty())
                .filter(m -> m.returnType instanceof TSParamType)
                .map(m -> ((TSParamType) m.returnType).params.get(0))
                .findFirst()
                .orElse(null);
            if (iterType == null) {
                return;
            }
            classDecl.bodyCode.add(new FormattedLine("[index: number]: %s", iterType));
        }


        if (isDirectlyImplementing(clazz.original, Map.class)) {
            BaseType valueType = classDecl.methods.stream()
                .filter(m -> m.name.equals("get") && m.params.size() == 1)
                .map(m -> m.returnType)
                .findFirst()
                .orElse(null);
            if (valueType == null) {
                return;
            }
            classDecl.bodyCode.add(new FormattedLine("[index: string | number]: %s", valueType));
        }
    }

    private boolean isDirectlyImplementing(Class<?> toExamine, Class<?> target) {
        if (!target.isAssignableFrom(toExamine)) return false;
        Class<?> superClass = toExamine.getSuperclass();
        if (superClass == null || superClass == Object.class) return true;
        return !target.isAssignableFrom(superClass);
    }
}
