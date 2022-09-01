package sun.security.ssl;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AccessController;
import java.security.CryptoPrimitive;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;

final class ClientHandshaker extends Handshaker {
  private PublicKey serverKey;
  
  private PublicKey ephemeralServerKey;
  
  private BigInteger serverDH;
  
  private DHCrypt dh;
  
  private ECDHCrypt ecdh;
  
  private HandshakeMessage.CertificateRequest certRequest;
  
  private boolean serverKeyExchangeReceived;
  
  private ProtocolVersion maxProtocolVersion;
  
  private static final boolean enableSNIExtension = Debug.getBooleanProperty("jsse.enableSNIExtension", true);
  
  private static final boolean allowUnsafeServerCertChange = Debug.getBooleanProperty("jdk.tls.allowUnsafeServerCertChange", false);
  
  private List<SNIServerName> requestedServerNames = Collections.emptyList();
  
  private boolean serverNamesAccepted = false;
  
  private X509Certificate[] reservedServerCerts = null;
  
  ClientHandshaker(SSLSocketImpl paramSSLSocketImpl, SSLContextImpl paramSSLContextImpl, ProtocolList paramProtocolList, ProtocolVersion paramProtocolVersion, boolean paramBoolean1, boolean paramBoolean2, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    super(paramSSLSocketImpl, paramSSLContextImpl, paramProtocolList, true, true, paramProtocolVersion, paramBoolean1, paramBoolean2, paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  ClientHandshaker(SSLEngineImpl paramSSLEngineImpl, SSLContextImpl paramSSLContextImpl, ProtocolList paramProtocolList, ProtocolVersion paramProtocolVersion, boolean paramBoolean1, boolean paramBoolean2, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    super(paramSSLEngineImpl, paramSSLContextImpl, paramProtocolList, true, true, paramProtocolVersion, paramBoolean1, paramBoolean2, paramArrayOfbyte1, paramArrayOfbyte2);
  }
  
  void processMessage(byte paramByte, int paramInt) throws IOException {
    if (this.state >= paramByte && paramByte != 0)
      throw new SSLProtocolException("Handshake message sequence violation, " + paramByte); 
    switch (paramByte) {
      case 0:
        serverHelloRequest(new HandshakeMessage.HelloRequest(this.input));
        break;
      case 2:
        serverHello(new HandshakeMessage.ServerHello(this.input, paramInt));
        break;
      case 11:
        if (this.keyExchange == CipherSuite.KeyExchange.K_DH_ANON || this.keyExchange == CipherSuite.KeyExchange.K_ECDH_ANON || this.keyExchange == CipherSuite.KeyExchange.K_KRB5 || this.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT)
          fatalSE((byte)10, "unexpected server cert chain"); 
        serverCertificate(new HandshakeMessage.CertificateMsg(this.input));
        this
          .serverKey = this.session.getPeerCertificates()[0].getPublicKey();
        break;
      case 12:
        this.serverKeyExchangeReceived = true;
        switch (this.keyExchange) {
          case K_RSA_EXPORT:
            if (this.serverKey == null)
              throw new SSLProtocolException("Server did not send certificate message"); 
            if (!(this.serverKey instanceof java.security.interfaces.RSAPublicKey))
              throw new SSLProtocolException("Protocol violation: the certificate type must be appropriate for the selected cipher suite's key exchange algorithm"); 
            if (JsseJce.getRSAKeyLength(this.serverKey) <= 512)
              throw new SSLProtocolException("Protocol violation: server sent a server key exchange message for key exchange " + this.keyExchange + " when the public key in the server certificate" + " is less than or equal to 512 bits in length"); 
            try {
              serverKeyExchange(new HandshakeMessage.RSA_ServerKeyExchange(this.input));
            } catch (GeneralSecurityException generalSecurityException) {
              throwSSLException("Server key", generalSecurityException);
            } 
            break;
          case K_DH_ANON:
            try {
              serverKeyExchange(new HandshakeMessage.DH_ServerKeyExchange(this.input, this.protocolVersion));
            } catch (GeneralSecurityException generalSecurityException) {
              throwSSLException("Server key", generalSecurityException);
            } 
            break;
          case K_DHE_DSS:
          case K_DHE_RSA:
            try {
              serverKeyExchange(new HandshakeMessage.DH_ServerKeyExchange(this.input, this.serverKey, this.clnt_random.random_bytes, this.svr_random.random_bytes, paramInt, this.localSupportedSignAlgs, this.protocolVersion));
            } catch (GeneralSecurityException generalSecurityException) {
              throwSSLException("Server key", generalSecurityException);
            } 
            break;
          case K_ECDHE_ECDSA:
          case K_ECDHE_RSA:
          case K_ECDH_ANON:
            try {
              serverKeyExchange(new HandshakeMessage.ECDH_ServerKeyExchange(this.input, this.serverKey, this.clnt_random.random_bytes, this.svr_random.random_bytes, this.localSupportedSignAlgs, this.protocolVersion));
            } catch (GeneralSecurityException generalSecurityException) {
              throwSSLException("Server key", generalSecurityException);
            } 
            break;
          case K_RSA:
          case K_DH_RSA:
          case K_DH_DSS:
          case K_ECDH_ECDSA:
          case K_ECDH_RSA:
            throw new SSLProtocolException("Protocol violation: server sent a server key exchange message for key exchange " + this.keyExchange);
          case K_KRB5:
          case K_KRB5_EXPORT:
            throw new SSLProtocolException("unexpected receipt of server key exchange algorithm");
        } 
        throw new SSLProtocolException("unsupported key exchange algorithm = " + this.keyExchange);
      case 13:
        if (this.keyExchange == CipherSuite.KeyExchange.K_DH_ANON || this.keyExchange == CipherSuite.KeyExchange.K_ECDH_ANON)
          throw new SSLHandshakeException("Client authentication requested for anonymous cipher suite."); 
        if (this.keyExchange == CipherSuite.KeyExchange.K_KRB5 || this.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT)
          throw new SSLHandshakeException("Client certificate requested for kerberos cipher suite."); 
        this.certRequest = new HandshakeMessage.CertificateRequest(this.input, this.protocolVersion);
        if (debug != null && Debug.isOn("handshake"))
          this.certRequest.print(System.out); 
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
          Collection<SignatureAndHashAlgorithm> collection1 = this.certRequest.getSignAlgorithms();
          if (collection1 == null || collection1.isEmpty())
            throw new SSLHandshakeException("No peer supported signature algorithms"); 
          Collection<SignatureAndHashAlgorithm> collection2 = SignatureAndHashAlgorithm.getSupportedAlgorithms(collection1);
          if (collection2.isEmpty())
            throw new SSLHandshakeException("No supported signature and hash algorithm in common"); 
          setPeerSupportedSignAlgs(collection2);
          this.session.setPeerSupportedSignatureAlgorithms(collection2);
        } 
        break;
      case 14:
        serverHelloDone(new HandshakeMessage.ServerHelloDone(this.input));
        break;
      case 20:
        if (!receivedChangeCipherSpec())
          fatalSE((byte)40, "Received Finished message before ChangeCipherSpec"); 
        serverFinished(new HandshakeMessage.Finished(this.protocolVersion, this.input, this.cipherSuite));
        break;
      default:
        throw new SSLProtocolException("Illegal client handshake msg, " + paramByte);
    } 
    if (this.state < paramByte)
      this.state = paramByte; 
  }
  
  private void serverHelloRequest(HandshakeMessage.HelloRequest paramHelloRequest) throws IOException {
    if (debug != null && Debug.isOn("handshake"))
      paramHelloRequest.print(System.out); 
    if (this.state < 1)
      if (!this.secureRenegotiation && !allowUnsafeRenegotiation) {
        if (this.activeProtocolVersion.v >= ProtocolVersion.TLS10.v) {
          warningSE((byte)100);
          this.invalidated = true;
        } else {
          fatalSE((byte)40, "Renegotiation is not allowed");
        } 
      } else {
        if (!this.secureRenegotiation && 
          debug != null && Debug.isOn("handshake"))
          System.out.println("Warning: continue with insecure renegotiation"); 
        kickstart();
      }  
  }
  
  private void serverHello(HandshakeMessage.ServerHello paramServerHello) throws IOException {
    this.serverKeyExchangeReceived = false;
    if (debug != null && Debug.isOn("handshake"))
      paramServerHello.print(System.out); 
    ProtocolVersion protocolVersion = paramServerHello.protocolVersion;
    if (!isNegotiable(protocolVersion))
      throw new SSLHandshakeException("Server chose " + protocolVersion + ", but that protocol version is not enabled or not supported " + "by the client."); 
    this.handshakeHash.protocolDetermined(protocolVersion);
    setVersion(protocolVersion);
    RenegotiationInfoExtension renegotiationInfoExtension = (RenegotiationInfoExtension)paramServerHello.extensions.get(ExtensionType.EXT_RENEGOTIATION_INFO);
    if (renegotiationInfoExtension != null) {
      if (this.isInitialHandshake) {
        if (!renegotiationInfoExtension.isEmpty())
          fatalSE((byte)40, "The renegotiation_info field is not empty"); 
        this.secureRenegotiation = true;
      } else {
        if (!this.secureRenegotiation)
          fatalSE((byte)40, "Unexpected renegotiation indication extension"); 
        byte[] arrayOfByte = new byte[this.clientVerifyData.length + this.serverVerifyData.length];
        System.arraycopy(this.clientVerifyData, 0, arrayOfByte, 0, this.clientVerifyData.length);
        System.arraycopy(this.serverVerifyData, 0, arrayOfByte, this.clientVerifyData.length, this.serverVerifyData.length);
        if (!MessageDigest.isEqual(arrayOfByte, renegotiationInfoExtension
            .getRenegotiatedConnection()))
          fatalSE((byte)40, "Incorrect verify data in ServerHello renegotiation_info message"); 
      } 
    } else if (this.isInitialHandshake) {
      if (!allowLegacyHelloMessages)
        fatalSE((byte)40, "Failed to negotiate the use of secure renegotiation"); 
      this.secureRenegotiation = false;
      if (debug != null && Debug.isOn("handshake"))
        System.out.println("Warning: No renegotiation indication extension in ServerHello"); 
    } else if (this.secureRenegotiation) {
      fatalSE((byte)40, "No renegotiation indication extension");
    } 
    this.svr_random = paramServerHello.svr_random;
    if (!isNegotiable(paramServerHello.cipherSuite))
      fatalSE((byte)47, "Server selected improper ciphersuite " + paramServerHello.cipherSuite); 
    setCipherSuite(paramServerHello.cipherSuite);
    if (this.protocolVersion.v >= ProtocolVersion.TLS12.v)
      this.handshakeHash.setFinishedAlg(this.cipherSuite.prfAlg.getPRFHashAlg()); 
    if (paramServerHello.compression_method != 0)
      fatalSE((byte)47, "compression type not supported, " + paramServerHello.compression_method); 
    if (this.session != null)
      if (this.session.getSessionId().equals(paramServerHello.sessionId)) {
        CipherSuite cipherSuite = this.session.getSuite();
        if (this.cipherSuite != cipherSuite)
          throw new SSLProtocolException("Server returned wrong cipher suite for session"); 
        ProtocolVersion protocolVersion1 = this.session.getProtocolVersion();
        if (this.protocolVersion != protocolVersion1)
          throw new SSLProtocolException("Server resumed session with wrong protocol version"); 
        if (cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT) {
          Principal principal = this.session.getLocalPrincipal();
          Subject subject = null;
          try {
            subject = AccessController.<Subject>doPrivileged((PrivilegedExceptionAction<Subject>)new Object(this));
          } catch (PrivilegedActionException privilegedActionException) {
            subject = null;
            if (debug != null && Debug.isOn("session"))
              System.out.println("Attempt to obtain subject failed!"); 
          } 
          if (subject != null) {
            Set<Principal> set = subject.getPrincipals(Principal.class);
            if (!set.contains(principal))
              throw new SSLProtocolException("Server resumed session with wrong subject identity"); 
            if (debug != null && Debug.isOn("session"))
              System.out.println("Subject identity is same"); 
          } else {
            if (debug != null && Debug.isOn("session"))
              System.out.println("Kerberos credentials are not present in the current Subject; check if  javax.security.auth.useSubjectAsCreds system property has been set to false"); 
            throw new SSLProtocolException("Server resumed session with no subject");
          } 
        } 
        this.resumingSession = true;
        this.state = 19;
        calculateConnectionKeys(this.session.getMasterSecret());
        if (debug != null && Debug.isOn("session"))
          System.out.println("%% Server resumed " + this.session); 
      } else {
        this.session = null;
        if (!this.enableNewSession)
          throw new SSLException("New session creation is disabled"); 
      }  
    if (this.resumingSession && this.session != null) {
      setHandshakeSessionSE(this.session);
      if (this.isInitialHandshake)
        this.session.setAsSessionResumption(true); 
      return;
    } 
    for (HelloExtension helloExtension : paramServerHello.extensions.list()) {
      ExtensionType extensionType = helloExtension.type;
      if (extensionType == ExtensionType.EXT_SERVER_NAME) {
        this.serverNamesAccepted = true;
        continue;
      } 
      if (extensionType != ExtensionType.EXT_ELLIPTIC_CURVES && extensionType != ExtensionType.EXT_EC_POINT_FORMATS && extensionType != ExtensionType.EXT_SERVER_NAME && extensionType != ExtensionType.EXT_RENEGOTIATION_INFO)
        fatalSE((byte)110, "Server sent an unsupported extension: " + extensionType); 
    } 
    this
      
      .session = new SSLSessionImpl(this.protocolVersion, this.cipherSuite, getLocalSupportedSignAlgs(), paramServerHello.sessionId, getHostSE(), getPortSE());
    this.session.setRequestedServerNames(this.requestedServerNames);
    setHandshakeSessionSE(this.session);
    if (debug != null && Debug.isOn("handshake"))
      System.out.println("** " + this.cipherSuite); 
  }
  
  private void serverKeyExchange(HandshakeMessage.RSA_ServerKeyExchange paramRSA_ServerKeyExchange) throws IOException, GeneralSecurityException {
    if (debug != null && Debug.isOn("handshake"))
      paramRSA_ServerKeyExchange.print(System.out); 
    if (!paramRSA_ServerKeyExchange.verify(this.serverKey, this.clnt_random, this.svr_random))
      fatalSE((byte)40, "server key exchange invalid"); 
    this.ephemeralServerKey = paramRSA_ServerKeyExchange.getPublicKey();
    if (!this.algorithmConstraints.permits(
        EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), this.ephemeralServerKey))
      throw new SSLHandshakeException("RSA ServerKeyExchange does not comply to algorithm constraints"); 
  }
  
