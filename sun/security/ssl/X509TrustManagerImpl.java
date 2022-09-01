package sun.security.ssl;

import java.net.Socket;
import java.security.AlgorithmConstraints;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import sun.security.util.HostnameChecker;
import sun.security.validator.KeyStores;
import sun.security.validator.Validator;

final class X509TrustManagerImpl extends X509ExtendedTrustManager implements X509TrustManager {
  private final String validatorType;
  
  private final Collection<X509Certificate> trustedCerts;
  
  private final PKIXBuilderParameters pkixParams;
  
  private volatile Validator clientValidator;
  
  private volatile Validator serverValidator;
  
  private static final Debug debug = Debug.getInstance("ssl");
  
  X509TrustManagerImpl(String paramString, KeyStore paramKeyStore) throws KeyStoreException {
    this.validatorType = paramString;
    this.pkixParams = null;
    if (paramKeyStore == null) {
      this.trustedCerts = Collections.emptySet();
    } else {
      this.trustedCerts = KeyStores.getTrustedCerts(paramKeyStore);
    } 
    showTrustedCerts();
  }
  
  X509TrustManagerImpl(String paramString, PKIXBuilderParameters paramPKIXBuilderParameters) {
    this.validatorType = paramString;
    this.pkixParams = paramPKIXBuilderParameters;
    Validator validator = getValidator("tls server");
    this.trustedCerts = validator.getTrustedCertificates();
    this.serverValidator = validator;
    showTrustedCerts();
  }
  
  public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException {
    checkTrusted(paramArrayOfX509Certificate, paramString, (Socket)null, true);
  }
  
  public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException {
    checkTrusted(paramArrayOfX509Certificate, paramString, (Socket)null, false);
  }
  
  public X509Certificate[] getAcceptedIssuers() {
    X509Certificate[] arrayOfX509Certificate = new X509Certificate[this.trustedCerts.size()];
    this.trustedCerts.toArray(arrayOfX509Certificate);
    return arrayOfX509Certificate;
  }
  
