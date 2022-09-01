package sun.security.ssl;

import java.security.AlgorithmConstraints;
import java.security.CryptoPrimitive;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import sun.security.util.KeyUtil;

final class SignatureAndHashAlgorithm {
  static final int SUPPORTED_ALG_PRIORITY_MAX_NUM = 240;
  
  private static final Set<CryptoPrimitive> SIGNATURE_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE));
  
  private SignatureAndHashAlgorithm(HashAlgorithm paramHashAlgorithm, SignatureAlgorithm paramSignatureAlgorithm, String paramString, int paramInt) {
    this.hash = paramHashAlgorithm;
    this.algorithm = paramString;
    this.id = (paramHashAlgorithm.value & 0xFF) << 8 | paramSignatureAlgorithm.value & 0xFF;
    this.priority = paramInt;
  }
  
  private SignatureAndHashAlgorithm(String paramString, int paramInt1, int paramInt2) {
    this.hash = HashAlgorithm.valueOf(paramInt1 >> 8 & 0xFF);
    this.algorithm = paramString;
    this.id = paramInt1;
    this.priority = 240 + paramInt2 + 1;
  }
  
  static SignatureAndHashAlgorithm valueOf(int paramInt1, int paramInt2, int paramInt3) {
    paramInt1 &= 0xFF;
    paramInt2 &= 0xFF;
    int i = paramInt1 << 8 | paramInt2;
    SignatureAndHashAlgorithm signatureAndHashAlgorithm = supportedMap.get(Integer.valueOf(i));
    if (signatureAndHashAlgorithm == null)
      signatureAndHashAlgorithm = new SignatureAndHashAlgorithm("Unknown (hash:0x" + Integer.toString(paramInt1, 16) + ", signature:0x" + Integer.toString(paramInt2, 16) + ")", i, paramInt3); 
    return signatureAndHashAlgorithm;
  }
  
  int getHashValue() {
    return this.id >> 8 & 0xFF;
  }
  
  int getSignatureValue() {
    return this.id & 0xFF;
  }
  
  String getAlgorithmName() {
    return this.algorithm;
  }
  
  static int sizeInRecord() {
    return 2;
  }
  
  static Collection<SignatureAndHashAlgorithm> getSupportedAlgorithms(AlgorithmConstraints paramAlgorithmConstraints) {
    ArrayList<SignatureAndHashAlgorithm> arrayList = new ArrayList();
    synchronized (priorityMap) {
      for (SignatureAndHashAlgorithm signatureAndHashAlgorithm : priorityMap.values()) {
        if (signatureAndHashAlgorithm.priority <= 240 && paramAlgorithmConstraints
          .permits(SIGNATURE_PRIMITIVE_SET, signatureAndHashAlgorithm.algorithm, null))
          arrayList.add(signatureAndHashAlgorithm); 
      } 
    } 
    return arrayList;
  }
  
  static Collection<SignatureAndHashAlgorithm> getSupportedAlgorithms(Collection<SignatureAndHashAlgorithm> paramCollection) {
    ArrayList<SignatureAndHashAlgorithm> arrayList = new ArrayList();
    for (SignatureAndHashAlgorithm signatureAndHashAlgorithm : paramCollection) {
      if (signatureAndHashAlgorithm.priority <= 240)
        arrayList.add(signatureAndHashAlgorithm); 
    } 
    return arrayList;
  }
  
  static String[] getAlgorithmNames(Collection<SignatureAndHashAlgorithm> paramCollection) {
    ArrayList<String> arrayList = new ArrayList();
    if (paramCollection != null)
      for (SignatureAndHashAlgorithm signatureAndHashAlgorithm : paramCollection)
        arrayList.add(signatureAndHashAlgorithm.algorithm);  
    String[] arrayOfString = new String[arrayList.size()];
    return arrayList.<String>toArray(arrayOfString);
  }
  
  static Set<String> getHashAlgorithmNames(Collection<SignatureAndHashAlgorithm> paramCollection) {
    HashSet<String> hashSet = new HashSet();
    if (paramCollection != null)
      for (SignatureAndHashAlgorithm signatureAndHashAlgorithm : paramCollection) {
        if (signatureAndHashAlgorithm.hash.value > 0)
          hashSet.add(signatureAndHashAlgorithm.hash.standardName); 
      }  
    return hashSet;
  }
  
  static String getHashAlgorithmName(SignatureAndHashAlgorithm paramSignatureAndHashAlgorithm) {
    return paramSignatureAndHashAlgorithm.hash.standardName;
  }
  
  private static void supports(HashAlgorithm paramHashAlgorithm, SignatureAlgorithm paramSignatureAlgorithm, String paramString, int paramInt) {
    SignatureAndHashAlgorithm signatureAndHashAlgorithm = new SignatureAndHashAlgorithm(paramHashAlgorithm, paramSignatureAlgorithm, paramString, paramInt);
    if (supportedMap.put(Integer.valueOf(signatureAndHashAlgorithm.id), signatureAndHashAlgorithm) != null)
      throw new RuntimeException("Duplicate SignatureAndHashAlgorithm definition, id: " + signatureAndHashAlgorithm.id); 
    if (priorityMap.put(Integer.valueOf(signatureAndHashAlgorithm.priority), signatureAndHashAlgorithm) != null)
      throw new RuntimeException("Duplicate SignatureAndHashAlgorithm definition, priority: " + signatureAndHashAlgorithm.priority); 
  }
  
  static SignatureAndHashAlgorithm getPreferableAlgorithm(Collection<SignatureAndHashAlgorithm> paramCollection, String paramString) {
    return getPreferableAlgorithm(paramCollection, paramString, null);
  }
  
  static SignatureAndHashAlgorithm getPreferableAlgorithm(Collection<SignatureAndHashAlgorithm> paramCollection, String paramString, PrivateKey paramPrivateKey) {
    if (paramString == null && !paramCollection.isEmpty()) {
      for (SignatureAndHashAlgorithm signatureAndHashAlgorithm : paramCollection) {
        if (signatureAndHashAlgorithm.priority <= 240)
          return signatureAndHashAlgorithm; 
      } 
      return null;
    } 
    if (paramString == null)
      return null; 
    int i = Integer.MAX_VALUE;
    if (paramPrivateKey != null && "rsa"
      .equalsIgnoreCase(paramPrivateKey.getAlgorithm()) && paramString
      .equalsIgnoreCase("rsa")) {
      int j = KeyUtil.getKeySize(paramPrivateKey);
      if (j >= 768) {
        i = HashAlgorithm.SHA512.length;
      } else if (j >= 512 && j < 768) {
        i = HashAlgorithm.SHA256.length;
      } else if (j > 0 && j < 512) {
        i = HashAlgorithm.SHA1.length;
      } 
    } 
    for (SignatureAndHashAlgorithm signatureAndHashAlgorithm : paramCollection) {
      int j = signatureAndHashAlgorithm.id & 0xFF;
      if (paramString.equalsIgnoreCase("rsa") && j == SignatureAlgorithm.RSA.value) {
        if (signatureAndHashAlgorithm.hash.length <= i)
          return signatureAndHashAlgorithm; 
        continue;
      } 
      if ((paramString
        .equalsIgnoreCase("dsa") && j == SignatureAlgorithm.DSA.value) || (paramString
        
        .equalsIgnoreCase("ecdsa") && j == SignatureAlgorithm.ECDSA.value) || (paramString
        
        .equalsIgnoreCase("ec") && j == SignatureAlgorithm.ECDSA.value))
        return signatureAndHashAlgorithm; 
    } 
    return null;
  }
  
  enum HashAlgorithm {
    UNDEFINED("undefined", "", -1, -1),
    NONE("none", "NONE", 0, -1),
    MD5("md5", "MD5", 1, 16),
    SHA1("sha1", "SHA-1", 2, 20),
    SHA224("sha224", "SHA-224", 3, 28),
    SHA256("sha256", "SHA-256", 4, 32),
    SHA384("sha384", "SHA-384", 5, 48),
    SHA512("sha512", "SHA-512", 6, 64);
    
    final String name;
    
    final String standardName;
    
    final int value;
    
    final int length;
    
    HashAlgorithm(String param1String1, String param1String2, int param1Int1, int param1Int2) {
      this.name = param1String1;
      this.standardName = param1String2;
      this.value = param1Int1;
      this.length = param1Int2;
    }
  }
  
  enum SignatureAlgorithm {
    UNDEFINED("undefined", -1),
    ANONYMOUS("anonymous", 0),
    RSA("rsa", 1),
    DSA("dsa", 2),
    ECDSA("ecdsa", 3);
    
    final String name;
    
    final int value;
    
    SignatureAlgorithm(String param1String1, int param1Int1) {
      this.name = param1String1;
      this.value = param1Int1;
    }
  }
  
  private static final Map<Integer, SignatureAndHashAlgorithm> supportedMap = Collections.synchronizedSortedMap(new TreeMap<>());
  
  private static final Map<Integer, SignatureAndHashAlgorithm> priorityMap = Collections.synchronizedSortedMap(new TreeMap<>());
  
  private HashAlgorithm hash;
  
  private int id;
  
  private String algorithm;
  
  private int priority;
  
  static {
    synchronized (supportedMap) {
      char c = 'ð';
      supports(HashAlgorithm.MD5, SignatureAlgorithm.RSA, "MD5withRSA", --c);
      supports(HashAlgorithm.SHA1, SignatureAlgorithm.DSA, "SHA1withDSA", --c);
      supports(HashAlgorithm.SHA1, SignatureAlgorithm.RSA, "SHA1withRSA", --c);
      supports(HashAlgorithm.SHA1, SignatureAlgorithm.ECDSA, "SHA1withECDSA", --c);
      supports(HashAlgorithm.SHA224, SignatureAlgorithm.RSA, "SHA224withRSA", --c);
      supports(HashAlgorithm.SHA224, SignatureAlgorithm.ECDSA, "SHA224withECDSA", --c);
      supports(HashAlgorithm.SHA256, SignatureAlgorithm.RSA, "SHA256withRSA", --c);
      supports(HashAlgorithm.SHA256, SignatureAlgorithm.ECDSA, "SHA256withECDSA", --c);
      supports(HashAlgorithm.SHA384, SignatureAlgorithm.RSA, "SHA384withRSA", --c);
      supports(HashAlgorithm.SHA384, SignatureAlgorithm.ECDSA, "SHA384withECDSA", --c);
      supports(HashAlgorithm.SHA512, SignatureAlgorithm.RSA, "SHA512withRSA", --c);
      supports(HashAlgorithm.SHA512, SignatureAlgorithm.ECDSA, "SHA512withECDSA", --c);
    } 
  }
}
