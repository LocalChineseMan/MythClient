package java.security;

import java.util.Set;

public interface AlgorithmConstraints {
  boolean permits(Set<CryptoPrimitive> paramSet, String paramString, AlgorithmParameters paramAlgorithmParameters);
  
  boolean permits(Set<CryptoPrimitive> paramSet, Key paramKey);
  
  boolean permits(Set<CryptoPrimitive> paramSet, String paramString, Key paramKey, AlgorithmParameters paramAlgorithmParameters);
}
