package sun.security.ec;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.util.HashMap;
import sun.security.action.PutAllAction;

public final class SunEC extends Provider {
  private static final long serialVersionUID = -2279741672933606418L;
  
  private static boolean useFullImplementation = true;
  
  static {
    try {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
              System.loadLibrary("sunec");
              return null;
            }
          });
    } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
      useFullImplementation = false;
    } 
  }
  
  public SunEC() {
    super("SunEC", 1.8D, "Sun Elliptic Curve provider (EC, ECDSA, ECDH)");
    if (System.getSecurityManager() == null) {
      SunECEntries.putEntries(this, useFullImplementation);
    } else {
      HashMap<Object, Object> hashMap = new HashMap<>();
      SunECEntries.putEntries(hashMap, useFullImplementation);
      AccessController.doPrivileged(new PutAllAction(this, hashMap));
    } 
  }
}
