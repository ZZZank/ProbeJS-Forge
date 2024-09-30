package zzzank.probejs.lang.typescript.code;

import lombok.val;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.Declaration;
import zzzank.probejs.lang.typescript.refer.ImportInfo;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class Code {
    public abstract Collection<ImportInfo> getImportInfos();

    public abstract List<String> format(Declaration declaration);

    public String line(Declaration declaration) {
        return format(declaration).get(0);
    }

    public Collection<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<>();
        for (val info : getImportInfos()) {
            try {
                classes.add(info.path.forName());
            } catch (Throwable ignored) {
            }
        }
        return classes;
    }
}
