package zzzank.probejs.lang.typescript.code;

import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.typescript.Declaration;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public abstract class Code {
    public abstract Collection<ClassPath> getUsedClassPaths();

    public abstract List<String> format(Declaration declaration);

    public String line(Declaration declaration) {
        return format(declaration).get(0);
    }

    public Collection<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<>();
        for (ClassPath usedClassPath : getUsedClassPaths()) {
            try {
                classes.add(usedClassPath.forName());
            } catch (Throwable ignored) {
            }
        }
        return classes;
    }
}
