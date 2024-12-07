package zzzank.probejs.utils;

import lombok.val;

import java.util.Collection;

/**
 * @author ZZZank
 */
public interface Asser {
    static void t(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static <T> T tNotNull(T value, String name) {
        if (value == null) {
            throw new NullPointerException("'" + name + "' must not be null");
        }
        return value;
    }

    static <T extends Iterable<?>> T tNotNullAll(T collection, String name) {
        tNotNull(collection, name);
        val elementName = "element in " + name;
        for (val o : collection) {
            tNotNull(o, elementName);
        }
        return collection;
    }
}
