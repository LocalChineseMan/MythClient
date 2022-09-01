package sun.security.internal.spec;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;

@Deprecated
public class TlsKeyMaterialParameterSpec implements AlgorithmParameterSpec {
  private final SecretKey masterSecret;
  
  private final int majorVersion;
  
  private final int minorVersion;
  
  private final byte[] clientRandom;
  
  private final byte[] serverRandom;
  
  private final String cipherAlgorithm;
  
  private final int cipherKeyLength;
  
  private final int ivLength;
  
  private final int macKeyLength;
  
  private final int expandedCipherKeyLength;
  
  private final String prfHashAlg;
  
  private final int prfHashLength;
  
  private final int prfBlockSize;
  
  public TlsKeyMaterialParameterSpec(SecretKey paramSecretKey, int paramInt1, int paramInt2, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, String paramString1, int paramInt3, int paramInt4, int paramInt5, int paramInt6, String paramString2, int paramInt7, int paramInt8) {
    if (!paramSecretKey.getAlgorithm().equals("TlsMasterSecret"))
      throw new IllegalArgumentException("Not a TLS master secret"); 
    if (paramString1 == null)
      throw new NullPointerException(); 
    this.masterSecret = paramSecretKey;
    this
      .majorVersion = TlsMasterSecretParameterSpec.checkVersion(paramInt1);
    this
      .minorVersion = TlsMasterSecretParameterSpec.checkVersion(paramInt2);
    this.clientRandom = (byte[])paramArrayOfbyte1.clone();
    this.serverRandom = (byte[])paramArrayOfbyte2.clone();
    this.cipherAlgorithm = paramString1;
    this.cipherKeyLength = checkSign(paramInt3);
    this.expandedCipherKeyLength = checkSign(paramInt4);
    this.ivLength = checkSign(paramInt5);
    this.macKeyLength = checkSign(paramInt6);
    this.prfHashAlg = paramString2;
    this.prfHashLength = paramInt7;
    this.prfBlockSize = paramInt8;
  }
  
  private static int checkSign(int paramInt) {
    if (paramInt < 0)
      throw new IllegalArgumentException("Value must not be negative"); 
    return paramInt;
  }
  
  public SecretKey getMasterSecret() {
    return this.masterSecret;
  }
  
  public int getMajorVersion() {
    return this.majorVersion;
  }
  
  public int getMinorVersion() {
    return this.minorVersion;
  }
  
  public byte[] getClientRandom() {
    return (byte[])this.clientRandom.clone();
  }
  
  public byte[] getServerRandom() {
    return (byte[])this.serverRandom.clone();
  }
  
  public String getCipherAlgorithm() {
    return this.cipherAlgorithm;
  }
  
  public int getCipherKeyLength() {
    return this.cipherKeyLength;
  }
  
  public int getExpandedCipherKeyLength() {
    if (this.majorVersion >= 3 && this.minorVersion >= 2)
      return 0; 
    return this.expandedCipherKeyLength;
  }
  
  public int getIvLength() {
    return this.ivLength;
  }
  
  public int getMacKeyLength() {
    return this.macKeyLength;
  }
  
  public String getPRFHashAlg() {
    return this.prfHashAlg;
  }
  
  public int getPRFHashLength() {
    return this.prfHashLength;
  }
  
  public int getPRFBlockSize() {
    return this.prfBlockSize;
  }
}
