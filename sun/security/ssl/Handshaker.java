package sun.security.ssl;

import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AlgorithmConstraints;
import java.security.CryptoPrimitive;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivilegedExceptionAction;
import java.security.ProviderException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLKeyException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLProtocolException;
import sun.misc.HexDumpEncoder;
import sun.security.internal.spec.TlsKeyMaterialParameterSpec;
import sun.security.internal.spec.TlsKeyMaterialSpec;
import sun.security.internal.spec.TlsMasterSecretParameterSpec;

abstract class Handshaker {
  ProtocolVersion protocolVersion;
  
  ProtocolVersion activeProtocolVersion;
  
  boolean secureRenegotiation;
  
  byte[] clientVerifyData;
  
  byte[] serverVerifyData;
  
  boolean isInitialHandshake;
  
  private ProtocolList enabledProtocols;
  
  private CipherSuiteList enabledCipherSuites;
  
  String identificationProtocol;
  
  AlgorithmConstraints algorithmConstraints = null;
  
  Collection<SignatureAndHashAlgorithm> localSupportedSignAlgs;
  
  Collection<SignatureAndHashAlgorithm> peerSupportedSignAlgs;
  
  private ProtocolList activeProtocols;
  
  private CipherSuiteList activeCipherSuites;
  
  List<SNIServerName> serverNames = Collections.emptyList();
  
  Collection<SNIMatcher> sniMatchers = Collections.emptyList();
  
  private boolean isClient;
  
  private boolean needCertVerify;
  
  SSLSocketImpl conn = null;
  
  SSLEngineImpl engine = null;
  
  HandshakeHash handshakeHash;
  
  HandshakeInStream input;
  
  HandshakeOutStream output;
  
  int state;
  
  SSLContextImpl sslContext;
  
  RandomCookie clnt_random;
  
  RandomCookie svr_random;
  
  SSLSessionImpl session;
  
  CipherSuite cipherSuite;
  
  CipherSuite.KeyExchange keyExchange;
  
  boolean resumingSession;
  
  boolean enableNewSession;
  
  private boolean sessKeysCalculated;
  
  boolean preferLocalCipherSuites = false;
  
  private SecretKey clntWriteKey;
  
  private SecretKey svrWriteKey;
  
  private IvParameterSpec clntWriteIV;
  
  private IvParameterSpec svrWriteIV;
  
  private SecretKey clntMacSecret;
  
  private SecretKey svrMacSecret;
  
  private volatile boolean taskDelegated = false;
  
  private volatile DelegatedTask<?> delegatedTask = null;
  
  private volatile Exception thrown = null;
  
  private Object thrownLock = new Object();
  
  static final Debug debug = Debug.getInstance("ssl");
  
  static final boolean allowUnsafeRenegotiation = Debug.getBooleanProperty("sun.security.ssl.allowUnsafeRenegotiation", false);
  
  static final boolean allowLegacyHelloMessages = Debug.getBooleanProperty("sun.security.ssl.allowLegacyHelloMessages", true);
  
  static final boolean rejectClientInitiatedRenego = Debug.getBooleanProperty("jdk.tls.rejectClientInitiatedRenegotiation", false);
  
  boolean invalidated;
  
