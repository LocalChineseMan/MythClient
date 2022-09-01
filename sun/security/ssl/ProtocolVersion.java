package sun.security.ssl;

import java.security.CryptoPrimitive;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public final class ProtocolVersion implements Comparable<ProtocolVersion> {
  static final int LIMIT_MAX_VALUE = 65535;
  
  static final int LIMIT_MIN_VALUE = 0;
  
  static final ProtocolVersion NONE = new ProtocolVersion(-1, "NONE");
  
  static final ProtocolVersion SSL20Hello = new ProtocolVersion(2, "SSLv2Hello");
  
  static final ProtocolVersion SSL30 = new ProtocolVersion(768, "SSLv3");
  
  static final ProtocolVersion TLS10 = new ProtocolVersion(769, "TLSv1");
  
  static final ProtocolVersion TLS11 = new ProtocolVersion(770, "TLSv1.1");
  
  static final ProtocolVersion TLS12 = new ProtocolVersion(771, "TLSv1.2");
  
  private static final boolean FIPS = SunJSSE.isFIPS();
  
  static final ProtocolVersion MIN = FIPS ? TLS10 : SSL30;
  
  static final ProtocolVersion MAX = TLS12;
  
  static final ProtocolVersion DEFAULT = TLS12;
  
  static final ProtocolVersion DEFAULT_HELLO = FIPS ? TLS10 : SSL30;
  
  static final Set<ProtocolVersion> availableProtocols;
  
  public final int v;
  
  public final byte major;
  
  public final byte minor;
  
  final String name;
  
  static {
    HashSet<ProtocolVersion> hashSet = new HashSet(5);
    ProtocolVersion[] arrayOfProtocolVersion = { SSL20Hello, SSL30, TLS10, TLS11, TLS12 };
    for (ProtocolVersion protocolVersion : arrayOfProtocolVersion) {
      if (SSLAlgorithmConstraints.DEFAULT_SSL_ONLY.permits(
          EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), protocolVersion.name, null))
        hashSet.add(protocolVersion); 
    } 
    availableProtocols = Collections.unmodifiableSet(hashSet);
  }
  
  private ProtocolVersion(int paramInt, String paramString) {
    this.v = paramInt;
    this.name = paramString;
    this.major = (byte)(paramInt >>> 8);
    this.minor = (byte)(paramInt & 0xFF);
  }
  
  private static ProtocolVersion valueOf(int paramInt) {
    if (paramInt == SSL30.v)
      return SSL30; 
    if (paramInt == TLS10.v)
      return TLS10; 
    if (paramInt == TLS11.v)
      return TLS11; 
    if (paramInt == TLS12.v)
      return TLS12; 
    if (paramInt == SSL20Hello.v)
      return SSL20Hello; 
    int i = paramInt >>> 8 & 0xFF;
    int j = paramInt & 0xFF;
    return new ProtocolVersion(paramInt, "Unknown-" + i + "." + j);
  }
  
  public static ProtocolVersion valueOf(int paramInt1, int paramInt2) {
    return valueOf((paramInt1 & 0xFF) << 8 | paramInt2 & 0xFF);
  }
  
  static ProtocolVersion valueOf(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("Protocol cannot be null"); 
    if (FIPS && (paramString.equals(SSL30.name) || paramString.equals(SSL20Hello.name)))
      throw new IllegalArgumentException("Only TLS 1.0 or later allowed in FIPS mode"); 
    if (paramString.equals(SSL30.name))
      return SSL30; 
    if (paramString.equals(TLS10.name))
      return TLS10; 
    if (paramString.equals(TLS11.name))
      return TLS11; 
    if (paramString.equals(TLS12.name))
      return TLS12; 
    if (paramString.equals(SSL20Hello.name))
      return SSL20Hello; 
    throw new IllegalArgumentException(paramString);
  }
  
  public String toString() {
    return this.name;
  }
  
  public int compareTo(ProtocolVersion paramProtocolVersion) {
    return this.v - paramProtocolVersion.v;
  }
}
