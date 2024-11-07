package zzzank.probejs.docs;

import zzzank.probejs.lang.transpiler.TypeConverter;
import zzzank.probejs.lang.transpiler.redirect.ClassRedirect;
import zzzank.probejs.plugin.ProbeJSPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ZZZank
 */
public class ClassWrapping extends ProbeJSPlugin {

    public static final Set<Class<?>> CONVERTIBLES = new HashSet<>();

    static {
        CONVERTIBLES.add(Class.class);
    }

    @Override
    public void addPredefinedTypes(TypeConverter converter) {
        converter.addTypeRedirect(new ClassRedirect(CONVERTIBLES));
    }
}
