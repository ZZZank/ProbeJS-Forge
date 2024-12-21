package zzzank.probejs.lang.typescript;

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

    public final Map<ClassPath, Reference> references = new HashMap<>();
    private final Set<String> usedNames = new HashSet<>();
    private final Set<String> excludedName = new HashSet<>();

    public void addImport(ImportInfo info) {
        val existed = references.get(info.path);
        //already resolved
        if (existed != null) {
            existed.info.mergeWith(info);
            return;
        }
        // So we determine a unique original that is safe to use at startup
        val uniqueName = computeSymbol(info.path);
        usedNames.add(uniqueName);
        this.references.put(info.path, new Reference(info, uniqueName));
    }

    public void exclude(String name) {
        excludedName.add(name);
    }

    public boolean containsSymbol(String name) {
        return excludedName.contains(name) || usedNames.contains(name);
    }

    private String computeSymbol(ClassPath path) {
        val original = path.getName();
        //try original, then try template
        var deduped = original;
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
            throw new RuntimeException(String.format("Trying to get a symbol of unresolved classpath: %s", path));
        }
        return input ? ImportType.TYPE.fmt(reference.deduped) : reference.deduped;
    }
}
