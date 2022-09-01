package sun.security.ssl;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;

final class EphemeralKeyManager {
  private static final int INDEX_RSA512 = 0;
  
  private static final int INDEX_RSA1024 = 1;
  
  private final EphemeralKeyPair[] keys = new EphemeralKeyPair[] { new EphemeralKeyPair(null), new EphemeralKeyPair(null) };
  
  KeyPair getRSAKeyPair(boolean paramBoolean, SecureRandom paramSecureRandom) {
    char c;
    boolean bool;
    if (paramBoolean) {
      c = 'Ȁ';
      bool = false;
    } else {
      c = 'Ѐ';
      bool = true;
    } 
    synchronized (this.keys) {
      KeyPair keyPair = this.keys[bool].getKeyPair();
      if (keyPair == null)
        try {
          KeyPairGenerator keyPairGenerator = JsseJce.getKeyPairGenerator("RSA");
          keyPairGenerator.initialize(c, paramSecureRandom);
          this.keys[bool] = new EphemeralKeyPair(keyPairGenerator.genKeyPair());
          keyPair = this.keys[bool].getKeyPair();
        } catch (Exception exception) {} 
      return keyPair;
    } 
  }
  
  private static class EphemeralKeyPair {
    private static final int MAX_USE = 200;
    
    private static final long USE_INTERVAL = 3600000L;
    
    private KeyPair keyPair;
    
    private int uses;
    
    private long expirationTime;
    
    private EphemeralKeyPair(KeyPair param1KeyPair) {
      this.keyPair = param1KeyPair;
      this.expirationTime = System.currentTimeMillis() + 3600000L;
    }
    
    private boolean isValid() {
      return (this.keyPair != null && this.uses < 200 && 
        System.currentTimeMillis() < this.expirationTime);
    }
    
    private KeyPair getKeyPair() {
      if (!isValid()) {
        this.keyPair = null;
        return null;
      } 
      this.uses++;
      return this.keyPair;
    }
  }
}
