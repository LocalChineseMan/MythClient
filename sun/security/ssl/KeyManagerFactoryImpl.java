package sun.security.ssl;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.X509ExtendedKeyManager;

abstract class KeyManagerFactoryImpl extends KeyManagerFactorySpi {
  X509ExtendedKeyManager keyManager;
  
  boolean isInitialized;
  
  protected KeyManager[] engineGetKeyManagers() {
    if (!this.isInitialized)
      throw new IllegalStateException("KeyManagerFactoryImpl is not initialized"); 
    return new KeyManager[] { this.keyManager };
  }
  
  public static final class KeyManagerFactoryImpl {}
  
  public static final class SunX509 extends KeyManagerFactoryImpl {
    protected void engineInit(KeyStore param1KeyStore, char[] param1ArrayOfchar) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      if (param1KeyStore != null && SunJSSE.isFIPS() && 
        param1KeyStore.getProvider() != SunJSSE.cryptoProvider)
        throw new KeyStoreException("FIPS mode: KeyStore must be from provider " + SunJSSE.cryptoProvider
            .getName()); 
      this.keyManager = new SunX509KeyManagerImpl(param1KeyStore, param1ArrayOfchar);
      this.isInitialized = true;
    }
    
    protected void engineInit(ManagerFactoryParameters param1ManagerFactoryParameters) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("SunX509KeyManager does not use ManagerFactoryParameters");
    }
  }
}
