package sun.security.ssl;

import java.security.AlgorithmConstraints;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.Key;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocket;
import sun.security.util.DisabledAlgorithmConstraints;

final class SSLAlgorithmConstraints implements AlgorithmConstraints {
  private static final AlgorithmConstraints tlsDisabledAlgConstraints = new DisabledAlgorithmConstraints("jdk.tls.disabledAlgorithms", new SSLAlgorithmDecomposer());
  
  private static final AlgorithmConstraints x509DisabledAlgConstraints = new DisabledAlgorithmConstraints("jdk.certpath.disabledAlgorithms", new SSLAlgorithmDecomposer(true));
  
  private AlgorithmConstraints userAlgConstraints = null;
  
  private AlgorithmConstraints peerAlgConstraints = null;
  
  private boolean enabledX509DisabledAlgConstraints = true;
  
  static final AlgorithmConstraints DEFAULT = new SSLAlgorithmConstraints(null);
  
  static final AlgorithmConstraints DEFAULT_SSL_ONLY = new SSLAlgorithmConstraints((SSLSocket)null, false);
  
  SSLAlgorithmConstraints(AlgorithmConstraints paramAlgorithmConstraints) {
    this.userAlgConstraints = paramAlgorithmConstraints;
  }
  
  SSLAlgorithmConstraints(SSLSocket paramSSLSocket, boolean paramBoolean) {
    if (paramSSLSocket != null)
      this
        .userAlgConstraints = paramSSLSocket.getSSLParameters().getAlgorithmConstraints(); 
    if (!paramBoolean)
      this.enabledX509DisabledAlgConstraints = false; 
  }
  
  SSLAlgorithmConstraints(SSLEngine paramSSLEngine, boolean paramBoolean) {
    if (paramSSLEngine != null)
      this
        .userAlgConstraints = paramSSLEngine.getSSLParameters().getAlgorithmConstraints(); 
    if (!paramBoolean)
      this.enabledX509DisabledAlgConstraints = false; 
  }
  
  SSLAlgorithmConstraints(SSLSocket paramSSLSocket, String[] paramArrayOfString, boolean paramBoolean) {
    if (paramSSLSocket != null) {
      this
        .userAlgConstraints = paramSSLSocket.getSSLParameters().getAlgorithmConstraints();
      this.peerAlgConstraints = new SupportedSignatureAlgorithmConstraints(paramArrayOfString);
    } 
    if (!paramBoolean)
      this.enabledX509DisabledAlgConstraints = false; 
  }
  
  SSLAlgorithmConstraints(SSLEngine paramSSLEngine, String[] paramArrayOfString, boolean paramBoolean) {
    if (paramSSLEngine != null) {
      this
        .userAlgConstraints = paramSSLEngine.getSSLParameters().getAlgorithmConstraints();
      this.peerAlgConstraints = new SupportedSignatureAlgorithmConstraints(paramArrayOfString);
    } 
    if (!paramBoolean)
      this.enabledX509DisabledAlgConstraints = false; 
  }
  
  public boolean permits(Set<CryptoPrimitive> paramSet, String paramString, AlgorithmParameters paramAlgorithmParameters) {
    boolean bool = true;
    if (this.peerAlgConstraints != null)
      bool = this.peerAlgConstraints.permits(paramSet, paramString, paramAlgorithmParameters); 
    if (bool && this.userAlgConstraints != null)
      bool = this.userAlgConstraints.permits(paramSet, paramString, paramAlgorithmParameters); 
    if (bool)
      bool = tlsDisabledAlgConstraints.permits(paramSet, paramString, paramAlgorithmParameters); 
    if (bool && this.enabledX509DisabledAlgConstraints)
      bool = x509DisabledAlgConstraints.permits(paramSet, paramString, paramAlgorithmParameters); 
    return bool;
  }
  
  public boolean permits(Set<CryptoPrimitive> paramSet, Key paramKey) {
    boolean bool = true;
    if (this.peerAlgConstraints != null)
      bool = this.peerAlgConstraints.permits(paramSet, paramKey); 
    if (bool && this.userAlgConstraints != null)
      bool = this.userAlgConstraints.permits(paramSet, paramKey); 
    if (bool)
      bool = tlsDisabledAlgConstraints.permits(paramSet, paramKey); 
    if (bool && this.enabledX509DisabledAlgConstraints)
      bool = x509DisabledAlgConstraints.permits(paramSet, paramKey); 
    return bool;
  }
  
  public boolean permits(Set<CryptoPrimitive> paramSet, String paramString, Key paramKey, AlgorithmParameters paramAlgorithmParameters) {
    boolean bool = true;
    if (this.peerAlgConstraints != null)
      bool = this.peerAlgConstraints.permits(paramSet, paramString, paramKey, paramAlgorithmParameters); 
    if (bool && this.userAlgConstraints != null)
      bool = this.userAlgConstraints.permits(paramSet, paramString, paramKey, paramAlgorithmParameters); 
    if (bool)
      bool = tlsDisabledAlgConstraints.permits(paramSet, paramString, paramKey, paramAlgorithmParameters); 
    if (bool && this.enabledX509DisabledAlgConstraints)
      bool = x509DisabledAlgConstraints.permits(paramSet, paramString, paramKey, paramAlgorithmParameters); 
    return bool;
  }
  
  private static class SupportedSignatureAlgorithmConstraints implements AlgorithmConstraints {
    private String[] supportedAlgorithms;
    
    SupportedSignatureAlgorithmConstraints(String[] param1ArrayOfString) {
      if (param1ArrayOfString != null) {
        this.supportedAlgorithms = (String[])param1ArrayOfString.clone();
      } else {
        this.supportedAlgorithms = null;
      } 
    }
    
    public boolean permits(Set<CryptoPrimitive> param1Set, String param1String, AlgorithmParameters param1AlgorithmParameters) {
      if (param1String == null || param1String.length() == 0)
        throw new IllegalArgumentException("No algorithm name specified"); 
      if (param1Set == null || param1Set.isEmpty())
        throw new IllegalArgumentException("No cryptographic primitive specified"); 
      if (this.supportedAlgorithms == null || this.supportedAlgorithms.length == 0)
        return false; 
      int i = param1String.indexOf("and");
      if (i > 0)
        param1String = param1String.substring(0, i); 
      for (String str : this.supportedAlgorithms) {
        if (param1String.equalsIgnoreCase(str))
          return true; 
      } 
      return false;
    }
    
    public final boolean permits(Set<CryptoPrimitive> param1Set, Key param1Key) {
      return true;
    }
    
    public final boolean permits(Set<CryptoPrimitive> param1Set, String param1String, Key param1Key, AlgorithmParameters param1AlgorithmParameters) {
      if (param1String == null || param1String.length() == 0)
        throw new IllegalArgumentException("No algorithm name specified"); 
      return permits(param1Set, param1String, param1AlgorithmParameters);
    }
  }
}