  Handshaker(SSLSocketImpl paramSSLSocketImpl, SSLContextImpl paramSSLContextImpl, ProtocolList paramProtocolList, boolean paramBoolean1, boolean paramBoolean2, ProtocolVersion paramProtocolVersion, boolean paramBoolean3, boolean paramBoolean4, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.conn = paramSSLSocketImpl;
    init(paramSSLContextImpl, paramProtocolList, paramBoolean1, paramBoolean2, paramProtocolVersion, paramBoolean3, paramBoolean4, paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  Handshaker(SSLEngineImpl paramSSLEngineImpl, SSLContextImpl paramSSLContextImpl, ProtocolList paramProtocolList, boolean paramBoolean1, boolean paramBoolean2, ProtocolVersion paramProtocolVersion, boolean paramBoolean3, boolean paramBoolean4, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    this.engine = paramSSLEngineImpl;
    init(paramSSLContextImpl, paramProtocolList, paramBoolean1, paramBoolean2, paramProtocolVersion, paramBoolean3, paramBoolean4, paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  private void init(SSLContextImpl paramSSLContextImpl, ProtocolList paramProtocolList, boolean paramBoolean1, boolean paramBoolean2, ProtocolVersion paramProtocolVersion, boolean paramBoolean3, boolean paramBoolean4, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (debug != null && Debug.isOn("handshake"))
      System.out.println("Allow unsafe renegotiation: " + allowUnsafeRenegotiation + "\nAllow legacy hello messages: " + allowLegacyHelloMessages + "\nIs initial handshake: " + paramBoolean3 + "\nIs secure renegotiation: " + paramBoolean4); 
    this.sslContext = paramSSLContextImpl;
    this.isClient = paramBoolean2;
    this.needCertVerify = paramBoolean1;
    this.activeProtocolVersion = paramProtocolVersion;
    this.isInitialHandshake = paramBoolean3;
    this.secureRenegotiation = paramBoolean4;
    this.clientVerifyData = paramArrayOfbyte1;
    this.serverVerifyData = paramArrayOfbyte2;
    this.enableNewSession = true;
    this.invalidated = false;
    this.sessKeysCalculated = false;
    setCipherSuite(CipherSuite.C_NULL);
    setEnabledProtocols(paramProtocolList);
    if (this.conn != null) {
      this.algorithmConstraints = new SSLAlgorithmConstraints(this.conn, true);
    } else {
      this.algorithmConstraints = new SSLAlgorithmConstraints(this.engine, true);
    } 
    this.state = -2;
  }
  
  void fatalSE(byte paramByte, String paramString) throws IOException {
    fatalSE(paramByte, paramString, null);
  }
  
  void fatalSE(byte paramByte, Throwable paramThrowable) throws IOException {
    fatalSE(paramByte, null, paramThrowable);
  }
  
  void fatalSE(byte paramByte, String paramString, Throwable paramThrowable) throws IOException {
    if (this.conn != null) {
      this.conn.fatal(paramByte, paramString, paramThrowable);
    } else {
      this.engine.fatal(paramByte, paramString, paramThrowable);
    } 
  }
  
  void warningSE(byte paramByte) {
    if (this.conn != null) {
      this.conn.warning(paramByte);
    } else {
      this.engine.warning(paramByte);
    } 
  }
  
  String getHostSE() {
    if (this.conn != null)
      return this.conn.getHost(); 
    return this.engine.getPeerHost();
  }
  
  String getHostAddressSE() {
    if (this.conn != null)
      return this.conn.getInetAddress().getHostAddress(); 
    return this.engine.getPeerHost();
  }
  
  int getPortSE() {
    if (this.conn != null)
      return this.conn.getPort(); 
    return this.engine.getPeerPort();
  }
  
  int getLocalPortSE() {
    if (this.conn != null)
      return this.conn.getLocalPort(); 
    return -1;
  }
  
  AccessControlContext getAccSE() {
    if (this.conn != null)
      return this.conn.getAcc(); 
    return this.engine.getAcc();
  }
  
  final boolean receivedChangeCipherSpec() {
    if (this.conn != null)
      return this.conn.receivedChangeCipherSpec(); 
    return this.engine.receivedChangeCipherSpec();
  }
  
  String getEndpointIdentificationAlgorithmSE() {
    SSLParameters sSLParameters;
    if (this.conn != null) {
      sSLParameters = this.conn.getSSLParameters();
    } else {
      sSLParameters = this.engine.getSSLParameters();
    } 
    return sSLParameters.getEndpointIdentificationAlgorithm();
  }
  
  private void setVersionSE(ProtocolVersion paramProtocolVersion) {
    if (this.conn != null) {
      this.conn.setVersion(paramProtocolVersion);
    } else {
      this.engine.setVersion(paramProtocolVersion);
    } 
  }
  
  void setVersion(ProtocolVersion paramProtocolVersion) {
    this.protocolVersion = paramProtocolVersion;
    setVersionSE(paramProtocolVersion);
    this.output.r.setVersion(paramProtocolVersion);
  }
  
  void setEnabledProtocols(ProtocolList paramProtocolList) {
    this.activeCipherSuites = null;
    this.activeProtocols = null;
    this.enabledProtocols = paramProtocolList;
  }
  
  void setEnabledCipherSuites(CipherSuiteList paramCipherSuiteList) {
    this.activeCipherSuites = null;
    this.activeProtocols = null;
    this.enabledCipherSuites = paramCipherSuiteList;
  }
  
  void setAlgorithmConstraints(AlgorithmConstraints paramAlgorithmConstraints) {
    this.activeCipherSuites = null;
    this.activeProtocols = null;
    this.algorithmConstraints = new SSLAlgorithmConstraints(paramAlgorithmConstraints);
    this.localSupportedSignAlgs = null;
  }
  
  Collection<SignatureAndHashAlgorithm> getLocalSupportedSignAlgs() {
    if (this.localSupportedSignAlgs == null)
      this
        .localSupportedSignAlgs = SignatureAndHashAlgorithm.getSupportedAlgorithms(this.algorithmConstraints); 
    return this.localSupportedSignAlgs;
  }
  
  void setPeerSupportedSignAlgs(Collection<SignatureAndHashAlgorithm> paramCollection) {
    this.peerSupportedSignAlgs = new ArrayList<>(paramCollection);
  }
  
  Collection<SignatureAndHashAlgorithm> getPeerSupportedSignAlgs() {
    return this.peerSupportedSignAlgs;
  }
  
  void setIdentificationProtocol(String paramString) {
    this.identificationProtocol = paramString;
  }
  
  void setSNIServerNames(List<SNIServerName> paramList) {
    this.serverNames = paramList;
  }
  
  void setSNIMatchers(Collection<SNIMatcher> paramCollection) {
    this.sniMatchers = paramCollection;
  }
  
  void setUseCipherSuitesOrder(boolean paramBoolean) {
    this.preferLocalCipherSuites = paramBoolean;
  }
  
  void activate(ProtocolVersion paramProtocolVersion) throws IOException {
    if (this.activeProtocols == null)
      this.activeProtocols = getActiveProtocols(); 
    if (this.activeProtocols.collection().isEmpty() || this.activeProtocols.max.v == ProtocolVersion.NONE.v)
      throw new SSLHandshakeException("No appropriate protocol (protocol is disabled or cipher suites are inappropriate)"); 
    if (this.activeCipherSuites == null)
      this.activeCipherSuites = getActiveCipherSuites(); 
    if (this.activeCipherSuites.collection().isEmpty())
      throw new SSLHandshakeException("No appropriate cipher suite"); 
    if (!this.isInitialHandshake) {
      this.protocolVersion = this.activeProtocolVersion;
    } else {
      this.protocolVersion = this.activeProtocols.max;
    } 
    if (paramProtocolVersion == null || paramProtocolVersion.v == ProtocolVersion.NONE.v)
      paramProtocolVersion = this.activeProtocols.helloVersion; 
    this.handshakeHash = new HandshakeHash(this.needCertVerify);
    this.input = new HandshakeInStream(this.handshakeHash);
    if (this.conn != null) {
      this.output = new HandshakeOutStream(this.protocolVersion, paramProtocolVersion, this.handshakeHash, this.conn);
      (this.conn.getAppInputStream()).r.setHandshakeHash(this.handshakeHash);
      (this.conn.getAppInputStream()).r.setHelloVersion(paramProtocolVersion);
      (this.conn.getAppOutputStream()).r.setHelloVersion(paramProtocolVersion);
    } else {
      this.output = new HandshakeOutStream(this.protocolVersion, paramProtocolVersion, this.handshakeHash, this.engine);
      this.engine.inputRecord.setHandshakeHash(this.handshakeHash);
      this.engine.inputRecord.setHelloVersion(paramProtocolVersion);
      this.engine.outputRecord.setHelloVersion(paramProtocolVersion);
    } 
    this.state = -1;
  }
  
  void setCipherSuite(CipherSuite paramCipherSuite) {
    this.cipherSuite = paramCipherSuite;
    this.keyExchange = paramCipherSuite.keyExchange;
  }
  
  boolean isNegotiable(CipherSuite paramCipherSuite) {
    if (this.activeCipherSuites == null)
      this.activeCipherSuites = getActiveCipherSuites(); 
    return isNegotiable(this.activeCipherSuites, paramCipherSuite);
  }
  
  static final boolean isNegotiable(CipherSuiteList paramCipherSuiteList, CipherSuite paramCipherSuite) {
    return (paramCipherSuiteList.contains(paramCipherSuite) && paramCipherSuite.isNegotiable());
  }
  
  boolean isNegotiable(ProtocolVersion paramProtocolVersion) {
    if (this.activeProtocols == null)
      this.activeProtocols = getActiveProtocols(); 
    return this.activeProtocols.contains(paramProtocolVersion);
  }
  
  ProtocolVersion selectProtocolVersion(ProtocolVersion paramProtocolVersion) {
    if (this.activeProtocols == null)
      this.activeProtocols = getActiveProtocols(); 
    return this.activeProtocols.selectProtocolVersion(paramProtocolVersion);
  }
  
  CipherSuiteList getActiveCipherSuites() {
    if (this.activeCipherSuites == null) {
      if (this.activeProtocols == null)
        this.activeProtocols = getActiveProtocols(); 
      ArrayList<CipherSuite> arrayList = new ArrayList();
      if (!this.activeProtocols.collection().isEmpty() && this.activeProtocols.min.v != ProtocolVersion.NONE.v)
        for (CipherSuite cipherSuite : this.enabledCipherSuites.collection()) {
          if (cipherSuite.obsoleted > this.activeProtocols.min.v && cipherSuite.supported <= this.activeProtocols.max.v) {
            if (this.algorithmConstraints.permits(
                EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), cipherSuite.name, null))
              arrayList.add(cipherSuite); 
            continue;
          } 
          if (debug != null && Debug.isOn("verbose")) {
            if (cipherSuite.obsoleted <= this.activeProtocols.min.v) {
              System.out.println("Ignoring obsoleted cipher suite: " + cipherSuite);
              continue;
            } 
            System.out.println("Ignoring unsupported cipher suite: " + cipherSuite);
          } 
        }  
      this.activeCipherSuites = new CipherSuiteList(arrayList);
    } 
    return this.activeCipherSuites;
  }
  
  ProtocolList getActiveProtocols() {
    if (this.activeProtocols == null) {
      boolean bool = false;
      ArrayList<ProtocolVersion> arrayList = new ArrayList(4);
      for (ProtocolVersion protocolVersion : this.enabledProtocols.collection()) {
        if (!this.algorithmConstraints.permits(
            EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), protocolVersion.name, null)) {
          if (debug != null && Debug.isOn("verbose"))
            System.out.println("Ignoring disabled protocol: " + protocolVersion); 
          continue;
        } 
        if (protocolVersion.v == ProtocolVersion.SSL20Hello.v) {
          bool = true;
          continue;
        } 
        if (!this.algorithmConstraints.permits(
            EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), protocolVersion.name, null)) {
          if (debug != null && Debug.isOn("verbose"))
            System.out.println("Ignoring disabled protocol: " + protocolVersion); 
          continue;
        } 
        boolean bool1 = false;
        for (CipherSuite cipherSuite : this.enabledCipherSuites.collection()) {
          if (cipherSuite.isAvailable() && cipherSuite.obsoleted > protocolVersion.v && cipherSuite.supported <= protocolVersion.v) {
            if (this.algorithmConstraints.permits(
                EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), cipherSuite.name, null)) {
              arrayList.add(protocolVersion);
              bool1 = true;
              break;
            } 
            if (debug != null && Debug.isOn("verbose"))
              System.out.println("Ignoring disabled cipher suite: " + cipherSuite + " for " + protocolVersion); 
            continue;
          } 
          if (debug != null && Debug.isOn("verbose"))
            System.out.println("Ignoring unsupported cipher suite: " + cipherSuite + " for " + protocolVersion); 
        } 
        if (!bool1 && debug != null && Debug.isOn("handshake"))
          System.out.println("No available cipher suite for " + protocolVersion); 
      } 
      if (!arrayList.isEmpty() && bool)
        arrayList.add(ProtocolVersion.SSL20Hello); 
      this.activeProtocols = new ProtocolList(arrayList);
    } 
    return this.activeProtocols;
  }
  
