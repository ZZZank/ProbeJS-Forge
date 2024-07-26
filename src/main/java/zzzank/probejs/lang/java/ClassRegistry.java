package zzzank.probejs.lang.java;

import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.java.clazz.members.ConstructorInfo;
import zzzank.probejs.lang.java.clazz.members.FieldInfo;
import zzzank.probejs.lang.java.clazz.members.MethodInfo;
import zzzank.probejs.lang.java.clazz.members.ParamInfo;
import zzzank.probejs.lang.java.type.TypeDescriptor;
import zzzank.probejs.lang.java.type.impl.VariableType;
import zzzank.probejs.utils.ReflectUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@HideFromJS
public class ClassRegistry {
    public static final ClassRegistry REGISTRY = new ClassRegistry();

    public Map<ClassPath, Clazz> foundClasses = new HashMap<>(256);

    public void fromPackage(Collection<ClassPath> classPaths) {
        for (ClassPath pack : classPaths) {
            if (!foundClasses.containsKey(pack)) {
                foundClasses.put(pack, pack.toClazz());
            }
        }
    }

    public void fromClazz(Collection<Clazz> classes) {
        for (Clazz c : classes) {
            if (!foundClasses.containsKey(c.classPath)) {
                foundClasses.put(c.classPath, c);
            }
        }
    }

    public void fromClasses(Collection<Class<?>> classes) {
        for (Class<?> c : classes) {
            if (c.isSynthetic() || c.isAnonymousClass() || !ReflectUtils.classExist(c.getName())) {
                // We test if the class actually exists from forName
                // I think some runtime class can have non-existing Class<?> object due to .getSuperClass
                // or .getInterfaces
                continue;
            }
            try {
                if (!foundClasses.containsKey(new ClassPath(c))) {
                    Clazz clazz = new Clazz(c);
                    foundClasses.put(clazz.classPath, clazz);
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private Set<Class<?>> retrieveClass(Clazz clazz) {
        Set<Class<?>> classes = new HashSet<>();

        for (ConstructorInfo constructor : clazz.constructors) {
            for (ParamInfo param : constructor.params) {
                classes.addAll(param.type.getClasses());
            }
            for (VariableType variableType : constructor.variableTypes) {
                classes.addAll(variableType.getClasses());
            }
        }

        for (MethodInfo method : clazz.methods) {
            for (ParamInfo param : method.params) {
                classes.addAll(param.type.getClasses());
            }
            for (VariableType variableType : method.variableTypes) {
                classes.addAll(variableType.getClasses());
            }
            classes.addAll(method.returnType.getClasses());
        }

        for (FieldInfo field : clazz.fields) {
            classes.addAll(field.type.getClasses());
        }

        for (VariableType variableType : clazz.variableTypes) {
            classes.addAll(variableType.getClasses());
        }

        if (clazz.superClass != null) {
            classes.addAll(clazz.superClass.getClasses());
        }
        for (TypeDescriptor i : clazz.interfaces) {
            classes.addAll(i.getClasses());
        }

        return classes;
    }

    public void discoverClasses() {
        Set<Clazz> currentClasses = new HashSet<>(foundClasses.values());

        int lastClassCount = 0;
        while (foundClasses.size() != lastClassCount) {
            lastClassCount = foundClasses.size();

            Set<Class<?>> fetchedClass = new HashSet<>(256);
            ProbeJS.LOGGER.debug("walking {} newly discovered classes", currentClasses.size());
            for (Clazz currentClass : currentClasses) {
                fetchedClass.addAll(retrieveClass(currentClass));
            }
            currentClasses.clear();

            for (Class<?> c : fetchedClass) {
                if (foundClasses.containsKey(new ClassPath(c))) {
                    continue;
                }
                try {
//                    Class.forName(c.getName());
                    Clazz clazz = new Clazz(c);
                    foundClasses.put(clazz.classPath, clazz);
                    currentClasses.add(clazz);
                } catch (Throwable ignored) {
                }
            }
        }
    }

    public Collection<Clazz> getFoundClasses() {
        return foundClasses.values();
    }

    public void writeTo(Path path) throws IOException {
        val classPaths = new ArrayList<>(foundClasses.keySet());
        Collections.sort(classPaths);

        var lastPath = new ClassPath(Collections.emptyList());
        try (val writer = Files.newBufferedWriter(path)) {
            for (val classPath : classPaths) {
                val commonPartsCount = classPath.getCommonPartsCount(lastPath);
                val copy = new ArrayList<>(classPath.parts());
                Collections.fill(copy.subList(0, commonPartsCount), "");
                writer.write(String.join(".", copy));
                writer.write('\n');
                lastPath = classPath;
            }
        }
    }

    public void loadFrom(Path path) {
        var lastPath = new ClassPath(Collections.emptyList());
        try (val reader = Files.newBufferedReader(path)) {
            for (val className : (Iterable<String>) reader.lines()::iterator) {
                val parts = className.split("\\.");
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].isEmpty()) {
                        parts[i] = lastPath.parts().get(i);
                    } else {
                        break;
                    }
                }
                val classPath = new ClassPath(Arrays.asList(parts));
                try {
                    fromClasses(Collections.singleton(classPath.forName()));
                } catch (Throwable ignored) {
                }
                lastPath = classPath;
            }
        } catch (IOException ignored) {
        }
    }
}
