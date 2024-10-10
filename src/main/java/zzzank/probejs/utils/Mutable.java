package zzzank.probejs.utils;

import lombok.AllArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author ZZZank
 */
@AllArgsConstructor
public class Mutable<T> implements Supplier<T>, Iterable<T> {
    public static <T> Mutable<T> of(T value) {
        return new Mutable<>(value);
    }

    public static <T> Mutable<T> ofNull() {
        return new Mutable<>(null);
    }

    private T value;

    @Override
    public T get() {
        return value;
    }

    /**
     * get the value this {@code Mutable} holds, and set it to null
     * @return
     */
    public T getAndForget() {
        val tmp = this.value;
        value = null;
        return tmp;
    }

    public T getOr(T otherValue) {
        return isNull() ? otherValue : value;
    }

    public T getOr(Supplier<? extends T> otherValue) {
        return isNull() ? otherValue.get() : value;
    }

    public T getOrThrow() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    public <X extends Throwable> T getOrThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value == null) {
            throw exceptionSupplier.get();
        }
        return value;
    }

    public boolean isNull() {
        return value == null;
    }

    public boolean notNull() {
        return value != null;
    }

    /**
     * @return {@code this}
     */
    public Mutable<T> set(T newValue) {
        value = newValue;
        return this;
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
     * If a value is present, returns an {@code Mutable} describing the result
     * of applying the given mapping function to the value, otherwise returns
     * an empty {@code Mutable}.
     *
     * <p>If the mapping function returns a {@code null} result then this method
     * returns an empty {@code Mutable}.
     *
     * @param mapper the mapping function to apply to a value, if present
     * @param <U> The type of the value returned from the mapping function
     * @return an {@code Mutable} describing the result of applying a mapping
     *         function to the value of this {@code Mutable}, if a value is
     *         present, otherwise an empty {@code Mutable}
     * @throws NullPointerException if the mapping function is {@code null}
     */
    public <U> Mutable<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (isNull()) {
            return ofNull();
        }
        return Mutable.of(mapper.apply(value));
    }

    /**
     * If a value is present, returns an {@code Mutable} describing the value,
     * otherwise returns the provided {@code Mutable}.
     *
     * @param another a {@code Mutable} candidate to be returned
     * @return returns an {@code Mutable} describing the value of this
     *         {@code Mutable}, if a value is present, otherwise {@code another}.
     * @throws NullPointerException if the provided {@code Mutable} is {@code null}
     */
    public Mutable<T> or(Mutable<? extends T> another) {
        Objects.requireNonNull(another);
        if (notNull()) {
            return this;
        }
        @SuppressWarnings("unchecked")
        Mutable<T> r = (Mutable<T>) another;
        return r;
    }

    /**
     * If a value is present, returns an {@code Mutable} describing the value,
     * otherwise returns an {@code Mutable} produced by the supplying function.
     *
     * @param supplier the supplying function that produces an {@code Mutable}
     *        to be returned
     * @return returns an {@code Mutable} describing the value of this
     *         {@code Mutable}, if a value is present, otherwise an
     *         {@code Mutable} produced by the supplying function.
     * @throws NullPointerException if the supplying function is {@code null} or
     *         produces a {@code null} result
     */
    public Mutable<T> or(Supplier<? extends Mutable<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        @SuppressWarnings("unchecked")
        Mutable<T> r = (Mutable<T>) supplier.get();
        return or(r);
    }

    /**
     * If a value is present, returns a sequential {@link Stream} containing
     * only that value, otherwise returns an empty {@code Stream}.
     *
     * @return the optional value as a {@code Stream}
     */
    public Stream<T> stream() {
        if (isNull()) {
            return Stream.empty();
        }
        return Stream.of(value);
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

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return Collections.singletonList(value).iterator();
    }
}
