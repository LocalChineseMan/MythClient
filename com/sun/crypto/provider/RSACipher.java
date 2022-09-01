package com.sun.crypto.provider;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import java.util.Locale;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import sun.security.internal.spec.TlsRsaPremasterSecretParameterSpec;
import sun.security.jca.Providers;
import sun.security.rsa.RSACore;
import sun.security.rsa.RSAKeyFactory;
import sun.security.rsa.RSAPadding;
import sun.security.util.KeyUtil;

public final class RSACipher extends CipherSpi {
  private static final byte[] B0 = new byte[0];
  
  private static final int MODE_ENCRYPT = 1;
  
  private static final int MODE_DECRYPT = 2;
  
  private static final int MODE_SIGN = 3;
  
  private static final int MODE_VERIFY = 4;
  
  private static final String PAD_NONE = "NoPadding";
  
  private static final String PAD_PKCS1 = "PKCS1Padding";
  
  private static final String PAD_OAEP_MGF1 = "OAEP";
  
  private int mode;
  
  private String paddingType;
  
  private RSAPadding padding;
  
  private AlgorithmParameterSpec spec = null;
  
  private byte[] buffer;
  
  private int bufOfs;
  
  private int outputSize;
  
  private RSAPublicKey publicKey;
  
  private RSAPrivateKey privateKey;
  
  private String oaepHashAlgorithm = "SHA-1";
  
  private SecureRandom random;
  
  public RSACipher() {
    this.paddingType = "PKCS1Padding";
  }
  
  protected void engineSetMode(String paramString) throws NoSuchAlgorithmException {
    if (!paramString.equalsIgnoreCase("ECB"))
      throw new NoSuchAlgorithmException("Unsupported mode " + paramString); 
  }
  
  protected void engineSetPadding(String paramString) throws NoSuchPaddingException {
    if (paramString.equalsIgnoreCase("NoPadding")) {
      this.paddingType = "NoPadding";
    } else if (paramString.equalsIgnoreCase("PKCS1Padding")) {
      this.paddingType = "PKCS1Padding";
    } else {
      String str = paramString.toLowerCase(Locale.ENGLISH);
      if (str.equals("oaeppadding")) {
        this.paddingType = "OAEP";
      } else if (str.startsWith("oaepwith") && str
        .endsWith("andmgf1padding")) {
        this.paddingType = "OAEP";
        this
          .oaepHashAlgorithm = paramString.substring(8, paramString.length() - 14);
        if (Providers.getProviderList()
          .getService("MessageDigest", this.oaepHashAlgorithm) == null)
          throw new NoSuchPaddingException("MessageDigest not available for " + paramString); 
      } else {
        throw new NoSuchPaddingException("Padding " + paramString + " not supported");
      } 
    } 
  }
  
  protected int engineGetBlockSize() {
    return 0;
  }
  
  protected int engineGetOutputSize(int paramInt) {
    return this.outputSize;
  }
  
  protected byte[] engineGetIV() {
    return null;
  }
  