  void setEnableSessionCreation(boolean paramBoolean) {
    this.enableNewSession = paramBoolean;
  }
  
  CipherBox newReadCipher() throws NoSuchAlgorithmException {
    CipherBox cipherBox;
    CipherSuite.BulkCipher bulkCipher = this.cipherSuite.cipher;
    if (this.isClient) {
      cipherBox = bulkCipher.newCipher(this.protocolVersion, this.svrWriteKey, this.svrWriteIV, this.sslContext
          .getSecureRandom(), false);
      this.svrWriteKey = null;
      this.svrWriteIV = null;
    } else {
      cipherBox = bulkCipher.newCipher(this.protocolVersion, this.clntWriteKey, this.clntWriteIV, this.sslContext
          .getSecureRandom(), false);
      this.clntWriteKey = null;
      this.clntWriteIV = null;
    } 
    return cipherBox;
  }
  
  CipherBox newWriteCipher() throws NoSuchAlgorithmException {
    CipherBox cipherBox;
    CipherSuite.BulkCipher bulkCipher = this.cipherSuite.cipher;
    if (this.isClient) {
      cipherBox = bulkCipher.newCipher(this.protocolVersion, this.clntWriteKey, this.clntWriteIV, this.sslContext
          .getSecureRandom(), true);
      this.clntWriteKey = null;
      this.clntWriteIV = null;
    } else {
      cipherBox = bulkCipher.newCipher(this.protocolVersion, this.svrWriteKey, this.svrWriteIV, this.sslContext
          .getSecureRandom(), true);
      this.svrWriteKey = null;
      this.svrWriteIV = null;
    } 
    return cipherBox;
  }
  
