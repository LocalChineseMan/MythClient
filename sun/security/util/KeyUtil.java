package sun.security.util;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.interfaces.DSAKey;
import java.security.interfaces.ECKey;
import java.security.interfaces.RSAKey;
import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;

public final class KeyUtil {
  public static final int getKeySize(Key paramKey) {
    int i = -1;
    if (paramKey instanceof Length) {
      try {
        Length length = (Length)paramKey;
        i = length.length();
      } catch (UnsupportedOperationException unsupportedOperationException) {}
      if (i >= 0)
        return i; 
    } 
    if (paramKey instanceof SecretKey) {
      SecretKey secretKey = (SecretKey)paramKey;
      String str = secretKey.getFormat();
      if ("RAW".equals(str) && secretKey.getEncoded() != null)
        i = (secretKey.getEncoded()).length * 8; 
    } else if (paramKey instanceof RSAKey) {
      RSAKey rSAKey = (RSAKey)paramKey;
      i = rSAKey.getModulus().bitLength();
    } else if (paramKey instanceof ECKey) {
      ECKey eCKey = (ECKey)paramKey;
      i = eCKey.getParams().getOrder().bitLength();
    } else if (paramKey instanceof DSAKey) {
      DSAKey dSAKey = (DSAKey)paramKey;
      i = dSAKey.getParams().getP().bitLength();
    } else if (paramKey instanceof DHKey) {
      DHKey dHKey = (DHKey)paramKey;
      i = dHKey.getParams().getP().bitLength();
    } 
    return i;
  }
  
  public static final void validate(Key paramKey) throws InvalidKeyException {
    if (paramKey == null)
      throw new NullPointerException("The key to be validated cannot be null"); 
    if (paramKey instanceof DHPublicKey)
      validateDHPublicKey((DHPublicKey)paramKey); 
  }
  
  public static final void validate(KeySpec paramKeySpec) throws InvalidKeyException {
    if (paramKeySpec == null)
      throw new NullPointerException("The key spec to be validated cannot be null"); 
    if (paramKeySpec instanceof DHPublicKeySpec)
      validateDHPublicKey((DHPublicKeySpec)paramKeySpec); 
  }
  
  public static final boolean isOracleJCEProvider(String paramString) {
    return (paramString != null && (paramString.equals("SunJCE") || paramString
      .startsWith("SunPKCS11")));
  }
  
  public static byte[] checkTlsPreMasterSecretKey(int paramInt1, int paramInt2, SecureRandom paramSecureRandom, byte[] paramArrayOfbyte, boolean paramBoolean) {
    if (paramSecureRandom == null)
      paramSecureRandom = new SecureRandom(); 
    byte[] arrayOfByte = new byte[48];
    paramSecureRandom.nextBytes(arrayOfByte);
    if (!paramBoolean && paramArrayOfbyte != null) {
      if (paramArrayOfbyte.length != 48)
        return arrayOfByte; 
      int i = (paramArrayOfbyte[0] & 0xFF) << 8 | paramArrayOfbyte[1] & 0xFF;
      if (paramInt1 != i && (
        paramInt1 > 769 || paramInt2 != i))
        paramArrayOfbyte = arrayOfByte; 
      return paramArrayOfbyte;
    } 
    return arrayOfByte;
  }
  
  private static void validateDHPublicKey(DHPublicKey paramDHPublicKey) throws InvalidKeyException {
    DHParameterSpec dHParameterSpec = paramDHPublicKey.getParams();
    BigInteger bigInteger1 = dHParameterSpec.getP();
    BigInteger bigInteger2 = dHParameterSpec.getG();
    BigInteger bigInteger3 = paramDHPublicKey.getY();
    validateDHPublicKey(bigInteger1, bigInteger2, bigInteger3);
  }
  
  private static void validateDHPublicKey(DHPublicKeySpec paramDHPublicKeySpec) throws InvalidKeyException {
    validateDHPublicKey(paramDHPublicKeySpec.getP(), paramDHPublicKeySpec
        .getG(), paramDHPublicKeySpec.getY());
  }
  
  private static void validateDHPublicKey(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) throws InvalidKeyException {
    BigInteger bigInteger1 = BigInteger.ONE;
    BigInteger bigInteger2 = paramBigInteger1.subtract(BigInteger.ONE);
    if (paramBigInteger3.compareTo(bigInteger1) <= 0)
      throw new InvalidKeyException("Diffie-Hellman public key is too small"); 
    if (paramBigInteger3.compareTo(bigInteger2) >= 0)
      throw new InvalidKeyException("Diffie-Hellman public key is too large"); 
    BigInteger bigInteger3 = paramBigInteger1.remainder(paramBigInteger3);
    if (bigInteger3.equals(BigInteger.ZERO))
      throw new InvalidKeyException("Invalid Diffie-Hellman parameters"); 
  }
  
  public static byte[] trimZeroes(byte[] paramArrayOfbyte) {
    byte b = 0;
    while (b < paramArrayOfbyte.length - 1 && paramArrayOfbyte[b] == 0)
      b++; 
    if (b == 0)
      return paramArrayOfbyte; 
    byte[] arrayOfByte = new byte[paramArrayOfbyte.length - b];
    System.arraycopy(paramArrayOfbyte, b, arrayOfByte, 0, arrayOfByte.length);
    return arrayOfByte;
  }
}
