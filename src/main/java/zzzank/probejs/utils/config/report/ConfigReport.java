package zzzank.probejs.utils.config.report;

/**
 * @author ZZZank
 */
public interface ConfigReport {

    boolean hasError();

    Exception asException();

    String message();
}
