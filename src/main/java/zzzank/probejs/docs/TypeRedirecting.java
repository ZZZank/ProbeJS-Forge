package zzzank.probejs.docs;

import dev.latvian.mods.rhino.NativeArray;
import dev.latvian.mods.rhino.ScriptableObject;
import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.transpiler.redirect.ClassRedirect;
import zzzank.probejs.lang.transpiler.redirect.RhizoGenericRedirect;
import zzzank.probejs.lang.transpiler.redirect.SimpleTypeRedirect;
import zzzank.probejs.lang.typescript.code.type.BaseType;
import zzzank.probejs.lang.typescript.code.type.Types;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.*;

/**
 * @author ZZZank
 */
public class TypeRedirecting implements ProbeJSPlugin {

    public static final Set<Class<?>> CLASS_CONVERTIBLES = new HashSet<>();
    public static final Map<Class<?>, BaseType> JS_OBJ = new IdentityHashMap<>();

    static {
        CLASS_CONVERTIBLES.add(Class.class);
        JS_OBJ.put(ScriptableObject.class, Types.EMPTY_OBJECT);
        JS_OBJ.put(NativeArray.class, Types.ANY.asArray());
    }

    @Override
    public void addPredefinedTypes(TypeConverter converter) {
        converter.addTypeRedirect(new RhizoGenericRedirect());
        //class wrapper
        converter.addTypeRedirect(new SimpleTypeRedirect(CLASS_CONVERTIBLES, (c) -> GlobalClasses.J_CLASS));
        converter.addTypeRedirect(new ClassRedirect(CLASS_CONVERTIBLES));
        //js objects
        converter.addTypeRedirect(new SimpleTypeRedirect(JS_OBJ.keySet(), (c) -> JS_OBJ.get(c.clazz)));
    }
}
