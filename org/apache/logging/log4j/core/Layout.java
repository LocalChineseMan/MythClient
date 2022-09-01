package org.apache.logging.log4j.core;

import java.util.Map;
import org.apache.logging.log4j.core.layout.Encoder;

public interface Layout<T extends java.io.Serializable> extends Encoder<LogEvent> {
  public static final String ELEMENT_TYPE = "layout";
  
  byte[] getFooter();
  
  byte[] getHeader();
  
  byte[] toByteArray(LogEvent paramLogEvent);
  
  T toSerializable(LogEvent paramLogEvent);
  
  String getContentType();
  
  Map<String, String> getContentFormat();
}
