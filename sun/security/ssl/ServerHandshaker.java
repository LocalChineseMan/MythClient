package sun.security.ssl;

import java.io.IOException;
import java.math.BigInteger;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.AlgorithmConstraints;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.Subject;
import sun.security.action.GetPropertyAction;
import sun.security.util.KeyUtil;
import sun.security.util.LegacyAlgorithmConstraints;

final class ServerHandshaker extends Handshaker {
  private byte doClientAuth;
  
  private X509Certificate[] certs;
  
  private PrivateKey privateKey;
  
  private Object serviceCreds;
  
  private boolean needClientVerify = false;
  
  private PrivateKey tempPrivateKey;
  
  private PublicKey tempPublicKey;
  
  private DHCrypt dh;
  
  private ECDHCrypt ecdh;
  
  private ProtocolVersion clientRequestedVersion;
  
  private SupportedEllipticCurvesExtension supportedCurves;
  
  SignatureAndHashAlgorithm preferableSignatureAlgorithm;
  
  private static final boolean useSmartEphemeralDHKeys;
  
  private static final boolean useLegacyEphemeralDHKeys;
  
  private static final int customizedDHKeySize;
  
  private static final AlgorithmConstraints legacyAlgorithmConstraints = new LegacyAlgorithmConstraints("jdk.tls.legacyAlgorithms", new SSLAlgorithmDecomposer());
  
  static {
    String str = AccessController.<String>doPrivileged(new GetPropertyAction("jdk.tls.ephemeralDHKeySize"));
    if (str == null || str.length() == 0) {
      useLegacyEphemeralDHKeys = false;
      useSmartEphemeralDHKeys = false;
      customizedDHKeySize = -1;
    } else if ("matched".equals(str)) {
      useLegacyEphemeralDHKeys = false;
      useSmartEphemeralDHKeys = true;
      customizedDHKeySize = -1;
    } else if ("legacy".equals(str)) {
      useLegacyEphemeralDHKeys = true;
      useSmartEphemeralDHKeys = false;
      customizedDHKeySize = -1;
    } else {
      useLegacyEphemeralDHKeys = false;
      useSmartEphemeralDHKeys = false;
      try {
        customizedDHKeySize = Integer.parseUnsignedInt(str);
        if (customizedDHKeySize < 1024 || customizedDHKeySize > 2048)
          throw new IllegalArgumentException("Customized DH key size should be positive integer between 1024 and 2048 bits, inclusive"); 
      } catch (NumberFormatException numberFormatException) {
        throw new IllegalArgumentException("Invalid system property jdk.tls.ephemeralDHKeySize");
      } 
    } 
  }
  
  ServerHandshaker(SSLSocketImpl paramSSLSocketImpl, SSLContextImpl paramSSLContextImpl, ProtocolList paramProtocolList, byte paramByte, ProtocolVersion paramProtocolVersion, boolean paramBoolean1, boolean paramBoolean2, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    super(paramSSLSocketImpl, paramSSLContextImpl, paramProtocolList, (paramByte != 0), false, paramProtocolVersion, paramBoolean1, paramBoolean2, paramArrayOfbyte1, paramArrayOfbyte2);
    this.doClientAuth = paramByte;
  }
  
  ServerHandshaker(SSLEngineImpl paramSSLEngineImpl, SSLContextImpl paramSSLContextImpl, ProtocolList paramProtocolList, byte paramByte, ProtocolVersion paramProtocolVersion, boolean paramBoolean1, boolean paramBoolean2, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    super(paramSSLEngineImpl, paramSSLContextImpl, paramProtocolList, (paramByte != 0), false, paramProtocolVersion, paramBoolean1, paramBoolean2, paramArrayOfbyte1, paramArrayOfbyte2);
    this.doClientAuth = paramByte;
  }
  
  void setClientAuth(byte paramByte) {
    this.doClientAuth = paramByte;
  }
  
