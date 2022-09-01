package java.security;

import java.security.spec.AlgorithmParameterSpec;

public abstract class KeyPairGeneratorSpi {
  public abstract void initialize(int paramInt, SecureRandom paramSecureRandom);
  
  public void initialize(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException {
    throw new UnsupportedOperationException();
  }
  
  public abstract KeyPair generateKeyPair();
}
