package org.apache.logging.log4j.core.util;

import org.apache.logging.log4j.util.Strings;

public final class Integers {
  private static final int BITS_PER_INT = 32;
  
  public static int parseInt(String s, int defaultValue) {
    return Strings.isEmpty(s) ? defaultValue : Integer.parseInt(s);
  }
  
  public static int parseInt(String s) {
    return parseInt(s, 0);
  }
  
  public static int ceilingNextPowerOfTwo(int x) {
    return 1 << 32 - Integer.numberOfLeadingZeros(x - 1);
  }
}