  private void serverKeyExchange(HandshakeMessage.DH_ServerKeyExchange paramDH_ServerKeyExchange) throws IOException {
    if (debug != null && Debug.isOn("handshake"))
      paramDH_ServerKeyExchange.print(System.out); 
    this
      .dh = new DHCrypt(paramDH_ServerKeyExchange.getModulus(), paramDH_ServerKeyExchange.getBase(), this.sslContext.getSecureRandom());
    this.serverDH = paramDH_ServerKeyExchange.getServerPublicKey();
    this.dh.checkConstraints(this.algorithmConstraints, this.serverDH);
  }
  
  private void serverKeyExchange(HandshakeMessage.ECDH_ServerKeyExchange paramECDH_ServerKeyExchange) throws IOException {
    if (debug != null && Debug.isOn("handshake"))
      paramECDH_ServerKeyExchange.print(System.out); 
    ECPublicKey eCPublicKey = paramECDH_ServerKeyExchange.getPublicKey();
    this.ecdh = new ECDHCrypt(eCPublicKey.getParams(), this.sslContext.getSecureRandom());
    this.ephemeralServerKey = eCPublicKey;
    if (!this.algorithmConstraints.permits(
        EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), this.ephemeralServerKey))
      throw new SSLHandshakeException("ECDH ServerKeyExchange does not comply to algorithm constraints"); 
  }
  
  private void serverHelloDone(HandshakeMessage.ServerHelloDone paramServerHelloDone) throws IOException {
    RSAClientKeyExchange rSAClientKeyExchange;
    DHClientKeyExchange dHClientKeyExchange;
    ECDHClientKeyExchange eCDHClientKeyExchange;
    KerberosClientKeyExchange kerberosClientKeyExchange1;
    PublicKey publicKey;
    SecretKey secretKey;
    ECParameterSpec eCParameterSpec;
    byte[] arrayOfByte;
    String str;
    KerberosClientKeyExchange kerberosClientKeyExchange2;
    if (debug != null && Debug.isOn("handshake"))
      paramServerHelloDone.print(System.out); 
    this.input.digestNow();
    PrivateKey privateKey = null;
    if (this.certRequest != null) {
      X509ExtendedKeyManager x509ExtendedKeyManager = this.sslContext.getX509KeyManager();
      ArrayList<String> arrayList = new ArrayList(4);
      for (byte b = 0; b < this.certRequest.types.length; b++) {
        String str2;
        switch (this.certRequest.types[b]) {
          case 1:
            str2 = "RSA";
            break;
          case 2:
            str2 = "DSA";
            break;
          case 64:
            str2 = JsseJce.isEcAvailable() ? "EC" : null;
            break;
          default:
            str2 = null;
            break;
        } 
        if (str2 != null && !arrayList.contains(str2))
          arrayList.add(str2); 
      } 
      String str1 = null;
      int i = arrayList.size();
      if (i != 0) {
        String[] arrayOfString = arrayList.<String>toArray(new String[i]);
        if (this.conn != null) {
          str1 = x509ExtendedKeyManager.chooseClientAlias(arrayOfString, (Principal[])this.certRequest
              .getAuthorities(), this.conn);
        } else {
          str1 = x509ExtendedKeyManager.chooseEngineClientAlias(arrayOfString, (Principal[])this.certRequest
              .getAuthorities(), this.engine);
        } 
      } 
      HandshakeMessage.CertificateMsg certificateMsg = null;
      if (str1 != null) {
        X509Certificate[] arrayOfX509Certificate = x509ExtendedKeyManager.getCertificateChain(str1);
        if (arrayOfX509Certificate != null && arrayOfX509Certificate.length != 0) {
          PublicKey publicKey1 = arrayOfX509Certificate[0].getPublicKey();
          if (publicKey1 instanceof ECPublicKey) {
            ECParameterSpec eCParameterSpec1 = ((ECPublicKey)publicKey1).getParams();
            int j = SupportedEllipticCurvesExtension.getCurveIndex(eCParameterSpec1);
            if (!SupportedEllipticCurvesExtension.isSupported(j))
              publicKey1 = null; 
          } 
          if (publicKey1 != null) {
            certificateMsg = new HandshakeMessage.CertificateMsg(arrayOfX509Certificate);
            privateKey = x509ExtendedKeyManager.getPrivateKey(str1);
            this.session.setLocalPrivateKey(privateKey);
            this.session.setLocalCertificates(arrayOfX509Certificate);
          } 
        } 
      } 
      if (certificateMsg == null)
        if (this.protocolVersion.v >= ProtocolVersion.TLS10.v) {
          certificateMsg = new HandshakeMessage.CertificateMsg(new X509Certificate[0]);
        } else {
          warningSE((byte)41);
        }  
      if (certificateMsg != null) {
        if (debug != null && Debug.isOn("handshake"))
          certificateMsg.print(System.out); 
        certificateMsg.write(this.output);
      } 
    } 
    switch (this.keyExchange) {
      case K_RSA_EXPORT:
      case K_RSA:
        if (this.serverKey == null)
          throw new SSLProtocolException("Server did not send certificate message"); 
        if (!(this.serverKey instanceof java.security.interfaces.RSAPublicKey))
          throw new SSLProtocolException("Server certificate does not include an RSA key"); 
        if (this.keyExchange == CipherSuite.KeyExchange.K_RSA) {
          publicKey = this.serverKey;
        } else if (JsseJce.getRSAKeyLength(this.serverKey) <= 512) {
          publicKey = this.serverKey;
        } else {
          if (this.ephemeralServerKey == null)
            throw new SSLProtocolException("Server did not send a RSA_EXPORT Server Key Exchange message"); 
          publicKey = this.ephemeralServerKey;
        } 
        rSAClientKeyExchange = new RSAClientKeyExchange(this.protocolVersion, this.maxProtocolVersion, this.sslContext.getSecureRandom(), publicKey);
        break;
      case K_DH_RSA:
      case K_DH_DSS:
        dHClientKeyExchange = new DHClientKeyExchange();
        break;
      case K_DH_ANON:
      case K_DHE_DSS:
      case K_DHE_RSA:
        if (this.dh == null)
          throw new SSLProtocolException("Server did not send a DH Server Key Exchange message"); 
        dHClientKeyExchange = new DHClientKeyExchange(this.dh.getPublicKey());
        break;
      case K_ECDHE_ECDSA:
      case K_ECDHE_RSA:
      case K_ECDH_ANON:
        if (this.ecdh == null)
          throw new SSLProtocolException("Server did not send a ECDH Server Key Exchange message"); 
        eCDHClientKeyExchange = new ECDHClientKeyExchange(this.ecdh.getPublicKey());
        break;
      case K_ECDH_ECDSA:
      case K_ECDH_RSA:
        if (this.serverKey == null)
          throw new SSLProtocolException("Server did not send certificate message"); 
        if (!(this.serverKey instanceof ECPublicKey))
          throw new SSLProtocolException("Server certificate does not include an EC key"); 
        eCParameterSpec = ((ECPublicKey)this.serverKey).getParams();
        this.ecdh = new ECDHCrypt(eCParameterSpec, this.sslContext.getSecureRandom());
        eCDHClientKeyExchange = new ECDHClientKeyExchange(this.ecdh.getPublicKey());
        break;
      case K_KRB5:
      case K_KRB5_EXPORT:
        str = null;
        for (SNIServerName sNIServerName : this.requestedServerNames) {
          if (sNIServerName instanceof SNIHostName) {
            str = ((SNIHostName)sNIServerName).getAsciiName();
            break;
          } 
        } 
        kerberosClientKeyExchange2 = null;
        if (str != null)
          try {
            kerberosClientKeyExchange2 = new KerberosClientKeyExchange(str, getAccSE(), this.protocolVersion, this.sslContext.getSecureRandom());
          } catch (IOException iOException) {
            if (this.serverNamesAccepted)
              throw iOException; 
            if (debug != null && Debug.isOn("handshake"))
              System.out.println("Warning, cannot use Server Name Indication: " + iOException
                  
                  .getMessage()); 
          }  
        if (kerberosClientKeyExchange2 == null) {
          String str1 = getHostSE();
          if (str1 == null)
            throw new IOException("Hostname is required to use Kerberos cipher suites"); 
          kerberosClientKeyExchange2 = new KerberosClientKeyExchange(str1, getAccSE(), this.protocolVersion, this.sslContext.getSecureRandom());
        } 
        this.session.setPeerPrincipal(kerberosClientKeyExchange2.getPeerPrincipal());
        this.session.setLocalPrincipal(kerberosClientKeyExchange2.getLocalPrincipal());
        kerberosClientKeyExchange1 = kerberosClientKeyExchange2;
        break;
      default:
        throw new RuntimeException("Unsupported key exchange: " + this.keyExchange);
    } 
    if (debug != null && Debug.isOn("handshake"))
      kerberosClientKeyExchange1.print(System.out); 
    kerberosClientKeyExchange1.write(this.output);
    this.output.doHashes();
    this.output.flush();
    switch (this.keyExchange) {
      case K_RSA_EXPORT:
      case K_RSA:
        secretKey = ((RSAClientKeyExchange)kerberosClientKeyExchange1).preMaster;
        break;
      case K_KRB5:
      case K_KRB5_EXPORT:
        arrayOfByte = kerberosClientKeyExchange1.getUnencryptedPreMasterSecret();
        secretKey = new SecretKeySpec(arrayOfByte, "TlsPremasterSecret");
        break;
      case K_DH_ANON:
      case K_DHE_DSS:
      case K_DHE_RSA:
        secretKey = this.dh.getAgreedSecret(this.serverDH, true);
        break;
      case K_ECDHE_ECDSA:
      case K_ECDHE_RSA:
      case K_ECDH_ANON:
        secretKey = this.ecdh.getAgreedSecret(this.ephemeralServerKey);
        break;
      case K_ECDH_ECDSA:
      case K_ECDH_RSA:
        secretKey = this.ecdh.getAgreedSecret(this.serverKey);
        break;
      default:
        throw new IOException("Internal error: unknown key exchange " + this.keyExchange);
    } 
    calculateKeys(secretKey, null);
    if (privateKey != null) {
      try {
        SignatureAndHashAlgorithm signatureAndHashAlgorithm;
        str = null;
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
          signatureAndHashAlgorithm = SignatureAndHashAlgorithm.getPreferableAlgorithm(this.peerSupportedSignAlgs, privateKey
              .getAlgorithm(), privateKey);
          if (signatureAndHashAlgorithm == null)
            throw new SSLHandshakeException("No supported signature algorithm"); 
          String str1 = SignatureAndHashAlgorithm.getHashAlgorithmName(signatureAndHashAlgorithm);
          if (str1 == null || str1.length() == 0)
            throw new SSLHandshakeException("No supported hash algorithm"); 
        } 
        HandshakeMessage.CertificateVerify certificateVerify = new HandshakeMessage.CertificateVerify(this.protocolVersion, this.handshakeHash, privateKey, this.session.getMasterSecret(), this.sslContext.getSecureRandom(), signatureAndHashAlgorithm);
      } catch (GeneralSecurityException generalSecurityException) {
        fatalSE((byte)40, "Error signing certificate verify", generalSecurityException);
        arrayOfByte = null;
      } 
      if (debug != null && Debug.isOn("handshake"))
        arrayOfByte.print(System.out); 
      arrayOfByte.write(this.output);
      this.output.doHashes();
    } 
    sendChangeCipherAndFinish(false);
  }
  
  private void serverFinished(HandshakeMessage.Finished paramFinished) throws IOException {
    if (debug != null && Debug.isOn("handshake"))
      paramFinished.print(System.out); 
    boolean bool = paramFinished.verify(this.handshakeHash, 2, this.session
        .getMasterSecret());
    if (!bool)
      fatalSE((byte)47, "server 'finished' message doesn't verify"); 
    if (this.secureRenegotiation)
      this.serverVerifyData = paramFinished.getVerifyData(); 
    if (!this.isInitialHandshake)
      this.session.setAsSessionResumption(false); 
    if (this.resumingSession) {
      this.input.digestNow();
      sendChangeCipherAndFinish(true);
    } 
    this.session.setLastAccessedTime(System.currentTimeMillis());
    if (!this.resumingSession)
      if (this.session.isRejoinable()) {
        ((SSLSessionContextImpl)this.sslContext
          .engineGetClientSessionContext())
          .put(this.session);
        if (debug != null && Debug.isOn("session"))
          System.out.println("%% Cached client session: " + this.session); 
      } else if (debug != null && Debug.isOn("session")) {
        System.out.println("%% Didn't cache non-resumable client session: " + this.session);
      }  
  }
  
  private void sendChangeCipherAndFinish(boolean paramBoolean) throws IOException {
    HandshakeMessage.Finished finished = new HandshakeMessage.Finished(this.protocolVersion, this.handshakeHash, 1, this.session.getMasterSecret(), this.cipherSuite);
    sendChangeCipherSpec(finished, paramBoolean);
    if (this.secureRenegotiation)
      this.clientVerifyData = finished.getVerifyData(); 
    this.state = 19;
  }
  
  HandshakeMessage getKickstartMessage() throws SSLException {
    SessionId sessionId = SSLSessionImpl.nullSession.getSessionId();
    CipherSuiteList cipherSuiteList = getActiveCipherSuites();
    this.maxProtocolVersion = this.protocolVersion;
    this
      
      .session = ((SSLSessionContextImpl)this.sslContext.engineGetClientSessionContext()).get(getHostSE(), getPortSE());
    if (debug != null && Debug.isOn("session"))
      if (this.session != null) {
        System.out.println("%% Client cached " + this.session + (
            
            this.session.isRejoinable() ? "" : " (not rejoinable)"));
      } else {
        System.out.println("%% No cached client session");
      }  
    if (this.session != null) {
      if (!allowUnsafeServerCertChange && this.session.isSessionResumption())
        try {
          this
            .reservedServerCerts = (X509Certificate[])this.session.getPeerCertificates();
        } catch (SSLPeerUnverifiedException sSLPeerUnverifiedException) {} 
      if (!this.session.isRejoinable())
        this.session = null; 
    } 
    if (this.session != null) {
      CipherSuite cipherSuite = this.session.getSuite();
      ProtocolVersion protocolVersion = this.session.getProtocolVersion();
      if (!isNegotiable(cipherSuite)) {
        if (debug != null && Debug.isOn("session"))
          System.out.println("%% can't resume, unavailable cipher"); 
        this.session = null;
      } 
      if (this.session != null && !isNegotiable(protocolVersion)) {
        if (debug != null && Debug.isOn("session"))
          System.out.println("%% can't resume, protocol disabled"); 
        this.session = null;
      } 
      if (this.session != null) {
        if (debug != null && (
          Debug.isOn("handshake") || Debug.isOn("session")))
          System.out.println("%% Try resuming " + this.session + " from port " + 
              getLocalPortSE()); 
        sessionId = this.session.getSessionId();
        this.maxProtocolVersion = protocolVersion;
        setVersion(protocolVersion);
      } 
      if (!this.enableNewSession) {
        if (this.session == null)
          throw new SSLHandshakeException("Can't reuse existing SSL client session"); 
        ArrayList<CipherSuite> arrayList = new ArrayList(2);
        arrayList.add(cipherSuite);
        if (!this.secureRenegotiation && cipherSuiteList
          .contains(CipherSuite.C_SCSV))
          arrayList.add(CipherSuite.C_SCSV); 
        cipherSuiteList = new CipherSuiteList(arrayList);
      } 
    } 
    if (this.session == null && !this.enableNewSession)
      throw new SSLHandshakeException("No existing session to resume"); 
    if (this.secureRenegotiation && cipherSuiteList.contains(CipherSuite.C_SCSV)) {
      ArrayList<CipherSuite> arrayList = new ArrayList(cipherSuiteList.size() - 1);
      for (CipherSuite cipherSuite : cipherSuiteList.collection()) {
        if (cipherSuite != CipherSuite.C_SCSV)
          arrayList.add(cipherSuite); 
      } 
      cipherSuiteList = new CipherSuiteList(arrayList);
    } 
    boolean bool = false;
    for (CipherSuite cipherSuite : cipherSuiteList.collection()) {
      if (isNegotiable(cipherSuite)) {
        bool = true;
        break;
      } 
    } 
    if (!bool)
      throw new SSLHandshakeException("No negotiable cipher suite"); 
    HandshakeMessage.ClientHello clientHello = new HandshakeMessage.ClientHello(this.sslContext.getSecureRandom(), this.maxProtocolVersion, sessionId, cipherSuiteList);
    if (this.maxProtocolVersion.v >= ProtocolVersion.TLS12.v) {
      Collection<SignatureAndHashAlgorithm> collection = getLocalSupportedSignAlgs();
      if (collection.isEmpty())
        throw new SSLHandshakeException("No supported signature algorithm"); 
      clientHello.addSignatureAlgorithmsExtension(collection);
    } 
    if (enableSNIExtension) {
      if (this.session != null) {
        this.requestedServerNames = this.session.getRequestedServerNames();
      } else {
        this.requestedServerNames = this.serverNames;
      } 
      if (!this.requestedServerNames.isEmpty())
        clientHello.addSNIExtension(this.requestedServerNames); 
    } 
    this.clnt_random = clientHello.clnt_random;
    if (this.secureRenegotiation || 
      !cipherSuiteList.contains(CipherSuite.C_SCSV))
      clientHello.addRenegotiationInfoExtension(this.clientVerifyData); 
    return clientHello;
  }
  
  void handshakeAlert(byte paramByte) throws SSLProtocolException {
    String str = Alerts.alertDescription(paramByte);
    if (debug != null && Debug.isOn("handshake"))
      System.out.println("SSL - handshake alert: " + str); 
    throw new SSLProtocolException("handshake alert:  " + str);
  }
  
  private void serverCertificate(HandshakeMessage.CertificateMsg paramCertificateMsg) throws IOException {
    if (debug != null && Debug.isOn("handshake"))
      paramCertificateMsg.print(System.out); 
    X509Certificate[] arrayOfX509Certificate = paramCertificateMsg.getCertificateChain();
    if (arrayOfX509Certificate.length == 0)
      fatalSE((byte)42, "empty certificate chain"); 
    if (this.reservedServerCerts != null) {
      String str = getEndpointIdentificationAlgorithmSE();
      if ((str == null || str.length() == 0) && 
        !isIdentityEquivalent(arrayOfX509Certificate[0], this.reservedServerCerts[0]))
        fatalSE((byte)42, "server certificate change is restricted during renegotiation"); 
    } 
    X509TrustManager x509TrustManager = this.sslContext.getX509TrustManager();
    try {
      String str;
      if (this.keyExchange == CipherSuite.KeyExchange.K_RSA_EXPORT && !this.serverKeyExchangeReceived) {
        str = CipherSuite.KeyExchange.K_RSA.name;
      } else {
        str = this.keyExchange.name;
      } 
      if (x509TrustManager instanceof X509ExtendedTrustManager) {
        if (this.conn != null) {
          ((X509ExtendedTrustManager)x509TrustManager).checkServerTrusted((X509Certificate[])arrayOfX509Certificate
              .clone(), str, this.conn);
        } else {
          ((X509ExtendedTrustManager)x509TrustManager).checkServerTrusted((X509Certificate[])arrayOfX509Certificate
              .clone(), str, this.engine);
        } 
      } else {
        throw new CertificateException("Improper X509TrustManager implementation");
      } 
    } catch (CertificateException certificateException) {
      fatalSE((byte)46, certificateException);
    } 
    this.session.setPeerCertificates(arrayOfX509Certificate);
  }
  
  private static boolean isIdentityEquivalent(X509Certificate paramX509Certificate1, X509Certificate paramX509Certificate2) {
    if (paramX509Certificate1.equals(paramX509Certificate2))
      return true; 
    Object object1 = getSubjectAltName(paramX509Certificate1, 7);
    Object object2 = getSubjectAltName(paramX509Certificate2, 7);
    if (object1 != null && object2 != null)
      return Objects.equals(object1, object2); 
    Object object3 = getSubjectAltName(paramX509Certificate1, 2);
    Object object4 = getSubjectAltName(paramX509Certificate2, 2);
    if (object3 != null && object4 != null)
      return Objects.equals(object3, object4); 
    X500Principal x500Principal1 = paramX509Certificate1.getSubjectX500Principal();
    X500Principal x500Principal2 = paramX509Certificate2.getSubjectX500Principal();
    X500Principal x500Principal3 = paramX509Certificate1.getIssuerX500Principal();
    X500Principal x500Principal4 = paramX509Certificate2.getIssuerX500Principal();
    if (!x500Principal1.getName().isEmpty() && 
      !x500Principal2.getName().isEmpty() && x500Principal1
      .equals(x500Principal2) && x500Principal3
      .equals(x500Principal4))
      return true; 
    return false;
  }
  
  private static Object getSubjectAltName(X509Certificate paramX509Certificate, int paramInt) {
    Collection<List<?>> collection;
    try {
      collection = paramX509Certificate.getSubjectAlternativeNames();
    } catch (CertificateParsingException certificateParsingException) {
      if (debug != null && Debug.isOn("handshake"))
        System.out.println("Attempt to obtain subjectAltNames extension failed!"); 
      return null;
    } 
    if (collection != null)
      for (List<Integer> list : collection) {
        int i = ((Integer)list.get(0)).intValue();
        if (i == paramInt)
          return list.get(1); 
      }  
    return null;
  }
}
