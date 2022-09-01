package sun.security.internal.spec;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;

@Deprecated
public class TlsPrfParameterSpec implements AlgorithmParameterSpec {
  private final SecretKey secret;
  
  private final String label;
  
  private final byte[] seed;
  
  private final int outputLength;
  
  private final String prfHashAlg;
  
  private final int prfHashLength;
  
  private final int prfBlockSize;
  
  public TlsPrfParameterSpec(SecretKey paramSecretKey, String paramString1, byte[] paramArrayOfbyte, int paramInt1, String paramString2, int paramInt2, int paramInt3) {
    if (paramString1 == null || paramArrayOfbyte == null)
      throw new NullPointerException("label and seed must not be null"); 
    if (paramInt1 <= 0)
      throw new IllegalArgumentException("outputLength must be positive"); 
    this.secret = paramSecretKey;
    this.label = paramString1;
    this.seed = (byte[])paramArrayOfbyte.clone();
    this.outputLength = paramInt1;
    this.prfHashAlg = paramString2;
    this.prfHashLength = paramInt2;
    this.prfBlockSize = paramInt3;
  }
  
  public SecretKey getSecret() {
    return this.secret;
  }
  
  public String getLabel() {
    return this.label;
  }
  
  public byte[] getSeed() {
    return (byte[])this.seed.clone();
  }
  
  public int getOutputLength() {
    return this.outputLength;
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
