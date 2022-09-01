package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;

public abstract class LogEventPatternConverter extends AbstractPatternConverter {
  protected LogEventPatternConverter(String name, String style) {
    super(name, style);
  }
  
  public abstract void format(LogEvent paramLogEvent, StringBuilder paramStringBuilder);
  
  public void format(Object obj, StringBuilder output) {
    if (obj instanceof LogEvent)
      format((LogEvent)obj, output); 
  }
  
  public boolean handlesThrowable() {
    return false;
  }
  
  public boolean isVariable() {
    return true;
  }
}
