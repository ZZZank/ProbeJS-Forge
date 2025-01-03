package zzzank.probejs.utils.config.report;

import lombok.AllArgsConstructor;

/**
 * @author ZZZank
 */
@AllArgsConstructor
public class WrappedException implements ConfigReport {
    private final Exception e;

    @Override
    public boolean hasError() {
        return true;
    }

    @Override
    public Exception asException() {
        return e;
    }

    @Override
    public String message() {
        return e.getLocalizedMessage();
    }
}
