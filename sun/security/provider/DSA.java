package sun.security.provider;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.SignatureSpi;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.util.Arrays;
import sun.security.jca.JCAUtil;
import sun.security.util.Debug;
import sun.security.util.DerInputStream;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;

abstract class DSA extends SignatureSpi {
  private static final boolean debug = false;
  
  private DSAParams params;
  
  private BigInteger presetP;
  
  private BigInteger presetQ;
  
  private BigInteger presetG;
  
  private BigInteger presetY;
  
  private BigInteger presetX;
  
  private SecureRandom signingRandom;
  
  private final MessageDigest md;
  
  DSA(MessageDigest paramMessageDigest) {
    this.md = paramMessageDigest;
  }
  
  protected void engineInitSign(PrivateKey paramPrivateKey) throws InvalidKeyException {
    if (!(paramPrivateKey instanceof DSAPrivateKey))
      throw new InvalidKeyException("not a DSA private key: " + paramPrivateKey); 
    DSAPrivateKey dSAPrivateKey = (DSAPrivateKey)paramPrivateKey;
    DSAParams dSAParams = dSAPrivateKey.getParams();
    if (dSAParams == null)
      throw new InvalidKeyException("DSA private key lacks parameters"); 
    this.params = dSAParams;
    this.presetX = dSAPrivateKey.getX();
    this.presetY = null;
    this.presetP = dSAParams.getP();
    this.presetQ = dSAParams.getQ();
    this.presetG = dSAParams.getG();
    this.md.reset();
  }
  
  protected void engineInitVerify(PublicKey paramPublicKey) throws InvalidKeyException {
    if (!(paramPublicKey instanceof DSAPublicKey))
      throw new InvalidKeyException("not a DSA public key: " + paramPublicKey); 
    DSAPublicKey dSAPublicKey = (DSAPublicKey)paramPublicKey;
    DSAParams dSAParams = dSAPublicKey.getParams();
    if (dSAParams == null)
      throw new InvalidKeyException("DSA public key lacks parameters"); 
    this.params = dSAParams;
    this.presetY = dSAPublicKey.getY();
    this.presetX = null;
    this.presetP = dSAParams.getP();
    this.presetQ = dSAParams.getQ();
    this.presetG = dSAParams.getG();
    this.md.reset();
  }
  
  protected void engineUpdate(byte paramByte) {
    this.md.update(paramByte);
  }
  
