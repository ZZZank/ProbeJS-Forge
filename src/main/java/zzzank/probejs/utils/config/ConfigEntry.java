package zzzank.probejs.utils.config;

import com.google.common.collect.ImmutableList;
import lombok.val;

import java.util.List;
import java.util.Objects;

/**
 * @author ZZZank
 */
public class ConfigEntry<T> {

    public final ConfigImpl source;
    public final String name;
    public final T defaultValue;
    private T value;
    public final String namespace;
    public final ImmutableList<String> comments;

    public ConfigEntry(ConfigImpl source, String name, T defaultValue, String namespace, List<String> comments) {
        this.source = Objects.requireNonNull(source);
        this.name = Objects.requireNonNull(name);
        this.defaultValue = Objects.requireNonNull(defaultValue);
        this.namespace = Objects.requireNonNull(namespace);
        this.comments = ImmutableList.copyOf(Objects.requireNonNull(comments));
    }

    public void set(T value) {
        setNoSave(value);
        source.save();
    }

    void setNoSave(T value) {
        if (value == null) {
            value = defaultValue;
        }
        if (Objects.equals(this.value, value)) {
            return;
        }
        try {
            this.value = value;
        } catch (Exception e) {
            this.value = defaultValue;
        }
    }

    public T adaptValue(Object o) {
        if (o == null) {
            return defaultValue;
        }
        Object result;
        if (this.defaultValue == null) {
            result = null;
        } else if (this.defaultValue instanceof CharSequence) {
            result = String.valueOf(o);
        } else if (this.defaultValue instanceof Number n) {
            if (o instanceof Number targetNumber) {
                if (n instanceof Long) {
                    result = targetNumber.longValue();
                } else if (n instanceof Double) {
                    result = targetNumber.doubleValue();
                } else if (n instanceof Float) {
                    result = targetNumber.floatValue();
                } else if (n instanceof Integer) {
                    result = targetNumber.intValue();
                } else if (n instanceof Short) {
                    result = targetNumber.shortValue();
                } else if (n instanceof Byte) {
                    result = targetNumber.byteValue();
                } else {
                    result = targetNumber;
                }
                return (T) result;
            }
            result = defaultValue;
        } else {
            result = defaultValue.getClass().isInstance(o) ? (T) o : defaultValue;
        }
        return (T) result;
    }

    public T getRaw() {
        return value;
    }

    public T get() {
        if (value == null) {
            set(defaultValue);
        }
        return value;
    }
}
