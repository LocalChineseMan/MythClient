package java.security;

import java.io.Serializable;

public abstract class SecureRandomSpi implements Serializable {
  private static final long serialVersionUID = -2991854161009191830L;
  
  protected abstract void engineSetSeed(byte[] paramArrayOfbyte);
  
  protected abstract void engineNextBytes(byte[] paramArrayOfbyte);
  
  protected abstract byte[] engineGenerateSeed(int paramInt);
}
