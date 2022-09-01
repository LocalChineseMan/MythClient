package org.apache.logging.log4j.spi;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;

public interface LocationAwareLogger {
  void logMessage(Level paramLevel, Marker paramMarker, String paramString, StackTraceElement paramStackTraceElement, Message paramMessage, Throwable paramThrowable);
}
