package com.probejs.info;

import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeResolver;
import com.probejs.util.PUtil;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldInfo implements Comparable<FieldInfo> {

    private final Field raw;
    private final String name;
    private ITypeInfo type;
    private final int modifiers;
    private final boolean shouldHide;
    private final Object value;

    private static String getRemappedOrDefault(Field field) {
        // String s = MethodInfo.RUNTIME.getMappedField(field.getDeclaringClass(), field);
        // return s.isEmpty() ? field.getName() : s;
        return field.getName();
    }

    public FieldInfo(Field field) {
        this.raw = field;
        this.name = getRemappedOrDefault(field);
        this.modifiers = field.getModifiers();
        this.shouldHide = field.getAnnotation(HideFromJS.class) != null;
        this.type = TypeResolver.resolveType(field.getGenericType());
        this.value = PUtil.tryOrDefault(() -> isStatic() ? field.get(null) : null, null);
    }

    public boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }

    public boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }

    public Field getRaw() {
        return raw;
    }

    public String getName() {
        return name;
    }

    public boolean shouldHide() {
        return shouldHide;
    }

    public ITypeInfo getType() {
        return type;
    }

    public Object getStaticValue() {
        return value;
    }

    public void setTypeInfo(ITypeInfo info) {
        this.type = info;
    }

    @Override
    public int compareTo(FieldInfo o) {
        return this.name.compareTo(o.name);
    }
}
