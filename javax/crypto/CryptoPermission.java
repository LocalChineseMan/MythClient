package javax.crypto;

import java.security.Permission;
import java.security.PermissionCollection;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;

class CryptoPermission extends Permission {
  private static final long serialVersionUID = 8987399626114087514L;
  
  private String alg;
  
  private int maxKeySize = Integer.MAX_VALUE;
  
  private String exemptionMechanism = null;
  
  private AlgorithmParameterSpec algParamSpec = null;
  
  private boolean checkParam = false;
  
  static final String ALG_NAME_WILDCARD = "*";
  
  CryptoPermission(String paramString) {
    super(null);
    this.alg = paramString;
  }
  
  CryptoPermission(String paramString, int paramInt) {
    super(null);
    this.alg = paramString;
    this.maxKeySize = paramInt;
  }
  
  CryptoPermission(String paramString, int paramInt, AlgorithmParameterSpec paramAlgorithmParameterSpec) {
    super(null);
    this.alg = paramString;
    this.maxKeySize = paramInt;
    this.checkParam = true;
    this.algParamSpec = paramAlgorithmParameterSpec;
  }
  
  CryptoPermission(String paramString1, String paramString2) {
    super(null);
    this.alg = paramString1;
    this.exemptionMechanism = paramString2;
  }
  
  CryptoPermission(String paramString1, int paramInt, String paramString2) {
    super(null);
    this.alg = paramString1;
    this.exemptionMechanism = paramString2;
    this.maxKeySize = paramInt;
  }
  
  CryptoPermission(String paramString1, int paramInt, AlgorithmParameterSpec paramAlgorithmParameterSpec, String paramString2) {
    super(null);
    this.alg = paramString1;
    this.exemptionMechanism = paramString2;
    this.maxKeySize = paramInt;
    this.checkParam = true;
    this.algParamSpec = paramAlgorithmParameterSpec;
  }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof CryptoPermission))
      return false; 
    CryptoPermission cryptoPermission = (CryptoPermission)paramPermission;
    if (!this.alg.equalsIgnoreCase(cryptoPermission.alg) && 
      !this.alg.equalsIgnoreCase("*"))
      return false; 
    if (cryptoPermission.maxKeySize <= this.maxKeySize) {
      if (!impliesParameterSpec(cryptoPermission.checkParam, cryptoPermission.algParamSpec))
        return false; 
      if (impliesExemptionMechanism(cryptoPermission.exemptionMechanism))
        return true; 
    } 
    return false;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof CryptoPermission))
      return false; 
    CryptoPermission cryptoPermission = (CryptoPermission)paramObject;
    if (!this.alg.equalsIgnoreCase(cryptoPermission.alg) || this.maxKeySize != cryptoPermission.maxKeySize)
      return false; 
    if (this.checkParam != cryptoPermission.checkParam)
      return false; 
    return (equalObjects(this.exemptionMechanism, cryptoPermission.exemptionMechanism) && 
      
      equalObjects(this.algParamSpec, cryptoPermission.algParamSpec));
  }
  
  public int hashCode() {
    int i = this.alg.hashCode();
    i ^= this.maxKeySize;
    if (this.exemptionMechanism != null)
      i ^= this.exemptionMechanism.hashCode(); 
    if (this.checkParam)
      i ^= 0x64; 
    if (this.algParamSpec != null)
      i ^= this.algParamSpec.hashCode(); 
    return i;
  }
  
  public String getActions() {
    return null;
  }
  
  public PermissionCollection newPermissionCollection() {
    return new CryptoPermissionCollection();
  }
  
  final String getAlgorithm() {
    return this.alg;
  }
  
  final String getExemptionMechanism() {
    return this.exemptionMechanism;
  }
  
  final int getMaxKeySize() {
    return this.maxKeySize;
  }
  
  final boolean getCheckParam() {
    return this.checkParam;
  }
  
  final AlgorithmParameterSpec getAlgorithmParameterSpec() {
    return this.algParamSpec;
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(100);
    stringBuilder.append("(CryptoPermission " + this.alg + " " + this.maxKeySize);
    if (this.algParamSpec != null)
      if (this.algParamSpec instanceof RC2ParameterSpec) {
        stringBuilder.append(" , effective " + ((RC2ParameterSpec)this.algParamSpec)
            .getEffectiveKeyBits());
      } else if (this.algParamSpec instanceof RC5ParameterSpec) {
        stringBuilder.append(" , rounds " + ((RC5ParameterSpec)this.algParamSpec)
            .getRounds());
      }  
    if (this.exemptionMechanism != null)
      stringBuilder.append(" " + this.exemptionMechanism); 
    stringBuilder.append(")");
    return stringBuilder.toString();
  }
  
  private boolean impliesExemptionMechanism(String paramString) {
    if (this.exemptionMechanism == null)
      return true; 
    if (paramString == null)
      return false; 
    if (this.exemptionMechanism.equals(paramString))
      return true; 
    return false;
  }
  
  private boolean impliesParameterSpec(boolean paramBoolean, AlgorithmParameterSpec paramAlgorithmParameterSpec) {
    if (this.checkParam && paramBoolean) {
      if (paramAlgorithmParameterSpec == null)
        return true; 
      if (this.algParamSpec == null)
        return false; 
      if (this.algParamSpec.getClass() != paramAlgorithmParameterSpec.getClass())
        return false; 
      if (paramAlgorithmParameterSpec instanceof RC2ParameterSpec && (
        (RC2ParameterSpec)paramAlgorithmParameterSpec).getEffectiveKeyBits() <= ((RC2ParameterSpec)this.algParamSpec)
        
        .getEffectiveKeyBits())
        return true; 
      if (paramAlgorithmParameterSpec instanceof RC5ParameterSpec && (
        (RC5ParameterSpec)paramAlgorithmParameterSpec).getRounds() <= ((RC5ParameterSpec)this.algParamSpec)
        .getRounds())
        return true; 
      if (paramAlgorithmParameterSpec instanceof PBEParameterSpec && (
        (PBEParameterSpec)paramAlgorithmParameterSpec).getIterationCount() <= ((PBEParameterSpec)this.algParamSpec)
        .getIterationCount())
        return true; 
      if (this.algParamSpec.equals(paramAlgorithmParameterSpec))
        return true; 
      return false;
    } 
    if (this.checkParam)
      return false; 
    return true;
  }
  
  private boolean equalObjects(Object paramObject1, Object paramObject2) {
    if (paramObject1 == null)
      return (paramObject2 == null); 
    return paramObject1.equals(paramObject2);
  }
}
