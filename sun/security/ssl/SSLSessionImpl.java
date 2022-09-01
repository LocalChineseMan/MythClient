package sun.security.ssl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import javax.crypto.SecretKey;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLPermission;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import javax.net.ssl.SSLSessionContext;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

final class SSLSessionImpl extends ExtendedSSLSession {
  static final SSLSessionImpl nullSession = new SSLSessionImpl();
  
  private static final byte compression_null = 0;
  
  private final ProtocolVersion protocolVersion;
  
  private final SessionId sessionId;
  
  private X509Certificate[] peerCerts;
  
  private byte compressionMethod;
  
  private CipherSuite cipherSuite;
  
  private SecretKey masterSecret;
  
  private final long creationTime = System.currentTimeMillis();
  
  private long lastUsedTime = 0L;
  
  private final String host;
  
  private final int port;
  
  private SSLSessionContextImpl context;
  
  private int sessionCount;
  
  private boolean invalidated;
  
  private X509Certificate[] localCerts;
  
  private PrivateKey localPrivateKey;
  
  private String[] localSupportedSignAlgs;
  
  private String[] peerSupportedSignAlgs;
  
  private List<SNIServerName> requestedServerNames;
  
  private Principal peerPrincipal;
  
  private Principal localPrincipal;
  
  private boolean isSessionResumption = false;
  
  private static volatile int counter = 0;
  
  private static boolean defaultRejoinable = true;
  
  private static final Debug debug = Debug.getInstance("ssl");
  
  private Hashtable<SecureKey, Object> table;
  
  private boolean acceptLargeFragments;
  
  private SSLSessionImpl() {
    this(ProtocolVersion.NONE, CipherSuite.C_NULL, (Collection<SignatureAndHashAlgorithm>)null, new SessionId(false, null), (String)null, -1);
  }
  
  SSLSessionImpl(ProtocolVersion paramProtocolVersion, CipherSuite paramCipherSuite, Collection<SignatureAndHashAlgorithm> paramCollection, SecureRandom paramSecureRandom, String paramString, int paramInt) {
    this(paramProtocolVersion, paramCipherSuite, paramCollection, new SessionId(defaultRejoinable, paramSecureRandom), paramString, paramInt);
  }
  
  void setMasterSecret(SecretKey paramSecretKey) {
    if (this.masterSecret == null) {
      this.masterSecret = paramSecretKey;
    } else {
      throw new RuntimeException("setMasterSecret() error");
    } 
  }
  
  SecretKey getMasterSecret() {
    return this.masterSecret;
  }
  
  void setPeerCertificates(X509Certificate[] paramArrayOfX509Certificate) {
    if (this.peerCerts == null)
      this.peerCerts = paramArrayOfX509Certificate; 
  }
  
  void setLocalCertificates(X509Certificate[] paramArrayOfX509Certificate) {
    this.localCerts = paramArrayOfX509Certificate;
  }
  
  void setLocalPrivateKey(PrivateKey paramPrivateKey) {
    this.localPrivateKey = paramPrivateKey;
  }
  
  void setPeerSupportedSignatureAlgorithms(Collection<SignatureAndHashAlgorithm> paramCollection) {
    this
      .peerSupportedSignAlgs = SignatureAndHashAlgorithm.getAlgorithmNames(paramCollection);
  }
  
  void setRequestedServerNames(List<SNIServerName> paramList) {
    this.requestedServerNames = new ArrayList<>(paramList);
  }
  
  void setPeerPrincipal(Principal paramPrincipal) {
    if (this.peerPrincipal == null)
      this.peerPrincipal = paramPrincipal; 
  }
  
  void setLocalPrincipal(Principal paramPrincipal) {
    this.localPrincipal = paramPrincipal;
  }
  
  boolean isRejoinable() {
    return (this.sessionId != null && this.sessionId.length() != 0 && !this.invalidated && 
      isLocalAuthenticationValid());
  }
  
  public synchronized boolean isValid() {
    return isRejoinable();
  }
  
  boolean isLocalAuthenticationValid() {
    if (this.localPrivateKey != null)
      try {
        this.localPrivateKey.getAlgorithm();
      } catch (Exception exception) {
        invalidate();
        return false;
      }  
    return true;
  }
  
  public byte[] getId() {
    return this.sessionId.getId();
  }
  
  public SSLSessionContext getSessionContext() {
    SecurityManager securityManager;
    if ((securityManager = System.getSecurityManager()) != null)
      securityManager.checkPermission(new SSLPermission("getSSLSessionContext")); 
    return this.context;
  }
  