  void processMessage(byte paramByte, int paramInt) throws IOException {
    HandshakeMessage.ClientHello clientHello;
    SecretKey secretKey;
    RSAClientKeyExchange rSAClientKeyExchange;
    if (this.state >= paramByte && this.state != 16 && paramByte != 15)
      throw new SSLProtocolException("Handshake message sequence violation, state = " + this.state + ", type = " + paramByte); 
    switch (paramByte) {
      case 1:
        clientHello = new HandshakeMessage.ClientHello(this.input, paramInt);
        clientHello(clientHello);
        break;
      case 11:
        if (this.doClientAuth == 0)
          fatalSE((byte)10, "client sent unsolicited cert chain"); 
        clientCertificate(new HandshakeMessage.CertificateMsg(this.input));
        break;
      case 16:
        switch (null.$SwitchMap$sun$security$ssl$CipherSuite$KeyExchange[this.keyExchange.ordinal()]) {
          case 1:
          case 2:
            rSAClientKeyExchange = new RSAClientKeyExchange(this.protocolVersion, this.clientRequestedVersion, this.sslContext.getSecureRandom(), this.input, paramInt, this.privateKey);
            secretKey = clientKeyExchange(rSAClientKeyExchange);
            break;
          case 3:
          case 4:
            secretKey = clientKeyExchange(new KerberosClientKeyExchange(this.protocolVersion, this.clientRequestedVersion, this.sslContext
                  
                  .getSecureRandom(), this.input, 
                  
                  getAccSE(), this.serviceCreds));
            break;
          case 5:
          case 6:
          case 7:
            secretKey = clientKeyExchange(new DHClientKeyExchange(this.input));
            break;
          case 8:
          case 9:
          case 10:
          case 11:
          case 12:
            secretKey = clientKeyExchange(new ECDHClientKeyExchange(this.input));
            break;
          default:
            throw new SSLProtocolException("Unrecognized key exchange: " + this.keyExchange);
        } 
        calculateKeys(secretKey, this.clientRequestedVersion);
        break;
      case 15:
        clientCertificateVerify(new HandshakeMessage.CertificateVerify(this.input, this.localSupportedSignAlgs, this.protocolVersion));
        break;
      case 20:
        if (!receivedChangeCipherSpec())
          fatalSE((byte)40, "Received Finished message before ChangeCipherSpec"); 
        clientFinished(new HandshakeMessage.Finished(this.protocolVersion, this.input, this.cipherSuite));
        break;
      default:
        throw new SSLProtocolException("Illegal server handshake msg, " + paramByte);
    } 
    if (this.state < paramByte)
      if (paramByte == 15) {
        this.state = paramByte + 2;
      } else {
        this.state = paramByte;
      }  
  }
  
