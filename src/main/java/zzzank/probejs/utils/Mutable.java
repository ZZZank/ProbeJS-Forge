package zzzank.probejs.utils;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

/**
 * @author ZZZank
 */
@RequiredArgsConstructor
@AllArgsConstructor
public class Mutable<T> implements Supplier<T> {

    private T value;

    @Override
    public T get() {
        return value;
    }

    /**
     * @return {@code this}
     */
    public Mutable<T> set(T newValue) {
        value = newValue;
        return this;
    }

    public boolean isNull() {
        return value == null;
    }

    /**
     * set if {@code get()} returns null
     *
     * @return {@code this}
     */
    public Mutable<T> setIfAbsent(T newValue) {
        if (get() == null) {
            this.value = newValue;
        }
        return this;
    }

    /**
     * <p>
     * Compares this object against the specified object. The result is <code>true</code> if and only if the argument
     * is not <code>null</code> and is a <code>MutableObject</code> object that contains the same <code>T</code>
     * value as this object.
     * </p>
     *
     * @param obj the object to compare with, <code>null</code> returns <code>false</code>
     * @return <code>true</code> if the objects are the same;
     * <code>true</code> if the objects have equivalent <code>value</code> fields;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Mutable<?> mutable)) {
            return false;
        } else if (this == obj) {
            return true;
        }
        return this.value.equals(mutable.value);
    }

    /**
     * @return the value's hash code or {@code 0} if the value is {@code null}.
     */
    @Override
    public int hashCode() {
        return value == null ? 0 : value.hashCode();
    }

    /**
     * @return the mutable value as a string
     */
    @Override
    public String toString() {
        return value == null ? "null" : value.toString();
    }
}
