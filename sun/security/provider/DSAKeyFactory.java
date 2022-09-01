package sun.security.provider;

import java.security.AccessController;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactorySpi;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import sun.security.action.GetPropertyAction;

public class DSAKeyFactory extends KeyFactorySpi {
  static final boolean SERIAL_INTEROP;
  
  private static final String SERIAL_PROP = "sun.security.key.serial.interop";
  
  static {
    String str = AccessController.<String>doPrivileged(new GetPropertyAction("sun.security.key.serial.interop", null));
    SERIAL_INTEROP = "true".equalsIgnoreCase(str);
  }
  
  protected PublicKey engineGeneratePublic(KeySpec paramKeySpec) throws InvalidKeySpecException {
    try {
      if (paramKeySpec instanceof DSAPublicKeySpec) {
        DSAPublicKeySpec dSAPublicKeySpec = (DSAPublicKeySpec)paramKeySpec;
        if (SERIAL_INTEROP)
          return new DSAPublicKey(dSAPublicKeySpec.getY(), dSAPublicKeySpec
              .getP(), dSAPublicKeySpec
              .getQ(), dSAPublicKeySpec
              .getG()); 
        return new DSAPublicKeyImpl(dSAPublicKeySpec.getY(), dSAPublicKeySpec
            .getP(), dSAPublicKeySpec
            .getQ(), dSAPublicKeySpec
            .getG());
      } 
      if (paramKeySpec instanceof X509EncodedKeySpec) {
        if (SERIAL_INTEROP)
          return new DSAPublicKey(((X509EncodedKeySpec)paramKeySpec)
              .getEncoded()); 
        return new DSAPublicKeyImpl(((X509EncodedKeySpec)paramKeySpec)
            .getEncoded());
      } 
      throw new InvalidKeySpecException("Inappropriate key specification");
    } catch (InvalidKeyException invalidKeyException) {
      throw new InvalidKeySpecException("Inappropriate key specification: " + invalidKeyException
          .getMessage());
    } 
  }
  
  protected PrivateKey engineGeneratePrivate(KeySpec paramKeySpec) throws InvalidKeySpecException {
    try {
      if (paramKeySpec instanceof DSAPrivateKeySpec) {
        DSAPrivateKeySpec dSAPrivateKeySpec = (DSAPrivateKeySpec)paramKeySpec;
        return new DSAPrivateKey(dSAPrivateKeySpec.getX(), dSAPrivateKeySpec
            .getP(), dSAPrivateKeySpec
            .getQ(), dSAPrivateKeySpec
            .getG());
      } 
      if (paramKeySpec instanceof PKCS8EncodedKeySpec)
        return new DSAPrivateKey(((PKCS8EncodedKeySpec)paramKeySpec)
            .getEncoded()); 
      throw new InvalidKeySpecException("Inappropriate key specification");
    } catch (InvalidKeyException invalidKeyException) {
      throw new InvalidKeySpecException("Inappropriate key specification: " + invalidKeyException
          .getMessage());
    } 
  }
  
  protected <T extends KeySpec> T engineGetKeySpec(Key paramKey, Class<T> paramClass) throws InvalidKeySpecException {
    try {
      if (paramKey instanceof DSAPublicKey) {
        Class<?> clazz1 = Class.forName("java.security.spec.DSAPublicKeySpec");
        Class<?> clazz2 = Class.forName("java.security.spec.X509EncodedKeySpec");
        if (clazz1.isAssignableFrom(paramClass)) {
          DSAPublicKey dSAPublicKey = (DSAPublicKey)paramKey;
          DSAParams dSAParams = dSAPublicKey.getParams();
          return paramClass.cast(new DSAPublicKeySpec(dSAPublicKey.getY(), dSAParams
                .getP(), dSAParams
                .getQ(), dSAParams
                .getG()));
        } 
        if (clazz2.isAssignableFrom(paramClass))
          return paramClass.cast(new X509EncodedKeySpec(paramKey.getEncoded())); 
        throw new InvalidKeySpecException("Inappropriate key specification");
      } 
      if (paramKey instanceof DSAPrivateKey) {
        Class<?> clazz1 = Class.forName("java.security.spec.DSAPrivateKeySpec");
        Class<?> clazz2 = Class.forName("java.security.spec.PKCS8EncodedKeySpec");
        if (clazz1.isAssignableFrom(paramClass)) {
          DSAPrivateKey dSAPrivateKey = (DSAPrivateKey)paramKey;
          DSAParams dSAParams = dSAPrivateKey.getParams();
          return paramClass.cast(new DSAPrivateKeySpec(dSAPrivateKey.getX(), dSAParams
                .getP(), dSAParams
                .getQ(), dSAParams
                .getG()));
        } 
        if (clazz2.isAssignableFrom(paramClass))
          return paramClass.cast(new PKCS8EncodedKeySpec(paramKey.getEncoded())); 
        throw new InvalidKeySpecException("Inappropriate key specification");
      } 
      throw new InvalidKeySpecException("Inappropriate key type");
    } catch (ClassNotFoundException classNotFoundException) {
      throw new InvalidKeySpecException("Unsupported key specification: " + classNotFoundException
          .getMessage());
    } 
  }
  
  protected Key engineTranslateKey(Key paramKey) throws InvalidKeyException {
    try {
      if (paramKey instanceof DSAPublicKey) {
        if (paramKey instanceof DSAPublicKey)
          return paramKey; 
        DSAPublicKeySpec dSAPublicKeySpec = engineGetKeySpec(paramKey, DSAPublicKeySpec.class);
        return engineGeneratePublic(dSAPublicKeySpec);
      } 
      if (paramKey instanceof DSAPrivateKey) {
        if (paramKey instanceof DSAPrivateKey)
          return paramKey; 
        DSAPrivateKeySpec dSAPrivateKeySpec = engineGetKeySpec(paramKey, DSAPrivateKeySpec.class);
        return engineGeneratePrivate(dSAPrivateKeySpec);
      } 
      throw new InvalidKeyException("Wrong algorithm type");
    } catch (InvalidKeySpecException invalidKeySpecException) {
      throw new InvalidKeyException("Cannot translate key: " + invalidKeySpecException
          .getMessage());
    } 
  }
}
