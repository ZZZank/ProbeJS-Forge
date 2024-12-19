package zzzank.probejs.utils.config.binding;

import org.jetbrains.annotations.NotNull;
import zzzank.probejs.utils.config.error.ConfigReport;

/**
 * @author ZZZank
 */
public interface ConfigBinding<T> {

    @NotNull
    T getDefault();

    T get();

    @NotNull
    ConfigReport set(T value);

    default ConfigReport reset() {
        return set(getDefault());
    }
}
