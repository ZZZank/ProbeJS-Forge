package zzzank.probejs.lang.typescript;

import com.mojang.datafixers.util.Pair;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.refer.ImportInfo;
import zzzank.probejs.lang.typescript.refer.Reference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Declaration {
    private static final String SYMBOL_TEMPLATE = "%s$%d";

    public final Map<ClassPath, Reference> references;
    private final Map<ClassPath, Pair<String, String>> symbols;

    private final Set<String> excludedName;

    public Declaration() {
        this.references = new HashMap<>();
        this.symbols = new HashMap<>();
        this.excludedName = new HashSet<>();
    }

    public void addClass(ClassPath path) {
        // So we determine a unique original that is safe to use at startup
        var names = getSymbolName(path);
        this.references.put(path, new Reference(path, names.getFirst(), names.getSecond()));
    }

    public void exclude(String name) {
        excludedName.add(name);
    }

    private void putSymbolName(ClassPath path, String name) {
        symbols.put(path, new Pair<>(name, String.format(ImportInfo.INPUT_TEMPLATE, name)));
    }

    private boolean containsSymbol(String name) {
        return excludedName.contains(name)
            || symbols.containsValue(new Pair<>(name, String.format(ImportInfo.INPUT_TEMPLATE, name)));
    }


    private Pair<String, String> getSymbolName(ClassPath path) {
        if (!symbols.containsKey(path)) {
            String name = path.getName();
            if (!containsSymbol(name)) {
                putSymbolName(path, name);
            } else {
                int counter = 0;
                while (containsSymbol(String.format(SYMBOL_TEMPLATE, name, counter))) {
                    counter++;
                }
                putSymbolName(path, String.format(SYMBOL_TEMPLATE, name, counter));
            }
        }

        return symbols.get(path);
    }

    public String getSymbol(ClassPath path) {
        return getSymbol(path, false);
    }

    public String getSymbol(ClassPath path, boolean input) {
        if (!this.references.containsKey(path)) {
            throw new RuntimeException("Trying to get a symbol of a classpath that is not resolved yet!");
        }
        var reference = this.references.get(path);
        return input ? reference.input : reference.original;
    }
}
