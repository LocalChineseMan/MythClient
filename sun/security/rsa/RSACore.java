package sun.security.rsa;

import java.math.BigInteger;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.WeakHashMap;
import javax.crypto.BadPaddingException;

public final class RSACore {
  private static final boolean ENABLE_BLINDING = true;
  
  private static final Map<BigInteger, BlindingParameters> blindingCache = new WeakHashMap<>();
  
  public static int getByteLength(BigInteger paramBigInteger) {
    int i = paramBigInteger.bitLength();
    return i + 7 >> 3;
  }
  
  public static int getByteLength(RSAKey paramRSAKey) {
    return getByteLength(paramRSAKey.getModulus());
  }
  
  public static byte[] convert(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramInt1 == 0 && paramInt2 == paramArrayOfbyte.length)
      return paramArrayOfbyte; 
    byte[] arrayOfByte = new byte[paramInt2];
    System.arraycopy(paramArrayOfbyte, paramInt1, arrayOfByte, 0, paramInt2);
    return arrayOfByte;
  }
  
  public static byte[] rsa(byte[] paramArrayOfbyte, RSAPublicKey paramRSAPublicKey) throws BadPaddingException {
    return crypt(paramArrayOfbyte, paramRSAPublicKey.getModulus(), paramRSAPublicKey.getPublicExponent());
  }
  
  @Deprecated
  public static byte[] rsa(byte[] paramArrayOfbyte, RSAPrivateKey paramRSAPrivateKey) throws BadPaddingException {
    return rsa(paramArrayOfbyte, paramRSAPrivateKey, true);
  }
  
  public static byte[] rsa(byte[] paramArrayOfbyte, RSAPrivateKey paramRSAPrivateKey, boolean paramBoolean) throws BadPaddingException {
    if (paramRSAPrivateKey instanceof RSAPrivateCrtKey)
      return crtCrypt(paramArrayOfbyte, (RSAPrivateCrtKey)paramRSAPrivateKey, paramBoolean); 
    return priCrypt(paramArrayOfbyte, paramRSAPrivateKey.getModulus(), paramRSAPrivateKey.getPrivateExponent());
  }
  
  private static byte[] crypt(byte[] paramArrayOfbyte, BigInteger paramBigInteger1, BigInteger paramBigInteger2) throws BadPaddingException {
    BigInteger bigInteger1 = parseMsg(paramArrayOfbyte, paramBigInteger1);
    BigInteger bigInteger2 = bigInteger1.modPow(paramBigInteger2, paramBigInteger1);
    return toByteArray(bigInteger2, getByteLength(paramBigInteger1));
  }
  
  private static byte[] priCrypt(byte[] paramArrayOfbyte, BigInteger paramBigInteger1, BigInteger paramBigInteger2) throws BadPaddingException {
    BigInteger bigInteger1 = parseMsg(paramArrayOfbyte, paramBigInteger1);
    BlindingRandomPair blindingRandomPair = null;
    blindingRandomPair = getBlindingRandomPair(null, paramBigInteger2, paramBigInteger1);
    bigInteger1 = bigInteger1.multiply(blindingRandomPair.u).mod(paramBigInteger1);
    BigInteger bigInteger2 = bigInteger1.modPow(paramBigInteger2, paramBigInteger1);
    bigInteger2 = bigInteger2.multiply(blindingRandomPair.v).mod(paramBigInteger1);
    return toByteArray(bigInteger2, getByteLength(paramBigInteger1));
  }
  
  private static byte[] crtCrypt(byte[] paramArrayOfbyte, RSAPrivateCrtKey paramRSAPrivateCrtKey, boolean paramBoolean) throws BadPaddingException {
    BigInteger bigInteger1 = paramRSAPrivateCrtKey.getModulus();
    BigInteger bigInteger2 = parseMsg(paramArrayOfbyte, bigInteger1);
    BigInteger bigInteger3 = bigInteger2;
    BigInteger bigInteger4 = paramRSAPrivateCrtKey.getPrimeP();
    BigInteger bigInteger5 = paramRSAPrivateCrtKey.getPrimeQ();
    BigInteger bigInteger6 = paramRSAPrivateCrtKey.getPrimeExponentP();
    BigInteger bigInteger7 = paramRSAPrivateCrtKey.getPrimeExponentQ();
    BigInteger bigInteger8 = paramRSAPrivateCrtKey.getCrtCoefficient();
    BigInteger bigInteger9 = paramRSAPrivateCrtKey.getPublicExponent();
    BigInteger bigInteger10 = paramRSAPrivateCrtKey.getPrivateExponent();
    BlindingRandomPair blindingRandomPair = getBlindingRandomPair(bigInteger9, bigInteger10, bigInteger1);
    bigInteger3 = bigInteger3.multiply(blindingRandomPair.u).mod(bigInteger1);
    BigInteger bigInteger11 = bigInteger3.modPow(bigInteger6, bigInteger4);
    BigInteger bigInteger12 = bigInteger3.modPow(bigInteger7, bigInteger5);
    BigInteger bigInteger13 = bigInteger11.subtract(bigInteger12);
    if (bigInteger13.signum() < 0)
      bigInteger13 = bigInteger13.add(bigInteger4); 
    BigInteger bigInteger14 = bigInteger13.multiply(bigInteger8).mod(bigInteger4);
    BigInteger bigInteger15 = bigInteger14.multiply(bigInteger5).add(bigInteger12);
    bigInteger15 = bigInteger15.multiply(blindingRandomPair.v).mod(bigInteger1);
    if (paramBoolean && !bigInteger2.equals(bigInteger15.modPow(bigInteger9, bigInteger1)))
      throw new BadPaddingException("RSA private key operation failed"); 
    return toByteArray(bigInteger15, getByteLength(bigInteger1));
  }
  
  private static BigInteger parseMsg(byte[] paramArrayOfbyte, BigInteger paramBigInteger) throws BadPaddingException {
    BigInteger bigInteger = new BigInteger(1, paramArrayOfbyte);
    if (bigInteger.compareTo(paramBigInteger) >= 0)
      throw new BadPaddingException("Message is larger than modulus"); 
    return bigInteger;
  }
  
  private static byte[] toByteArray(BigInteger paramBigInteger, int paramInt) {
    byte[] arrayOfByte1 = paramBigInteger.toByteArray();
    int i = arrayOfByte1.length;
    if (i == paramInt)
      return arrayOfByte1; 
    if (i == paramInt + 1 && arrayOfByte1[0] == 0) {
      byte[] arrayOfByte = new byte[paramInt];
      System.arraycopy(arrayOfByte1, 1, arrayOfByte, 0, paramInt);
      return arrayOfByte;
    } 
    assert i < paramInt;
    byte[] arrayOfByte2 = new byte[paramInt];
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, paramInt - i, i);
    return arrayOfByte2;
  }
  
  private static BlindingRandomPair getBlindingRandomPair(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3) {
    BlindingParameters blindingParameters = null;
    synchronized (blindingCache) {
      blindingParameters = blindingCache.get(paramBigInteger3);
    } 
    if (blindingParameters == null) {
      blindingParameters = new BlindingParameters(paramBigInteger1, paramBigInteger2, paramBigInteger3);
      synchronized (blindingCache) {
        blindingCache.putIfAbsent(paramBigInteger3, blindingParameters);
      } 
    } 
    BlindingRandomPair blindingRandomPair = blindingParameters.getBlindingRandomPair(paramBigInteger1, paramBigInteger2, paramBigInteger3);
    if (blindingRandomPair == null) {
      blindingParameters = new BlindingParameters(paramBigInteger1, paramBigInteger2, paramBigInteger3);
      synchronized (blindingCache) {
        blindingCache.replace(paramBigInteger3, blindingParameters);
      } 
      blindingRandomPair = blindingParameters.getBlindingRandomPair(paramBigInteger1, paramBigInteger2, paramBigInteger3);
    } 
    return blindingRandomPair;
  }
  
  private static final class RSACore {}
  
  private static final class RSACore {}
}
