package sun.security.ssl;

import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

final class CipherSuite implements Comparable<CipherSuite> {
  static final int SUPPORTED_SUITES_PRIORITY = 1;
  
  static final int DEFAULT_SUITES_PRIORITY = 300;
  
  static final boolean DYNAMIC_AVAILABILITY = true;
  
  private static final boolean ALLOW_ECC = Debug.getBooleanProperty("com.sun.net.ssl.enableECC", true);
  
  private static final Map<Integer, CipherSuite> idMap;
  
  private static final Map<String, CipherSuite> nameMap;
  
  final String name;
  
  final int id;
  
  final int priority;
  
  final KeyExchange keyExchange;
  
  final BulkCipher cipher;
  
  final MacAlg macAlg;
  
  final PRF prfAlg;
  
  final boolean exportable;
  
  final boolean allowed;
  
  final int obsoleted;
  
  final int supported;
  
  private CipherSuite(String paramString, int paramInt1, int paramInt2, KeyExchange paramKeyExchange, BulkCipher paramBulkCipher, boolean paramBoolean, int paramInt3, int paramInt4, PRF paramPRF) {
    this.name = paramString;
    this.id = paramInt1;
    this.priority = paramInt2;
    this.keyExchange = paramKeyExchange;
    this.cipher = paramBulkCipher;
    this.exportable = paramBulkCipher.exportable;
    if (paramBulkCipher.cipherType == CipherType.AEAD_CIPHER) {
      this.macAlg = M_NULL;
    } else if (paramString.endsWith("_MD5")) {
      this.macAlg = M_MD5;
    } else if (paramString.endsWith("_SHA")) {
      this.macAlg = M_SHA;
    } else if (paramString.endsWith("_SHA256")) {
      this.macAlg = M_SHA256;
    } else if (paramString.endsWith("_SHA384")) {
      this.macAlg = M_SHA384;
    } else if (paramString.endsWith("_NULL")) {
      this.macAlg = M_NULL;
    } else if (paramString.endsWith("_SCSV")) {
      this.macAlg = M_NULL;
    } else {
      throw new IllegalArgumentException("Unknown MAC algorithm for ciphersuite " + paramString);
    } 
    paramBoolean &= paramKeyExchange.allowed;
    paramBoolean &= paramBulkCipher.allowed;
    this.allowed = paramBoolean;
    this.obsoleted = paramInt3;
    this.supported = paramInt4;
    this.prfAlg = paramPRF;
  }
  
  private CipherSuite(String paramString, int paramInt) {
    this.name = paramString;
    this.id = paramInt;
    this.allowed = false;
    this.priority = 0;
    this.keyExchange = null;
    this.cipher = null;
    this.macAlg = null;
    this.exportable = false;
    this.obsoleted = 65535;
    this.supported = 0;
    this.prfAlg = PRF.P_NONE;
  }
  
  boolean isAvailable() {
    return (this.allowed && this.keyExchange.isAvailable() && this.cipher.isAvailable());
  }
  
  boolean isNegotiable() {
    return (this != C_SCSV && isAvailable());
  }
  
  public int compareTo(CipherSuite paramCipherSuite) {
    return paramCipherSuite.priority - this.priority;
  }
  
  public String toString() {
    return this.name;
  }
  
  static CipherSuite valueOf(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("Name must not be null"); 
    CipherSuite cipherSuite = nameMap.get(paramString);
    if (cipherSuite == null || !cipherSuite.allowed)
      throw new IllegalArgumentException("Unsupported ciphersuite " + paramString); 
    return cipherSuite;
  }
  
  static CipherSuite valueOf(int paramInt1, int paramInt2) {
    paramInt1 &= 0xFF;
    paramInt2 &= 0xFF;
    int i = paramInt1 << 8 | paramInt2;
    CipherSuite cipherSuite = idMap.get(Integer.valueOf(i));
    if (cipherSuite == null) {
      String str1 = Integer.toString(paramInt1, 16);
      String str2 = Integer.toString(paramInt2, 16);
      cipherSuite = new CipherSuite("Unknown 0x" + str1 + ":0x" + str2, i);
    } 
    return cipherSuite;
  }
  
  static Collection<CipherSuite> allowedCipherSuites() {
    return nameMap.values();
  }
  
  private static void add(String paramString, int paramInt1, int paramInt2, KeyExchange paramKeyExchange, BulkCipher paramBulkCipher, boolean paramBoolean, int paramInt3, int paramInt4, PRF paramPRF) {
    CipherSuite cipherSuite = new CipherSuite(paramString, paramInt1, paramInt2, paramKeyExchange, paramBulkCipher, paramBoolean, paramInt3, paramInt4, paramPRF);
    if (idMap.put(Integer.valueOf(paramInt1), cipherSuite) != null)
      throw new RuntimeException("Duplicate ciphersuite definition: " + paramInt1 + ", " + paramString); 
    if (cipherSuite.allowed && 
      nameMap.put(paramString, cipherSuite) != null)
      throw new RuntimeException("Duplicate ciphersuite definition: " + paramInt1 + ", " + paramString); 
  }
  
  private static void add(String paramString, int paramInt1, int paramInt2, KeyExchange paramKeyExchange, BulkCipher paramBulkCipher, boolean paramBoolean, int paramInt3) {
    PRF pRF = PRF.P_SHA256;
    if (paramInt3 < ProtocolVersion.TLS12.v)
      pRF = PRF.P_NONE; 
    add(paramString, paramInt1, paramInt2, paramKeyExchange, paramBulkCipher, paramBoolean, paramInt3, 0, pRF);
  }
  
