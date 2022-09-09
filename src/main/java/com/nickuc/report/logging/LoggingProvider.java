package com.nickuc.report.logging;

public interface LoggingProvider {

    void info(String message);

    void error(String message);

    void warn(String message);

}
