package zzzank.probejs.utils.config.error;

/**
 * @author ZZZank
 */
public interface ConfigReport {

    boolean hasError();

    Exception asException();

    Exception NO_ERROR_IN_FACT = new Exception("there's no error");

    String message();
}