  SessionId getSessionId() {
    return this.sessionId;
  }
  
  CipherSuite getSuite() {
    return this.cipherSuite;
  }
  
  void setSuite(CipherSuite paramCipherSuite) {
    this.cipherSuite = paramCipherSuite;
    if (debug != null && Debug.isOn("session"))
      System.out.println("%% Negotiating:  " + this); 
  }
  
  boolean isSessionResumption() {
    return this.isSessionResumption;
  }
  
  void setAsSessionResumption(boolean paramBoolean) {
    this.isSessionResumption = paramBoolean;
  }
  
  public String getCipherSuite() {
    return (getSuite()).name;
  }
  
  ProtocolVersion getProtocolVersion() {
    return this.protocolVersion;
  }
  
  public String getProtocol() {
    return (getProtocolVersion()).name;
  }
  
  byte getCompression() {
    return this.compressionMethod;
  }
  
  public int hashCode() {
    return this.sessionId.hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (paramObject instanceof SSLSessionImpl) {
      SSLSessionImpl sSLSessionImpl = (SSLSessionImpl)paramObject;
      return (this.sessionId != null && this.sessionId.equals(sSLSessionImpl
          .getSessionId()));
    } 
    return false;
  }
  
  public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
    if (this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT)
      throw new SSLPeerUnverifiedException("no certificates expected for Kerberos cipher suites"); 
    if (this.peerCerts == null)
      throw new SSLPeerUnverifiedException("peer not authenticated"); 
    return (Certificate[])this.peerCerts.clone();
  }
  
  public Certificate[] getLocalCertificates() {
    return (this.localCerts == null) ? null : (Certificate[])this.localCerts
      .clone();
  }
  
  public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
    if (this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT)
      throw new SSLPeerUnverifiedException("no certificates expected for Kerberos cipher suites"); 
    if (this.peerCerts == null)
      throw new SSLPeerUnverifiedException("peer not authenticated"); 
    X509Certificate[] arrayOfX509Certificate = new X509Certificate[this.peerCerts.length];
    for (byte b = 0; b < this.peerCerts.length; b++) {
      byte[] arrayOfByte = null;
      try {
        arrayOfByte = this.peerCerts[b].getEncoded();
        arrayOfX509Certificate[b] = X509Certificate.getInstance(arrayOfByte);
      } catch (CertificateEncodingException certificateEncodingException) {
        throw new SSLPeerUnverifiedException(certificateEncodingException.getMessage());
      } catch (CertificateException certificateException) {
        throw new SSLPeerUnverifiedException(certificateException.getMessage());
      } 
    } 
    return arrayOfX509Certificate;
  }
  
  public X509Certificate[] getCertificateChain() throws SSLPeerUnverifiedException {
    if (this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT)
      throw new SSLPeerUnverifiedException("no certificates expected for Kerberos cipher suites"); 
    if (this.peerCerts != null)
      return (X509Certificate[])this.peerCerts.clone(); 
    throw new SSLPeerUnverifiedException("peer not authenticated");
  }
  
  public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
    if (this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT) {
      if (this.peerPrincipal == null)
        throw new SSLPeerUnverifiedException("peer not authenticated"); 
      return this.peerPrincipal;
    } 
    if (this.peerCerts == null)
      throw new SSLPeerUnverifiedException("peer not authenticated"); 
    return this.peerCerts[0].getSubjectX500Principal();
  }
  
  public Principal getLocalPrincipal() {
    if (this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || this.cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT)
      return (this.localPrincipal == null) ? null : this.localPrincipal; 
    return (this.localCerts == null) ? null : this.localCerts[0]
      .getSubjectX500Principal();
  }
  
  public long getCreationTime() {
    return this.creationTime;
  }
  
  public long getLastAccessedTime() {
    return (this.lastUsedTime != 0L) ? this.lastUsedTime : this.creationTime;
  }
  
  void setLastAccessedTime(long paramLong) {
    this.lastUsedTime = paramLong;
  }
  
  public InetAddress getPeerAddress() {
    try {
      return InetAddress.getByName(this.host);
    } catch (UnknownHostException unknownHostException) {
      return null;
    } 
  }
  
  public String getPeerHost() {
    return this.host;
  }
  
  public int getPeerPort() {
    return this.port;
  }
  
  void setContext(SSLSessionContextImpl paramSSLSessionContextImpl) {
    if (this.context == null)
      this.context = paramSSLSessionContextImpl; 
  }
  
  public synchronized void invalidate() {
    if (this == nullSession)
      return; 
    this.invalidated = true;
    if (debug != null && Debug.isOn("session"))
      System.out.println("%% Invalidated:  " + this); 
    if (this.context != null) {
      this.context.remove(this.sessionId);
      this.context = null;
    } 
  }
  
  SSLSessionImpl(ProtocolVersion paramProtocolVersion, CipherSuite paramCipherSuite, Collection<SignatureAndHashAlgorithm> paramCollection, SessionId paramSessionId, String paramString, int paramInt) {
    this.table = new Hashtable<>();
    this
      .acceptLargeFragments = Debug.getBooleanProperty("jsse.SSLEngine.acceptLargeFragments", false);
    this.protocolVersion = paramProtocolVersion;
    this.sessionId = paramSessionId;
    this.peerCerts = null;
    this.compressionMethod = 0;
    this.cipherSuite = paramCipherSuite;
    this.masterSecret = null;
    this.host = paramString;
    this.port = paramInt;
    this.sessionCount = ++counter;
    this.localSupportedSignAlgs = SignatureAndHashAlgorithm.getAlgorithmNames(paramCollection);
    if (debug != null && Debug.isOn("session"))
      System.out.println("%% Initialized:  " + this); 
  }
  
  public void putValue(String paramString, Object paramObject) {
    if (paramString == null || paramObject == null)
      throw new IllegalArgumentException("arguments can not be null"); 
    SecureKey secureKey = new SecureKey(paramString);
    Object object = this.table.put(secureKey, paramObject);
    if (object instanceof SSLSessionBindingListener) {
      SSLSessionBindingEvent sSLSessionBindingEvent = new SSLSessionBindingEvent(this, paramString);
      ((SSLSessionBindingListener)object).valueUnbound(sSLSessionBindingEvent);
    } 
    if (paramObject instanceof SSLSessionBindingListener) {
      SSLSessionBindingEvent sSLSessionBindingEvent = new SSLSessionBindingEvent(this, paramString);
      ((SSLSessionBindingListener)paramObject).valueBound(sSLSessionBindingEvent);
    } 
  }
  
  public Object getValue(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("argument can not be null"); 
    SecureKey secureKey = new SecureKey(paramString);
    return this.table.get(secureKey);
  }
  
  public void removeValue(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("argument can not be null"); 
    SecureKey secureKey = new SecureKey(paramString);
    Object object = this.table.remove(secureKey);
    if (object instanceof SSLSessionBindingListener) {
      SSLSessionBindingEvent sSLSessionBindingEvent = new SSLSessionBindingEvent(this, paramString);
      ((SSLSessionBindingListener)object).valueUnbound(sSLSessionBindingEvent);
    } 
  }
  
  public String[] getValueNames() {
    Vector<Object> vector = new Vector();
    Object object = SecureKey.getCurrentSecurityContext();
    for (Enumeration<SecureKey> enumeration = this.table.keys(); enumeration.hasMoreElements(); ) {
      SecureKey secureKey = enumeration.nextElement();
      if (object.equals(secureKey.getSecurityContext()))
        vector.addElement(secureKey.getAppKey()); 
    } 
    String[] arrayOfString = new String[vector.size()];
    vector.copyInto((Object[])arrayOfString);
    return arrayOfString;
  }
  
  protected synchronized void expandBufferSizes() {
    this.acceptLargeFragments = true;
  }
  
  public synchronized int getPacketBufferSize() {
    return this.acceptLargeFragments ? 33305 : 16921;
  }
  
  public synchronized int getApplicationBufferSize() {
    return getPacketBufferSize() - 5;
  }
  
  public String[] getLocalSupportedSignatureAlgorithms() {
    if (this.localSupportedSignAlgs != null)
      return (String[])this.localSupportedSignAlgs.clone(); 
    return new String[0];
  }
  
  public String[] getPeerSupportedSignatureAlgorithms() {
    if (this.peerSupportedSignAlgs != null)
      return (String[])this.peerSupportedSignAlgs.clone(); 
    return new String[0];
  }
  
  public List<SNIServerName> getRequestedServerNames() {
    if (this.requestedServerNames != null && !this.requestedServerNames.isEmpty())
      return Collections.unmodifiableList(this.requestedServerNames); 
    return Collections.emptyList();
  }
  
  public String toString() {
    return "[Session-" + this.sessionCount + ", " + 
      getCipherSuite() + "]";
  }
  
  protected void finalize() throws Throwable {
    String[] arrayOfString = getValueNames();
    for (byte b = 0; b < arrayOfString.length; b++)
      removeValue(arrayOfString[b]); 
  }
}
