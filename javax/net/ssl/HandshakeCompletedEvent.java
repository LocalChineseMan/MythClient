package javax.net.ssl;

import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.EventObject;
import javax.security.cert.X509Certificate;

public class HandshakeCompletedEvent extends EventObject {
  private static final long serialVersionUID = 7914963744257769778L;
  
  private transient SSLSession session;
  
  public HandshakeCompletedEvent(SSLSocket paramSSLSocket, SSLSession paramSSLSession) {
    super(paramSSLSocket);
    this.session = paramSSLSession;
  }
  
  public SSLSession getSession() {
    return this.session;
  }
  
  public String getCipherSuite() {
    return this.session.getCipherSuite();
  }
  
  public Certificate[] getLocalCertificates() {
    return this.session.getLocalCertificates();
  }
  
  public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
    return this.session.getPeerCertificates();
  }
  
  public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
    return this.session.getPeerCertificateChain();
  }
  
  public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
    Principal principal;
    try {
      principal = this.session.getPeerPrincipal();
    } catch (AbstractMethodError abstractMethodError) {
      Certificate[] arrayOfCertificate = getPeerCertificates();
      principal = ((X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal();
    } 
    return principal;
  }
  
  public Principal getLocalPrincipal() {
    Principal principal;
    try {
      principal = this.session.getLocalPrincipal();
    } catch (AbstractMethodError abstractMethodError) {
      principal = null;
      Certificate[] arrayOfCertificate = getLocalCertificates();
      if (arrayOfCertificate != null)
        principal = ((X509Certificate)arrayOfCertificate[0]).getSubjectX500Principal(); 
    } 
    return principal;
  }
  
  public SSLSocket getSocket() {
    return (SSLSocket)getSource();
  }
}
