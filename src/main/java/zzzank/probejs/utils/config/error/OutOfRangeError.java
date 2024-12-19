package zzzank.probejs.utils.config.error;

/**
 * @author ZZZank
 */
public final class OutOfRangeError extends RuntimeError {

    public OutOfRangeError(String name, Object received, Object min, Object max) {
        super(String.format("value %s for config entry '%s' not in range: [%s, %s]", received, name, min, max));
    }

    @Override
    public Exception asException() {
        return new IllegalArgumentException(message());
    }
}
