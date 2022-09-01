package sun.security.ec;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyFactorySpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public final class ECKeyFactory extends KeyFactorySpi {
  private static KeyFactory instance;
  
  private static KeyFactory getInstance() {
    if (instance == null)
      try {
        instance = KeyFactory.getInstance("EC", "SunEC");
      } catch (NoSuchProviderException noSuchProviderException) {
        throw new RuntimeException(noSuchProviderException);
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new RuntimeException(noSuchAlgorithmException);
      }  
    return instance;
  }
  
  public static ECKey toECKey(Key paramKey) throws InvalidKeyException {
    if (paramKey instanceof ECKey) {
      ECKey eCKey = (ECKey)paramKey;
      checkKey(eCKey);
      return eCKey;
    } 
    return (ECKey)getInstance().translateKey(paramKey);
  }
  
  private static void checkKey(ECKey paramECKey) throws InvalidKeyException {
    if (paramECKey instanceof ECPublicKey) {
      if (paramECKey instanceof ECPublicKeyImpl)
        return; 
    } else if (paramECKey instanceof ECPrivateKey) {
      if (paramECKey instanceof ECPrivateKeyImpl)
        return; 
    } else {
      throw new InvalidKeyException("Neither a public nor a private key");
    } 
    String str = ((Key)paramECKey).getAlgorithm();
    if (!str.equals("EC"))
      throw new InvalidKeyException("Not an EC key: " + str); 
  }
  
  protected Key engineTranslateKey(Key paramKey) throws InvalidKeyException {
    if (paramKey == null)
      throw new InvalidKeyException("Key must not be null"); 
    String str = paramKey.getAlgorithm();
    if (!str.equals("EC"))
      throw new InvalidKeyException("Not an EC key: " + str); 
    if (paramKey instanceof PublicKey)
      return implTranslatePublicKey((PublicKey)paramKey); 
    if (paramKey instanceof PrivateKey)
      return implTranslatePrivateKey((PrivateKey)paramKey); 
    throw new InvalidKeyException("Neither a public nor a private key");
  }
  
  protected PublicKey engineGeneratePublic(KeySpec paramKeySpec) throws InvalidKeySpecException {
    try {
      return implGeneratePublic(paramKeySpec);
    } catch (InvalidKeySpecException invalidKeySpecException) {
      throw invalidKeySpecException;
    } catch (GeneralSecurityException generalSecurityException) {
      throw new InvalidKeySpecException(generalSecurityException);
    } 
  }
  
  protected PrivateKey engineGeneratePrivate(KeySpec paramKeySpec) throws InvalidKeySpecException {
    try {
      return implGeneratePrivate(paramKeySpec);
    } catch (InvalidKeySpecException invalidKeySpecException) {
      throw invalidKeySpecException;
    } catch (GeneralSecurityException generalSecurityException) {
      throw new InvalidKeySpecException(generalSecurityException);
    } 
  }
  
  private PublicKey implTranslatePublicKey(PublicKey paramPublicKey) throws InvalidKeyException {
    if (paramPublicKey instanceof ECPublicKey) {
      if (paramPublicKey instanceof ECPublicKeyImpl)
        return paramPublicKey; 
      ECPublicKey eCPublicKey = (ECPublicKey)paramPublicKey;
      return new ECPublicKeyImpl(eCPublicKey
          .getW(), eCPublicKey
          .getParams());
    } 
    if ("X.509".equals(paramPublicKey.getFormat())) {
      byte[] arrayOfByte = paramPublicKey.getEncoded();
      return new ECPublicKeyImpl(arrayOfByte);
    } 
    throw new InvalidKeyException("Public keys must be instance of ECPublicKey or have X.509 encoding");
  }
  
  private PrivateKey implTranslatePrivateKey(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (paramPrivateKey instanceof ECPrivateKey) {
      if (paramPrivateKey instanceof ECPrivateKeyImpl)
        return paramPrivateKey; 
      ECPrivateKey eCPrivateKey = (ECPrivateKey)paramPrivateKey;
      return new ECPrivateKeyImpl(eCPrivateKey
          .getS(), eCPrivateKey
          .getParams());
    } 
    if ("PKCS#8".equals(paramPrivateKey.getFormat()))
      return new ECPrivateKeyImpl(paramPrivateKey.getEncoded()); 
    throw new InvalidKeyException("Private keys must be instance of ECPrivateKey or have PKCS#8 encoding");
  }
  
  private PublicKey implGeneratePublic(KeySpec paramKeySpec) throws GeneralSecurityException {
    if (paramKeySpec instanceof X509EncodedKeySpec) {
      X509EncodedKeySpec x509EncodedKeySpec = (X509EncodedKeySpec)paramKeySpec;
      return new ECPublicKeyImpl(x509EncodedKeySpec.getEncoded());
    } 
    if (paramKeySpec instanceof ECPublicKeySpec) {
      ECPublicKeySpec eCPublicKeySpec = (ECPublicKeySpec)paramKeySpec;
      return new ECPublicKeyImpl(eCPublicKeySpec
          .getW(), eCPublicKeySpec
          .getParams());
    } 
    throw new InvalidKeySpecException("Only ECPublicKeySpec and X509EncodedKeySpec supported for EC public keys");
  }
  
  private PrivateKey implGeneratePrivate(KeySpec paramKeySpec) throws GeneralSecurityException {
    if (paramKeySpec instanceof PKCS8EncodedKeySpec) {
      PKCS8EncodedKeySpec pKCS8EncodedKeySpec = (PKCS8EncodedKeySpec)paramKeySpec;
      return new ECPrivateKeyImpl(pKCS8EncodedKeySpec.getEncoded());
    } 
    if (paramKeySpec instanceof ECPrivateKeySpec) {
      ECPrivateKeySpec eCPrivateKeySpec = (ECPrivateKeySpec)paramKeySpec;
      return new ECPrivateKeyImpl(eCPrivateKeySpec.getS(), eCPrivateKeySpec.getParams());
    } 
    throw new InvalidKeySpecException("Only ECPrivateKeySpec and PKCS8EncodedKeySpec supported for EC private keys");
  }
  
  protected <T extends KeySpec> T engineGetKeySpec(Key paramKey, Class<T> paramClass) throws InvalidKeySpecException {
    try {
      paramKey = engineTranslateKey(paramKey);
    } catch (InvalidKeyException invalidKeyException) {
      throw new InvalidKeySpecException(invalidKeyException);
    } 
    if (paramKey instanceof ECPublicKey) {
      ECPublicKey eCPublicKey = (ECPublicKey)paramKey;
      if (ECPublicKeySpec.class.isAssignableFrom(paramClass))
        return paramClass.cast(new ECPublicKeySpec(eCPublicKey
              .getW(), eCPublicKey
              .getParams())); 
      if (X509EncodedKeySpec.class.isAssignableFrom(paramClass))
        return paramClass.cast(new X509EncodedKeySpec(paramKey.getEncoded())); 
      throw new InvalidKeySpecException("KeySpec must be ECPublicKeySpec or X509EncodedKeySpec for EC public keys");
    } 
    if (paramKey instanceof ECPrivateKey) {
      if (PKCS8EncodedKeySpec.class.isAssignableFrom(paramClass))
        return paramClass.cast(new PKCS8EncodedKeySpec(paramKey.getEncoded())); 
      if (ECPrivateKeySpec.class.isAssignableFrom(paramClass)) {
        ECPrivateKey eCPrivateKey = (ECPrivateKey)paramKey;
        return paramClass.cast(new ECPrivateKeySpec(eCPrivateKey
              .getS(), eCPrivateKey
              .getParams()));
      } 
      throw new InvalidKeySpecException("KeySpec must be ECPrivateKeySpec or PKCS8EncodedKeySpec for EC private keys");
    } 
    throw new InvalidKeySpecException("Neither public nor private key");
  }
}
