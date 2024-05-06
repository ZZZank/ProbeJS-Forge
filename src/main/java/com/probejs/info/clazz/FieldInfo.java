package com.probejs.info.clazz;

import com.probejs.info.type.IType;
import com.probejs.info.type.TypeResolver;
import com.probejs.util.PUtil;
import com.probejs.util.RemapperBridge;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldInfo implements Comparable<FieldInfo> {

    @Getter
    private final Field raw;
    @Getter
    private final String name;
    @Getter
    private IType type;
    private final int modifiers;
    private final boolean shouldHide;
    private final Object value;

    private static String getRemappedOrDefault(Field field, Class<?> clazz) {
        String mapped = RemapperBridge.getRemapper().getMappedField(clazz, field);
        if (!mapped.isEmpty()) {
            return mapped;
        }
        // String s = MethodInfo.RUNTIME.getMappedField(field.getDeclaringClass(), field);
        // return s.isEmpty() ? field.getName() : s;
        return field.getName();
    }

    public FieldInfo(Field field, Class<?> clazz) {
        this.raw = field;
        this.name = getRemappedOrDefault(field, clazz);
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

    public boolean shouldHide() {
        return shouldHide;
    }

    public Object getStaticValue() {
        return value;
    }

    public void setTypeInfo(IType info) {
        this.type = info;
    }

    @Override
    public int compareTo(FieldInfo o) {
        return this.name.compareTo(o.name);
    }
}
