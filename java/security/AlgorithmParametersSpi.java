package java.security;

import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

public abstract class AlgorithmParametersSpi {
  protected abstract void engineInit(AlgorithmParameterSpec paramAlgorithmParameterSpec) throws InvalidParameterSpecException;
  
  protected abstract void engineInit(byte[] paramArrayOfbyte) throws IOException;
  
  protected abstract void engineInit(byte[] paramArrayOfbyte, String paramString) throws IOException;
  
  protected abstract <T extends AlgorithmParameterSpec> T engineGetParameterSpec(Class<T> paramClass) throws InvalidParameterSpecException;
  
  protected abstract byte[] engineGetEncoded() throws IOException;
  
  protected abstract byte[] engineGetEncoded(String paramString) throws IOException;
  
  protected abstract String engineToString();
}
