package org.apache.logging.log4j.spi;

public interface LoggerContextShutdownAware {
  void contextShutdown(LoggerContext paramLoggerContext);
}
