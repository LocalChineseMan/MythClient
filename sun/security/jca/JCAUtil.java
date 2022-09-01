package sun.security.jca;

import java.security.SecureRandom;

public final class JCAUtil {
  private static final Object LOCK = JCAUtil.class;
  
  private static volatile SecureRandom secureRandom;
  
  private static final int ARRAY_SIZE = 4096;
  
  public static int getTempArraySize(int paramInt) {
    return Math.min(4096, paramInt);
  }
  
  public static SecureRandom getSecureRandom() {
    SecureRandom secureRandom = secureRandom;
    if (secureRandom == null)
      synchronized (LOCK) {
        secureRandom = secureRandom;
        if (secureRandom == null) {
          secureRandom = new SecureRandom();
          secureRandom = secureRandom;
        } 
      }  
    return secureRandom;
  }
}
