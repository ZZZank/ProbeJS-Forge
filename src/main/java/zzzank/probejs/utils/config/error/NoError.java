package zzzank.probejs.utils.config.error;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author ZZZank
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoError implements ConfigReport {
    public static final NoError INSTANCE = new NoError();

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public Exception asException() {
        return NO_ERROR_IN_FACT;
    }

    @Override
    public String message() {
        return NO_ERROR_IN_FACT.getMessage();
    }
}
