package java.security;

import java.io.Serializable;

public final class KeyPair implements Serializable {
  private static final long serialVersionUID = -7565189502268009837L;
  
  private PrivateKey privateKey;
  
  private PublicKey publicKey;
  
  public KeyPair(PublicKey paramPublicKey, PrivateKey paramPrivateKey) {
    this.publicKey = paramPublicKey;
    this.privateKey = paramPrivateKey;
  }
  
  public PublicKey getPublic() {
    return this.publicKey;
  }
  
  public PrivateKey getPrivate() {
    return this.privateKey;
  }
}
