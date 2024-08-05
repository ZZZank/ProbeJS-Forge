package zzzank.probejs.lang.typescript.code.member;

import org.jetbrains.annotations.Nullable;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.ts.FunctionDeclaration;
import zzzank.probejs.lang.typescript.code.ts.VariableDeclaration;
import zzzank.probejs.lang.typescript.code.ts.Wrapped;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.TSVariableType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InterfaceDecl extends ClassDecl {
    private final Wrapped.Namespace namespace;

    public InterfaceDecl(String name, @Nullable BaseType superClass, List<BaseType> interfaces, List<TSVariableType> variableTypes) {
        super(name, superClass, interfaces, variableTypes);
        this.namespace = new Wrapped.Namespace(name);

    }

    @Override
    public List<String> formatRaw(Declaration declaration) {
        for (MethodDecl method : methods) {
            method.isInterface = true;
        }
        // Format head - export interface name<T> extends ... {
        String head = String.format("export interface %s", name);
        if (!variableTypes.isEmpty()) {
            String variables = variableTypes.stream().map(type -> type.line(declaration, BaseType.FormatType.VARIABLE)).collect(Collectors.joining(", "));
            head = String.format("%s<%s>", head, variables);
        }
        if (!interfaces.isEmpty()) {
            String formatted = interfaces.stream().map(type -> type.line(declaration)).collect(Collectors.joining(", "));
            head = String.format("%s extends %s", head, formatted);
        }
        head = String.format("%s {", head);

        // Format body - fields, constructors, methods
        List<String> body = new ArrayList<>();

        for (FieldDecl field : fields) {
            // if (!field.isStatic) throw new RuntimeException("Why an interface can have a non-static field?");
            // Because ProbeJS can add non-static fields to it... And it's legal in TypeScript.
            namespace.addCode(new VariableDeclaration(
                    field.name,
                    field.type
            ));
        }
        body.add("");
        for (MethodDecl method : methods) {
            if (method.isStatic) {
                namespace.addCode(new FunctionDeclaration(
                    method.name,
                    method.variableTypes,
                    method.params,
                    method.returnType
                ));
            } else {
                body.addAll(method.format(declaration));
            }
        }

        // Adds a marker in it to prevent VSCode from not recognizing the namespace to import
        if (namespace.isEmpty()) {
            namespace.addCode(new Code() {
                @Override
                public Collection<ClassPath> getUsedClassPaths() {
                    return Collections.emptyList();
                }

                @Override
                public List<String> format(Declaration declaration) {
                    return Collections.singletonList("const probejs$$marker: never");
                }
            });
        }

        // tail - }
        List<String> tail = new ArrayList<>();
        for (Code code : bodyCode) {
            tail.addAll(code.format(declaration));
        }
        tail.add("}\n");

        // Concatenate them as a whole
        List<String> formatted = new ArrayList<>();
        formatted.add(head);
        formatted.addAll(body);
        formatted.addAll(tail);

        // Static methods and fields, adds it even if it's empty, so auto import can still discover it
        formatted.addAll(namespace.format(declaration));
        return formatted;
    }
}
