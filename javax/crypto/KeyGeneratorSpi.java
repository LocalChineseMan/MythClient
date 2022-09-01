package javax.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

public abstract class KeyGeneratorSpi {
  protected abstract void engineInit(SecureRandom paramSecureRandom);
  
  protected abstract void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidAlgorithmParameterException;
  
  protected abstract void engineInit(int paramInt, SecureRandom paramSecureRandom);
  
  protected abstract SecretKey engineGenerateKey();
}
