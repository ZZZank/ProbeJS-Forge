package zzzank.probejs.lang.java.clazz;

import dev.latvian.mods.rhino.util.HideFromJS;
import org.jetbrains.annotations.Nullable;
import zzzank.probejs.lang.java.base.TypeVariableHolder;
import zzzank.probejs.lang.java.clazz.members.ConstructorInfo;
import zzzank.probejs.lang.java.clazz.members.FieldInfo;
import zzzank.probejs.lang.java.clazz.members.MethodInfo;
import zzzank.probejs.lang.java.type.TypeAdapter;
import zzzank.probejs.lang.java.type.TypeDescriptor;
import zzzank.probejs.utils.CollectUtils;
import zzzank.probejs.utils.ReflectUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class Clazz extends TypeVariableHolder {

    @HideFromJS
    public final Class<?> original;
    public final ClassPath classPath;
    public final List<ConstructorInfo> constructors;
    public final List<FieldInfo> fields;
    public final List<MethodInfo> methods;
    @Nullable
    public final TypeDescriptor superClass;
    public final List<TypeDescriptor> interfaces;
    public final ClassAttribute attribute;

    public Clazz(Class<?> clazz, MemberCollector collector) {
        super(clazz.getTypeParameters(), clazz.getAnnotations());

        this.original = clazz;
        this.classPath = ClassPath.fromJava(original);

        collector.accept(clazz);
        this.constructors = collector.constructors().collect(Collectors.toList());
        this.methods = collector.methods().collect(Collectors.toList());
        this.fields = collector.fields().collect(Collectors.toList());

        this.superClass = clazz.getSuperclass() == Object.class
            ? null
            : TypeAdapter.getTypeDescription(clazz.getAnnotatedSuperclass());

        this.interfaces = CollectUtils.mapToList(clazz.getAnnotatedInterfaces(), TypeAdapter::getTypeDescription);
        this.attribute = new ClassAttribute(clazz);
    }

    @Override
    public int hashCode() {
        return classPath.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Clazz clazz = (Clazz) o;
        return Objects.equals(classPath, clazz.classPath);
    }

    public enum ClassType {
        INTERFACE,
        ENUM,
        RECORD,
        CLASS
    }

    public static class ClassAttribute {

        public final ClassType type;
        public final boolean isAbstract;
        public final boolean isInterface;
        public final Class<?> raw;

        public ClassAttribute(Class<?> clazz) {
            if (clazz.isInterface()) {
                this.type = ClassType.INTERFACE;
            } else if (clazz.isEnum()) {
                this.type = ClassType.ENUM;
//            } else if (clazz.isRecord()) {
//                this.type = ClassType.RECORD;
            } else {
                this.type = ClassType.CLASS;
            }

            int modifiers = clazz.getModifiers();
            this.isAbstract = Modifier.isAbstract(modifiers);
            this.isInterface = type == ClassType.INTERFACE;
            this.raw = clazz;
        }
    }
}
