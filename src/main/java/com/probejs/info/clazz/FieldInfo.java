package com.probejs.info.clazz;

import com.probejs.info.type.TypeResolver;
import com.probejs.util.PUtil;
import com.probejs.util.RemapperBridge;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Getter
public class FieldInfo extends BaseMemberInfo implements Comparable<FieldInfo> {

    private final Field raw;
    private final int modifiers;
    private final boolean shouldHide;
    private final Object staticValue;
    private final ClassInfo from;

    private static String getRemappedOrDefault(Field field, Class<?> clazz) {
        String mapped = RemapperBridge.getRemapper().getMappedField(clazz, field);
        if (!mapped.isEmpty()) {
            return mapped;
        }
        return field.getName();
    }

    public FieldInfo(Field field, Class<?> clazz) {
        super(getRemappedOrDefault(field, clazz), TypeResolver.resolve(field.getGenericType()));
        this.raw = field;
        this.from = ClassInfo.ofCache(clazz);
        this.modifiers = field.getModifiers();
        this.shouldHide = field.getAnnotation(HideFromJS.class) != null;
        this.staticValue = PUtil.tryOrDefault(() -> isStatic() ? field.get(null) : null, null);
    }

    public boolean isStatic() {
        return Modifier.isStatic(modifiers);
    }

    public boolean isFinal() {
        return Modifier.isFinal(modifiers);
    }

    @Override
    public int compareTo(FieldInfo o) {
        return this.name.compareTo(o.name);
    }
}
