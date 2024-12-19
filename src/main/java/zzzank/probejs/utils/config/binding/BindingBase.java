package zzzank.probejs.utils.config.binding;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import zzzank.probejs.utils.config.error.ConfigReport;
import zzzank.probejs.utils.config.error.NoError;
import zzzank.probejs.utils.config.error.WrappedException;

/**
 * @author ZZZank
 */
public abstract class BindingBase<T> implements ConfigBinding<T> {

    @NotNull
    protected final T defaultValue;
    @NotNull
    protected final String name;

    protected BindingBase(@NotNull T defaultValue, @NotNull String name) {
        this.defaultValue = defaultValue;
        this.name = name;
    }

    @Override
    public @NotNull T getDefault() {
        return defaultValue;
    }

    @Override
    public @NotNull ConfigReport set(T value) {
        val validated = validate(value);
        if (validated.hasError()) {
            return validated;
        }
        try {
            setImpl(value);
        } catch (Exception e) {
            setImpl(defaultValue);
            return new WrappedException(e);
        }
        return NoError.INSTANCE;
    }

    abstract protected void setImpl(T value);

    public ConfigReport validate(T value) {
        return NoError.INSTANCE;
    }
}