  Authenticator newReadAuthenticator() throws NoSuchAlgorithmException, InvalidKeyException {
    Authenticator authenticator = null;
    if (this.cipherSuite.cipher.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
      authenticator = new Authenticator(this.protocolVersion);
    } else {
      CipherSuite.MacAlg macAlg = this.cipherSuite.macAlg;
      if (this.isClient) {
        authenticator = macAlg.newMac(this.protocolVersion, this.svrMacSecret);
        this.svrMacSecret = null;
      } else {
        authenticator = macAlg.newMac(this.protocolVersion, this.clntMacSecret);
        this.clntMacSecret = null;
      } 
    } 
    return authenticator;
  }
  
  Authenticator newWriteAuthenticator() throws NoSuchAlgorithmException, InvalidKeyException {
    Authenticator authenticator = null;
    if (this.cipherSuite.cipher.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
      authenticator = new Authenticator(this.protocolVersion);
    } else {
      CipherSuite.MacAlg macAlg = this.cipherSuite.macAlg;
      if (this.isClient) {
        authenticator = macAlg.newMac(this.protocolVersion, this.clntMacSecret);
        this.clntMacSecret = null;
      } else {
        authenticator = macAlg.newMac(this.protocolVersion, this.svrMacSecret);
        this.svrMacSecret = null;
      } 
    } 
    return authenticator;
  }
  
