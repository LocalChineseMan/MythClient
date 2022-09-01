package org.apache.logging.log4j.core.pattern;

public final class NotANumber {
  public static final NotANumber NAN = new NotANumber();
  
  public static final String VALUE = "\000";
  
  public String toString() {
    return "\000";
  }
}
