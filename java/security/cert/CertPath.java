package java.security.cert;

import java.io.NotSerializableException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public abstract class CertPath implements Serializable {
  private static final long serialVersionUID = 6068470306649138683L;
  
  private String type;
  
  protected CertPath(String paramString) {
    this.type = paramString;
  }
  
  public String getType() {
    return this.type;
  }
  
  public abstract Iterator<String> getEncodings();
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof CertPath))
      return false; 
    CertPath certPath = (CertPath)paramObject;
    if (!certPath.getType().equals(this.type))
      return false; 
    List<? extends Certificate> list1 = getCertificates();
    List<? extends Certificate> list2 = certPath.getCertificates();
    return list1.equals(list2);
  }
  
  public int hashCode() {
    int i = this.type.hashCode();
    i = 31 * i + getCertificates().hashCode();
    return i;
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    Iterator<? extends Certificate> iterator = getCertificates().iterator();
    stringBuffer.append("\n" + this.type + " Cert Path: length = " + 
        getCertificates().size() + ".\n");
    stringBuffer.append("[\n");
    byte b = 1;
    while (iterator.hasNext()) {
      stringBuffer.append("=========================================================Certificate " + b + " start.\n");
      Certificate certificate = iterator.next();
      stringBuffer.append(certificate.toString());
      stringBuffer.append("\n=========================================================Certificate " + b + " end.\n\n\n");
      b++;
    } 
    stringBuffer.append("\n]");
    return stringBuffer.toString();
  }
  
  public abstract byte[] getEncoded() throws CertificateEncodingException;
  
  public abstract byte[] getEncoded(String paramString) throws CertificateEncodingException;
  
  public abstract List<? extends Certificate> getCertificates();
  
  protected Object writeReplace() throws ObjectStreamException {
    try {
      return new CertPathRep(this.type, getEncoded());
    } catch (CertificateException certificateException) {
      NotSerializableException notSerializableException = new NotSerializableException("java.security.cert.CertPath: " + this.type);
      notSerializableException.initCause(certificateException);
      throw notSerializableException;
    } 
  }
  
  protected static class CertPath {}
}
