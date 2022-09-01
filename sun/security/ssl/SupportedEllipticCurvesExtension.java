package sun.security.ssl;

import java.io.IOException;
import java.security.spec.ECParameterSpec;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.SSLProtocolException;

final class SupportedEllipticCurvesExtension extends HelloExtension {
  static final SupportedEllipticCurvesExtension DEFAULT;
  
  private static final boolean fips = SunJSSE.isFIPS();
  
  private final int[] curveIds;
  
  private static final int ARBITRARY_PRIME = 65281;
  
  private static final int ARBITRARY_CHAR2 = 65282;
  
  private static final String[] NAMED_CURVE_OID_TABLE;
  
  private static final Map<String, Integer> curveIndices;
  
  static {
    int[] arrayOfInt;
  }
  
  static {
    if (!fips) {
      arrayOfInt = new int[] { 
          23, 1, 3, 19, 21, 6, 7, 9, 10, 24, 
          11, 12, 25, 13, 14, 15, 16, 17, 2, 18, 
          4, 5, 20, 8, 22 };
    } else {
      arrayOfInt = new int[] { 
          23, 1, 3, 19, 21, 6, 7, 9, 10, 24, 
          11, 12, 25, 13, 14 };
    } 
    DEFAULT = new SupportedEllipticCurvesExtension(arrayOfInt);
    NAMED_CURVE_OID_TABLE = new String[] { 
        null, "1.3.132.0.1", "1.3.132.0.2", "1.3.132.0.15", "1.3.132.0.24", "1.3.132.0.25", "1.3.132.0.26", "1.3.132.0.27", "1.3.132.0.3", "1.3.132.0.16", 
        "1.3.132.0.17", "1.3.132.0.36", "1.3.132.0.37", "1.3.132.0.38", "1.3.132.0.39", "1.3.132.0.9", "1.3.132.0.8", "1.3.132.0.30", "1.3.132.0.31", "1.2.840.10045.3.1.1", 
        "1.3.132.0.32", "1.3.132.0.33", "1.3.132.0.10", "1.2.840.10045.3.1.7", "1.3.132.0.34", "1.3.132.0.35" };
    curveIndices = new HashMap<>();
    for (byte b = 1; b < NAMED_CURVE_OID_TABLE.length; b++)
      curveIndices.put(NAMED_CURVE_OID_TABLE[b], Integer.valueOf(b)); 
  }
  
  private SupportedEllipticCurvesExtension(int[] paramArrayOfint) {
    super(ExtensionType.EXT_ELLIPTIC_CURVES);
    this.curveIds = paramArrayOfint;
  }
  
  SupportedEllipticCurvesExtension(HandshakeInStream paramHandshakeInStream, int paramInt) throws IOException {
    super(ExtensionType.EXT_ELLIPTIC_CURVES);
    int i = paramHandshakeInStream.getInt16();
    if ((paramInt & 0x1) != 0 || i + 2 != paramInt)
      throw new SSLProtocolException("Invalid " + this.type + " extension"); 
    this.curveIds = new int[i >> 1];
    for (byte b = 0; b < this.curveIds.length; b++)
      this.curveIds[b] = paramHandshakeInStream.getInt16(); 
  }
  
  boolean contains(int paramInt) {
    for (int i : this.curveIds) {
      if (paramInt == i)
        return true; 
    } 
    return false;
  }
  
  int[] curveIds() {
    return this.curveIds;
  }
  
  int length() {
    return 6 + (this.curveIds.length << 1);
  }
  
  void send(HandshakeOutStream paramHandshakeOutStream) throws IOException {
    paramHandshakeOutStream.putInt16(this.type.id);
    int i = this.curveIds.length << 1;
    paramHandshakeOutStream.putInt16(i + 2);
    paramHandshakeOutStream.putInt16(i);
    for (int j : this.curveIds)
      paramHandshakeOutStream.putInt16(j); 
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Extension " + this.type + ", curve names: {");
    boolean bool = true;
    for (int i : this.curveIds) {
      if (bool) {
        bool = false;
      } else {
        stringBuilder.append(", ");
      } 
      String str = getCurveOid(i);
      if (str != null) {
        ECParameterSpec eCParameterSpec = JsseJce.getECParameterSpec(str);
        if (eCParameterSpec != null) {
          stringBuilder.append(eCParameterSpec.toString().split(" ")[0]);
        } else {
          stringBuilder.append(str);
        } 
      } else if (i == 65281) {
        stringBuilder.append("arbitrary_explicit_prime_curves");
      } else if (i == 65282) {
        stringBuilder.append("arbitrary_explicit_char2_curves");
      } else {
        stringBuilder.append("unknown curve " + i);
      } 
    } 
    stringBuilder.append("}");
    return stringBuilder.toString();
  }
  
  static boolean isSupported(int paramInt) {
    if (paramInt <= 0 || paramInt >= NAMED_CURVE_OID_TABLE.length)
      return false; 
    if (!fips)
      return true; 
    return DEFAULT.contains(paramInt);
  }
  
  static int getCurveIndex(ECParameterSpec paramECParameterSpec) {
    String str = JsseJce.getNamedCurveOid(paramECParameterSpec);
    if (str == null)
      return -1; 
    Integer integer = curveIndices.get(str);
    return (integer == null) ? -1 : integer.intValue();
  }
  
  static String getCurveOid(int paramInt) {
    if (paramInt > 0 && paramInt < NAMED_CURVE_OID_TABLE.length)
      return NAMED_CURVE_OID_TABLE[paramInt]; 
    return null;
  }
}
