package zzzank.probejs.lang.typescript.code.member;

import lombok.val;
import org.jetbrains.annotations.Nullable;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.Code;
import zzzank.probejs.lang.typescript.code.member.clazz.ConstructorBuilder;
import zzzank.probejs.lang.typescript.code.member.clazz.MethodBuilder;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.ts.TSVariableType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.refer.ImportInfos;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The Class name in the ClassDecl must be the name of its corresponding classpath.
 */
public class ClassDecl extends CommentableCode {
    public final String name;
    @Nullable
    public BaseType superClass;
    public final List<BaseType> interfaces;
    public final List<TSVariableType> variableTypes;

    public boolean isAbstract = false;
    public boolean isNative = true;

    public final List<FieldDecl> fields = new ArrayList<>();
    public final List<ConstructorDecl> constructors = new ArrayList<>();
    public final List<MethodDecl> methods = new ArrayList<>();

    /**
     * Reserved field to inject custom code body
     */
    public final List<Code> bodyCode = new ArrayList<>();

    public ClassDecl(
        String name,
        @Nullable BaseType superClass,
        List<BaseType> interfaces,
        List<TSVariableType> variableTypes
    ) {
        this.name = name;
        this.superClass = superClass;
        this.interfaces = interfaces;
        this.variableTypes = variableTypes;
    }

    @Override
    public ImportInfos getImportInfos() {
        val infos = ImportInfos.of()
            .fromCodes(fields)
            .fromCodes(constructors)
            .fromCodes(methods)
            .fromCodes(interfaces)
            .fromCodes(variableTypes)
            .fromCodes(bodyCode);
        if (superClass != null) {
            infos.addAll(superClass.getImportInfos(null));
        }
        return infos;
    }

    @Override
    public List<String> formatRaw(Declaration declaration) {
        // Format head - export abstract (class / interface) name<T> extends ... implements ... {
        List<String> modifiers = new ArrayList<>();
        modifiers.add("export");
        if (isAbstract) {
            modifiers.add("abstract");
        }
        modifiers.add("class");

        String head = String.format("%s %s", String.join(" ", modifiers), name);
        if (!variableTypes.isEmpty()) {
            String variables = variableTypes.stream()
                .map(type -> type.line(declaration, BaseType.FormatType.VARIABLE))
                .collect(Collectors.joining(", "));
            head = String.format("%s<%s>", head, variables);
        }

        if (superClass != null) {
            head = String.format("%s extends %s", head, superClass.line(declaration));
        }
        if (interfaces.size() != 0) {
            String formatted = interfaces.stream()
                .map(type -> type.line(declaration))
                .collect(Collectors.joining(", "));
            head = String.format("%s implements %s", head, formatted);
        }
        head = String.format("%s {", head);

        // Format body - fields, constructors, methods
        List<String> body = new ArrayList<>();

        for (val field : fields) {
            body.addAll(field.format(declaration));
        }
        body.add("");
        for (val constructor : constructors) {
            body.addAll(constructor.format(declaration));
        }
        body.add("");
        for (val method : methods) {
            body.addAll(method.format(declaration));
        }

        // tail - custom code, }
        List<String> tail = new ArrayList<>();
        for (Code code : bodyCode) {
            tail.addAll(code.format(declaration));
        }
        tail.add("}");

        // Concatenate them as a whole
        List<String> formatted = new ArrayList<>();
        formatted.add(head);
        formatted.addAll(body);
        formatted.addAll(tail);
        return formatted;
    }

    public static class Builder {
        public final String name;
        @Nullable
        public BaseType superClass = null;
        public final List<BaseType> interfaces = new ArrayList<>();
        public final List<TSVariableType> variableTypes = new ArrayList<>();

        public boolean isAbstract = false;
        public boolean isInterface = false;

        public final List<FieldDecl> fields = new ArrayList<>();
        public final List<ConstructorDecl> constructors = new ArrayList<>();
        public final List<MethodDecl> methods = new ArrayList<>();

        public Builder(String name) {
            this.name = name;
        }

        public Builder abstractClass() {
            this.isAbstract = true;
            return this;
        }

        public Builder interfaceClass() {
            this.isInterface = true;
            return this;
        }

        public Builder field(String symbol, BaseType baseType) {
            return field(symbol, baseType, false);
        }

        public Builder field(String symbol, BaseType baseType, boolean isStatic) {
            return field(symbol, baseType, isStatic, false);
        }

        public Builder field(String symbol, BaseType baseType, boolean isStatic, boolean isFinal) {
            var field = new FieldDecl(symbol, baseType);
            field.isStatic = isStatic;
            field.isFinal = isFinal;
            fields.add(field);
            return this;
        }

        public Builder superClass(BaseType superClass) {
            this.superClass = superClass;
            return this;
        }

        public Builder interfaces(BaseType... interfaces) {
            this.interfaces.addAll(Arrays.asList(interfaces));
            return this;
        }

        public Builder typeVariables(String... symbols) {
            for (String symbol : symbols) {
                typeVariables(Types.generic(symbol));
            }
            return this;
        }

        public Builder typeVariables(TSVariableType... variableTypes) {
            this.variableTypes.addAll(Arrays.asList(variableTypes));
            return this;
        }

        public Builder method(String name, Consumer<MethodBuilder> method) {
            MethodBuilder builder = new MethodBuilder(name);
            method.accept(builder);
            methods.add(builder.buildAsMethod());
            return this;
        }

        public Builder ctor(Consumer<ConstructorBuilder> constructor) {
            ConstructorBuilder builder = new ConstructorBuilder();
            constructor.accept(builder);
            constructors.add(builder.buildAsConstructor());
            return this;
        }

        public ClassDecl build() {
            var decl = isInterface ? new InterfaceDecl(
                name,
                superClass,
                interfaces,
                variableTypes
            ) : new ClassDecl(
                name,
                superClass,
                interfaces,
                variableTypes
            );
            decl.isAbstract = isAbstract;
            decl.methods.addAll(methods);
            decl.fields.addAll(fields);
            decl.constructors.addAll(constructors);
            decl.isNative = false;

            decl.addComment("""
                This is a class generated by ProbeJS, you shall not load/require this class for your usages
                because it doesn't exist in the JVM. The class exist only for type hinting purpose.
                Loading the class will not throw an error, but instead the class loaded will be undefined.
                """);
            return decl;
        }
    }
}