  boolean isDone() {
    return (this.state == 20);
  }
  
  SSLSessionImpl getSession() {
    return this.session;
  }
  
  void setHandshakeSessionSE(SSLSessionImpl paramSSLSessionImpl) {
    if (this.conn != null) {
      this.conn.setHandshakeSession(paramSSLSessionImpl);
    } else {
      this.engine.setHandshakeSession(paramSSLSessionImpl);
    } 
  }
  
  boolean isSecureRenegotiation() {
    return this.secureRenegotiation;
  }
  
  byte[] getClientVerifyData() {
    return this.clientVerifyData;
  }
  
  byte[] getServerVerifyData() {
    return this.serverVerifyData;
  }
  
  void process_record(InputRecord paramInputRecord, boolean paramBoolean) throws IOException {
    checkThrown();
    this.input.incomingRecord(paramInputRecord);
    if (this.conn != null || paramBoolean) {
      processLoop();
    } else {
      delegateTask((PrivilegedExceptionAction<?>)new Object(this));
    } 
  }
  
  void processLoop() throws IOException {
    while (this.input.available() >= 4) {
      this.input.mark(4);
      byte b = (byte)this.input.getInt8();
      int i = this.input.getInt24();
      if (this.input.available() < i) {
        this.input.reset();
        return;
      } 
      if (b == 0) {
        this.input.reset();
        processMessage(b, i);
        this.input.ignore(4 + i);
        continue;
      } 
      this.input.mark(i);
      processMessage(b, i);
      this.input.digestNow();
    } 
  }
  
