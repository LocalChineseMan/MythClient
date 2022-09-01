package java.security.cert;

import java.io.IOException;
import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import sun.security.x509.NameConstraintsExtension;

public class TrustAnchor {
  private final PublicKey pubKey;
  
  private final String caName;
  
  private final X500Principal caPrincipal;
  
  private final X509Certificate trustedCert;
  
  private byte[] ncBytes;
  
  private NameConstraintsExtension nc;
  
  public TrustAnchor(X509Certificate paramX509Certificate, byte[] paramArrayOfbyte) {
    if (paramX509Certificate == null)
      throw new NullPointerException("the trustedCert parameter must be non-null"); 
    this.trustedCert = paramX509Certificate;
    this.pubKey = null;
    this.caName = null;
    this.caPrincipal = null;
    setNameConstraints(paramArrayOfbyte);
  }
  
  public TrustAnchor(X500Principal paramX500Principal, PublicKey paramPublicKey, byte[] paramArrayOfbyte) {
    if (paramX500Principal == null || paramPublicKey == null)
      throw new NullPointerException(); 
    this.trustedCert = null;
    this.caPrincipal = paramX500Principal;
    this.caName = paramX500Principal.getName();
    this.pubKey = paramPublicKey;
    setNameConstraints(paramArrayOfbyte);
  }
  
  public TrustAnchor(String paramString, PublicKey paramPublicKey, byte[] paramArrayOfbyte) {
    if (paramPublicKey == null)
      throw new NullPointerException("the pubKey parameter must be non-null"); 
    if (paramString == null)
      throw new NullPointerException("the caName parameter must be non-null"); 
    if (paramString.length() == 0)
      throw new IllegalArgumentException("the caName parameter must be a non-empty String"); 
    this.caPrincipal = new X500Principal(paramString);
    this.pubKey = paramPublicKey;
    this.caName = paramString;
    this.trustedCert = null;
    setNameConstraints(paramArrayOfbyte);
  }
  
  public final X509Certificate getTrustedCert() {
    return this.trustedCert;
  }
  
  public final X500Principal getCA() {
    return this.caPrincipal;
  }
  
  public final String getCAName() {
    return this.caName;
  }
  
  public final PublicKey getCAPublicKey() {
    return this.pubKey;
  }
  
  private void setNameConstraints(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null) {
      this.ncBytes = null;
      this.nc = null;
    } else {
      this.ncBytes = (byte[])paramArrayOfbyte.clone();
      try {
        this.nc = new NameConstraintsExtension(Boolean.FALSE, paramArrayOfbyte);
      } catch (IOException iOException) {
        IllegalArgumentException illegalArgumentException = new IllegalArgumentException(iOException.getMessage());
        illegalArgumentException.initCause(iOException);
        throw illegalArgumentException;
      } 
    } 
  }
  
  public final byte[] getNameConstraints() {
    return (this.ncBytes == null) ? null : (byte[])this.ncBytes.clone();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("[\n");
    if (this.pubKey != null) {
      stringBuffer.append("  Trusted CA Public Key: " + this.pubKey.toString() + "\n");
      stringBuffer.append("  Trusted CA Issuer Name: " + 
          String.valueOf(this.caName) + "\n");
    } else {
      stringBuffer.append("  Trusted CA cert: " + this.trustedCert.toString() + "\n");
    } 
    if (this.nc != null)
      stringBuffer.append("  Name Constraints: " + this.nc.toString() + "\n"); 
    return stringBuffer.toString();
  }
}
