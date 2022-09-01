package com.sun.net.ssl.internal.ssl;

import sun.security.ssl.SunJSSE;

public final class Provider extends SunJSSE {
  private static final long serialVersionUID = 3231825739635378733L;
  
  public Provider() {}
  
  public Provider(java.security.Provider paramProvider) {
    super(paramProvider);
  }
  
  public Provider(String paramString) {
    super(paramString);
  }
  
  public static synchronized boolean isFIPS() {
    return SunJSSE.isFIPS();
  }
  
  public static synchronized void install() {}
}
