package sun.security.provider;

public final class NativePRNG {
  static boolean isAvailable() {
    return false;
  }
  
  public static final class NonBlocking {
    static boolean isAvailable() {
      return false;
    }
  }
  
  public static final class Blocking {
    static boolean isAvailable() {
      return false;
    }
  }
}