  protected AlgorithmParameters engineGetParameters() {
    if (this.spec != null && this.spec instanceof OAEPParameterSpec)
      try {
        AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance("OAEP", 
            SunJCE.getInstance());
        algorithmParameters.init(this.spec);
        return algorithmParameters;
      } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
        throw new RuntimeException("Cannot find OAEP  AlgorithmParameters implementation in SunJCE provider");
      } catch (InvalidParameterSpecException invalidParameterSpecException) {
        throw new RuntimeException("OAEPParameterSpec not supported");
      }  
    return null;
  }
  
  protected void engineInit(int paramInt, Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException {
    try {
      init(paramInt, paramKey, paramSecureRandom, null);
    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
      InvalidKeyException invalidKeyException = new InvalidKeyException("Wrong parameters");
      invalidKeyException.initCause(invalidAlgorithmParameterException);
      throw invalidKeyException;
    } 
  }
  
  protected void engineInit(int paramInt, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    init(paramInt, paramKey, paramSecureRandom, paramAlgorithmParameterSpec);
  }
  
  protected void engineInit(int paramInt, Key paramKey, AlgorithmParameters paramAlgorithmParameters, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
    if (paramAlgorithmParameters == null) {
      init(paramInt, paramKey, paramSecureRandom, null);
    } else {
      try {
        OAEPParameterSpec oAEPParameterSpec = paramAlgorithmParameters.<OAEPParameterSpec>getParameterSpec(OAEPParameterSpec.class);
        init(paramInt, paramKey, paramSecureRandom, oAEPParameterSpec);
      } catch (InvalidParameterSpecException invalidParameterSpecException) {
        InvalidAlgorithmParameterException invalidAlgorithmParameterException = new InvalidAlgorithmParameterException("Wrong parameter");
        invalidAlgorithmParameterException.initCause(invalidParameterSpecException);
        throw invalidAlgorithmParameterException;
      } 
    } 
  }
  
  private void init(int paramInt, Key paramKey, SecureRandom paramSecureRandom, AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
    boolean bool;
    switch (paramInt) {
      case 1:
      case 3:
        bool = true;
        break;
      case 2:
      case 4:
        bool = false;
        break;
      default:
        throw new InvalidKeyException("Unknown mode: " + paramInt);
    } 
    RSAKey rSAKey = RSAKeyFactory.toRSAKey(paramKey);
    if (paramKey instanceof RSAPublicKey) {
      this.mode = bool ? 1 : 4;
      this.publicKey = (RSAPublicKey)paramKey;
      this.privateKey = null;
    } else {
      this.mode = bool ? 3 : 2;
      this.privateKey = (RSAPrivateKey)paramKey;
      this.publicKey = null;
    } 
    int i = RSACore.getByteLength(rSAKey.getModulus());
    this.outputSize = i;
    this.bufOfs = 0;
    if (this.paddingType == "NoPadding") {
      if (paramAlgorithmParameterSpec != null)
        throw new InvalidAlgorithmParameterException("Parameters not supported"); 
      this.padding = RSAPadding.getInstance(3, i, paramSecureRandom);
      this.buffer = new byte[i];
    } else if (this.paddingType == "PKCS1Padding") {
      if (paramAlgorithmParameterSpec != null) {
        if (!(paramAlgorithmParameterSpec instanceof TlsRsaPremasterSecretParameterSpec))
          throw new InvalidAlgorithmParameterException("Parameters not supported"); 
        this.spec = paramAlgorithmParameterSpec;
        this.random = paramSecureRandom;
      } 
      boolean bool1 = (this.mode <= 2) ? true : true;
      this.padding = RSAPadding.getInstance(bool1, i, paramSecureRandom);
      if (bool) {
        int j = this.padding.getMaxDataSize();
        this.buffer = new byte[j];
      } else {
        this.buffer = new byte[i];
      } 
    } else {
      if (this.mode == 3 || this.mode == 4)
        throw new InvalidKeyException("OAEP cannot be used to sign or verify signatures"); 
      if (paramAlgorithmParameterSpec != null) {
        if (!(paramAlgorithmParameterSpec instanceof OAEPParameterSpec))
          throw new InvalidAlgorithmParameterException("Wrong Parameters for OAEP Padding"); 
        this.spec = paramAlgorithmParameterSpec;
      } else {
        this.spec = new OAEPParameterSpec(this.oaepHashAlgorithm, "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);
      } 
      this.padding = RSAPadding.getInstance(4, i, paramSecureRandom, (OAEPParameterSpec)this.spec);
      if (bool) {
        int j = this.padding.getMaxDataSize();
        this.buffer = new byte[j];
      } else {
        this.buffer = new byte[i];
      } 
    } 
  }
  
  private void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramInt2 == 0 || paramArrayOfbyte == null)
      return; 
    if (this.bufOfs + paramInt2 > this.buffer.length) {
      this.bufOfs = this.buffer.length + 1;
      return;
    } 
    System.arraycopy(paramArrayOfbyte, paramInt1, this.buffer, this.bufOfs, paramInt2);
    this.bufOfs += paramInt2;
  }
  
  private byte[] doFinal() throws BadPaddingException, IllegalBlockSizeException {
    if (this.bufOfs > this.buffer.length)
      throw new IllegalBlockSizeException("Data must not be longer than " + this.buffer.length + " bytes"); 
    try {
      byte[] arrayOfByte1;
      byte[] arrayOfByte2;
      byte[] arrayOfByte3;
      switch (this.mode) {
        case 3:
          arrayOfByte1 = this.padding.pad(this.buffer, 0, this.bufOfs);
          arrayOfByte2 = RSACore.rsa(arrayOfByte1, this.privateKey, true);
          return arrayOfByte2;
        case 4:
          arrayOfByte2 = RSACore.convert(this.buffer, 0, this.bufOfs);
          arrayOfByte1 = RSACore.rsa(arrayOfByte2, this.publicKey);
          arrayOfByte3 = this.padding.unpad(arrayOfByte1);
          return arrayOfByte3;
        case 1:
          arrayOfByte1 = this.padding.pad(this.buffer, 0, this.bufOfs);
          arrayOfByte3 = RSACore.rsa(arrayOfByte1, this.publicKey);
          return arrayOfByte3;
        case 2:
          arrayOfByte3 = RSACore.convert(this.buffer, 0, this.bufOfs);
          arrayOfByte1 = RSACore.rsa(arrayOfByte3, this.privateKey, false);
          return this.padding.unpad(arrayOfByte1);
      } 
      throw new AssertionError("Internal error");
    } finally {
      this.bufOfs = 0;
    } 
  }
  
  protected byte[] engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    update(paramArrayOfbyte, paramInt1, paramInt2);
    return B0;
  }
  
  protected int engineUpdate(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    update(paramArrayOfbyte1, paramInt1, paramInt2);
    return 0;
  }
  
  protected byte[] engineDoFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws BadPaddingException, IllegalBlockSizeException {
    update(paramArrayOfbyte, paramInt1, paramInt2);
    return doFinal();
  }
  
  protected int engineDoFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException, BadPaddingException, IllegalBlockSizeException {
    if (this.outputSize > paramArrayOfbyte2.length - paramInt3)
      throw new ShortBufferException("Need " + this.outputSize + " bytes for output"); 
    update(paramArrayOfbyte1, paramInt1, paramInt2);
    byte[] arrayOfByte = doFinal();
    int i = arrayOfByte.length;
    System.arraycopy(arrayOfByte, 0, paramArrayOfbyte2, paramInt3, i);
    return i;
  }
  
  protected byte[] engineWrap(Key paramKey) throws InvalidKeyException, IllegalBlockSizeException {
    byte[] arrayOfByte = paramKey.getEncoded();
    if (arrayOfByte == null || arrayOfByte.length == 0)
      throw new InvalidKeyException("Could not obtain encoded key"); 
    if (arrayOfByte.length > this.buffer.length)
      throw new InvalidKeyException("Key is too long for wrapping"); 
    update(arrayOfByte, 0, arrayOfByte.length);
    try {
      return doFinal();
    } catch (BadPaddingException badPaddingException) {
      throw new InvalidKeyException("Wrapping failed", badPaddingException);
    } 
  }
  
  protected Key engineUnwrap(byte[] paramArrayOfbyte, String paramString, int paramInt) throws InvalidKeyException, NoSuchAlgorithmException {
    if (paramArrayOfbyte.length > this.buffer.length)
      throw new InvalidKeyException("Key is too long for unwrapping"); 
    boolean bool = paramString.equals("TlsRsaPremasterSecret");
    BadPaddingException badPaddingException = null;
    byte[] arrayOfByte = null;
    update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
    try {
      arrayOfByte = doFinal();
    } catch (BadPaddingException badPaddingException1) {
      if (bool) {
        badPaddingException = badPaddingException1;
      } else {
        throw new InvalidKeyException("Unwrapping failed", badPaddingException1);
      } 
    } catch (IllegalBlockSizeException illegalBlockSizeException) {
      throw new InvalidKeyException("Unwrapping failed", illegalBlockSizeException);
    } 
    if (bool) {
      if (!(this.spec instanceof TlsRsaPremasterSecretParameterSpec))
        throw new IllegalStateException("No TlsRsaPremasterSecretParameterSpec specified"); 
      arrayOfByte = KeyUtil.checkTlsPreMasterSecretKey(((TlsRsaPremasterSecretParameterSpec)this.spec)
          .getClientVersion(), ((TlsRsaPremasterSecretParameterSpec)this.spec)
          .getServerVersion(), this.random, arrayOfByte, (badPaddingException != null));
    } 
    return ConstructKeys.constructKey(arrayOfByte, paramString, paramInt);
  }
  
  protected int engineGetKeySize(Key paramKey) throws InvalidKeyException {
    RSAKey rSAKey = RSAKeyFactory.toRSAKey(paramKey);
    return rSAKey.getModulus().bitLength();
  }
}
