package com.probejs.info;

import com.probejs.info.type.ITypeInfo;
import com.probejs.info.type.TypeInfoResolver;
import com.probejs.util.PUtil;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldInfo {
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
        name = getRemappedOrDefault(field);
        modifiers = field.getModifiers();
        shouldHide = field.getAnnotation(HideFromJS.class) != null;
        type = TypeInfoResolver.resolveType(field.getGenericType());
        value = PUtil.tryOrDefault(() -> isStatic() ? field.get(null) : null, null);
    }

    public boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }

    public boolean isFinal() {
        return Modifier.isFinal(modifiers);
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
}
