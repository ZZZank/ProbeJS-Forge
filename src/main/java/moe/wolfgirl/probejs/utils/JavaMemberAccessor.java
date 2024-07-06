package moe.wolfgirl.probejs.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author ZZZank
 */
public interface JavaMemberAccessor {
    static Constructor<?>[] constructorsSafe(Class<?> c) {
        try {
            return c.getConstructors();
        } catch (Throwable e) {
            return new Constructor[0];
        }
    }

    static Field[] fieldsSafe(Class<?> c) {
        try {
            return c.getFields();
        } catch (Throwable e) {
            return new Field[0];
        }
    }

    static Method[] methodsSafe(Class<?> c) {
        try {
            return c.getMethods();
        } catch (Throwable e) {
            return new Method[0];
        }
    }
}
