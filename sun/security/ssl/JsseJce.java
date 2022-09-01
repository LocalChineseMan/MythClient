package sun.security.ssl;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.KeyFactory;
import java.security.KeyManagementException;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedExceptionAction;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import sun.security.jca.ProviderList;
import sun.security.jca.Providers;
import sun.security.util.ECUtil;

final class JsseJce {
  private static final ProviderList fipsProviderList;
  
  private static Boolean ecAvailable;
  
  private static final boolean kerberosAvailable;
  
  static final String CIPHER_RSA_PKCS1 = "RSA/ECB/PKCS1Padding";
  
  static final String CIPHER_RC4 = "RC4";
  
  static final String CIPHER_DES = "DES/CBC/NoPadding";
  
  static final String CIPHER_3DES = "DESede/CBC/NoPadding";
  
  static final String CIPHER_AES = "AES/CBC/NoPadding";
  
  static final String CIPHER_AES_GCM = "AES/GCM/NoPadding";
  
  static final String SIGNATURE_DSA = "DSA";
  
  static final String SIGNATURE_ECDSA = "SHA1withECDSA";
  
  static final String SIGNATURE_RAWDSA = "RawDSA";
  
  static final String SIGNATURE_RAWECDSA = "NONEwithECDSA";
  
  static final String SIGNATURE_RAWRSA = "NONEwithRSA";
  
  static final String SIGNATURE_SSLRSA = "MD5andSHA1withRSA";
  
