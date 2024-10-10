package zzzank.probejs.lang.java.type.impl;

import zzzank.probejs.lang.java.clazz.ClassPath;
import zzzank.probejs.lang.java.type.TypeDescriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

public class ClassType extends TypeDescriptor {
    public final ClassPath classPath;
    public final Class<?> clazz;

    public ClassType(AnnotatedType type) {
        super(type.getAnnotations());
        clazz = (Class<?>) type.getType();
        classPath = ClassPath.fromJava(clazz);
    }

    public ClassType(Type type) {
        super(new Annotation[]{});
        clazz = (Class<?>) type;
        classPath = ClassPath.fromJava(clazz);
    }

    @Override
    public Stream<TypeDescriptor> stream() {
        return Stream.of(this);
    }

    @Override
    public Collection<ClassPath> getClassPaths() {
        return Collections.singletonList(classPath);
    }

    @Override
    public Collection<Class<?>> getClasses() {
        return Collections.singletonList(clazz);
    }

    @Override
    public int hashCode() {
        return classPath.hashCode();
    }
}
