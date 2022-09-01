package org.apache.logging.log4j.spi;

import java.util.List;

public interface LoggerContextShutdownEnabled {
  void addShutdownListener(LoggerContextShutdownAware paramLoggerContextShutdownAware);
  
  List<LoggerContextShutdownAware> getListeners();
}
