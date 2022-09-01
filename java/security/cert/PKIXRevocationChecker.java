package java.security.cert;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class PKIXRevocationChecker extends PKIXCertPathChecker {
  private URI ocspResponder;
  
  private X509Certificate ocspResponderCert;
  
  private List<Extension> ocspExtensions = Collections.emptyList();
  
  private Map<X509Certificate, byte[]> ocspResponses = (Map)Collections.emptyMap();
  
  private Set<Option> options = Collections.emptySet();
  
  public void setOcspResponder(URI paramURI) {
    this.ocspResponder = paramURI;
  }
  
  public URI getOcspResponder() {
    return this.ocspResponder;
  }
  
  public void setOcspResponderCert(X509Certificate paramX509Certificate) {
    this.ocspResponderCert = paramX509Certificate;
  }
  
  public X509Certificate getOcspResponderCert() {
    return this.ocspResponderCert;
  }
  
  public void setOcspExtensions(List<Extension> paramList) {
    this
      .ocspExtensions = (paramList == null) ? Collections.<Extension>emptyList() : new ArrayList<>(paramList);
  }
  
  public List<Extension> getOcspExtensions() {
    return Collections.unmodifiableList(this.ocspExtensions);
  }
  
  public void setOcspResponses(Map<X509Certificate, byte[]> paramMap) {
    if (paramMap == null) {
      this.ocspResponses = Collections.emptyMap();
    } else {
      HashMap<Object, Object> hashMap = new HashMap<>(paramMap.size());
      for (Map.Entry<X509Certificate, byte> entry : paramMap.entrySet())
        hashMap.put(entry.getKey(), ((byte[])entry.getValue()).clone()); 
      this.ocspResponses = (Map)hashMap;
    } 
  }
  
  public Map<X509Certificate, byte[]> getOcspResponses() {
    HashMap<Object, Object> hashMap = new HashMap<>(this.ocspResponses.size());
    for (Map.Entry<X509Certificate, byte> entry : this.ocspResponses.entrySet())
      hashMap.put(entry.getKey(), ((byte[])entry.getValue()).clone()); 
    return (Map)hashMap;
  }
  
  public void setOptions(Set<Option> paramSet) {
    this
      .options = (paramSet == null) ? Collections.<Option>emptySet() : new HashSet<>(paramSet);
  }
  
  public Set<Option> getOptions() {
    return Collections.unmodifiableSet(this.options);
  }
  
  public PKIXRevocationChecker clone() {
    PKIXRevocationChecker pKIXRevocationChecker = (PKIXRevocationChecker)super.clone();
    pKIXRevocationChecker.ocspExtensions = new ArrayList<>(this.ocspExtensions);
    pKIXRevocationChecker.ocspResponses = (Map)new HashMap<>((Map)this.ocspResponses);
    for (Map.Entry<X509Certificate, byte> entry : pKIXRevocationChecker.ocspResponses.entrySet()) {
      byte[] arrayOfByte = (byte[])entry.getValue();
      entry.setValue(arrayOfByte.clone());
    } 
    pKIXRevocationChecker.options = new HashSet<>(this.options);
    return pKIXRevocationChecker;
  }
  
  public abstract List<CertPathValidatorException> getSoftFailExceptions();
  
  public enum PKIXRevocationChecker {
  
  }
}
