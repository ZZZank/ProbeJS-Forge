package zzzank.probejs.utils.config.binding;

import org.jetbrains.annotations.NotNull;

/**
 * @author ZZZank
 */
public class ReadOnlyBinding<T> extends BindingBase<T> {

    public ReadOnlyBinding(@NotNull T defaultValue, @NotNull String name) {
        super(defaultValue, name);
    }

    @Override
    public T get() {
        return defaultValue;
    }

    @Override
    protected void setImpl(T value) {
    }
}
