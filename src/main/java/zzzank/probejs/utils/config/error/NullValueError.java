package zzzank.probejs.utils.config.error;

/**
 * @author ZZZank
 */
public final class NullValueError extends RuntimeError {

    public NullValueError(String name) {
        super("config entry '" + name + "' received a null value");
    }

    @Override
    public Exception asException() {
        return new NullPointerException(message());
    }
}