  private static void add(String paramString, int paramInt1, int paramInt2, KeyExchange paramKeyExchange, BulkCipher paramBulkCipher, boolean paramBoolean) {
    add(paramString, paramInt1, paramInt2, paramKeyExchange, paramBulkCipher, paramBoolean, 65535);
  }
  
  private static void add(String paramString, int paramInt) {
    CipherSuite cipherSuite = new CipherSuite(paramString, paramInt);
    if (idMap.put(Integer.valueOf(paramInt), cipherSuite) != null)
      throw new RuntimeException("Duplicate ciphersuite definition: " + paramInt + ", " + paramString); 
  }
  
  enum KeyExchange {
    K_NULL("NULL", false),
    K_RSA("RSA", true),
    K_RSA_EXPORT("RSA_EXPORT", true),
    K_DH_RSA("DH_RSA", false),
    K_DH_DSS("DH_DSS", false),
    K_DHE_DSS("DHE_DSS", true),
    K_DHE_RSA("DHE_RSA", true),
    K_DH_ANON("DH_anon", true),
    K_ECDH_ECDSA("ECDH_ECDSA", CipherSuite.ALLOW_ECC),
    K_ECDH_RSA("ECDH_RSA", CipherSuite.ALLOW_ECC),
    K_ECDHE_ECDSA("ECDHE_ECDSA", CipherSuite.ALLOW_ECC),
    K_ECDHE_RSA("ECDHE_RSA", CipherSuite.ALLOW_ECC),
    K_ECDH_ANON("ECDH_anon", CipherSuite.ALLOW_ECC),
    K_KRB5("KRB5", true),
    K_KRB5_EXPORT("KRB5_EXPORT", true),
    K_SCSV("SCSV", true);
    
    final String name;
    
    final boolean allowed;
    
    private final boolean alwaysAvailable;
    
    KeyExchange(String param1String1, boolean param1Boolean) {
      this.name = param1String1;
      this.allowed = param1Boolean;
      this
        .alwaysAvailable = (param1Boolean && !param1String1.startsWith("EC") && !param1String1.startsWith("KRB"));
    }
    
    boolean isAvailable() {
      if (this.alwaysAvailable)
        return true; 
      if (this.name.startsWith("EC"))
        return (this.allowed && JsseJce.isEcAvailable()); 
      if (this.name.startsWith("KRB"))
        return (this.allowed && JsseJce.isKerberosAvailable()); 
      return this.allowed;
    }
    
    public String toString() {
      return this.name;
    }
  }
  
  enum CipherType {
    STREAM_CIPHER, BLOCK_CIPHER, AEAD_CIPHER;
  }
  
  static final class BulkCipher {
    private static final Map<BulkCipher, Boolean> availableCache = new HashMap<>(8);
    
    final String description;
    
    final String transformation;
    
    final String algorithm;
    
    final boolean allowed;
    
    final int keySize;
    
    final int expandedKeySize;
    
    final int ivSize;
    
    final int fixedIvSize;
    
    final boolean exportable;
    
    final CipherSuite.CipherType cipherType;
    
    final int tagSize = 16;
    
    private static final SecureRandom secureRandom;
    
    static {
      try {
        secureRandom = JsseJce.getSecureRandom();
      } catch (KeyManagementException keyManagementException) {
        throw new RuntimeException(keyManagementException);
      } 
    }
    
    BulkCipher(String param1String, CipherSuite.CipherType param1CipherType, int param1Int1, int param1Int2, int param1Int3, int param1Int4, boolean param1Boolean) {
      this.transformation = param1String;
      String[] arrayOfString = param1String.split("/");
      this.algorithm = arrayOfString[0];
      this.cipherType = param1CipherType;
      this.description = this.algorithm + "/" + (param1Int1 << 3);
      this.keySize = param1Int1;
      this.ivSize = param1Int3;
      this.fixedIvSize = param1Int4;
      this.allowed = param1Boolean;
      this.expandedKeySize = param1Int2;
      this.exportable = true;
    }
    
    BulkCipher(String param1String, CipherSuite.CipherType param1CipherType, int param1Int1, int param1Int2, int param1Int3, boolean param1Boolean) {
      this.transformation = param1String;
      String[] arrayOfString = param1String.split("/");
      this.algorithm = arrayOfString[0];
      this.cipherType = param1CipherType;
      this.description = this.algorithm + "/" + (param1Int1 << 3);
      this.keySize = param1Int1;
      this.ivSize = param1Int2;
      this.fixedIvSize = param1Int3;
      this.allowed = param1Boolean;
      this.expandedKeySize = param1Int1;
      this.exportable = false;
    }
    
    CipherBox newCipher(ProtocolVersion param1ProtocolVersion, SecretKey param1SecretKey, IvParameterSpec param1IvParameterSpec, SecureRandom param1SecureRandom, boolean param1Boolean) throws NoSuchAlgorithmException {
      return CipherBox.newCipherBox(param1ProtocolVersion, this, param1SecretKey, param1IvParameterSpec, param1SecureRandom, param1Boolean);
    }
    
