package sun.security.ssl;

import java.util.HashSet;
import java.util.Set;
import sun.security.util.AlgorithmDecomposer;

class SSLAlgorithmDecomposer extends AlgorithmDecomposer {
  private final boolean onlyX509;
  
  SSLAlgorithmDecomposer(boolean paramBoolean) {
    this.onlyX509 = paramBoolean;
  }
  
  SSLAlgorithmDecomposer() {
    this(false);
  }
  
  private Set<String> decomposes(CipherSuite.KeyExchange paramKeyExchange) {
    HashSet<String> hashSet = new HashSet();
    switch (paramKeyExchange) {
      case K_NULL:
        if (!this.onlyX509)
          hashSet.add("K_NULL"); 
        break;
      case K_RSA:
        hashSet.add("RSA");
        break;
      case K_RSA_EXPORT:
        hashSet.add("RSA");
        hashSet.add("RSA_EXPORT");
        break;
      case K_DH_RSA:
        hashSet.add("RSA");
        hashSet.add("DH");
        hashSet.add("DiffieHellman");
        hashSet.add("DH_RSA");
        break;
      case K_DH_DSS:
        hashSet.add("DSA");
        hashSet.add("DSS");
        hashSet.add("DH");
        hashSet.add("DiffieHellman");
        hashSet.add("DH_DSS");
        break;
      case K_DHE_DSS:
        hashSet.add("DSA");
        hashSet.add("DSS");
        hashSet.add("DH");
        hashSet.add("DHE");
        hashSet.add("DiffieHellman");
        hashSet.add("DHE_DSS");
        break;
      case K_DHE_RSA:
        hashSet.add("RSA");
        hashSet.add("DH");
        hashSet.add("DHE");
        hashSet.add("DiffieHellman");
        hashSet.add("DHE_RSA");
        break;
      case K_DH_ANON:
        if (!this.onlyX509) {
          hashSet.add("ANON");
          hashSet.add("DH");
          hashSet.add("DiffieHellman");
          hashSet.add("DH_ANON");
        } 
        break;
      case K_ECDH_ECDSA:
        hashSet.add("ECDH");
        hashSet.add("ECDSA");
        hashSet.add("ECDH_ECDSA");
        break;
      case K_ECDH_RSA:
        hashSet.add("ECDH");
        hashSet.add("RSA");
        hashSet.add("ECDH_RSA");
        break;
      case K_ECDHE_ECDSA:
        hashSet.add("ECDHE");
        hashSet.add("ECDSA");
        hashSet.add("ECDHE_ECDSA");
        break;
      case K_ECDHE_RSA:
        hashSet.add("ECDHE");
        hashSet.add("RSA");
        hashSet.add("ECDHE_RSA");
        break;
      case K_ECDH_ANON:
        if (!this.onlyX509) {
          hashSet.add("ECDH");
          hashSet.add("ANON");
          hashSet.add("ECDH_ANON");
        } 
        break;
      case K_KRB5:
        if (!this.onlyX509)
          hashSet.add("KRB5"); 
        break;
      case K_KRB5_EXPORT:
        if (!this.onlyX509)
          hashSet.add("KRB5_EXPORT"); 
        break;
    } 
    return hashSet;
  }
  
  private Set<String> decomposes(CipherSuite.BulkCipher paramBulkCipher) {
    HashSet<String> hashSet = new HashSet();
    if (paramBulkCipher.transformation != null)
      hashSet.addAll(super.decompose(paramBulkCipher.transformation)); 
    if (paramBulkCipher == CipherSuite.B_NULL) {
      hashSet.add("C_NULL");
    } else if (paramBulkCipher == CipherSuite.B_RC2_40) {
      hashSet.add("RC2_CBC_40");
    } else if (paramBulkCipher == CipherSuite.B_RC4_40) {
      hashSet.add("RC4_40");
    } else if (paramBulkCipher == CipherSuite.B_RC4_128) {
      hashSet.add("RC4_128");
    } else if (paramBulkCipher == CipherSuite.B_DES_40) {
      hashSet.add("DES40_CBC");
      hashSet.add("DES_CBC_40");
    } else if (paramBulkCipher == CipherSuite.B_DES) {
      hashSet.add("DES_CBC");
    } else if (paramBulkCipher == CipherSuite.B_3DES) {
      hashSet.add("3DES_EDE_CBC");
    } else if (paramBulkCipher == CipherSuite.B_AES_128) {
      hashSet.add("AES_128_CBC");
    } else if (paramBulkCipher == CipherSuite.B_AES_256) {
      hashSet.add("AES_256_CBC");
    } else if (paramBulkCipher == CipherSuite.B_AES_128_GCM) {
      hashSet.add("AES_128_GCM");
    } else if (paramBulkCipher == CipherSuite.B_AES_256_GCM) {
      hashSet.add("AES_256_GCM");
    } 
    return hashSet;
  }
  
  private Set<String> decomposes(CipherSuite.MacAlg paramMacAlg, CipherSuite.BulkCipher paramBulkCipher) {
    HashSet<String> hashSet = new HashSet();
    if (paramMacAlg == CipherSuite.M_NULL && paramBulkCipher.cipherType != CipherSuite.CipherType.AEAD_CIPHER) {
      hashSet.add("M_NULL");
    } else if (paramMacAlg == CipherSuite.M_MD5) {
      hashSet.add("MD5");
      hashSet.add("HmacMD5");
    } else if (paramMacAlg == CipherSuite.M_SHA) {
      hashSet.add("SHA1");
      hashSet.add("SHA-1");
      hashSet.add("HmacSHA1");
    } else if (paramMacAlg == CipherSuite.M_SHA256) {
      hashSet.add("SHA256");
      hashSet.add("SHA-256");
      hashSet.add("HmacSHA256");
    } else if (paramMacAlg == CipherSuite.M_SHA384) {
      hashSet.add("SHA384");
      hashSet.add("SHA-384");
      hashSet.add("HmacSHA384");
    } 
    return hashSet;
  }
  
  private Set<String> decompose(CipherSuite.KeyExchange paramKeyExchange, CipherSuite.BulkCipher paramBulkCipher, CipherSuite.MacAlg paramMacAlg) {
    HashSet<String> hashSet = new HashSet();
    if (paramKeyExchange != null)
      hashSet.addAll(decomposes(paramKeyExchange)); 
    if (this.onlyX509)
      return hashSet; 
    if (paramBulkCipher != null)
      hashSet.addAll(decomposes(paramBulkCipher)); 
    if (paramMacAlg != null)
      hashSet.addAll(decomposes(paramMacAlg, paramBulkCipher)); 
    return hashSet;
  }
  
  public Set<String> decompose(String paramString) {
    if (paramString.startsWith("SSL_") || paramString.startsWith("TLS_")) {
      CipherSuite cipherSuite = null;
      try {
        cipherSuite = CipherSuite.valueOf(paramString);
      } catch (IllegalArgumentException illegalArgumentException) {}
      if (cipherSuite != null)
        return decompose(cipherSuite.keyExchange, cipherSuite.cipher, cipherSuite.macAlg); 
    } 
    return super.decompose(paramString);
  }
}