  static {
    boolean bool;
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws Exception {
              Class.forName("sun.security.krb5.PrincipalName", true, (ClassLoader)null);
              return null;
            }
          });
      bool = true;
    } catch (Exception exception) {
      bool = false;
    } 
    kerberosAvailable = bool;
    if (!SunJSSE.isFIPS()) {
      fipsProviderList = null;
    } else {
      Provider provider = Security.getProvider("SUN");
      if (provider == null)
        throw new RuntimeException("FIPS mode: SUN provider must be installed"); 
      SunCertificates sunCertificates = new SunCertificates(provider);
      fipsProviderList = ProviderList.newList(new Provider[] { SunJSSE.cryptoProvider, (Provider)sunCertificates });
    } 
  }
  
  private static final class JsseJce {}
  
  static synchronized boolean isEcAvailable() {
    if (ecAvailable == null)
      try {
        getSignature("SHA1withECDSA");
        getSignature("NONEwithECDSA");
        getKeyAgreement("ECDH");
        getKeyFactory("EC");
        getKeyPairGenerator("EC");
        ecAvailable = Boolean.valueOf(true);
      } catch (Exception exception) {
        ecAvailable = Boolean.valueOf(false);
      }  
    return ecAvailable.booleanValue();
  }
  
  static synchronized void clearEcAvailable() {
    ecAvailable = null;
  }
  
  static boolean isKerberosAvailable() {
    return kerberosAvailable;
  }
  
  static Cipher getCipher(String paramString) throws NoSuchAlgorithmException {
    try {
      if (SunJSSE.cryptoProvider == null)
        return Cipher.getInstance(paramString); 
      return Cipher.getInstance(paramString, SunJSSE.cryptoProvider);
    } catch (NoSuchPaddingException noSuchPaddingException) {
      throw new NoSuchAlgorithmException(noSuchPaddingException);
    } 
  }
  
  static Signature getSignature(String paramString) throws NoSuchAlgorithmException {
    if (SunJSSE.cryptoProvider == null)
      return Signature.getInstance(paramString); 
    if (paramString == "MD5andSHA1withRSA")
      if (SunJSSE.cryptoProvider.getService("Signature", paramString) == null)
        try {
          return Signature.getInstance(paramString, "SunJSSE");
        } catch (NoSuchProviderException noSuchProviderException) {
          throw new NoSuchAlgorithmException(noSuchProviderException);
        }   
    return Signature.getInstance(paramString, SunJSSE.cryptoProvider);
  }
  
  static KeyGenerator getKeyGenerator(String paramString) throws NoSuchAlgorithmException {
    if (SunJSSE.cryptoProvider == null)
      return KeyGenerator.getInstance(paramString); 
    return KeyGenerator.getInstance(paramString, SunJSSE.cryptoProvider);
  }
  
  static KeyPairGenerator getKeyPairGenerator(String paramString) throws NoSuchAlgorithmException {
    if (SunJSSE.cryptoProvider == null)
      return KeyPairGenerator.getInstance(paramString); 
    return KeyPairGenerator.getInstance(paramString, SunJSSE.cryptoProvider);
  }
  
  static KeyAgreement getKeyAgreement(String paramString) throws NoSuchAlgorithmException {
    if (SunJSSE.cryptoProvider == null)
      return KeyAgreement.getInstance(paramString); 
    return KeyAgreement.getInstance(paramString, SunJSSE.cryptoProvider);
  }
  
  static Mac getMac(String paramString) throws NoSuchAlgorithmException {
    if (SunJSSE.cryptoProvider == null)
      return Mac.getInstance(paramString); 
    return Mac.getInstance(paramString, SunJSSE.cryptoProvider);
  }
  
  static KeyFactory getKeyFactory(String paramString) throws NoSuchAlgorithmException {
    if (SunJSSE.cryptoProvider == null)
      return KeyFactory.getInstance(paramString); 
    return KeyFactory.getInstance(paramString, SunJSSE.cryptoProvider);
  }
  
  static SecureRandom getSecureRandom() throws KeyManagementException {
    if (SunJSSE.cryptoProvider == null)
      return new SecureRandom(); 
    try {
      return SecureRandom.getInstance("PKCS11", SunJSSE.cryptoProvider);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      for (Provider.Service service : SunJSSE.cryptoProvider.getServices()) {
        if (service.getType().equals("SecureRandom"))
          try {
            return SecureRandom.getInstance(service.getAlgorithm(), SunJSSE.cryptoProvider);
          } catch (NoSuchAlgorithmException noSuchAlgorithmException1) {} 
      } 
      throw new KeyManagementException("FIPS mode: no SecureRandom  implementation found in provider " + SunJSSE.cryptoProvider
          .getName());
    } 
  }
  
  static MessageDigest getMD5() {
    return getMessageDigest("MD5");
  }
  
  static MessageDigest getSHA() {
    return getMessageDigest("SHA");
  }
  
  static MessageDigest getMessageDigest(String paramString) {
    try {
      if (SunJSSE.cryptoProvider == null)
        return MessageDigest.getInstance(paramString); 
      return MessageDigest.getInstance(paramString, SunJSSE.cryptoProvider);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new RuntimeException("Algorithm " + paramString + " not available", noSuchAlgorithmException);
    } 
  }
  
  static int getRSAKeyLength(PublicKey paramPublicKey) {
    BigInteger bigInteger;
    if (paramPublicKey instanceof RSAPublicKey) {
      bigInteger = ((RSAPublicKey)paramPublicKey).getModulus();
    } else {
      RSAPublicKeySpec rSAPublicKeySpec = getRSAPublicKeySpec(paramPublicKey);
      bigInteger = rSAPublicKeySpec.getModulus();
    } 
    return bigInteger.bitLength();
  }
  
  static RSAPublicKeySpec getRSAPublicKeySpec(PublicKey paramPublicKey) {
    if (paramPublicKey instanceof RSAPublicKey) {
      RSAPublicKey rSAPublicKey = (RSAPublicKey)paramPublicKey;
      return new RSAPublicKeySpec(rSAPublicKey.getModulus(), rSAPublicKey
          .getPublicExponent());
    } 
    try {
      KeyFactory keyFactory = getKeyFactory("RSA");
      return keyFactory.<RSAPublicKeySpec>getKeySpec(paramPublicKey, RSAPublicKeySpec.class);
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    } 
  }
  
  static ECParameterSpec getECParameterSpec(String paramString) {
    return ECUtil.getECParameterSpec(SunJSSE.cryptoProvider, paramString);
  }
  
  static String getNamedCurveOid(ECParameterSpec paramECParameterSpec) {
    return ECUtil.getCurveName(SunJSSE.cryptoProvider, paramECParameterSpec);
  }
  
  static ECPoint decodePoint(byte[] paramArrayOfbyte, EllipticCurve paramEllipticCurve) throws IOException {
    return ECUtil.decodePoint(paramArrayOfbyte, paramEllipticCurve);
  }
  
  static byte[] encodePoint(ECPoint paramECPoint, EllipticCurve paramEllipticCurve) {
    return ECUtil.encodePoint(paramECPoint, paramEllipticCurve);
  }
  
  static Object beginFipsProvider() {
    if (fipsProviderList == null)
      return null; 
    return Providers.beginThreadProviderList(fipsProviderList);
  }
  
  static void endFipsProvider(Object paramObject) {
    if (fipsProviderList != null)
      Providers.endThreadProviderList((ProviderList)paramObject); 
  }
}
