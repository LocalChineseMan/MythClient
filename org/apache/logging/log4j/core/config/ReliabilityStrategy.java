package org.apache.logging.log4j.core.config;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.Supplier;

public interface ReliabilityStrategy {
  void log(Supplier<LoggerConfig> paramSupplier, String paramString1, String paramString2, Marker paramMarker, Level paramLevel, Message paramMessage, Throwable paramThrowable);
  
  void log(Supplier<LoggerConfig> paramSupplier, LogEvent paramLogEvent);
  
  LoggerConfig getActiveLoggerConfig(Supplier<LoggerConfig> paramSupplier);
  
  void afterLogEvent();
  
  void beforeStopAppenders();
  
  void beforeStopConfiguration(Configuration paramConfiguration);
}
