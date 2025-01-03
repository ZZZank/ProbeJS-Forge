package zzzank.probejs.utils.config.report;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author ZZZank
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoError implements ConfigReport {
    public static final NoError INSTANCE = new NoError();
    public static final Exception EXCEPTION = new Exception("there's no report");

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public Exception asException() {
        return EXCEPTION;
    }

    @Override
    public String message() {
        return EXCEPTION.getMessage();
    }
}
