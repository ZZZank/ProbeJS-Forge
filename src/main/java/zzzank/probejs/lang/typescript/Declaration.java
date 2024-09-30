package zzzank.probejs.lang.typescript;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import lombok.val;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.refer.ImportInfo;
import zzzank.probejs.lang.typescript.refer.ImportType;
import zzzank.probejs.lang.typescript.refer.Reference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Declaration {
    private static final String UNIQUE_TEMPLATE = "%s$%d";

    public final Map<ClassPath, Reference> references;
    private final BiMap<ClassPath, String> dedupedSymbols;

    private final Set<String> excludedName;

    public Declaration() {
        this.references = new HashMap<>();
        this.excludedName = new HashSet<>();
        this.dedupedSymbols = HashBiMap.create();
    }

    public void addImport(ImportInfo info) {
        // So we determine a unique original that is safe to use at startup
        val name = resolveSymbol(info.path);
        val old = references.get(info.path);
        if (old != null) {
            old.info.types.addAll(info.types);
        }
        this.references.put(info.path, new Reference(info, name));
    }

    public void exclude(String name) {
        excludedName.add(name);
    }

    public boolean containsSymbol(String name) {
        return excludedName.contains(name) || dedupedSymbols.containsValue(name);
    }

    public String resolveSymbol(ClassPath path) {
        //already resolved
        var deduped = dedupedSymbols.get(path);
        if (deduped != null) {
            return deduped;
        }
        //try original, then try template
        val original = path.getName();
        deduped = original;
        int counter = 0;
        while (containsSymbol(deduped)) {
            deduped = String.format(UNIQUE_TEMPLATE, original, counter++);
        }
        return deduped;
    }

    public String getSymbol(ClassPath path) {
        return getSymbol(path, false);
    }

    public String getSymbol(ClassPath path, boolean input) {
        val reference = this.references.get(path);
        if (reference == null) {
            throw new RuntimeException("Trying to get a symbol of a classpath that is not resolved yet!");
        }
        return input ? ImportType.TYPE.fmt(reference.deduped) : reference.deduped;
    }
}
