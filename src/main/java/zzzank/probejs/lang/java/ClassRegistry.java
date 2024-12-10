package zzzank.probejs.lang.java;

import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.val;
import zzzank.probejs.ProbeConfig;
import zzzank.probejs.ProbeJS;
import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.java.clazz.Clazz;
import zzzank.probejs.lang.java.clazz.MemberCollector;
import zzzank.probejs.utils.ReflectUtils;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@HideFromJS
public class ClassRegistry {
    public static final ClassRegistry REGISTRY = new ClassRegistry();

    public Map<ClassPath, Clazz> foundClasses = new HashMap<>(256);
    public final MemberCollector collector = new ClazzMemberCollector();

    public void fromClazz(Collection<Clazz> classes) {
        for (val c : classes) {
            if (!foundClasses.containsKey(c.classPath)) {
                foundClasses.put(c.classPath, c);
            }
        }
    }

    public void fromClasses(Collection<Class<?>> classes) {
        for (val c : classes) {
            fromClass(c);
        }
    }

    public void fromClass(Class<?> c) {
        if (!ReflectUtils.classExist(c.getName()) || c.isSynthetic() || c.isAnonymousClass() || c.isPrimitive()) {
            // We test if the class actually exists from forName
            // I think some runtime class can have non-existing Class<?> object due to .getSuperClass
            // or .getInterfaces
            return;
        }
        try {
            if (!foundClasses.containsKey(ClassPath.fromJava(c))) {
                val clazz = new Clazz(c, collector);
                foundClasses.put(clazz.classPath, clazz);
            }
        } catch (Throwable ignored) {
        }
    }

    private Set<Class<?>> retrieveClass(Clazz clazz) {
        Set<Class<?>> classes = new HashSet<>();

        for (val constructor : clazz.constructors) {
            for (val param : constructor.params) {
                classes.addAll(param.type.getClasses());
            }
            for (val variableType : constructor.variableTypes) {
                classes.addAll(variableType.getClasses());
            }
        }

        for (val method : clazz.methods) {
            for (val param : method.params) {
                classes.addAll(param.type.getClasses());
            }
            for (val variableType : method.variableTypes) {
                classes.addAll(variableType.getClasses());
            }
            classes.addAll(method.returnType.getClasses());
        }

        for (val field : clazz.fields) {
            classes.addAll(field.type.getClasses());
        }

        for (val variableType : clazz.variableTypes) {
            classes.addAll(variableType.getClasses());
        }

        if (clazz.superClass != null) {
            classes.addAll(clazz.superClass.getClasses());
        }
        for (val i : clazz.interfaces) {
            classes.addAll(i.getClasses());
        }

        return classes;
    }

    public void walkClass() {
        Set<Clazz> currentClasses = new HashSet<>(foundClasses.values());

        int lastClassCount = 0;
        while (foundClasses.size() != lastClassCount) {
            lastClassCount = foundClasses.size();

            Set<Class<?>> fetchedClass = new HashSet<>(256);
            ProbeJS.LOGGER.debug("walking {} newly discovered classes", currentClasses.size());
            for (val currentClass : currentClasses) {
                fetchedClass.addAll(retrieveClass(currentClass));
            }
            currentClasses.clear();

            for (val c : fetchedClass) {
                if (foundClasses.containsKey(ClassPath.fromJava(c))) {
                    continue;
                }
                try {
//                    Class.forName(c.getName());
                    val clazz = new Clazz(c, collector);
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

        var lastPath = new ClassPath(new String[0]);
        try (val writer = Files.newBufferedWriter(path)) {
            for (val classPath : classPaths) {
                val commonPartsCount = classPath.getCommonPartsCount(lastPath);
                val copy = new ArrayList<>(classPath.getParts());
                Collections.fill(copy.subList(0, commonPartsCount), "");
                writer.write(String.join(".", copy));
                writer.write('\n');
                lastPath = classPath;
            }
        }
    }

    public void loadFrom(Path path) {
        var lastPath = new ClassPath(new String[0]);
        try (val reader = Files.newBufferedReader(path)) {
            for (val className : (Iterable<String>) reader.lines()::iterator) {
                val parts = className.split("\\.");
                for (int i = 0; i < parts.length; i++) {
                    if (parts[i].isEmpty()) {
                        parts[i] = lastPath.getPart(i);
                    } else {
                        break;
                    }
                }
                val classPath = new ClassPath(parts);
                try {
                    val c= Class.forName(classPath.getJavaPath());
                    if (!ProbeConfig.publicClassOnly.get() || Modifier.isPublic(c.getModifiers())) {
                        fromClass(c);
                    }
                } catch (Throwable ignored) {
                }
                lastPath = classPath;
            }
        } catch (IOException ignored) {
        }
    }
}