  private void clientHello(HandshakeMessage.ClientHello paramClientHello) throws IOException {
    HandshakeMessage.RSA_ServerKeyExchange rSA_ServerKeyExchange;
    HandshakeMessage.DH_ServerKeyExchange dH_ServerKeyExchange;
    if (debug != null && Debug.isOn("handshake"))
      paramClientHello.print(System.out); 
    if (rejectClientInitiatedRenego && !this.isInitialHandshake && this.state != 0)
      fatalSE((byte)40, "Client initiated renegotiation is not allowed"); 
    ServerNameExtension serverNameExtension = (ServerNameExtension)paramClientHello.extensions.get(ExtensionType.EXT_SERVER_NAME);
    if (!this.sniMatchers.isEmpty())
      if (serverNameExtension != null && 
        !serverNameExtension.isMatched(this.sniMatchers))
        fatalSE((byte)112, "Unrecognized server name indication");  
    boolean bool = false;
    CipherSuiteList cipherSuiteList = paramClientHello.getCipherSuites();
    if (cipherSuiteList.contains(CipherSuite.C_SCSV)) {
      bool = true;
      if (this.isInitialHandshake) {
        this.secureRenegotiation = true;
      } else if (this.secureRenegotiation) {
        fatalSE((byte)40, "The SCSV is present in a secure renegotiation");
      } else {
        fatalSE((byte)40, "The SCSV is present in a insecure renegotiation");
      } 
    } 
    RenegotiationInfoExtension renegotiationInfoExtension = (RenegotiationInfoExtension)paramClientHello.extensions.get(ExtensionType.EXT_RENEGOTIATION_INFO);
    if (renegotiationInfoExtension != null) {
      bool = true;
      if (this.isInitialHandshake) {
        if (!renegotiationInfoExtension.isEmpty())
          fatalSE((byte)40, "The renegotiation_info field is not empty"); 
        this.secureRenegotiation = true;
      } else {
        if (!this.secureRenegotiation)
          fatalSE((byte)40, "The renegotiation_info is present in a insecure renegotiation"); 
        if (!MessageDigest.isEqual(this.clientVerifyData, renegotiationInfoExtension
            .getRenegotiatedConnection()))
          fatalSE((byte)40, "Incorrect verify data in ClientHello renegotiation_info message"); 
      } 
    } else if (!this.isInitialHandshake && this.secureRenegotiation) {
      fatalSE((byte)40, "Inconsistent secure renegotiation indication");
    } 
    if (!bool || !this.secureRenegotiation)
      if (this.isInitialHandshake) {
        if (!allowLegacyHelloMessages)
          fatalSE((byte)40, "Failed to negotiate the use of secure renegotiation"); 
        if (debug != null && Debug.isOn("handshake"))
          System.out.println("Warning: No renegotiation indication in ClientHello, allow legacy ClientHello"); 
      } else if (!allowUnsafeRenegotiation) {
        if (this.activeProtocolVersion.v >= ProtocolVersion.TLS10.v) {
          warningSE((byte)100);
          this.invalidated = true;
          if (this.input.available() > 0)
            fatalSE((byte)10, "ClientHello followed by an unexpected  handshake message"); 
          return;
        } 
        fatalSE((byte)40, "Renegotiation is not allowed");
      } else if (debug != null && Debug.isOn("handshake")) {
        System.out.println("Warning: continue with insecure renegotiation");
      }  
    this.input.digestNow();
    HandshakeMessage.ServerHello serverHello = new HandshakeMessage.ServerHello();
    this.clientRequestedVersion = paramClientHello.protocolVersion;
    ProtocolVersion protocolVersion = selectProtocolVersion(this.clientRequestedVersion);
    if (protocolVersion == null || protocolVersion.v == ProtocolVersion.SSL20Hello.v)
      fatalSE((byte)40, "Client requested protocol " + this.clientRequestedVersion + " not enabled or not supported"); 
    this.handshakeHash.protocolDetermined(protocolVersion);
    setVersion(protocolVersion);
    serverHello.protocolVersion = this.protocolVersion;
    this.clnt_random = paramClientHello.clnt_random;
    this.svr_random = new RandomCookie(this.sslContext.getSecureRandom());
    serverHello.svr_random = this.svr_random;
    this.session = null;
    if (paramClientHello.sessionId.length() != 0) {
      SSLSessionImpl sSLSessionImpl = ((SSLSessionContextImpl)this.sslContext.engineGetServerSessionContext()).get(paramClientHello.sessionId.getId());
      if (sSLSessionImpl != null) {
        this.resumingSession = sSLSessionImpl.isRejoinable();
        if (this.resumingSession) {
          ProtocolVersion protocolVersion1 = sSLSessionImpl.getProtocolVersion();
          if (protocolVersion1 != this.protocolVersion)
            this.resumingSession = false; 
        } 
        if (this.resumingSession) {
          List<SNIServerName> list = sSLSessionImpl.getRequestedServerNames();
          if (serverNameExtension != null) {
            if (!serverNameExtension.isIdentical(list))
              this.resumingSession = false; 
          } else if (!list.isEmpty()) {
            this.resumingSession = false;
          } 
          if (!this.resumingSession && debug != null && 
            Debug.isOn("handshake"))
            System.out.println("The requested server name indication is not identical to the previous one"); 
        } 
        if (this.resumingSession && this.doClientAuth == 2)
          try {
            sSLSessionImpl.getPeerPrincipal();
          } catch (SSLPeerUnverifiedException sSLPeerUnverifiedException) {
            this.resumingSession = false;
          }  
        if (this.resumingSession) {
          CipherSuite cipherSuite = sSLSessionImpl.getSuite();
          if (cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5 || cipherSuite.keyExchange == CipherSuite.KeyExchange.K_KRB5_EXPORT) {
            Principal principal = sSLSessionImpl.getLocalPrincipal();
            Subject subject = null;
            try {
              subject = AccessController.<Subject>doPrivileged((PrivilegedExceptionAction<Subject>)new Object(this));
            } catch (PrivilegedActionException privilegedActionException) {
              subject = null;
              if (debug != null && Debug.isOn("session"))
                System.out.println("Attempt to obtain subject failed!"); 
            } 
            if (subject != null) {
              if (Krb5Helper.isRelated(subject, principal)) {
                if (debug != null && Debug.isOn("session"))
                  System.out.println("Subject can provide creds for princ"); 
              } else {
                this.resumingSession = false;
                if (debug != null && Debug.isOn("session"))
                  System.out.println("Subject cannot provide creds for princ"); 
              } 
            } else {
              this.resumingSession = false;
              if (debug != null && Debug.isOn("session"))
                System.out.println("Kerberos credentials are not present in the current Subject; check if  javax.security.auth.useSubjectAsCreds system property has been set to false"); 
            } 
          } 
        } 
        if (this.resumingSession) {
          CipherSuite cipherSuite = sSLSessionImpl.getSuite();
          if (!isNegotiable(cipherSuite) || 
            !paramClientHello.getCipherSuites().contains(cipherSuite)) {
            this.resumingSession = false;
          } else {
            setCipherSuite(cipherSuite);
          } 
        } 
        if (this.resumingSession) {
          this.session = sSLSessionImpl;
          if (debug != null && (
            Debug.isOn("handshake") || Debug.isOn("session")))
            System.out.println("%% Resuming " + this.session); 
        } 
      } 
    } 
    if (this.session == null) {
      if (!this.enableNewSession)
        throw new SSLException("Client did not resume a session"); 
      this
        .supportedCurves = (SupportedEllipticCurvesExtension)paramClientHello.extensions.get(ExtensionType.EXT_ELLIPTIC_CURVES);
      if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
        SignatureAlgorithmsExtension signatureAlgorithmsExtension = (SignatureAlgorithmsExtension)paramClientHello.extensions.get(ExtensionType.EXT_SIGNATURE_ALGORITHMS);
        if (signatureAlgorithmsExtension != null) {
          Collection<SignatureAndHashAlgorithm> collection1 = signatureAlgorithmsExtension.getSignAlgorithms();
          if (collection1 == null || collection1.isEmpty())
            throw new SSLHandshakeException("No peer supported signature algorithms"); 
          Collection<SignatureAndHashAlgorithm> collection2 = SignatureAndHashAlgorithm.getSupportedAlgorithms(collection1);
          if (collection2.isEmpty())
            throw new SSLHandshakeException("No supported signature and hash algorithm in common"); 
          setPeerSupportedSignAlgs(collection2);
        } 
      } 
      this
        
        .session = new SSLSessionImpl(this.protocolVersion, CipherSuite.C_NULL, getLocalSupportedSignAlgs(), this.sslContext.getSecureRandom(), getHostAddressSE(), getPortSE());
      if (this.protocolVersion.v >= ProtocolVersion.TLS12.v && 
        this.peerSupportedSignAlgs != null)
        this.session.setPeerSupportedSignatureAlgorithms(this.peerSupportedSignAlgs); 
      List<?> list = Collections.emptyList();
      if (serverNameExtension != null)
        list = serverNameExtension.getServerNames(); 
      this.session.setRequestedServerNames((List)list);
      setHandshakeSessionSE(this.session);
      chooseCipherSuite(paramClientHello);
      this.session.setSuite(this.cipherSuite);
      this.session.setLocalPrivateKey(this.privateKey);
    } else {
      setHandshakeSessionSE(this.session);
    } 
    if (this.protocolVersion.v >= ProtocolVersion.TLS12.v)
      this.handshakeHash.setFinishedAlg(this.cipherSuite.prfAlg.getPRFHashAlg()); 
    serverHello.cipherSuite = this.cipherSuite;
    serverHello.sessionId = this.session.getSessionId();
    serverHello.compression_method = this.session.getCompression();
    if (this.secureRenegotiation) {
      RenegotiationInfoExtension renegotiationInfoExtension1 = new RenegotiationInfoExtension(this.clientVerifyData, this.serverVerifyData);
      serverHello.extensions.add(renegotiationInfoExtension1);
    } 
    if (!this.sniMatchers.isEmpty() && serverNameExtension != null)
      if (!this.resumingSession) {
        ServerNameExtension serverNameExtension1 = new ServerNameExtension();
        serverHello.extensions.add(serverNameExtension1);
      }  
    if (debug != null && Debug.isOn("handshake")) {
      serverHello.print(System.out);
      System.out.println("Cipher suite:  " + this.session.getSuite());
    } 
    serverHello.write(this.output);
    if (this.resumingSession) {
      calculateConnectionKeys(this.session.getMasterSecret());
      sendChangeCipherAndFinish(false);
      return;
    } 
    if (this.keyExchange != CipherSuite.KeyExchange.K_KRB5 && this.keyExchange != CipherSuite.KeyExchange.K_KRB5_EXPORT)
      if (this.keyExchange != CipherSuite.KeyExchange.K_DH_ANON && this.keyExchange != CipherSuite.KeyExchange.K_ECDH_ANON) {
        if (this.certs == null)
          throw new RuntimeException("no certificates"); 
        HandshakeMessage.CertificateMsg certificateMsg = new HandshakeMessage.CertificateMsg(this.certs);
        this.session.setLocalCertificates(this.certs);
        if (debug != null && Debug.isOn("handshake"))
          certificateMsg.print(System.out); 
        certificateMsg.write(this.output);
      } else if (this.certs != null) {
        throw new RuntimeException("anonymous keyexchange with certs");
      }  
    switch (null.$SwitchMap$sun$security$ssl$CipherSuite$KeyExchange[this.keyExchange.ordinal()]) {
      case 1:
      case 3:
      case 4:
        rSA_ServerKeyExchange = null;
        break;
      case 2:
        if (JsseJce.getRSAKeyLength(this.certs[0].getPublicKey()) > 512) {
          try {
            rSA_ServerKeyExchange = new HandshakeMessage.RSA_ServerKeyExchange(this.tempPublicKey, this.privateKey, this.clnt_random, this.svr_random, this.sslContext.getSecureRandom());
            this.privateKey = this.tempPrivateKey;
          } catch (GeneralSecurityException generalSecurityException) {
            throwSSLException("Error generating RSA server key exchange", generalSecurityException);
            rSA_ServerKeyExchange = null;
          } 
          break;
        } 
        rSA_ServerKeyExchange = null;
        break;
      case 5:
      case 6:
        try {
          HandshakeMessage.DH_ServerKeyExchange dH_ServerKeyExchange1 = new HandshakeMessage.DH_ServerKeyExchange(this.dh, this.privateKey, this.clnt_random.random_bytes, this.svr_random.random_bytes, this.sslContext.getSecureRandom(), this.preferableSignatureAlgorithm, this.protocolVersion);
        } catch (GeneralSecurityException generalSecurityException) {
          throwSSLException("Error generating DH server key exchange", generalSecurityException);
          rSA_ServerKeyExchange = null;
        } 
        break;
      case 7:
        dH_ServerKeyExchange = new HandshakeMessage.DH_ServerKeyExchange(this.dh, this.protocolVersion);
        break;
      case 10:
      case 11:
      case 12:
        try {
          HandshakeMessage.ECDH_ServerKeyExchange eCDH_ServerKeyExchange = new HandshakeMessage.ECDH_ServerKeyExchange(this.ecdh, this.privateKey, this.clnt_random.random_bytes, this.svr_random.random_bytes, this.sslContext.getSecureRandom(), this.preferableSignatureAlgorithm, this.protocolVersion);
        } catch (GeneralSecurityException generalSecurityException) {
          throwSSLException("Error generating ECDH server key exchange", generalSecurityException);
          dH_ServerKeyExchange = null;
        } 
        break;
      case 8:
      case 9:
        dH_ServerKeyExchange = null;
        break;
      default:
        throw new RuntimeException("internal error: " + this.keyExchange);
    } 
    if (dH_ServerKeyExchange != null) {
      if (debug != null && Debug.isOn("handshake"))
        dH_ServerKeyExchange.print(System.out); 
      dH_ServerKeyExchange.write(this.output);
    } 
    if (this.doClientAuth != 0 && this.keyExchange != CipherSuite.KeyExchange.K_DH_ANON && this.keyExchange != CipherSuite.KeyExchange.K_ECDH_ANON && this.keyExchange != CipherSuite.KeyExchange.K_KRB5 && this.keyExchange != CipherSuite.KeyExchange.K_KRB5_EXPORT) {
      Collection<SignatureAndHashAlgorithm> collection = null;
      if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
        collection = getLocalSupportedSignAlgs();
        if (collection.isEmpty())
          throw new SSLHandshakeException("No supported signature algorithm"); 
        Set<String> set = SignatureAndHashAlgorithm.getHashAlgorithmNames(collection);
        if (set.isEmpty())
          throw new SSLHandshakeException("No supported signature algorithm"); 
      } 
      X509Certificate[] arrayOfX509Certificate = this.sslContext.getX509TrustManager().getAcceptedIssuers();
      HandshakeMessage.CertificateRequest certificateRequest = new HandshakeMessage.CertificateRequest(arrayOfX509Certificate, this.keyExchange, collection, this.protocolVersion);
      if (debug != null && Debug.isOn("handshake"))
        certificateRequest.print(System.out); 
      certificateRequest.write(this.output);
    } 
    HandshakeMessage.ServerHelloDone serverHelloDone = new HandshakeMessage.ServerHelloDone();
    if (debug != null && Debug.isOn("handshake"))
      serverHelloDone.print(System.out); 
    serverHelloDone.write(this.output);
    this.output.flush();
  }
  
  private void chooseCipherSuite(HandshakeMessage.ClientHello paramClientHello) throws IOException {
    CipherSuiteList cipherSuiteList1, cipherSuiteList2;
    if (this.preferLocalCipherSuites) {
      cipherSuiteList1 = getActiveCipherSuites();
      cipherSuiteList2 = paramClientHello.getCipherSuites();
    } else {
      cipherSuiteList1 = paramClientHello.getCipherSuites();
      cipherSuiteList2 = getActiveCipherSuites();
    } 
    ArrayList<CipherSuite> arrayList = new ArrayList();
    for (CipherSuite cipherSuite : cipherSuiteList1.collection()) {
      if (!isNegotiable(cipherSuiteList2, cipherSuite))
        continue; 
      if (this.doClientAuth == 2 && (
        cipherSuite.keyExchange == CipherSuite.KeyExchange.K_DH_ANON || cipherSuite.keyExchange == CipherSuite.KeyExchange.K_ECDH_ANON))
        continue; 
      if (!legacyAlgorithmConstraints.permits(null, cipherSuite.name, null)) {
        arrayList.add(cipherSuite);
        continue;
      } 
      if (!trySetCipherSuite(cipherSuite))
        continue; 
      return;
    } 
    for (CipherSuite cipherSuite : arrayList) {
      if (trySetCipherSuite(cipherSuite))
        return; 
    } 
    fatalSE((byte)40, "no cipher suites in common");
  }
  
  boolean trySetCipherSuite(CipherSuite paramCipherSuite) {
    if (this.resumingSession)
      return true; 
    if (!paramCipherSuite.isNegotiable())
      return false; 
    if (this.protocolVersion.v >= paramCipherSuite.obsoleted)
      return false; 
    if (this.protocolVersion.v < paramCipherSuite.supported)
      return false; 
    CipherSuite.KeyExchange keyExchange = paramCipherSuite.keyExchange;
    this.privateKey = null;
    this.certs = null;
    this.dh = null;
    this.tempPrivateKey = null;
    this.tempPublicKey = null;
    Collection<?> collection = null;
    if (this.protocolVersion.v >= ProtocolVersion.TLS12.v)
      if (this.peerSupportedSignAlgs != null) {
        collection = this.peerSupportedSignAlgs;
      } else {
        SignatureAndHashAlgorithm signatureAndHashAlgorithm = null;
        switch (null.$SwitchMap$sun$security$ssl$CipherSuite$KeyExchange[keyExchange.ordinal()]) {
          case 1:
          case 5:
          case 8:
          case 10:
          case 13:
            signatureAndHashAlgorithm = SignatureAndHashAlgorithm.valueOf(SignatureAndHashAlgorithm.HashAlgorithm.SHA1.value, SignatureAndHashAlgorithm.SignatureAlgorithm.RSA.value, 0);
            break;
          case 6:
          case 14:
            signatureAndHashAlgorithm = SignatureAndHashAlgorithm.valueOf(SignatureAndHashAlgorithm.HashAlgorithm.SHA1.value, SignatureAndHashAlgorithm.SignatureAlgorithm.DSA.value, 0);
            break;
          case 9:
          case 11:
            signatureAndHashAlgorithm = SignatureAndHashAlgorithm.valueOf(SignatureAndHashAlgorithm.HashAlgorithm.SHA1.value, SignatureAndHashAlgorithm.SignatureAlgorithm.ECDSA.value, 0);
            break;
        } 
        if (signatureAndHashAlgorithm == null) {
          collection = Collections.emptySet();
        } else {
          collection = new ArrayList(1);
          collection.add(signatureAndHashAlgorithm);
        } 
        this.session.setPeerSupportedSignatureAlgorithms((Collection)collection);
      }  
    switch (null.$SwitchMap$sun$security$ssl$CipherSuite$KeyExchange[keyExchange.ordinal()]) {
      case 1:
        if (!setupPrivateKeyAndChain("RSA"))
          return false; 
        break;
      case 2:
        if (!setupPrivateKeyAndChain("RSA"))
          return false; 
        try {
          if (JsseJce.getRSAKeyLength(this.certs[0].getPublicKey()) > 512 && 
            !setupEphemeralRSAKeys(paramCipherSuite.exportable))
            return false; 
        } catch (RuntimeException runtimeException) {
          return false;
        } 
        break;
      case 5:
        if (!setupPrivateKeyAndChain("RSA"))
          return false; 
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
          this
            .preferableSignatureAlgorithm = SignatureAndHashAlgorithm.getPreferableAlgorithm((Collection)collection, "RSA", this.privateKey);
          if (this.preferableSignatureAlgorithm == null)
            return false; 
        } 
        setupEphemeralDHKeys(paramCipherSuite.exportable, this.privateKey);
        break;
      case 10:
        if (!setupPrivateKeyAndChain("RSA"))
          return false; 
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
          this
            .preferableSignatureAlgorithm = SignatureAndHashAlgorithm.getPreferableAlgorithm((Collection)collection, "RSA", this.privateKey);
          if (this.preferableSignatureAlgorithm == null)
            return false; 
        } 
        if (!setupEphemeralECDHKeys())
          return false; 
        break;
      case 6:
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
          this
            .preferableSignatureAlgorithm = SignatureAndHashAlgorithm.getPreferableAlgorithm((Collection)collection, "DSA");
          if (this.preferableSignatureAlgorithm == null)
            return false; 
        } 
        if (!setupPrivateKeyAndChain("DSA"))
          return false; 
        setupEphemeralDHKeys(paramCipherSuite.exportable, this.privateKey);
        break;
      case 11:
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
          this
            .preferableSignatureAlgorithm = SignatureAndHashAlgorithm.getPreferableAlgorithm((Collection)collection, "ECDSA");
          if (this.preferableSignatureAlgorithm == null)
            return false; 
        } 
        if (!setupPrivateKeyAndChain("EC_EC"))
          return false; 
        if (!setupEphemeralECDHKeys())
          return false; 
        break;
      case 8:
        if (!setupPrivateKeyAndChain("EC_RSA"))
          return false; 
        setupStaticECDHKeys();
        break;
      case 9:
        if (!setupPrivateKeyAndChain("EC_EC"))
          return false; 
        setupStaticECDHKeys();
        break;
      case 3:
      case 4:
        if (!setupKerberosKeys())
          return false; 
        break;
      case 7:
        setupEphemeralDHKeys(paramCipherSuite.exportable, (Key)null);
        break;
      case 12:
        if (!setupEphemeralECDHKeys())
          return false; 
        break;
      default:
        throw new RuntimeException("Unrecognized cipherSuite: " + paramCipherSuite);
    } 
    setCipherSuite(paramCipherSuite);
    if (this.protocolVersion.v >= ProtocolVersion.TLS12.v && 
      this.peerSupportedSignAlgs == null)
      setPeerSupportedSignAlgs(collection); 
    return true;
  }
  
  private boolean setupEphemeralRSAKeys(boolean paramBoolean) {
    KeyPair keyPair = this.sslContext.getEphemeralKeyManager().getRSAKeyPair(paramBoolean, this.sslContext.getSecureRandom());
    if (keyPair == null)
      return false; 
    this.tempPublicKey = keyPair.getPublic();
    this.tempPrivateKey = keyPair.getPrivate();
    return true;
  }
  
  private void setupEphemeralDHKeys(boolean paramBoolean, Key paramKey) {
    int i = paramBoolean ? 512 : 1024;
    if (!paramBoolean)
      if (useLegacyEphemeralDHKeys) {
        i = 768;
      } else if (useSmartEphemeralDHKeys) {
        if (paramKey != null) {
          int j = KeyUtil.getKeySize(paramKey);
          i = (j <= 1024) ? 1024 : 2048;
        } 
      } else if (customizedDHKeySize > 0) {
        i = customizedDHKeySize;
      }  
    this.dh = new DHCrypt(i, this.sslContext.getSecureRandom());
  }
  
  private boolean setupEphemeralECDHKeys() {
    int i = -1;
    if (this.supportedCurves != null) {
      for (int j : this.supportedCurves.curveIds()) {
        if (SupportedEllipticCurvesExtension.isSupported(j)) {
          i = j;
          break;
        } 
      } 
      if (i < 0)
        return false; 
    } else {
      i = SupportedEllipticCurvesExtension.DEFAULT.curveIds()[0];
    } 
    String str = SupportedEllipticCurvesExtension.getCurveOid(i);
    this.ecdh = new ECDHCrypt(str, this.sslContext.getSecureRandom());
    return true;
  }
  
  private void setupStaticECDHKeys() {
    this.ecdh = new ECDHCrypt(this.privateKey, this.certs[0].getPublicKey());
  }
  
  private boolean setupPrivateKeyAndChain(String paramString) {
    String str1;
    X509ExtendedKeyManager x509ExtendedKeyManager = this.sslContext.getX509KeyManager();
    if (this.conn != null) {
      str1 = x509ExtendedKeyManager.chooseServerAlias(paramString, null, this.conn);
    } else {
      str1 = x509ExtendedKeyManager.chooseEngineServerAlias(paramString, (Principal[])null, this.engine);
    } 
    if (str1 == null)
      return false; 
    PrivateKey privateKey = x509ExtendedKeyManager.getPrivateKey(str1);
    if (privateKey == null)
      return false; 
    X509Certificate[] arrayOfX509Certificate = x509ExtendedKeyManager.getCertificateChain(str1);
    if (arrayOfX509Certificate == null || arrayOfX509Certificate.length == 0)
      return false; 
    String str2 = paramString.split("_")[0];
    PublicKey publicKey = arrayOfX509Certificate[0].getPublicKey();
    if (!privateKey.getAlgorithm().equals(str2) || 
      !publicKey.getAlgorithm().equals(str2))
      return false; 
    if (str2.equals("EC")) {
      if (!(publicKey instanceof ECPublicKey))
        return false; 
      ECParameterSpec eCParameterSpec = ((ECPublicKey)publicKey).getParams();
      int i = SupportedEllipticCurvesExtension.getCurveIndex(eCParameterSpec);
      if (!SupportedEllipticCurvesExtension.isSupported(i))
        return false; 
      if (this.supportedCurves != null && !this.supportedCurves.contains(i))
        return false; 
    } 
    this.privateKey = privateKey;
    this.certs = arrayOfX509Certificate;
    return true;
  }
  
  private boolean setupKerberosKeys() {
    if (this.serviceCreds != null)
      return true; 
    try {
      AccessControlContext accessControlContext = getAccSE();
      this.serviceCreds = AccessController.doPrivileged((PrivilegedExceptionAction<?>)new Object(this, accessControlContext));
      if (this.serviceCreds != null) {
        if (debug != null && Debug.isOn("handshake"))
          System.out.println("Using Kerberos creds"); 
        String str = Krb5Helper.getServerPrincipalName(this.serviceCreds);
        if (str != null) {
          SecurityManager securityManager = System.getSecurityManager();
          try {
            if (securityManager != null)
              securityManager.checkPermission(Krb5Helper.getServicePermission(str, "accept"), accessControlContext); 
          } catch (SecurityException securityException) {
            this.serviceCreds = null;
            if (debug != null && Debug.isOn("handshake"))
              System.out.println("Permission to access Kerberos secret key denied"); 
            return false;
          } 
        } 
      } 
      return (this.serviceCreds != null);
    } catch (PrivilegedActionException privilegedActionException) {
      if (debug != null && Debug.isOn("handshake"))
        System.out.println("Attempt to obtain Kerberos key failed: " + privilegedActionException
            .toString()); 
      return false;
    } 
  }
  
  private SecretKey clientKeyExchange(KerberosClientKeyExchange paramKerberosClientKeyExchange) throws IOException {
    if (debug != null && Debug.isOn("handshake"))
      paramKerberosClientKeyExchange.print(System.out); 
    this.session.setPeerPrincipal(paramKerberosClientKeyExchange.getPeerPrincipal());
    this.session.setLocalPrincipal(paramKerberosClientKeyExchange.getLocalPrincipal());
    byte[] arrayOfByte = paramKerberosClientKeyExchange.getUnencryptedPreMasterSecret();
    return new SecretKeySpec(arrayOfByte, "TlsPremasterSecret");
  }
  
  private SecretKey clientKeyExchange(DHClientKeyExchange paramDHClientKeyExchange) throws IOException {
    if (debug != null && Debug.isOn("handshake"))
      paramDHClientKeyExchange.print(System.out); 
    BigInteger bigInteger = paramDHClientKeyExchange.getClientPublicKey();
    this.dh.checkConstraints(this.algorithmConstraints, bigInteger);
    return this.dh.getAgreedSecret(bigInteger, false);
  }
  
  private SecretKey clientKeyExchange(ECDHClientKeyExchange paramECDHClientKeyExchange) throws IOException {
    if (debug != null && Debug.isOn("handshake"))
      paramECDHClientKeyExchange.print(System.out); 
    byte[] arrayOfByte = paramECDHClientKeyExchange.getEncodedPoint();
    this.ecdh.checkConstraints(this.algorithmConstraints, arrayOfByte);
    return this.ecdh.getAgreedSecret(arrayOfByte);
  }
  
  private void clientCertificateVerify(HandshakeMessage.CertificateVerify paramCertificateVerify) throws IOException {
    if (debug != null && Debug.isOn("handshake"))
      paramCertificateVerify.print(System.out); 
    if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
      SignatureAndHashAlgorithm signatureAndHashAlgorithm = paramCertificateVerify.getPreferableSignatureAlgorithm();
      if (signatureAndHashAlgorithm == null)
        throw new SSLHandshakeException("Illegal CertificateVerify message"); 
      String str = SignatureAndHashAlgorithm.getHashAlgorithmName(signatureAndHashAlgorithm);
      if (str == null || str.length() == 0)
        throw new SSLHandshakeException("No supported hash algorithm"); 
    } 
    try {
      PublicKey publicKey = this.session.getPeerCertificates()[0].getPublicKey();
      boolean bool = paramCertificateVerify.verify(this.protocolVersion, this.handshakeHash, publicKey, this.session
          .getMasterSecret());
      if (!bool)
        fatalSE((byte)42, "certificate verify message signature error"); 
    } catch (GeneralSecurityException generalSecurityException) {
      fatalSE((byte)42, "certificate verify format error", generalSecurityException);
    } 
    this.needClientVerify = false;
  }
  
  private void clientFinished(HandshakeMessage.Finished paramFinished) throws IOException {
    if (debug != null && Debug.isOn("handshake"))
      paramFinished.print(System.out); 
    if (this.doClientAuth == 2)
      this.session.getPeerPrincipal(); 
    if (this.needClientVerify)
      fatalSE((byte)40, "client did not send certificate verify message"); 
    boolean bool = paramFinished.verify(this.handshakeHash, 1, this.session
        .getMasterSecret());
    if (!bool)
      fatalSE((byte)40, "client 'finished' message doesn't verify"); 
    if (this.secureRenegotiation)
      this.clientVerifyData = paramFinished.getVerifyData(); 
    if (!this.resumingSession) {
      this.input.digestNow();
      sendChangeCipherAndFinish(true);
    } 
    this.session.setLastAccessedTime(System.currentTimeMillis());
    if (!this.resumingSession && this.session.isRejoinable()) {
      ((SSLSessionContextImpl)this.sslContext.engineGetServerSessionContext())
        .put(this.session);
      if (debug != null && Debug.isOn("session"))
        System.out.println("%% Cached server session: " + this.session); 
    } else if (!this.resumingSession && debug != null && 
      Debug.isOn("session")) {
      System.out.println("%% Didn't cache non-resumable server session: " + this.session);
    } 
  }
  
  private void sendChangeCipherAndFinish(boolean paramBoolean) throws IOException {
    this.output.flush();
    HandshakeMessage.Finished finished = new HandshakeMessage.Finished(this.protocolVersion, this.handshakeHash, 2, this.session.getMasterSecret(), this.cipherSuite);
    sendChangeCipherSpec(finished, paramBoolean);
    if (this.secureRenegotiation)
      this.serverVerifyData = finished.getVerifyData(); 
    if (paramBoolean)
      this.state = 20; 
  }
  
  HandshakeMessage getKickstartMessage() {
    return (HandshakeMessage)new HandshakeMessage.HelloRequest();
  }
  
  void handshakeAlert(byte paramByte) throws SSLProtocolException {
    String str = Alerts.alertDescription(paramByte);
    if (debug != null && Debug.isOn("handshake"))
      System.out.println("SSL -- handshake alert:  " + str); 
    if (paramByte == 41 && this.doClientAuth == 1)
      return; 
    throw new SSLProtocolException("handshake alert: " + str);
  }
  
  private SecretKey clientKeyExchange(RSAClientKeyExchange paramRSAClientKeyExchange) throws IOException {
    if (debug != null && Debug.isOn("handshake"))
      paramRSAClientKeyExchange.print(System.out); 
    return paramRSAClientKeyExchange.preMaster;
  }
  
  private void clientCertificate(HandshakeMessage.CertificateMsg paramCertificateMsg) throws IOException {
    if (debug != null && Debug.isOn("handshake"))
      paramCertificateMsg.print(System.out); 
    X509Certificate[] arrayOfX509Certificate = paramCertificateMsg.getCertificateChain();
    if (arrayOfX509Certificate.length == 0) {
      if (this.doClientAuth == 1)
        return; 
      fatalSE((byte)42, "null cert chain");
    } 
    X509TrustManager x509TrustManager = this.sslContext.getX509TrustManager();
    try {
      String str2;
      PublicKey publicKey = arrayOfX509Certificate[0].getPublicKey();
      String str1 = publicKey.getAlgorithm();
      if (str1.equals("RSA")) {
        str2 = "RSA";
      } else if (str1.equals("DSA")) {
        str2 = "DSA";
      } else if (str1.equals("EC")) {
        str2 = "EC";
      } else {
        str2 = "UNKNOWN";
      } 
      if (x509TrustManager instanceof X509ExtendedTrustManager) {
        if (this.conn != null) {
          ((X509ExtendedTrustManager)x509TrustManager).checkClientTrusted((X509Certificate[])arrayOfX509Certificate
              .clone(), str2, this.conn);
        } else {
          ((X509ExtendedTrustManager)x509TrustManager).checkClientTrusted((X509Certificate[])arrayOfX509Certificate
              .clone(), str2, this.engine);
        } 
      } else {
        throw new CertificateException("Improper X509TrustManager implementation");
      } 
    } catch (CertificateException certificateException) {
      fatalSE((byte)46, certificateException);
    } 
    this.needClientVerify = true;
    this.session.setPeerCertificates(arrayOfX509Certificate);
  }
}
