package zzzank.probejs.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author ZZZank
 */
public interface ReflectUtils {
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

    static Class<?> classOrNull(String name) {
        return classOrNull(name, false);
    }

    static Class<?> classOrNull(String name, boolean printError) {
        try {
            return Class.forName(name);
        } catch (Throwable e) {
            if (printError) {
                e.printStackTrace();
            }
        }
        return null;
    }

    static boolean classExist(String name) {
        return classOrNull(name) != null;
    }
}