  protected void engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    this.md.update(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  protected void engineUpdate(ByteBuffer paramByteBuffer) {
    this.md.update(paramByteBuffer);
  }
  
  protected byte[] engineSign() throws SignatureException {
    BigInteger bigInteger1 = generateK(this.presetQ);
    BigInteger bigInteger2 = generateR(this.presetP, this.presetQ, this.presetG, bigInteger1);
    BigInteger bigInteger3 = generateS(this.presetX, this.presetQ, bigInteger2, bigInteger1);
    try {
      DerOutputStream derOutputStream = new DerOutputStream(100);
      derOutputStream.putInteger(bigInteger2);
      derOutputStream.putInteger(bigInteger3);
      DerValue derValue = new DerValue((byte)48, derOutputStream.toByteArray());
      return derValue.toByteArray();
    } catch (IOException iOException) {
      throw new SignatureException("error encoding signature");
    } 
  }
  
  protected boolean engineVerify(byte[] paramArrayOfbyte) throws SignatureException {
    return engineVerify(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  protected boolean engineVerify(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws SignatureException {
    BigInteger bigInteger1 = null;
    BigInteger bigInteger2 = null;
    try {
      DerInputStream derInputStream = new DerInputStream(paramArrayOfbyte, paramInt1, paramInt2);
      DerValue[] arrayOfDerValue = derInputStream.getSequence(2);
      bigInteger1 = arrayOfDerValue[0].getBigInteger();
      bigInteger2 = arrayOfDerValue[1].getBigInteger();
    } catch (IOException iOException) {
      throw new SignatureException("invalid encoding for signature");
    } 
    if (bigInteger1.signum() < 0)
      bigInteger1 = new BigInteger(1, bigInteger1.toByteArray()); 
    if (bigInteger2.signum() < 0)
      bigInteger2 = new BigInteger(1, bigInteger2.toByteArray()); 
    if (bigInteger1.compareTo(this.presetQ) == -1 && bigInteger2.compareTo(this.presetQ) == -1) {
      BigInteger bigInteger3 = generateW(this.presetP, this.presetQ, this.presetG, bigInteger2);
      BigInteger bigInteger4 = generateV(this.presetY, this.presetP, this.presetQ, this.presetG, bigInteger3, bigInteger1);
      return bigInteger4.equals(bigInteger1);
    } 
    throw new SignatureException("invalid signature: out of range values");
  }
  
  @Deprecated
  protected void engineSetParameter(String paramString, Object paramObject) {
    throw new InvalidParameterException("No parameter accepted");
  }
  
  @Deprecated
  protected Object engineGetParameter(String paramString) {
    return null;
  }
  
  private BigInteger generateR(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) {
    BigInteger bigInteger = paramBigInteger3.modPow(paramBigInteger4, paramBigInteger1);
    return bigInteger.mod(paramBigInteger2);
  }
  
  private BigInteger generateS(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) throws SignatureException {
    byte[] arrayOfByte;
    try {
      arrayOfByte = this.md.digest();
    } catch (RuntimeException runtimeException) {
      throw new SignatureException(runtimeException.getMessage());
    } 
    int i = paramBigInteger2.bitLength() / 8;
    if (i < arrayOfByte.length)
      arrayOfByte = Arrays.copyOfRange(arrayOfByte, 0, i); 
    BigInteger bigInteger1 = new BigInteger(1, arrayOfByte);
    BigInteger bigInteger2 = paramBigInteger4.modInverse(paramBigInteger2);
    return paramBigInteger1.multiply(paramBigInteger3).add(bigInteger1).multiply(bigInteger2).mod(paramBigInteger2);
  }
  
  private BigInteger generateW(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) {
    return paramBigInteger4.modInverse(paramBigInteger2);
  }
  
  private BigInteger generateV(BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigInteger paramBigInteger6) throws SignatureException {
    byte[] arrayOfByte;
    try {
      arrayOfByte = this.md.digest();
    } catch (RuntimeException runtimeException) {
      throw new SignatureException(runtimeException.getMessage());
    } 
    int i = paramBigInteger3.bitLength() / 8;
    if (i < arrayOfByte.length)
      arrayOfByte = Arrays.copyOfRange(arrayOfByte, 0, i); 
    BigInteger bigInteger1 = new BigInteger(1, arrayOfByte);
    BigInteger bigInteger2 = bigInteger1.multiply(paramBigInteger5).mod(paramBigInteger3);
    BigInteger bigInteger3 = paramBigInteger6.multiply(paramBigInteger5).mod(paramBigInteger3);
    BigInteger bigInteger4 = paramBigInteger4.modPow(bigInteger2, paramBigInteger2);
    BigInteger bigInteger5 = paramBigInteger1.modPow(bigInteger3, paramBigInteger2);
    BigInteger bigInteger6 = bigInteger4.multiply(bigInteger5);
    BigInteger bigInteger7 = bigInteger6.mod(paramBigInteger2);
    return bigInteger7.mod(paramBigInteger3);
  }
  
  protected BigInteger generateK(BigInteger paramBigInteger) {
    SecureRandom secureRandom = getSigningRandom();
    byte[] arrayOfByte = new byte[paramBigInteger.bitLength() / 8];
    while (true) {
      secureRandom.nextBytes(arrayOfByte);
      BigInteger bigInteger = (new BigInteger(1, arrayOfByte)).mod(paramBigInteger);
      if (bigInteger.signum() > 0 && bigInteger.compareTo(paramBigInteger) < 0)
        return bigInteger; 
    } 
  }
  
  protected SecureRandom getSigningRandom() {
    if (this.signingRandom == null)
      if (this.appRandom != null) {
        this.signingRandom = this.appRandom;
      } else {
        this.signingRandom = JCAUtil.getSecureRandom();
      }  
    return this.signingRandom;
  }
  
  public String toString() {
    String str = "DSA Signature";
    if (this.presetP != null && this.presetQ != null && this.presetG != null) {
      str = str + "\n\tp: " + Debug.toHexString(this.presetP);
      str = str + "\n\tq: " + Debug.toHexString(this.presetQ);
      str = str + "\n\tg: " + Debug.toHexString(this.presetG);
    } else {
      str = str + "\n\t P, Q or G not initialized.";
    } 
    if (this.presetY != null)
      str = str + "\n\ty: " + Debug.toHexString(this.presetY); 
    if (this.presetY == null && this.presetX == null)
      str = str + "\n\tUNINIIALIZED"; 
    return str;
  }
  
  private static void debug(Exception paramException) {}
  
  private static void debug(String paramString) {}
  
  public static final class DSA {}
  
  public static final class DSA {}
  
  static class LegacyDSA extends DSA {
    private int[] kSeed;
    
    private byte[] kSeedAsByteArray;
    
    private int[] kSeedLast;
    
    private static final int round1_kt = 1518500249;
    
    private static final int round2_kt = 1859775393;
    
    private static final int round3_kt = -1894007588;
    
    private static final int round4_kt = -899497514;
    
    public LegacyDSA(MessageDigest param1MessageDigest) throws NoSuchAlgorithmException {
      super(param1MessageDigest);
    }
    
    @Deprecated
    protected void engineSetParameter(String param1String, Object param1Object) {
      if (param1String.equals("KSEED")) {
        if (param1Object instanceof byte[]) {
          this.kSeed = byteArray2IntArray((byte[])param1Object);
          this.kSeedAsByteArray = (byte[])param1Object;
        } else {
          DSA.debug("unrecognized param: " + param1String);
          throw new InvalidParameterException("kSeed not a byte array");
        } 
      } else {
        throw new InvalidParameterException("Unsupported parameter");
      } 
    }
    
    @Deprecated
    protected Object engineGetParameter(String param1String) {
      if (param1String.equals("KSEED"))
        return this.kSeedAsByteArray; 
      return null;
    }
    
    protected BigInteger generateK(BigInteger param1BigInteger) {
      BigInteger bigInteger = null;
      if (this.kSeed != null && !Arrays.equals(this.kSeed, this.kSeedLast)) {
        bigInteger = generateKUsingKSeed(this.kSeed, param1BigInteger);
        if (bigInteger.signum() > 0 && bigInteger.compareTo(param1BigInteger) < 0) {
          this.kSeedLast = (int[])this.kSeed.clone();
          return bigInteger;
        } 
      } 
      SecureRandom secureRandom = getSigningRandom();
      while (true) {
        int[] arrayOfInt = new int[5];
        for (byte b = 0; b < 5; ) {
          arrayOfInt[b] = secureRandom.nextInt();
          b++;
        } 
        bigInteger = generateKUsingKSeed(arrayOfInt, param1BigInteger);
        if (bigInteger.signum() > 0 && bigInteger.compareTo(param1BigInteger) < 0) {
          this.kSeedLast = arrayOfInt;
          return bigInteger;
        } 
      } 
    }
    
    private BigInteger generateKUsingKSeed(int[] param1ArrayOfint, BigInteger param1BigInteger) {
      int[] arrayOfInt1 = { -271733879, -1732584194, 271733878, -1009589776, 1732584193 };
      int[] arrayOfInt2 = SHA_7(param1ArrayOfint, arrayOfInt1);
      byte[] arrayOfByte = new byte[arrayOfInt2.length * 4];
      for (byte b = 0; b < arrayOfInt2.length; b++) {
        int i = arrayOfInt2[b];
        for (byte b1 = 0; b1 < 4; b1++)
          arrayOfByte[b * 4 + b1] = (byte)(i >>> 24 - b1 * 8); 
      } 
      return (new BigInteger(1, arrayOfByte)).mod(param1BigInteger);
    }
    
    static int[] SHA_7(int[] param1ArrayOfint1, int[] param1ArrayOfint2) {
      int[] arrayOfInt1 = new int[80];
      System.arraycopy(param1ArrayOfint1, 0, arrayOfInt1, 0, param1ArrayOfint1.length);
      int i = 0;
      int j;
      for (j = 16; j <= 79; j++) {
        i = arrayOfInt1[j - 3] ^ arrayOfInt1[j - 8] ^ arrayOfInt1[j - 14] ^ arrayOfInt1[j - 16];
        arrayOfInt1[j] = i << 1 | i >>> 31;
      } 
      j = param1ArrayOfint2[0];
      int k = param1ArrayOfint2[1], m = param1ArrayOfint2[2], n = param1ArrayOfint2[3], i1 = param1ArrayOfint2[4];
      byte b;
      for (b = 0; b < 20; b++) {
        i = (j << 5 | j >>> 27) + (k & m | (k ^ 0xFFFFFFFF) & n) + i1 + arrayOfInt1[b] + 1518500249;
        i1 = n;
        n = m;
        m = k << 30 | k >>> 2;
        k = j;
        j = i;
      } 
      for (b = 20; b < 40; b++) {
        i = (j << 5 | j >>> 27) + (k ^ m ^ n) + i1 + arrayOfInt1[b] + 1859775393;
        i1 = n;
        n = m;
        m = k << 30 | k >>> 2;
        k = j;
        j = i;
      } 
      for (b = 40; b < 60; b++) {
        i = (j << 5 | j >>> 27) + (k & m | k & n | m & n) + i1 + arrayOfInt1[b] + -1894007588;
        i1 = n;
        n = m;
        m = k << 30 | k >>> 2;
        k = j;
        j = i;
      } 
      for (b = 60; b < 80; b++) {
        i = (j << 5 | j >>> 27) + (k ^ m ^ n) + i1 + arrayOfInt1[b] + -899497514;
        i1 = n;
        n = m;
        m = k << 30 | k >>> 2;
        k = j;
        j = i;
      } 
      int[] arrayOfInt2 = new int[5];
      arrayOfInt2[0] = param1ArrayOfint2[0] + j;
      arrayOfInt2[1] = param1ArrayOfint2[1] + k;
      arrayOfInt2[2] = param1ArrayOfint2[2] + m;
      arrayOfInt2[3] = param1ArrayOfint2[3] + n;
      arrayOfInt2[4] = param1ArrayOfint2[4] + i1;
      return arrayOfInt2;
    }
    
    private int[] byteArray2IntArray(byte[] param1ArrayOfbyte) {
      byte[] arrayOfByte;
      byte b1 = 0;
      int i = param1ArrayOfbyte.length % 4;
      switch (i) {
        case 3:
          arrayOfByte = new byte[param1ArrayOfbyte.length + 1];
          break;
        case 2:
          arrayOfByte = new byte[param1ArrayOfbyte.length + 2];
          break;
        case 1:
          arrayOfByte = new byte[param1ArrayOfbyte.length + 3];
          break;
        default:
          arrayOfByte = new byte[param1ArrayOfbyte.length + 0];
          break;
      } 
      System.arraycopy(param1ArrayOfbyte, 0, arrayOfByte, 0, param1ArrayOfbyte.length);
      int[] arrayOfInt = new int[arrayOfByte.length / 4];
      for (byte b2 = 0; b2 < arrayOfByte.length; b2 += 4) {
        arrayOfInt[b1] = arrayOfByte[b2 + 3] & 0xFF;
        arrayOfInt[b1] = arrayOfInt[b1] | arrayOfByte[b2 + 2] << 8 & 0xFF00;
        arrayOfInt[b1] = arrayOfInt[b1] | arrayOfByte[b2 + 1] << 16 & 0xFF0000;
        arrayOfInt[b1] = arrayOfInt[b1] | arrayOfByte[b2 + 0] << 24 & 0xFF000000;
        b1++;
      } 
      return arrayOfInt;
    }
  }
  
  public static final class SHA1withDSA extends LegacyDSA {
    public SHA1withDSA() throws NoSuchAlgorithmException {
      super(MessageDigest.getInstance("SHA-1"));
    }
  }
  
  public static final class DSA {}
}