  public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket) throws CertificateException {
    checkTrusted(paramArrayOfX509Certificate, paramString, paramSocket, true);
  }
  
  public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket) throws CertificateException {
    checkTrusted(paramArrayOfX509Certificate, paramString, paramSocket, false);
  }
  
  public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine) throws CertificateException {
    checkTrusted(paramArrayOfX509Certificate, paramString, paramSSLEngine, true);
  }
  
  public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine) throws CertificateException {
    checkTrusted(paramArrayOfX509Certificate, paramString, paramSSLEngine, false);
  }
  
  private Validator checkTrustedInit(X509Certificate[] paramArrayOfX509Certificate, String paramString, boolean paramBoolean) {
    if (paramArrayOfX509Certificate == null || paramArrayOfX509Certificate.length == 0)
      throw new IllegalArgumentException("null or zero-length certificate chain"); 
    if (paramString == null || paramString.length() == 0)
      throw new IllegalArgumentException("null or zero-length authentication type"); 
    Validator validator = null;
    if (paramBoolean) {
      validator = this.clientValidator;
      if (validator == null)
        synchronized (this) {
          validator = this.clientValidator;
          if (validator == null) {
            validator = getValidator("tls client");
            this.clientValidator = validator;
          } 
        }  
    } else {
      validator = this.serverValidator;
      if (validator == null)
        synchronized (this) {
          validator = this.serverValidator;
          if (validator == null) {
            validator = getValidator("tls server");
            this.serverValidator = validator;
          } 
        }  
    } 
    return validator;
  }
  
  private void checkTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, Socket paramSocket, boolean paramBoolean) throws CertificateException {
    Validator validator = checkTrustedInit(paramArrayOfX509Certificate, paramString, paramBoolean);
    SSLAlgorithmConstraints sSLAlgorithmConstraints = null;
    if (paramSocket != null && paramSocket.isConnected() && paramSocket instanceof SSLSocket) {
      SSLSocket sSLSocket = (SSLSocket)paramSocket;
      SSLSession sSLSession = sSLSocket.getHandshakeSession();
      if (sSLSession == null)
        throw new CertificateException("No handshake session"); 
      String str = sSLSocket.getSSLParameters().getEndpointIdentificationAlgorithm();
      if (str != null && str.length() != 0)
        checkIdentity(sSLSession, paramArrayOfX509Certificate[0], str, paramBoolean, 
            getRequestedServerNames(paramSocket)); 
      ProtocolVersion protocolVersion = ProtocolVersion.valueOf(sSLSession.getProtocol());
      if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
        if (sSLSession instanceof ExtendedSSLSession) {
          ExtendedSSLSession extendedSSLSession = (ExtendedSSLSession)sSLSession;
          String[] arrayOfString = extendedSSLSession.getLocalSupportedSignatureAlgorithms();
          sSLAlgorithmConstraints = new SSLAlgorithmConstraints(sSLSocket, arrayOfString, false);
        } else {
          sSLAlgorithmConstraints = new SSLAlgorithmConstraints(sSLSocket, false);
        } 
      } else {
        sSLAlgorithmConstraints = new SSLAlgorithmConstraints(sSLSocket, false);
      } 
    } 
    X509Certificate[] arrayOfX509Certificate = null;
    if (paramBoolean) {
      arrayOfX509Certificate = validate(validator, paramArrayOfX509Certificate, sSLAlgorithmConstraints, null);
    } else {
      arrayOfX509Certificate = validate(validator, paramArrayOfX509Certificate, sSLAlgorithmConstraints, paramString);
    } 
    if (debug != null && Debug.isOn("trustmanager")) {
      System.out.println("Found trusted certificate:");
      System.out.println(arrayOfX509Certificate[arrayOfX509Certificate.length - 1]);
    } 
  }
  
  private void checkTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString, SSLEngine paramSSLEngine, boolean paramBoolean) throws CertificateException {
    Validator validator = checkTrustedInit(paramArrayOfX509Certificate, paramString, paramBoolean);
    SSLAlgorithmConstraints sSLAlgorithmConstraints = null;
    if (paramSSLEngine != null) {
      SSLSession sSLSession = paramSSLEngine.getHandshakeSession();
      if (sSLSession == null)
        throw new CertificateException("No handshake session"); 
      String str = paramSSLEngine.getSSLParameters().getEndpointIdentificationAlgorithm();
      if (str != null && str.length() != 0)
        checkIdentity(sSLSession, paramArrayOfX509Certificate[0], str, paramBoolean, 
            getRequestedServerNames(paramSSLEngine)); 
      ProtocolVersion protocolVersion = ProtocolVersion.valueOf(sSLSession.getProtocol());
      if (protocolVersion.v >= ProtocolVersion.TLS12.v) {
        if (sSLSession instanceof ExtendedSSLSession) {
          ExtendedSSLSession extendedSSLSession = (ExtendedSSLSession)sSLSession;
          String[] arrayOfString = extendedSSLSession.getLocalSupportedSignatureAlgorithms();
          sSLAlgorithmConstraints = new SSLAlgorithmConstraints(paramSSLEngine, arrayOfString, false);
        } else {
          sSLAlgorithmConstraints = new SSLAlgorithmConstraints(paramSSLEngine, false);
        } 
      } else {
        sSLAlgorithmConstraints = new SSLAlgorithmConstraints(paramSSLEngine, false);
      } 
    } 
    X509Certificate[] arrayOfX509Certificate = null;
    if (paramBoolean) {
      arrayOfX509Certificate = validate(validator, paramArrayOfX509Certificate, sSLAlgorithmConstraints, null);
    } else {
      arrayOfX509Certificate = validate(validator, paramArrayOfX509Certificate, sSLAlgorithmConstraints, paramString);
    } 
    if (debug != null && Debug.isOn("trustmanager")) {
      System.out.println("Found trusted certificate:");
      System.out.println(arrayOfX509Certificate[arrayOfX509Certificate.length - 1]);
    } 
  }
  
  private void showTrustedCerts() {
    if (debug != null && Debug.isOn("trustmanager"))
      for (X509Certificate x509Certificate : this.trustedCerts) {
        System.out.println("adding as trusted cert:");
        System.out.println("  Subject: " + x509Certificate
            .getSubjectX500Principal());
        System.out.println("  Issuer:  " + x509Certificate
            .getIssuerX500Principal());
        System.out.println("  Algorithm: " + x509Certificate
            .getPublicKey().getAlgorithm() + "; Serial number: 0x" + x509Certificate
            
            .getSerialNumber().toString(16));
        System.out.println("  Valid from " + x509Certificate
            .getNotBefore() + " until " + x509Certificate
            .getNotAfter());
        System.out.println();
      }  
  }
  
  private Validator getValidator(String paramString) {
    Validator validator;
    if (this.pkixParams == null) {
      validator = Validator.getInstance(this.validatorType, paramString, this.trustedCerts);
    } else {
      validator = Validator.getInstance(this.validatorType, paramString, this.pkixParams);
    } 
    return validator;
  }
  
  private static X509Certificate[] validate(Validator paramValidator, X509Certificate[] paramArrayOfX509Certificate, AlgorithmConstraints paramAlgorithmConstraints, String paramString) throws CertificateException {
    Object object = JsseJce.beginFipsProvider();
    try {
      return paramValidator.validate(paramArrayOfX509Certificate, null, paramAlgorithmConstraints, paramString);
    } finally {
      JsseJce.endFipsProvider(object);
    } 
  }
  
  private static String getHostNameInSNI(List<SNIServerName> paramList) {
    SNIHostName sNIHostName = null;
    Iterator<SNIServerName> iterator = paramList.iterator();
    while (true) {
      if (iterator.hasNext()) {
        SNIServerName sNIServerName = iterator.next();
        if (sNIServerName.getType() != 0)
          continue; 
        if (sNIServerName instanceof SNIHostName) {
          sNIHostName = (SNIHostName)sNIServerName;
          break;
        } 
        try {
          sNIHostName = new SNIHostName(sNIServerName.getEncoded());
          break;
        } catch (IllegalArgumentException illegalArgumentException) {
          if (debug != null && Debug.isOn("trustmanager"))
            System.out.println("Illegal server name: " + sNIServerName); 
        } 
      } else {
        break;
      } 
      if (sNIHostName != null)
        return sNIHostName.getAsciiName(); 
      return null;
    } 
    if (sNIHostName != null)
      return sNIHostName.getAsciiName(); 
    return null;
  }
  
  static List<SNIServerName> getRequestedServerNames(Socket paramSocket) {
    if (paramSocket != null && paramSocket.isConnected() && paramSocket instanceof SSLSocket) {
      SSLSocket sSLSocket = (SSLSocket)paramSocket;
      SSLSession sSLSession = sSLSocket.getHandshakeSession();
      if (sSLSession != null && sSLSession instanceof ExtendedSSLSession) {
        ExtendedSSLSession extendedSSLSession = (ExtendedSSLSession)sSLSession;
        return extendedSSLSession.getRequestedServerNames();
      } 
    } 
    return Collections.emptyList();
  }
  
  static List<SNIServerName> getRequestedServerNames(SSLEngine paramSSLEngine) {
    if (paramSSLEngine != null) {
      SSLSession sSLSession = paramSSLEngine.getHandshakeSession();
      if (sSLSession != null && sSLSession instanceof ExtendedSSLSession) {
        ExtendedSSLSession extendedSSLSession = (ExtendedSSLSession)sSLSession;
        return extendedSSLSession.getRequestedServerNames();
      } 
    } 
    return Collections.emptyList();
  }
  
  private static void checkIdentity(SSLSession paramSSLSession, X509Certificate paramX509Certificate, String paramString, boolean paramBoolean, List<SNIServerName> paramList) throws CertificateException {
    boolean bool = false;
    String str = paramSSLSession.getPeerHost();
    if (paramBoolean) {
      String str1 = getHostNameInSNI(paramList);
      if (str1 != null)
        try {
          checkIdentity(str1, paramX509Certificate, paramString);
          bool = true;
        } catch (CertificateException certificateException) {
          if (str1.equalsIgnoreCase(str))
            throw certificateException; 
        }  
    } 
    if (!bool)
      checkIdentity(str, paramX509Certificate, paramString); 
  }
  
  static void checkIdentity(String paramString1, X509Certificate paramX509Certificate, String paramString2) throws CertificateException {
    if (paramString2 != null && paramString2.length() != 0) {
      if (paramString1 != null && paramString1.startsWith("[") && paramString1
        .endsWith("]"))
        paramString1 = paramString1.substring(1, paramString1.length() - 1); 
      if (paramString2.equalsIgnoreCase("HTTPS")) {
        HostnameChecker.getInstance((byte)1).match(paramString1, paramX509Certificate);
      } else if (paramString2.equalsIgnoreCase("LDAP") || paramString2
        .equalsIgnoreCase("LDAPS")) {
        HostnameChecker.getInstance((byte)2).match(paramString1, paramX509Certificate);
      } else {
        throw new CertificateException("Unknown identification algorithm: " + paramString2);
      } 
    } 
  }
}
