package zzzank.probejs.docs;

import com.google.common.collect.ImmutableMap;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.transpiler.redirect.SimpleTypeRedirect;
import zzzank.probejs.lang.typescript.ScriptDump;
import zzzank.probejs.lang.typescript.code.member.TypeDecl;
import zzzank.probejs.lang.typescript.code.ts.Statements;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.lang.typescript.code.type.js.JSPrimitiveType;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.Map;

public class Primitives extends ProbeJSPlugin {
    public static final JSPrimitiveType LONG = Types.primitive("long");
    public static final JSPrimitiveType INTEGER = Types.primitive("integer");
    public static final JSPrimitiveType SHORT = Types.primitive("short");
    public static final JSPrimitiveType BYTE = Types.primitive("byte");
    public static final JSPrimitiveType DOUBLE = Types.primitive("double");
    public static final JSPrimitiveType FLOAT = Types.primitive("float");
    public static final JSPrimitiveType CHARACTER = Types.primitive("character");
    public static final JSPrimitiveType CHAR_SEQUENCE = Types.primitive("charseq");

    public static final JSPrimitiveType TS_NUMBER = Types.primitive("Number");
    public static final JSPrimitiveType TS_STRING = Types.primitive("String");

    public static final Map<Class<?>, BaseType> MAPPING = ImmutableMap.<Class<?>, BaseType>builder()
        //obj
        .put(Object.class, Types.ANY)
        //string
        .put(String.class, Types.STRING)
        .put(CharSequence.class, CHAR_SEQUENCE)
        .put(Character.class, CHARACTER)
        .put(Character.TYPE, CHARACTER)
        //void
        .put(Void.class, Types.VOID)
        .put(Void.TYPE, Types.VOID)
        //number
        .put(Long.class, LONG)
        .put(Long.TYPE, LONG)
        .put(Integer.class, INTEGER)
        .put(Integer.TYPE, INTEGER)
        .put(Short.class, SHORT)
        .put(Short.TYPE, SHORT)
        .put(Byte.class, BYTE)
        .put(Byte.TYPE, BYTE)
        .put(Number.class, Types.NUMBER)
        .put(Double.class, DOUBLE)
        .put(Double.TYPE, DOUBLE)
        .put(Float.class, FLOAT)
        .put(Float.TYPE, FLOAT)
        //bool
        .put(Boolean.class, Types.BOOLEAN)
        .put(Boolean.TYPE, Types.BOOLEAN)
        .build();

    @Override
    public void addPredefinedTypes(TypeConverter converter) {
        converter.addTypeRedirect(new SimpleTypeRedirect(MAPPING.keySet(), (type) -> MAPPING.get(type.clazz)));
    }

    @Override
    public void addGlobals(ScriptDump scriptDump) {
        var numberBoth = Types.and(Types.NUMBER, TS_NUMBER);
        scriptDump.addGlobal("primitives",
            //for number types, we can safely mark them as a primitive type instead of an interface
            //because the classes that represent them are `final`, so there's no need of taking inheritance into account
            new TypeDecl("long", numberBoth),
            new TypeDecl("integer", numberBoth),
            new TypeDecl("short", numberBoth),
            new TypeDecl("byte", numberBoth),
            new TypeDecl("double", numberBoth),
            new TypeDecl("float", numberBoth),
            //for CharSequence, we should NOT mark it as a primitive type, because of inheritance
            Statements.clazz("character").interfaceClass().interfaces(TS_STRING).build(),
            Statements.clazz("charseq").interfaceClass().interfaces(TS_STRING).build()
        );
    }
}
