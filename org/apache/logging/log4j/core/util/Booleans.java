package org.apache.logging.log4j.core.util;

public final class Booleans {
  public static boolean parseBoolean(String s, boolean defaultValue) {
    return ("true".equalsIgnoreCase(s) || (defaultValue && !"false".equalsIgnoreCase(s)));
  }
}