    boolean isAvailable() {
      if (!this.allowed)
        return false; 
      if (this == CipherSuite.B_AES_256 || this.cipherType == CipherSuite.CipherType.AEAD_CIPHER)
        return isAvailable(this); 
      return true;
    }
    
    static synchronized void clearAvailableCache() {
      availableCache.clear();
    }
    
    private static synchronized boolean isAvailable(BulkCipher param1BulkCipher) {
      Boolean bool = availableCache.get(param1BulkCipher);
      if (bool == null) {
        int i = param1BulkCipher.keySize * 8;
        if (i > 128)
          try {
            if (Cipher.getMaxAllowedKeyLength(param1BulkCipher.transformation) < i)
              bool = Boolean.FALSE; 
          } catch (Exception exception) {
            bool = Boolean.FALSE;
          }  
        if (bool == null) {
          bool = Boolean.FALSE;
          CipherBox cipherBox = null;
          try {
            IvParameterSpec ivParameterSpec;
            SecretKeySpec secretKeySpec = new SecretKeySpec(new byte[param1BulkCipher.expandedKeySize], param1BulkCipher.algorithm);
            if (param1BulkCipher.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
              ivParameterSpec = new IvParameterSpec(new byte[param1BulkCipher.fixedIvSize]);
            } else {
              ivParameterSpec = new IvParameterSpec(new byte[param1BulkCipher.ivSize]);
            } 
            cipherBox = param1BulkCipher.newCipher(ProtocolVersion.DEFAULT, secretKeySpec, ivParameterSpec, secureRandom, true);
            bool = cipherBox.isAvailable();
          } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
          
          } finally {
            if (cipherBox != null)
              cipherBox.dispose(); 
          } 
        } 
        availableCache.put(param1BulkCipher, bool);
      } 
      return bool.booleanValue();
    }
    
