package sun.security.x509;

import java.io.IOException;
import sun.security.util.DerOutputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;

public class CertificatePolicyId {
  private ObjectIdentifier id;
  
  public CertificatePolicyId(ObjectIdentifier paramObjectIdentifier) {
    this.id = paramObjectIdentifier;
  }
  
  public CertificatePolicyId(DerValue paramDerValue) throws IOException {
    this.id = paramDerValue.getOID();
  }
  
  public ObjectIdentifier getIdentifier() {
    return this.id;
  }
  
  public String toString() {
    return "CertificatePolicyId: [" + this.id
      .toString() + "]\n";
  }
  
  public void encode(DerOutputStream paramDerOutputStream) throws IOException {
    paramDerOutputStream.putOID(this.id);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject instanceof CertificatePolicyId)
      return this.id.equals(((CertificatePolicyId)paramObject)
          .getIdentifier()); 
    return false;
  }
  
  public int hashCode() {
    return this.id.hashCode();
  }
}
