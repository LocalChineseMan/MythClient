package java.security.cert;

public interface CertSelector extends Cloneable {
  boolean match(Certificate paramCertificate);
  
  Object clone();
}