    public String toString() {
      return this.description;
    }
  }
  
  static final class MacAlg {
    final String name;
    
    final int size;
    
    final int hashBlockSize;
    
    final int minimalPaddingSize;
    
    MacAlg(String param1String, int param1Int1, int param1Int2, int param1Int3) {
      this.name = param1String;
      this.size = param1Int1;
      this.hashBlockSize = param1Int2;
      this.minimalPaddingSize = param1Int3;
    }
    
    MAC newMac(ProtocolVersion param1ProtocolVersion, SecretKey param1SecretKey) throws NoSuchAlgorithmException, InvalidKeyException {
      return new MAC(this, param1ProtocolVersion, param1SecretKey);
    }
    
    public String toString() {
      return this.name;
    }
  }
  
  static final BulkCipher B_NULL = new BulkCipher("NULL", CipherType.STREAM_CIPHER, 0, 0, 0, 0, true);
  
  static final BulkCipher B_RC4_40 = new BulkCipher("RC4", CipherType.STREAM_CIPHER, 5, 16, 0, 0, true);
  
  static final BulkCipher B_RC2_40 = new BulkCipher("RC2", CipherType.BLOCK_CIPHER, 5, 16, 8, 0, false);
  
  static final BulkCipher B_DES_40 = new BulkCipher("DES/CBC/NoPadding", CipherType.BLOCK_CIPHER, 5, 8, 8, 0, true);
  
  static final BulkCipher B_RC4_128 = new BulkCipher("RC4", CipherType.STREAM_CIPHER, 16, 0, 0, true);
  
  static final BulkCipher B_DES = new BulkCipher("DES/CBC/NoPadding", CipherType.BLOCK_CIPHER, 8, 8, 0, true);
  
  static final BulkCipher B_3DES = new BulkCipher("DESede/CBC/NoPadding", CipherType.BLOCK_CIPHER, 24, 8, 0, true);
  
  static final BulkCipher B_IDEA = new BulkCipher("IDEA", CipherType.BLOCK_CIPHER, 16, 8, 0, false);
  
  static final BulkCipher B_AES_128 = new BulkCipher("AES/CBC/NoPadding", CipherType.BLOCK_CIPHER, 16, 16, 0, true);
  
  static final BulkCipher B_AES_256 = new BulkCipher("AES/CBC/NoPadding", CipherType.BLOCK_CIPHER, 32, 16, 0, true);
  
  static final BulkCipher B_AES_128_GCM = new BulkCipher("AES/GCM/NoPadding", CipherType.AEAD_CIPHER, 16, 12, 4, true);
  
  static final BulkCipher B_AES_256_GCM = new BulkCipher("AES/GCM/NoPadding", CipherType.AEAD_CIPHER, 32, 12, 4, true);
  
  static final MacAlg M_NULL = new MacAlg("NULL", 0, 0, 0);
  
  static final MacAlg M_MD5 = new MacAlg("MD5", 16, 64, 9);
  
  static final MacAlg M_SHA = new MacAlg("SHA", 20, 64, 9);
  
  static final MacAlg M_SHA256 = new MacAlg("SHA256", 32, 64, 9);
  
  static final MacAlg M_SHA384 = new MacAlg("SHA384", 48, 128, 17);
  
  static final CipherSuite C_NULL;
  
  static final CipherSuite C_SCSV;
  
  enum PRF {
    P_NONE("NONE", 0, 0),
    P_SHA256("SHA-256", 32, 64),
    P_SHA384("SHA-384", 48, 128),
    P_SHA512("SHA-512", 64, 128);
    
    private final String prfHashAlg;
    
    private final int prfHashLength;
    
    private final int prfBlockSize;
    
    PRF(String param1String1, int param1Int1, int param1Int2) {
      this.prfHashAlg = param1String1;
      this.prfHashLength = param1Int1;
      this.prfBlockSize = param1Int2;
    }
    
    String getPRFHashAlg() {
      return this.prfHashAlg;
    }
    
    int getPRFHashLength() {
      return this.prfHashLength;
    }
    
    int getPRFBlockSize() {
      return this.prfBlockSize;
    }
  }
  
  static {
    idMap = new HashMap<>();
    nameMap = new HashMap<>();
    boolean bool = !SunJSSE.isFIPS() ? true : false;
    add("SSL_NULL_WITH_NULL_NULL", 0, 1, KeyExchange.K_NULL, B_NULL, false);
    char c = 'ɘ';
    char c1 = '￿';
    int i = ProtocolVersion.TLS11.v;
    int j = ProtocolVersion.TLS12.v;
    add("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384", 49188, --c, KeyExchange.K_ECDHE_ECDSA, B_AES_256, true, c1, j, PRF.P_SHA384);
    add("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", 49192, --c, KeyExchange.K_ECDHE_RSA, B_AES_256, true, c1, j, PRF.P_SHA384);
    add("TLS_RSA_WITH_AES_256_CBC_SHA256", 61, --c, KeyExchange.K_RSA, B_AES_256, true, c1, j, PRF.P_SHA256);
    add("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA384", 49190, --c, KeyExchange.K_ECDH_ECDSA, B_AES_256, true, c1, j, PRF.P_SHA384);
    add("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA384", 49194, --c, KeyExchange.K_ECDH_RSA, B_AES_256, true, c1, j, PRF.P_SHA384);
    add("TLS_DHE_RSA_WITH_AES_256_CBC_SHA256", 107, --c, KeyExchange.K_DHE_RSA, B_AES_256, true, c1, j, PRF.P_SHA256);
    add("TLS_DHE_DSS_WITH_AES_256_CBC_SHA256", 106, --c, KeyExchange.K_DHE_DSS, B_AES_256, true, c1, j, PRF.P_SHA256);
    add("TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", 49162, --c, KeyExchange.K_ECDHE_ECDSA, B_AES_256, true);
    add("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", 49172, --c, KeyExchange.K_ECDHE_RSA, B_AES_256, true);
    add("TLS_RSA_WITH_AES_256_CBC_SHA", 53, --c, KeyExchange.K_RSA, B_AES_256, true);
    add("TLS_ECDH_ECDSA_WITH_AES_256_CBC_SHA", 49157, --c, KeyExchange.K_ECDH_ECDSA, B_AES_256, true);
    add("TLS_ECDH_RSA_WITH_AES_256_CBC_SHA", 49167, --c, KeyExchange.K_ECDH_RSA, B_AES_256, true);
    add("TLS_DHE_RSA_WITH_AES_256_CBC_SHA", 57, --c, KeyExchange.K_DHE_RSA, B_AES_256, true);
    add("TLS_DHE_DSS_WITH_AES_256_CBC_SHA", 56, --c, KeyExchange.K_DHE_DSS, B_AES_256, true);
    add("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256", 49187, --c, KeyExchange.K_ECDHE_ECDSA, B_AES_128, true, c1, j, PRF.P_SHA256);
    add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256", 49191, --c, KeyExchange.K_ECDHE_RSA, B_AES_128, true, c1, j, PRF.P_SHA256);
    add("TLS_RSA_WITH_AES_128_CBC_SHA256", 60, --c, KeyExchange.K_RSA, B_AES_128, true, c1, j, PRF.P_SHA256);
    add("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA256", 49189, --c, KeyExchange.K_ECDH_ECDSA, B_AES_128, true, c1, j, PRF.P_SHA256);
    add("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA256", 49193, --c, KeyExchange.K_ECDH_RSA, B_AES_128, true, c1, j, PRF.P_SHA256);
    add("TLS_DHE_RSA_WITH_AES_128_CBC_SHA256", 103, --c, KeyExchange.K_DHE_RSA, B_AES_128, true, c1, j, PRF.P_SHA256);
    add("TLS_DHE_DSS_WITH_AES_128_CBC_SHA256", 64, --c, KeyExchange.K_DHE_DSS, B_AES_128, true, c1, j, PRF.P_SHA256);
    add("TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA", 49161, --c, KeyExchange.K_ECDHE_ECDSA, B_AES_128, true);
    add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", 49171, --c, KeyExchange.K_ECDHE_RSA, B_AES_128, true);
    add("TLS_RSA_WITH_AES_128_CBC_SHA", 47, --c, KeyExchange.K_RSA, B_AES_128, true);
    add("TLS_ECDH_ECDSA_WITH_AES_128_CBC_SHA", 49156, --c, KeyExchange.K_ECDH_ECDSA, B_AES_128, true);
    add("TLS_ECDH_RSA_WITH_AES_128_CBC_SHA", 49166, --c, KeyExchange.K_ECDH_RSA, B_AES_128, true);
    add("TLS_DHE_RSA_WITH_AES_128_CBC_SHA", 51, --c, KeyExchange.K_DHE_RSA, B_AES_128, true);
    add("TLS_DHE_DSS_WITH_AES_128_CBC_SHA", 50, --c, KeyExchange.K_DHE_DSS, B_AES_128, true);
    add("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", 49196, --c, KeyExchange.K_ECDHE_ECDSA, B_AES_256_GCM, true, c1, j, PRF.P_SHA384);
    add("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256", 49195, --c, KeyExchange.K_ECDHE_ECDSA, B_AES_128_GCM, true, c1, j, PRF.P_SHA256);
    add("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384", 49200, --c, KeyExchange.K_ECDHE_RSA, B_AES_256_GCM, true, c1, j, PRF.P_SHA384);
    add("TLS_RSA_WITH_AES_256_GCM_SHA384", 157, --c, KeyExchange.K_RSA, B_AES_256_GCM, true, c1, j, PRF.P_SHA384);
    add("TLS_ECDH_ECDSA_WITH_AES_256_GCM_SHA384", 49198, --c, KeyExchange.K_ECDH_ECDSA, B_AES_256_GCM, true, c1, j, PRF.P_SHA384);
    add("TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384", 49202, --c, KeyExchange.K_ECDH_RSA, B_AES_256_GCM, true, c1, j, PRF.P_SHA384);
    add("TLS_DHE_RSA_WITH_AES_256_GCM_SHA384", 159, --c, KeyExchange.K_DHE_RSA, B_AES_256_GCM, true, c1, j, PRF.P_SHA384);
    add("TLS_DHE_DSS_WITH_AES_256_GCM_SHA384", 163, --c, KeyExchange.K_DHE_DSS, B_AES_256_GCM, true, c1, j, PRF.P_SHA384);
    add("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", 49199, --c, KeyExchange.K_ECDHE_RSA, B_AES_128_GCM, true, c1, j, PRF.P_SHA256);
    add("TLS_RSA_WITH_AES_128_GCM_SHA256", 156, --c, KeyExchange.K_RSA, B_AES_128_GCM, true, c1, j, PRF.P_SHA256);
    add("TLS_ECDH_ECDSA_WITH_AES_128_GCM_SHA256", 49197, --c, KeyExchange.K_ECDH_ECDSA, B_AES_128_GCM, true, c1, j, PRF.P_SHA256);
    add("TLS_ECDH_RSA_WITH_AES_128_GCM_SHA256", 49201, --c, KeyExchange.K_ECDH_RSA, B_AES_128_GCM, true, c1, j, PRF.P_SHA256);
    add("TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", 158, --c, KeyExchange.K_DHE_RSA, B_AES_128_GCM, true, c1, j, PRF.P_SHA256);
    add("TLS_DHE_DSS_WITH_AES_128_GCM_SHA256", 162, --c, KeyExchange.K_DHE_DSS, B_AES_128_GCM, true, c1, j, PRF.P_SHA256);
    add("TLS_ECDHE_ECDSA_WITH_3DES_EDE_CBC_SHA", 49160, --c, KeyExchange.K_ECDHE_ECDSA, B_3DES, true);
    add("TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", 49170, --c, KeyExchange.K_ECDHE_RSA, B_3DES, true);
    add("SSL_RSA_WITH_3DES_EDE_CBC_SHA", 10, --c, KeyExchange.K_RSA, B_3DES, true);
    add("TLS_ECDH_ECDSA_WITH_3DES_EDE_CBC_SHA", 49155, --c, KeyExchange.K_ECDH_ECDSA, B_3DES, true);
    add("TLS_ECDH_RSA_WITH_3DES_EDE_CBC_SHA", 49165, --c, KeyExchange.K_ECDH_RSA, B_3DES, true);
    add("SSL_DHE_RSA_WITH_3DES_EDE_CBC_SHA", 22, --c, KeyExchange.K_DHE_RSA, B_3DES, true);
    add("SSL_DHE_DSS_WITH_3DES_EDE_CBC_SHA", 19, --c, KeyExchange.K_DHE_DSS, B_3DES, bool);
    add("TLS_EMPTY_RENEGOTIATION_INFO_SCSV", 255, --c, KeyExchange.K_SCSV, B_NULL, true);
    c = 'Ĭ';
    add("TLS_DH_anon_WITH_AES_256_GCM_SHA384", 167, --c, KeyExchange.K_DH_ANON, B_AES_256_GCM, bool, c1, j, PRF.P_SHA384);
    add("TLS_DH_anon_WITH_AES_128_GCM_SHA256", 166, --c, KeyExchange.K_DH_ANON, B_AES_128_GCM, bool, c1, j, PRF.P_SHA256);
    add("TLS_DH_anon_WITH_AES_256_CBC_SHA256", 109, --c, KeyExchange.K_DH_ANON, B_AES_256, bool, c1, j, PRF.P_SHA256);
    add("TLS_ECDH_anon_WITH_AES_256_CBC_SHA", 49177, --c, KeyExchange.K_ECDH_ANON, B_AES_256, bool);
    add("TLS_DH_anon_WITH_AES_256_CBC_SHA", 58, --c, KeyExchange.K_DH_ANON, B_AES_256, bool);
    add("TLS_DH_anon_WITH_AES_128_CBC_SHA256", 108, --c, KeyExchange.K_DH_ANON, B_AES_128, bool, c1, j, PRF.P_SHA256);
    add("TLS_ECDH_anon_WITH_AES_128_CBC_SHA", 49176, --c, KeyExchange.K_ECDH_ANON, B_AES_128, bool);
    add("TLS_DH_anon_WITH_AES_128_CBC_SHA", 52, --c, KeyExchange.K_DH_ANON, B_AES_128, bool);
    add("TLS_ECDH_anon_WITH_3DES_EDE_CBC_SHA", 49175, --c, KeyExchange.K_ECDH_ANON, B_3DES, bool);
    add("SSL_DH_anon_WITH_3DES_EDE_CBC_SHA", 27, --c, KeyExchange.K_DH_ANON, B_3DES, bool);
    add("TLS_ECDHE_ECDSA_WITH_RC4_128_SHA", 49159, --c, KeyExchange.K_ECDHE_ECDSA, B_RC4_128, bool);
    add("TLS_ECDHE_RSA_WITH_RC4_128_SHA", 49169, --c, KeyExchange.K_ECDHE_RSA, B_RC4_128, bool);
    add("SSL_RSA_WITH_RC4_128_SHA", 5, --c, KeyExchange.K_RSA, B_RC4_128, bool);
    add("TLS_ECDH_ECDSA_WITH_RC4_128_SHA", 49154, --c, KeyExchange.K_ECDH_ECDSA, B_RC4_128, bool);
    add("TLS_ECDH_RSA_WITH_RC4_128_SHA", 49164, --c, KeyExchange.K_ECDH_RSA, B_RC4_128, bool);
    add("SSL_RSA_WITH_RC4_128_MD5", 4, --c, KeyExchange.K_RSA, B_RC4_128, bool);
    add("TLS_ECDH_anon_WITH_RC4_128_SHA", 49174, --c, KeyExchange.K_ECDH_ANON, B_RC4_128, bool);
    add("SSL_DH_anon_WITH_RC4_128_MD5", 24, --c, KeyExchange.K_DH_ANON, B_RC4_128, bool);
    add("SSL_RSA_WITH_DES_CBC_SHA", 9, --c, KeyExchange.K_RSA, B_DES, bool, j);
    add("SSL_DHE_RSA_WITH_DES_CBC_SHA", 21, --c, KeyExchange.K_DHE_RSA, B_DES, bool, j);
    add("SSL_DHE_DSS_WITH_DES_CBC_SHA", 18, --c, KeyExchange.K_DHE_DSS, B_DES, bool, j);
    add("SSL_DH_anon_WITH_DES_CBC_SHA", 26, --c, KeyExchange.K_DH_ANON, B_DES, bool, j);
    add("SSL_RSA_EXPORT_WITH_DES40_CBC_SHA", 8, --c, KeyExchange.K_RSA_EXPORT, B_DES_40, bool, i);
    add("SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", 20, --c, KeyExchange.K_DHE_RSA, B_DES_40, bool, i);
    add("SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA", 17, --c, KeyExchange.K_DHE_DSS, B_DES_40, bool, i);
    add("SSL_DH_anon_EXPORT_WITH_DES40_CBC_SHA", 25, --c, KeyExchange.K_DH_ANON, B_DES_40, bool, i);
    add("SSL_RSA_EXPORT_WITH_RC4_40_MD5", 3, --c, KeyExchange.K_RSA_EXPORT, B_RC4_40, bool, i);
    add("SSL_DH_anon_EXPORT_WITH_RC4_40_MD5", 23, --c, KeyExchange.K_DH_ANON, B_RC4_40, bool, i);
    add("TLS_RSA_WITH_NULL_SHA256", 59, --c, KeyExchange.K_RSA, B_NULL, bool, c1, j, PRF.P_SHA256);
    add("TLS_ECDHE_ECDSA_WITH_NULL_SHA", 49158, --c, KeyExchange.K_ECDHE_ECDSA, B_NULL, bool);
    add("TLS_ECDHE_RSA_WITH_NULL_SHA", 49168, --c, KeyExchange.K_ECDHE_RSA, B_NULL, bool);
    add("SSL_RSA_WITH_NULL_SHA", 2, --c, KeyExchange.K_RSA, B_NULL, bool);
    add("TLS_ECDH_ECDSA_WITH_NULL_SHA", 49153, --c, KeyExchange.K_ECDH_ECDSA, B_NULL, bool);
    add("TLS_ECDH_RSA_WITH_NULL_SHA", 49163, --c, KeyExchange.K_ECDH_RSA, B_NULL, bool);
    add("TLS_ECDH_anon_WITH_NULL_SHA", 49173, --c, KeyExchange.K_ECDH_ANON, B_NULL, bool);
    add("SSL_RSA_WITH_NULL_MD5", 1, --c, KeyExchange.K_RSA, B_NULL, bool);
    add("TLS_KRB5_WITH_3DES_EDE_CBC_SHA", 31, --c, KeyExchange.K_KRB5, B_3DES, bool);
    add("TLS_KRB5_WITH_3DES_EDE_CBC_MD5", 35, --c, KeyExchange.K_KRB5, B_3DES, bool);
    add("TLS_KRB5_WITH_RC4_128_SHA", 32, --c, KeyExchange.K_KRB5, B_RC4_128, bool);
    add("TLS_KRB5_WITH_RC4_128_MD5", 36, --c, KeyExchange.K_KRB5, B_RC4_128, bool);
    add("TLS_KRB5_WITH_DES_CBC_SHA", 30, --c, KeyExchange.K_KRB5, B_DES, bool, j);
    add("TLS_KRB5_WITH_DES_CBC_MD5", 34, --c, KeyExchange.K_KRB5, B_DES, bool, j);
    add("TLS_KRB5_EXPORT_WITH_DES_CBC_40_SHA", 38, --c, KeyExchange.K_KRB5_EXPORT, B_DES_40, bool, i);
    add("TLS_KRB5_EXPORT_WITH_DES_CBC_40_MD5", 41, --c, KeyExchange.K_KRB5_EXPORT, B_DES_40, bool, i);
    add("TLS_KRB5_EXPORT_WITH_RC4_40_SHA", 40, --c, KeyExchange.K_KRB5_EXPORT, B_RC4_40, bool, i);
    add("TLS_KRB5_EXPORT_WITH_RC4_40_MD5", 43, --c, KeyExchange.K_KRB5_EXPORT, B_RC4_40, bool, i);
    add("SSL_RSA_EXPORT_WITH_RC2_CBC_40_MD5", 6);
    add("SSL_RSA_WITH_IDEA_CBC_SHA", 7);
    add("SSL_DH_DSS_EXPORT_WITH_DES40_CBC_SHA", 11);
    add("SSL_DH_DSS_WITH_DES_CBC_SHA", 12);
    add("SSL_DH_DSS_WITH_3DES_EDE_CBC_SHA", 13);
    add("SSL_DH_RSA_EXPORT_WITH_DES40_CBC_SHA", 14);
    add("SSL_DH_RSA_WITH_DES_CBC_SHA", 15);
    add("SSL_DH_RSA_WITH_3DES_EDE_CBC_SHA", 16);
    add("SSL_FORTEZZA_DMS_WITH_NULL_SHA", 28);
    add("SSL_FORTEZZA_DMS_WITH_FORTEZZA_CBC_SHA", 29);
    add("SSL_RSA_EXPORT1024_WITH_DES_CBC_SHA", 98);
    add("SSL_DHE_DSS_EXPORT1024_WITH_DES_CBC_SHA", 99);
    add("SSL_RSA_EXPORT1024_WITH_RC4_56_SHA", 100);
    add("SSL_DHE_DSS_EXPORT1024_WITH_RC4_56_SHA", 101);
    add("SSL_DHE_DSS_WITH_RC4_128_SHA", 102);
    add("NETSCAPE_RSA_FIPS_WITH_3DES_EDE_CBC_SHA", 65504);
    add("NETSCAPE_RSA_FIPS_WITH_DES_CBC_SHA", 65505);
    add("SSL_RSA_FIPS_WITH_DES_CBC_SHA", 65278);
    add("SSL_RSA_FIPS_WITH_3DES_EDE_CBC_SHA", 65279);
    add("TLS_KRB5_WITH_IDEA_CBC_SHA", 33);
    add("TLS_KRB5_WITH_IDEA_CBC_MD5", 37);
    add("TLS_KRB5_EXPORT_WITH_RC2_CBC_40_SHA", 39);
    add("TLS_KRB5_EXPORT_WITH_RC2_CBC_40_MD5", 42);
    add("TLS_RSA_WITH_SEED_CBC_SHA", 150);
    add("TLS_DH_DSS_WITH_SEED_CBC_SHA", 151);
    add("TLS_DH_RSA_WITH_SEED_CBC_SHA", 152);
    add("TLS_DHE_DSS_WITH_SEED_CBC_SHA", 153);
    add("TLS_DHE_RSA_WITH_SEED_CBC_SHA", 154);
    add("TLS_DH_anon_WITH_SEED_CBC_SHA", 155);
    add("TLS_PSK_WITH_RC4_128_SHA", 138);
    add("TLS_PSK_WITH_3DES_EDE_CBC_SHA", 139);
    add("TLS_PSK_WITH_AES_128_CBC_SHA", 140);
    add("TLS_PSK_WITH_AES_256_CBC_SHA", 141);
    add("TLS_DHE_PSK_WITH_RC4_128_SHA", 142);
    add("TLS_DHE_PSK_WITH_3DES_EDE_CBC_SHA", 143);
    add("TLS_DHE_PSK_WITH_AES_128_CBC_SHA", 144);
    add("TLS_DHE_PSK_WITH_AES_256_CBC_SHA", 145);
    add("TLS_RSA_PSK_WITH_RC4_128_SHA", 146);
    add("TLS_RSA_PSK_WITH_3DES_EDE_CBC_SHA", 147);
    add("TLS_RSA_PSK_WITH_AES_128_CBC_SHA", 148);
    add("TLS_RSA_PSK_WITH_AES_256_CBC_SHA", 149);
    add("TLS_PSK_WITH_NULL_SHA", 44);
    add("TLS_DHE_PSK_WITH_NULL_SHA", 45);
    add("TLS_RSA_PSK_WITH_NULL_SHA", 46);
    add("TLS_DH_DSS_WITH_AES_128_CBC_SHA", 48);
    add("TLS_DH_RSA_WITH_AES_128_CBC_SHA", 49);
    add("TLS_DH_DSS_WITH_AES_256_CBC_SHA", 54);
    add("TLS_DH_RSA_WITH_AES_256_CBC_SHA", 55);
    add("TLS_DH_DSS_WITH_AES_128_CBC_SHA256", 62);
    add("TLS_DH_RSA_WITH_AES_128_CBC_SHA256", 63);
    add("TLS_DH_DSS_WITH_AES_256_CBC_SHA256", 104);
    add("TLS_DH_RSA_WITH_AES_256_CBC_SHA256", 105);
    add("TLS_DH_RSA_WITH_AES_128_GCM_SHA256", 160);
    add("TLS_DH_RSA_WITH_AES_256_GCM_SHA384", 161);
    add("TLS_DH_DSS_WITH_AES_128_GCM_SHA256", 164);
    add("TLS_DH_DSS_WITH_AES_256_GCM_SHA384", 165);
    add("TLS_PSK_WITH_AES_128_GCM_SHA256", 168);
    add("TLS_PSK_WITH_AES_256_GCM_SHA384", 169);
    add("TLS_DHE_PSK_WITH_AES_128_GCM_SHA256", 170);
    add("TLS_DHE_PSK_WITH_AES_256_GCM_SHA384", 171);
    add("TLS_RSA_PSK_WITH_AES_128_GCM_SHA256", 172);
    add("TLS_RSA_PSK_WITH_AES_256_GCM_SHA384", 173);
    add("TLS_PSK_WITH_AES_128_CBC_SHA256", 174);
    add("TLS_PSK_WITH_AES_256_CBC_SHA384", 175);
    add("TLS_PSK_WITH_NULL_SHA256", 176);
    add("TLS_PSK_WITH_NULL_SHA384", 177);
    add("TLS_DHE_PSK_WITH_AES_128_CBC_SHA256", 178);
    add("TLS_DHE_PSK_WITH_AES_256_CBC_SHA384", 179);
    add("TLS_DHE_PSK_WITH_NULL_SHA256", 180);
    add("TLS_DHE_PSK_WITH_NULL_SHA384", 181);
    add("TLS_RSA_PSK_WITH_AES_128_CBC_SHA256", 182);
    add("TLS_RSA_PSK_WITH_AES_256_CBC_SHA384", 183);
    add("TLS_RSA_PSK_WITH_NULL_SHA256", 184);
    add("TLS_RSA_PSK_WITH_NULL_SHA384", 185);
    add("TLS_RSA_WITH_CAMELLIA_128_CBC_SHA", 65);
    add("TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA", 66);
    add("TLS_DH_RSA_WITH_CAMELLIA_128_CBC_SHA", 67);
    add("TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA", 68);
    add("TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA", 69);
    add("TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA", 70);
    add("TLS_RSA_WITH_CAMELLIA_256_CBC_SHA", 132);
    add("TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA", 133);
    add("TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA", 134);
    add("TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA", 135);
    add("TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA", 136);
    add("TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA", 137);
    add("TLS_RSA_WITH_CAMELLIA_128_CBC_SHA256", 186);
    add("TLS_DH_DSS_WITH_CAMELLIA_128_CBC_SHA256", 187);
    add("TLS_DH_RSA_WITH_CAMELLIA_128_CBC_SHA256", 188);
    add("TLS_DHE_DSS_WITH_CAMELLIA_128_CBC_SHA256", 189);
    add("TLS_DHE_RSA_WITH_CAMELLIA_128_CBC_SHA256", 190);
    add("TLS_DH_anon_WITH_CAMELLIA_128_CBC_SHA256", 191);
    add("TLS_RSA_WITH_CAMELLIA_256_CBC_SHA256", 192);
    add("TLS_DH_DSS_WITH_CAMELLIA_256_CBC_SHA256", 193);
    add("TLS_DH_RSA_WITH_CAMELLIA_256_CBC_SHA256", 194);
    add("TLS_DHE_DSS_WITH_CAMELLIA_256_CBC_SHA256", 195);
    add("TLS_DHE_RSA_WITH_CAMELLIA_256_CBC_SHA256", 196);
    add("TLS_DH_anon_WITH_CAMELLIA_256_CBC_SHA256", 197);
    add("TLS_SRP_SHA_WITH_3DES_EDE_CBC_SHA", 49178);
    add("TLS_SRP_SHA_RSA_WITH_3DES_EDE_CBC_SHA", 49179);
    add("TLS_SRP_SHA_DSS_WITH_3DES_EDE_CBC_SHA", 49180);
    add("TLS_SRP_SHA_WITH_AES_128_CBC_SHA", 49181);
    add("TLS_SRP_SHA_RSA_WITH_AES_128_CBC_SHA", 49182);
    add("TLS_SRP_SHA_DSS_WITH_AES_128_CBC_SHA", 49183);
    add("TLS_SRP_SHA_WITH_AES_256_CBC_SHA", 49184);
    add("TLS_SRP_SHA_RSA_WITH_AES_256_CBC_SHA", 49185);
    add("TLS_SRP_SHA_DSS_WITH_AES_256_CBC_SHA", 49186);
    add("TLS_ECDHE_PSK_WITH_RC4_128_SHA", 49203);
    add("TLS_ECDHE_PSK_WITH_3DES_EDE_CBC_SHA", 49204);
    add("TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA", 49205);
    add("TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA", 49206);
    add("TLS_ECDHE_PSK_WITH_AES_128_CBC_SHA256", 49207);
    add("TLS_ECDHE_PSK_WITH_AES_256_CBC_SHA384", 49208);
    add("TLS_ECDHE_PSK_WITH_NULL_SHA", 49209);
    add("TLS_ECDHE_PSK_WITH_NULL_SHA256", 49210);
    add("TLS_ECDHE_PSK_WITH_NULL_SHA384", 49211);
    C_NULL = valueOf(0, 0);
    C_SCSV = valueOf(0, 255);
  }
}
