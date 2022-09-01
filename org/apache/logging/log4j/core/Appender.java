package org.apache.logging.log4j.core;

import java.io.Serializable;

public interface Appender extends LifeCycle {
  public static final String ELEMENT_TYPE = "appender";
  
  void append(LogEvent paramLogEvent);
  
  String getName();
  
  Layout<? extends Serializable> getLayout();
  
  boolean ignoreExceptions();
  
  ErrorHandler getHandler();
  
  void setHandler(ErrorHandler paramErrorHandler);
}
