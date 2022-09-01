package org.apache.logging.log4j.core;

public interface ErrorHandler {
  void error(String paramString);
  
  void error(String paramString, Throwable paramThrowable);
  
  void error(String paramString, LogEvent paramLogEvent, Throwable paramThrowable);
}
