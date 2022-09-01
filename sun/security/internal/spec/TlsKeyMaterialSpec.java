package sun.security.internal.spec;

import java.security.spec.KeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

@Deprecated
public class TlsKeyMaterialSpec implements KeySpec, SecretKey {
  static final long serialVersionUID = 812912859129525028L;
  
  private final SecretKey clientMacKey;
  
  private final SecretKey serverMacKey;
  
  private final SecretKey clientCipherKey;
  
  private final SecretKey serverCipherKey;
  
  private final IvParameterSpec clientIv;
  
  private final IvParameterSpec serverIv;
  
  public TlsKeyMaterialSpec(SecretKey paramSecretKey1, SecretKey paramSecretKey2) {
    this(paramSecretKey1, paramSecretKey2, null, null, null, null);
  }
  
  public TlsKeyMaterialSpec(SecretKey paramSecretKey1, SecretKey paramSecretKey2, SecretKey paramSecretKey3, SecretKey paramSecretKey4) {
    this(paramSecretKey1, paramSecretKey2, paramSecretKey3, null, paramSecretKey4, null);
  }
  
  public TlsKeyMaterialSpec(SecretKey paramSecretKey1, SecretKey paramSecretKey2, SecretKey paramSecretKey3, IvParameterSpec paramIvParameterSpec1, SecretKey paramSecretKey4, IvParameterSpec paramIvParameterSpec2) {
    this.clientMacKey = paramSecretKey1;
    this.serverMacKey = paramSecretKey2;
    this.clientCipherKey = paramSecretKey3;
    this.serverCipherKey = paramSecretKey4;
    this.clientIv = paramIvParameterSpec1;
    this.serverIv = paramIvParameterSpec2;
  }
  
  public String getAlgorithm() {
    return "TlsKeyMaterial";
  }
  
  public String getFormat() {
    return null;
  }
  
  public byte[] getEncoded() {
    return null;
  }
  
  public SecretKey getClientMacKey() {
    return this.clientMacKey;
  }
  
  public SecretKey getServerMacKey() {
    return this.serverMacKey;
  }
  
  public SecretKey getClientCipherKey() {
    return this.clientCipherKey;
  }
  
  public IvParameterSpec getClientIv() {
    return this.clientIv;
  }
  
  public SecretKey getServerCipherKey() {
    return this.serverCipherKey;
  }
  
  public IvParameterSpec getServerIv() {
    return this.serverIv;
  }
}
