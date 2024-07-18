package moe.wolfgirl.probejs.lang.java.clazz.members;

import moe.wolfgirl.probejs.features.rhizo.RemapperBridge;
import moe.wolfgirl.probejs.lang.java.base.AnnotationHolder;
import moe.wolfgirl.probejs.lang.java.type.TypeAdapter;
import moe.wolfgirl.probejs.lang.java.type.TypeDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldInfo extends AnnotationHolder {
    public final String name;
    public final TypeDescriptor type;
    public final FieldAttributes attributes;

    public FieldInfo(Class<?> from, Field field) {
        super(field.getAnnotations());
        this.name = RemapperBridge.remapField(from, field);
        this.type = TypeAdapter.getTypeDescription(field.getAnnotatedType());
        this.attributes = new FieldAttributes(field);
    }

    public static class FieldAttributes {
        public final boolean isFinal;
        public final boolean isStatic;
        private final Field field;

        public FieldAttributes(Field field) {
            int modifiers = field.getModifiers();
            this.isFinal = Modifier.isFinal(modifiers);
            this.isStatic = Modifier.isStatic(modifiers);
            this.field = field;
        }

        public Object getStaticValue() throws IllegalAccessException {
            if (isStatic) throw new RuntimeException("The field is not static!");
            return field.get(null);
        }
    }
}
