package sun.security.internal.spec;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;

@Deprecated
public class TlsMasterSecretParameterSpec implements AlgorithmParameterSpec {
  private final SecretKey premasterSecret;
  
  private final int majorVersion;
  
  private final int minorVersion;
  
  private final byte[] clientRandom;
  
  private final byte[] serverRandom;
  
  private final String prfHashAlg;
  
  private final int prfHashLength;
  
  private final int prfBlockSize;
  
  public TlsMasterSecretParameterSpec(SecretKey paramSecretKey, int paramInt1, int paramInt2, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, String paramString, int paramInt3, int paramInt4) {
    if (paramSecretKey == null)
      throw new NullPointerException("premasterSecret must not be null"); 
    this.premasterSecret = paramSecretKey;
    this.majorVersion = checkVersion(paramInt1);
    this.minorVersion = checkVersion(paramInt2);
    this.clientRandom = (byte[])paramArrayOfbyte1.clone();
    this.serverRandom = (byte[])paramArrayOfbyte2.clone();
    this.prfHashAlg = paramString;
    this.prfHashLength = paramInt3;
    this.prfBlockSize = paramInt4;
  }
  
  static int checkVersion(int paramInt) {
    if (paramInt < 0 || paramInt > 255)
      throw new IllegalArgumentException("Version must be between 0 and 255"); 
    return paramInt;
  }
  
  public SecretKey getPremasterSecret() {
    return this.premasterSecret;
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
