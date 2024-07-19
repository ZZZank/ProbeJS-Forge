package moe.wolfgirl.probejs.utils;

import java.util.function.Supplier;

/**
 * @author ZZZank
 */
public class Lazy<T> implements Supplier<T> {

    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier, null);
    }

    public static <T> Lazy<T> ofImmediate(T value) {
        return new Lazy<>(null, value);
    }

    private final Supplier<T> supplier;
    private T value;

    public Lazy(Supplier<T> supplier, T value) {
        this.supplier = supplier;
        this.value = value;
    }

    public boolean forget() {
        if (supplier == null) {
            return false;
        }
        value = null;
        return true;
    }

    public boolean valueLoaded() {
        return value != null;
    }

    @Override
    public T get() {
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }
}