  boolean activated() {
    return (this.state >= -1);
  }
  
  boolean started() {
    return (this.state >= 0);
  }
  
  void kickstart() throws IOException {
    if (this.state >= 0)
      return; 
    HandshakeMessage handshakeMessage = getKickstartMessage();
    if (debug != null && Debug.isOn("handshake"))
      handshakeMessage.print(System.out); 
    handshakeMessage.write(this.output);
    this.output.flush();
    this.state = handshakeMessage.messageType();
  }
  
  void sendChangeCipherSpec(HandshakeMessage.Finished paramFinished, boolean paramBoolean) throws IOException {
    EngineOutputRecord engineOutputRecord;
    this.output.flush();
    if (this.conn != null) {
      OutputRecord outputRecord = new OutputRecord((byte)20);
    } else {
      engineOutputRecord = new EngineOutputRecord((byte)20, this.engine);
    } 
    engineOutputRecord.setVersion(this.protocolVersion);
    engineOutputRecord.write(1);
    if (this.conn != null) {
      this.conn.writeLock.lock();
      try {
        this.conn.writeRecord((OutputRecord)engineOutputRecord);
        this.conn.changeWriteCiphers();
        if (debug != null && Debug.isOn("handshake"))
          paramFinished.print(System.out); 
        paramFinished.write(this.output);
        this.output.flush();
      } finally {
        this.conn.writeLock.unlock();
      } 
    } else {
      synchronized (this.engine.writeLock) {
        this.engine.writeRecord(engineOutputRecord);
        this.engine.changeWriteCiphers();
        if (debug != null && Debug.isOn("handshake"))
          paramFinished.print(System.out); 
        paramFinished.write(this.output);
        if (paramBoolean)
          this.output.setFinishedMsg(); 
        this.output.flush();
      } 
    } 
  }
  
  void calculateKeys(SecretKey paramSecretKey, ProtocolVersion paramProtocolVersion) {
    SecretKey secretKey = calculateMasterSecret(paramSecretKey, paramProtocolVersion);
    this.session.setMasterSecret(secretKey);
    calculateConnectionKeys(secretKey);
  }
  
