package zzzank.probejs.lang.typescript.code.member;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.Nullable;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.code.ts.VariableDeclaration;
import zzzank.probejs.lang.typescript.code.ts.Wrapped;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.ts.TSVariableType;
import zzzank.probejs.lang.typescript.refer.ImportType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Setter
@Accessors(chain = true)
public class InterfaceDecl extends ClassDecl {

    public boolean withStatic = true;
    public boolean withNamespace = true;

    public InterfaceDecl(String name, @Nullable BaseType superClass, List<BaseType> interfaces, List<TSVariableType> variableTypes) {
        super(name, superClass, interfaces, variableTypes);
    }

    public ClassDecl createStaticClass(List<MethodDecl> staticMethods) {
        val classDecl = new ClassDecl(
            ImportType.STATIC.fmt(this.name),
            null,
            Collections.singletonList(Types.primitive(this.name)),
            this.variableTypes
        );
        //methods will at the original interface
        classDecl.methods.addAll(staticMethods);
        classDecl.fields.addAll(fields);
        return classDecl;
    }

    public Wrapped.Namespace createNamespace(List<MethodDecl> staticMethods) {
        val namespace = new Wrapped.Namespace(this.name);
        for (val field : fields) {
            // if (!field.isStatic) throw new RuntimeException("Why an interface can have a non-static field?");
            // Because ProbeJS can add non-static fields to it... And it's legal in TypeScript.
            namespace.addCode(field.asVariableDecl());
        }
        for (val method : staticMethods) {
            namespace.addCode(method.asFunctionDecl());
        }
        // Adds a marker in it to prevent VSCode from not recognizing the namespace to import
        if (namespace.isEmpty()) {
            namespace.addCode(new VariableDeclaration("probejs$$marker", Types.NEVER));
        }
        return namespace;
    }

    @Override
    public List<String> formatRaw(Declaration declaration) {
        for (val method : methods) {
            method.isInterface = true;
        }

        // Format head - export interface name<T> extends ... {
        String head = String.format("export interface %s", name);
        if (!variableTypes.isEmpty()) {
            head += Types.join(", ", "<", ">", variableTypes)
                .line(declaration, BaseType.FormatType.VARIABLE);
        }
        if (!interfaces.isEmpty()) {
            head += " extends " + Types.join(", ", interfaces).line(declaration);
        }
        head += " {";

        // Format body - fields, constructors, methods
        List<String> body = new ArrayList<>();

        val staticMethods = new ArrayList<MethodDecl>();
        for (val method : methods) {
            if (method.isStatic) {
                staticMethods.add(method);
            } else {
                body.addAll(method.format(declaration));
            }
        }
        //but, includes no field, because all fields in an interface is static

        // tail - }
        List<String> tail = new ArrayList<>();
        for (val code : bodyCode) {
            tail.addAll(code.format(declaration));
        }
        tail.add("}\n");

        // Concatenate them as a whole
        List<String> formatted = new ArrayList<>();
        formatted.add(head);
        formatted.addAll(body);
        formatted.addAll(tail);

        // Static methods and fields, adds it even if it's empty, so auto import can still discover it
        if (this.withNamespace) {
            formatted.addAll(createNamespace(staticMethods).format(declaration));
        }
        if (this.withStatic) {
            formatted.addAll(createStaticClass(staticMethods).format(declaration));
        }
        return formatted;
    }
}
