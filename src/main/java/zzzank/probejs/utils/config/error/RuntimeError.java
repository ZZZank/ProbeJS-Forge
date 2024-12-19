package zzzank.probejs.utils.config.error;

/**
 * @author ZZZank
 */
public class RuntimeError implements ConfigReport {
    private final String message;

    public RuntimeError(String message) {
        this.message = message;
    }

    @Override
    public boolean hasError() {
        return true;
    }

    @Override
    public Exception asException() {
        return new RuntimeException(message());
    }

    @Override
    public String message() {
        return message;
    }
}
