package sun.security.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import sun.security.x509.X509Key;

public class ECUtil {
  public static ECPoint decodePoint(byte[] paramArrayOfbyte, EllipticCurve paramEllipticCurve) throws IOException {
    if (paramArrayOfbyte.length == 0 || paramArrayOfbyte[0] != 4)
      throw new IOException("Only uncompressed point format supported"); 
    int i = (paramArrayOfbyte.length - 1) / 2;
    if (i != paramEllipticCurve.getField().getFieldSize() + 7 >> 3)
      throw new IOException("Point does not match field size"); 
    byte[] arrayOfByte1 = Arrays.copyOfRange(paramArrayOfbyte, 1, 1 + i);
    byte[] arrayOfByte2 = Arrays.copyOfRange(paramArrayOfbyte, i + 1, i + 1 + i);
    return new ECPoint(new BigInteger(1, arrayOfByte1), new BigInteger(1, arrayOfByte2));
  }
  
  public static byte[] encodePoint(ECPoint paramECPoint, EllipticCurve paramEllipticCurve) {
    int i = paramEllipticCurve.getField().getFieldSize() + 7 >> 3;
    byte[] arrayOfByte1 = trimZeroes(paramECPoint.getAffineX().toByteArray());
    byte[] arrayOfByte2 = trimZeroes(paramECPoint.getAffineY().toByteArray());
    if (arrayOfByte1.length > i || arrayOfByte2.length > i)
      throw new RuntimeException("Point coordinates do not match field size"); 
    byte[] arrayOfByte3 = new byte[1 + (i << 1)];
    arrayOfByte3[0] = 4;
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, i - arrayOfByte1.length + 1, arrayOfByte1.length);
    System.arraycopy(arrayOfByte2, 0, arrayOfByte3, arrayOfByte3.length - arrayOfByte2.length, arrayOfByte2.length);
    return arrayOfByte3;
  }
  
  public static byte[] trimZeroes(byte[] paramArrayOfbyte) {
    byte b = 0;
    while (b < paramArrayOfbyte.length - 1 && paramArrayOfbyte[b] == 0)
      b++; 
    if (b == 0)
      return paramArrayOfbyte; 
    return Arrays.copyOfRange(paramArrayOfbyte, b, paramArrayOfbyte.length);
  }
  
  private static KeyFactory getKeyFactory() {
    try {
      return KeyFactory.getInstance("EC", "SunEC");
    } catch (NoSuchAlgorithmException|java.security.NoSuchProviderException noSuchAlgorithmException) {
      throw new RuntimeException(noSuchAlgorithmException);
    } 
  }
  
  public static ECPublicKey decodeX509ECPublicKey(byte[] paramArrayOfbyte) throws InvalidKeySpecException {
    KeyFactory keyFactory = getKeyFactory();
    X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(paramArrayOfbyte);
    return (ECPublicKey)keyFactory.generatePublic(x509EncodedKeySpec);
  }
  
  public static byte[] x509EncodeECPublicKey(ECPoint paramECPoint, ECParameterSpec paramECParameterSpec) throws InvalidKeySpecException {
    KeyFactory keyFactory = getKeyFactory();
    ECPublicKeySpec eCPublicKeySpec = new ECPublicKeySpec(paramECPoint, paramECParameterSpec);
    X509Key x509Key = (X509Key)keyFactory.generatePublic(eCPublicKeySpec);
    return x509Key.getEncoded();
  }
  
  public static ECPrivateKey decodePKCS8ECPrivateKey(byte[] paramArrayOfbyte) throws InvalidKeySpecException {
    KeyFactory keyFactory = getKeyFactory();
    PKCS8EncodedKeySpec pKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(paramArrayOfbyte);
    return (ECPrivateKey)keyFactory.generatePrivate(pKCS8EncodedKeySpec);
  }
  
  public static ECPrivateKey generateECPrivateKey(BigInteger paramBigInteger, ECParameterSpec paramECParameterSpec) throws InvalidKeySpecException {
    KeyFactory keyFactory = getKeyFactory();
    ECPrivateKeySpec eCPrivateKeySpec = new ECPrivateKeySpec(paramBigInteger, paramECParameterSpec);
    return (ECPrivateKey)keyFactory.generatePrivate(eCPrivateKeySpec);
  }
  
  private static AlgorithmParameters getECParameters(Provider paramProvider) {
    try {
      if (paramProvider != null)
        return AlgorithmParameters.getInstance("EC", paramProvider); 
      return AlgorithmParameters.getInstance("EC");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new RuntimeException(noSuchAlgorithmException);
    } 
  }
  
  public static byte[] encodeECParameterSpec(Provider paramProvider, ECParameterSpec paramECParameterSpec) {
    AlgorithmParameters algorithmParameters = getECParameters(paramProvider);
    try {
      algorithmParameters.init(paramECParameterSpec);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      throw new RuntimeException("Not a known named curve: " + paramECParameterSpec);
    } 
    try {
      return algorithmParameters.getEncoded();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public static ECParameterSpec getECParameterSpec(Provider paramProvider, ECParameterSpec paramECParameterSpec) {
    AlgorithmParameters algorithmParameters = getECParameters(paramProvider);
    try {
      algorithmParameters.init(paramECParameterSpec);
      return algorithmParameters.<ECParameterSpec>getParameterSpec(ECParameterSpec.class);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      return null;
    } 
  }
  
  public static ECParameterSpec getECParameterSpec(Provider paramProvider, byte[] paramArrayOfbyte) throws IOException {
    AlgorithmParameters algorithmParameters = getECParameters(paramProvider);
    algorithmParameters.init(paramArrayOfbyte);
    try {
      return algorithmParameters.<ECParameterSpec>getParameterSpec(ECParameterSpec.class);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      return null;
    } 
  }
  
  public static ECParameterSpec getECParameterSpec(Provider paramProvider, String paramString) {
    AlgorithmParameters algorithmParameters = getECParameters(paramProvider);
    try {
      algorithmParameters.init(new ECGenParameterSpec(paramString));
      return algorithmParameters.<ECParameterSpec>getParameterSpec(ECParameterSpec.class);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      return null;
    } 
  }
  
  public static ECParameterSpec getECParameterSpec(Provider paramProvider, int paramInt) {
    AlgorithmParameters algorithmParameters = getECParameters(paramProvider);
    try {
      algorithmParameters.init(new ECKeySizeParameterSpec(paramInt));
      return algorithmParameters.<ECParameterSpec>getParameterSpec(ECParameterSpec.class);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      return null;
    } 
  }
  
  public static String getCurveName(Provider paramProvider, ECParameterSpec paramECParameterSpec) {
    ECGenParameterSpec eCGenParameterSpec;
    AlgorithmParameters algorithmParameters = getECParameters(paramProvider);
    try {
      algorithmParameters.init(paramECParameterSpec);
      eCGenParameterSpec = algorithmParameters.<ECGenParameterSpec>getParameterSpec(ECGenParameterSpec.class);
    } catch (InvalidParameterSpecException invalidParameterSpecException) {
      return null;
    } 
    if (eCGenParameterSpec == null)
      return null; 
    return eCGenParameterSpec.getName();
  }
}