  private SecretKey calculateMasterSecret(SecretKey paramSecretKey, ProtocolVersion paramProtocolVersion) {
    String str1;
    CipherSuite.PRF pRF;
    if (debug != null && Debug.isOn("keygen")) {
      HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
      System.out.println("SESSION KEYGEN:");
      System.out.println("PreMaster Secret:");
      printHex(hexDumpEncoder, paramSecretKey.getEncoded());
    } 
    if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
      str1 = "SunTls12MasterSecret";
      pRF = this.cipherSuite.prfAlg;
    } else {
      str1 = "SunTlsMasterSecret";
      pRF = CipherSuite.PRF.P_NONE;
    } 
    String str2 = pRF.getPRFHashAlg();
    int i = pRF.getPRFHashLength();
    int j = pRF.getPRFBlockSize();
    TlsMasterSecretParameterSpec tlsMasterSecretParameterSpec = new TlsMasterSecretParameterSpec(paramSecretKey, this.protocolVersion.major, this.protocolVersion.minor, this.clnt_random.random_bytes, this.svr_random.random_bytes, str2, i, j);
    try {
      KeyGenerator keyGenerator = JsseJce.getKeyGenerator(str1);
      keyGenerator.init(tlsMasterSecretParameterSpec);
      return keyGenerator.generateKey();
    } catch (InvalidAlgorithmParameterException|NoSuchAlgorithmException invalidAlgorithmParameterException) {
      if (debug != null && Debug.isOn("handshake")) {
        System.out.println("RSA master secret generation error:");
        invalidAlgorithmParameterException.printStackTrace(System.out);
      } 
      throw new ProviderException(invalidAlgorithmParameterException);
    } 
  }
  
  void calculateConnectionKeys(SecretKey paramSecretKey) {
    String str1;
    CipherSuite.PRF pRF;
    int i = this.cipherSuite.macAlg.size;
    boolean bool = this.cipherSuite.exportable;
    CipherSuite.BulkCipher bulkCipher = this.cipherSuite.cipher;
    boolean bool1 = bool ? bulkCipher.expandedKeySize : false;
    if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
      str1 = "SunTls12KeyMaterial";
      pRF = this.cipherSuite.prfAlg;
    } else {
      str1 = "SunTlsKeyMaterial";
      pRF = CipherSuite.PRF.P_NONE;
    } 
    String str2 = pRF.getPRFHashAlg();
    int j = pRF.getPRFHashLength();
    int k = pRF.getPRFBlockSize();
    int m = bulkCipher.ivSize;
    if (bulkCipher.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
      m = bulkCipher.fixedIvSize;
    } else if (this.protocolVersion.v >= ProtocolVersion.TLS11.v && bulkCipher.cipherType == CipherSuite.CipherType.BLOCK_CIPHER) {
      m = 0;
    } 
    TlsKeyMaterialParameterSpec tlsKeyMaterialParameterSpec = new TlsKeyMaterialParameterSpec(paramSecretKey, this.protocolVersion.major, this.protocolVersion.minor, this.clnt_random.random_bytes, this.svr_random.random_bytes, bulkCipher.algorithm, bulkCipher.keySize, bool1, m, i, str2, j, k);
    try {
      KeyGenerator keyGenerator = JsseJce.getKeyGenerator(str1);
      keyGenerator.init(tlsKeyMaterialParameterSpec);
      TlsKeyMaterialSpec tlsKeyMaterialSpec = (TlsKeyMaterialSpec)keyGenerator.generateKey();
      this.clntWriteKey = tlsKeyMaterialSpec.getClientCipherKey();
      this.svrWriteKey = tlsKeyMaterialSpec.getServerCipherKey();
      this.clntWriteIV = tlsKeyMaterialSpec.getClientIv();
      this.svrWriteIV = tlsKeyMaterialSpec.getServerIv();
      this.clntMacSecret = tlsKeyMaterialSpec.getClientMacKey();
      this.svrMacSecret = tlsKeyMaterialSpec.getServerMacKey();
    } catch (GeneralSecurityException generalSecurityException) {
      throw new ProviderException(generalSecurityException);
    } 
    this.sessKeysCalculated = true;
    if (debug != null && Debug.isOn("keygen"))
      synchronized (System.out) {
        HexDumpEncoder hexDumpEncoder = new HexDumpEncoder();
        System.out.println("CONNECTION KEYGEN:");
        System.out.println("Client Nonce:");
        printHex(hexDumpEncoder, this.clnt_random.random_bytes);
        System.out.println("Server Nonce:");
        printHex(hexDumpEncoder, this.svr_random.random_bytes);
        System.out.println("Master Secret:");
        printHex(hexDumpEncoder, paramSecretKey.getEncoded());
        if (this.clntMacSecret != null) {
          System.out.println("Client MAC write Secret:");
          printHex(hexDumpEncoder, this.clntMacSecret.getEncoded());
          System.out.println("Server MAC write Secret:");
          printHex(hexDumpEncoder, this.svrMacSecret.getEncoded());
        } else {
          System.out.println("... no MAC keys used for this cipher");
        } 
        if (this.clntWriteKey != null) {
          System.out.println("Client write key:");
          printHex(hexDumpEncoder, this.clntWriteKey.getEncoded());
          System.out.println("Server write key:");
          printHex(hexDumpEncoder, this.svrWriteKey.getEncoded());
        } else {
          System.out.println("... no encryption keys used");
        } 
        if (this.clntWriteIV != null) {
          System.out.println("Client write IV:");
          printHex(hexDumpEncoder, this.clntWriteIV.getIV());
          System.out.println("Server write IV:");
          printHex(hexDumpEncoder, this.svrWriteIV.getIV());
        } else if (this.protocolVersion.v >= ProtocolVersion.TLS11.v) {
          System.out.println("... no IV derived for this protocol");
        } else {
          System.out.println("... no IV used for this cipher");
        } 
        System.out.flush();
      }  
  }
  
  boolean sessionKeysCalculated() {
    return this.sessKeysCalculated;
  }
  
  private static void printHex(HexDumpEncoder paramHexDumpEncoder, byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null) {
      System.out.println("(key bytes not available)");
    } else {
      try {
        paramHexDumpEncoder.encodeBuffer(paramArrayOfbyte, System.out);
      } catch (IOException iOException) {}
    } 
  }
  
  static void throwSSLException(String paramString, Throwable paramThrowable) throws SSLException {
    SSLException sSLException = new SSLException(paramString);
    sSLException.initCause(paramThrowable);
    throw sSLException;
  }
  
  private <T> void delegateTask(PrivilegedExceptionAction<T> paramPrivilegedExceptionAction) {
    this.delegatedTask = new DelegatedTask(this, paramPrivilegedExceptionAction);
    this.taskDelegated = false;
    this.thrown = null;
  }
  
  DelegatedTask<?> getTask() {
    if (!this.taskDelegated) {
      this.taskDelegated = true;
      return this.delegatedTask;
    } 
    return null;
  }
  
  boolean taskOutstanding() {
    return (this.delegatedTask != null);
  }
  
  void checkThrown() throws SSLException {
    synchronized (this.thrownLock) {
      if (this.thrown != null) {
        String str = this.thrown.getMessage();
        if (str == null)
          str = "Delegated task threw Exception/Error"; 
        Exception exception = this.thrown;
        this.thrown = null;
        if (exception instanceof RuntimeException)
          throw new RuntimeException(str, exception); 
        if (exception instanceof SSLHandshakeException)
          throw (SSLHandshakeException)(new SSLHandshakeException(str))
            .initCause(exception); 
        if (exception instanceof SSLKeyException)
          throw (SSLKeyException)(new SSLKeyException(str))
            .initCause(exception); 
        if (exception instanceof SSLPeerUnverifiedException)
          throw (SSLPeerUnverifiedException)(new SSLPeerUnverifiedException(str))
            .initCause(exception); 
        if (exception instanceof SSLProtocolException)
          throw (SSLProtocolException)(new SSLProtocolException(str))
            .initCause(exception); 
        throw new SSLException(str, exception);
      } 
    } 
  }
  
  abstract HandshakeMessage getKickstartMessage() throws SSLException;
  
  abstract void processMessage(byte paramByte, int paramInt) throws IOException;
  
  abstract void handshakeAlert(byte paramByte) throws SSLProtocolException;
  
  class Handshaker {}
}
