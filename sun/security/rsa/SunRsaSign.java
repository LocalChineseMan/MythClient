package sun.security.rsa;

import java.security.AccessController;
import java.security.Provider;
import java.util.HashMap;
import sun.security.action.PutAllAction;

public final class SunRsaSign extends Provider {
  private static final long serialVersionUID = 866040293550393045L;
  
  public SunRsaSign() {
    super("SunRsaSign", 1.8D, "Sun RSA signature provider");
    if (System.getSecurityManager() == null) {
      SunRsaSignEntries.putEntries(this);
    } else {
      HashMap<Object, Object> hashMap = new HashMap<>();
      SunRsaSignEntries.putEntries(hashMap);
      AccessController.doPrivileged(new PutAllAction(this, hashMap));
    } 
  }
}
