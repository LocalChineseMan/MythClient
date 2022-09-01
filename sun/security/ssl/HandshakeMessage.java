package sun.security.ssl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.security.DigestException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLKeyException;
import javax.net.ssl.SSLProtocolException;
import sun.security.internal.spec.TlsPrfParameterSpec;

public abstract class HandshakeMessage {
  static final byte ht_hello_request = 0;
  
  static final byte ht_client_hello = 1;
  
  static final byte ht_server_hello = 2;
  
  static final byte ht_certificate = 11;
  
  static final byte ht_server_key_exchange = 12;
  
  static final byte ht_certificate_request = 13;
  
  static final byte ht_server_hello_done = 14;
  
  static final byte ht_certificate_verify = 15;
  
  static final byte ht_client_key_exchange = 16;
  
  static final byte ht_finished = 20;
  
  public static final Debug debug = Debug.getInstance("ssl");
  
  static byte[] toByteArray(BigInteger paramBigInteger) {
    byte[] arrayOfByte = paramBigInteger.toByteArray();
    if (arrayOfByte.length > 1 && arrayOfByte[0] == 0) {
      int i = arrayOfByte.length - 1;
      byte[] arrayOfByte1 = new byte[i];
      System.arraycopy(arrayOfByte, 1, arrayOfByte1, 0, i);
      arrayOfByte = arrayOfByte1;
    } 
    return arrayOfByte;
  }
  
  static final byte[] MD5_pad1 = genPad(54, 48);
  
  static final byte[] MD5_pad2 = genPad(92, 48);
  
  static final byte[] SHA_pad1 = genPad(54, 40);
  
  static final byte[] SHA_pad2 = genPad(92, 40);
  
  private static byte[] genPad(int paramInt1, int paramInt2) {
    byte[] arrayOfByte = new byte[paramInt2];
    Arrays.fill(arrayOfByte, (byte)paramInt1);
    return arrayOfByte;
  }
  
  final void write(HandshakeOutStream paramHandshakeOutStream) throws IOException {
    int i = messageLength();
    if (i >= 16777216)
      throw new SSLException("Handshake message too big, type = " + 
          messageType() + ", len = " + i); 
    paramHandshakeOutStream.write(messageType());
    paramHandshakeOutStream.putInt24(i);
    send(paramHandshakeOutStream);
  }
  
  abstract int messageType();
  
  abstract int messageLength();
  
  abstract void send(HandshakeOutStream paramHandshakeOutStream) throws IOException;
  
  abstract void print(PrintStream paramPrintStream) throws IOException;
  
  static final class HandshakeMessage {}
  
  static final class ClientHello extends HandshakeMessage {
    ProtocolVersion protocolVersion;
    
    RandomCookie clnt_random;
    
    SessionId sessionId;
    
    private CipherSuiteList cipherSuites;
    
    byte[] compression_methods;
    
    HelloExtensions extensions = new HelloExtensions();
    
    private static final byte[] NULL_COMPRESSION = new byte[] { 0 };
    
    ClientHello(SecureRandom param1SecureRandom, ProtocolVersion param1ProtocolVersion, SessionId param1SessionId, CipherSuiteList param1CipherSuiteList) {
      this.protocolVersion = param1ProtocolVersion;
      this.sessionId = param1SessionId;
      this.cipherSuites = param1CipherSuiteList;
      if (param1CipherSuiteList.containsEC()) {
        this.extensions.add(SupportedEllipticCurvesExtension.DEFAULT);
        this.extensions.add(SupportedEllipticPointFormatsExtension.DEFAULT);
      } 
      this.clnt_random = new RandomCookie(param1SecureRandom);
      this.compression_methods = NULL_COMPRESSION;
    }
    
    ClientHello(HandshakeInStream param1HandshakeInStream, int param1Int) throws IOException {
      this.protocolVersion = ProtocolVersion.valueOf(param1HandshakeInStream.getInt8(), param1HandshakeInStream.getInt8());
      this.clnt_random = new RandomCookie(param1HandshakeInStream);
      this.sessionId = new SessionId(param1HandshakeInStream.getBytes8());
      this.cipherSuites = new CipherSuiteList(param1HandshakeInStream);
      this.compression_methods = param1HandshakeInStream.getBytes8();
      if (messageLength() != param1Int)
        this.extensions = new HelloExtensions(param1HandshakeInStream); 
    }
    
    CipherSuiteList getCipherSuites() {
      return this.cipherSuites;
    }
    
    void addRenegotiationInfoExtension(byte[] param1ArrayOfbyte) {
      RenegotiationInfoExtension renegotiationInfoExtension = new RenegotiationInfoExtension(param1ArrayOfbyte, new byte[0]);
      this.extensions.add(renegotiationInfoExtension);
    }
    
    void addSNIExtension(List<SNIServerName> param1List) {
      try {
        this.extensions.add(new ServerNameExtension(param1List));
      } catch (IOException iOException) {}
    }
    
    void addSignatureAlgorithmsExtension(Collection<SignatureAndHashAlgorithm> param1Collection) {
      SignatureAlgorithmsExtension signatureAlgorithmsExtension = new SignatureAlgorithmsExtension(param1Collection);
      this.extensions.add(signatureAlgorithmsExtension);
    }
    
    int messageType() {
      return 1;
    }
    
    int messageLength() {
      return 38 + this.sessionId.length() + this.cipherSuites.size() * 2 + this.compression_methods.length + this.extensions.length();
    }
    
    void send(HandshakeOutStream param1HandshakeOutStream) throws IOException {
      param1HandshakeOutStream.putInt8(this.protocolVersion.major);
      param1HandshakeOutStream.putInt8(this.protocolVersion.minor);
      this.clnt_random.send(param1HandshakeOutStream);
      param1HandshakeOutStream.putBytes8(this.sessionId.getId());
      this.cipherSuites.send(param1HandshakeOutStream);
      param1HandshakeOutStream.putBytes8(this.compression_methods);
      this.extensions.send(param1HandshakeOutStream);
    }
    
    void print(PrintStream param1PrintStream) throws IOException {
      param1PrintStream.println("*** ClientHello, " + this.protocolVersion);
      if (debug != null && Debug.isOn("verbose")) {
        param1PrintStream.print("RandomCookie:  ");
        this.clnt_random.print(param1PrintStream);
        param1PrintStream.print("Session ID:  ");
        param1PrintStream.println(this.sessionId);
        param1PrintStream.println("Cipher Suites: " + this.cipherSuites);
        Debug.println(param1PrintStream, "Compression Methods", this.compression_methods);
        this.extensions.print(param1PrintStream);
        param1PrintStream.println("***");
      } 
    }
  }
  
  static final class ServerHello extends HandshakeMessage {
    ProtocolVersion protocolVersion;
    
    RandomCookie svr_random;
    
    SessionId sessionId;
    
    CipherSuite cipherSuite;
    
    byte compression_method;
    
    int messageType() {
      return 2;
    }
    
    HelloExtensions extensions = new HelloExtensions();
    
    ServerHello() {}
    
    ServerHello(HandshakeInStream param1HandshakeInStream, int param1Int) throws IOException {
      this.protocolVersion = ProtocolVersion.valueOf(param1HandshakeInStream.getInt8(), param1HandshakeInStream
          .getInt8());
      this.svr_random = new RandomCookie(param1HandshakeInStream);
      this.sessionId = new SessionId(param1HandshakeInStream.getBytes8());
      this.cipherSuite = CipherSuite.valueOf(param1HandshakeInStream.getInt8(), param1HandshakeInStream.getInt8());
      this.compression_method = (byte)param1HandshakeInStream.getInt8();
      if (messageLength() != param1Int)
        this.extensions = new HelloExtensions(param1HandshakeInStream); 
    }
    
    int messageLength() {
      return 38 + this.sessionId.length() + this.extensions.length();
    }
    
    void send(HandshakeOutStream param1HandshakeOutStream) throws IOException {
      param1HandshakeOutStream.putInt8(this.protocolVersion.major);
      param1HandshakeOutStream.putInt8(this.protocolVersion.minor);
      this.svr_random.send(param1HandshakeOutStream);
      param1HandshakeOutStream.putBytes8(this.sessionId.getId());
      param1HandshakeOutStream.putInt8(this.cipherSuite.id >> 8);
      param1HandshakeOutStream.putInt8(this.cipherSuite.id & 0xFF);
      param1HandshakeOutStream.putInt8(this.compression_method);
      this.extensions.send(param1HandshakeOutStream);
    }
    
    void print(PrintStream param1PrintStream) throws IOException {
      param1PrintStream.println("*** ServerHello, " + this.protocolVersion);
      if (debug != null && Debug.isOn("verbose")) {
        param1PrintStream.print("RandomCookie:  ");
        this.svr_random.print(param1PrintStream);
        param1PrintStream.print("Session ID:  ");
        param1PrintStream.println(this.sessionId);
        param1PrintStream.println("Cipher Suite: " + this.cipherSuite);
        param1PrintStream.println("Compression Method: " + this.compression_method);
        this.extensions.print(param1PrintStream);
        param1PrintStream.println("***");
      } 
    }
  }
  
  static final class CertificateMsg extends HandshakeMessage {
    private X509Certificate[] chain;
    
    private List<byte[]> encodedChain;
    
    private int messageLength;
    
    int messageType() {
      return 11;
    }
    
    CertificateMsg(X509Certificate[] param1ArrayOfX509Certificate) {
      this.chain = param1ArrayOfX509Certificate;
    }
    
    CertificateMsg(HandshakeInStream param1HandshakeInStream) throws IOException {
      int i = param1HandshakeInStream.getInt24();
      ArrayList<Certificate> arrayList = new ArrayList(4);
      CertificateFactory certificateFactory = null;
      while (i > 0) {
        byte[] arrayOfByte = param1HandshakeInStream.getBytes24();
        i -= 3 + arrayOfByte.length;
        try {
          if (certificateFactory == null)
            certificateFactory = CertificateFactory.getInstance("X.509"); 
          arrayList.add(certificateFactory.generateCertificate(new ByteArrayInputStream(arrayOfByte)));
        } catch (CertificateException certificateException) {
          throw (SSLProtocolException)(new SSLProtocolException(certificateException
              .getMessage())).initCause(certificateException);
        } 
      } 
      this.chain = arrayList.<X509Certificate>toArray(new X509Certificate[arrayList.size()]);
    }
    
    int messageLength() {
      if (this.encodedChain == null) {
        this.messageLength = 3;
        this.encodedChain = (List)new ArrayList<>(this.chain.length);
        try {
          for (X509Certificate x509Certificate : this.chain) {
            byte[] arrayOfByte = x509Certificate.getEncoded();
            this.encodedChain.add(arrayOfByte);
            this.messageLength += arrayOfByte.length + 3;
          } 
        } catch (CertificateEncodingException certificateEncodingException) {
          this.encodedChain = null;
          throw new RuntimeException("Could not encode certificates", certificateEncodingException);
        } 
      } 
      return this.messageLength;
    }
    
    void send(HandshakeOutStream param1HandshakeOutStream) throws IOException {
      param1HandshakeOutStream.putInt24(messageLength() - 3);
      for (byte[] arrayOfByte : this.encodedChain)
        param1HandshakeOutStream.putBytes24(arrayOfByte); 
    }
    
    void print(PrintStream param1PrintStream) throws IOException {
      param1PrintStream.println("*** Certificate chain");
      if (debug != null && Debug.isOn("verbose")) {
        for (byte b = 0; b < this.chain.length; b++)
          param1PrintStream.println("chain [" + b + "] = " + this.chain[b]); 
        param1PrintStream.println("***");
      } 
    }
    
    X509Certificate[] getCertificateChain() {
      return (X509Certificate[])this.chain.clone();
    }
  }
  
  static abstract class ServerKeyExchange extends HandshakeMessage {
    int messageType() {
      return 12;
    }
  }
  
  static final class HandshakeMessage {}
  
  static final class HandshakeMessage {}
  
  static final class ECDH_ServerKeyExchange extends ServerKeyExchange {
    private static final int CURVE_EXPLICIT_PRIME = 1;
    
    private static final int CURVE_EXPLICIT_CHAR2 = 2;
    
    private static final int CURVE_NAMED_CURVE = 3;
    
    private int curveId;
    
    private byte[] pointBytes;
    
    private byte[] signatureBytes;
    
    private ECPublicKey publicKey;
    
    ProtocolVersion protocolVersion;
    
    private SignatureAndHashAlgorithm preferableSignatureAlgorithm;
    
    ECDH_ServerKeyExchange(ECDHCrypt param1ECDHCrypt, PrivateKey param1PrivateKey, byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2, SecureRandom param1SecureRandom, SignatureAndHashAlgorithm param1SignatureAndHashAlgorithm, ProtocolVersion param1ProtocolVersion) throws GeneralSecurityException {
      Signature signature;
      this.protocolVersion = param1ProtocolVersion;
      this.publicKey = (ECPublicKey)param1ECDHCrypt.getPublicKey();
      ECParameterSpec eCParameterSpec = this.publicKey.getParams();
      ECPoint eCPoint = this.publicKey.getW();
      this.pointBytes = JsseJce.encodePoint(eCPoint, eCParameterSpec.getCurve());
      this.curveId = SupportedEllipticCurvesExtension.getCurveIndex(eCParameterSpec);
      if (param1PrivateKey == null)
        return; 
      if (param1ProtocolVersion.v >= ProtocolVersion.TLS12.v) {
        this.preferableSignatureAlgorithm = param1SignatureAndHashAlgorithm;
        signature = JsseJce.getSignature(param1SignatureAndHashAlgorithm.getAlgorithmName());
      } else {
        signature = getSignature(param1PrivateKey.getAlgorithm());
      } 
      signature.initSign(param1PrivateKey);
      updateSignature(signature, param1ArrayOfbyte1, param1ArrayOfbyte2);
      this.signatureBytes = signature.sign();
    }
    
    ECDH_ServerKeyExchange(HandshakeInStream param1HandshakeInStream, PublicKey param1PublicKey, byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2, Collection<SignatureAndHashAlgorithm> param1Collection, ProtocolVersion param1ProtocolVersion) throws IOException, GeneralSecurityException {
      ECParameterSpec eCParameterSpec;
      Signature signature;
      this.protocolVersion = param1ProtocolVersion;
      int i = param1HandshakeInStream.getInt8();
      if (i == 3) {
        this.curveId = param1HandshakeInStream.getInt16();
        if (!SupportedEllipticCurvesExtension.isSupported(this.curveId))
          throw new SSLHandshakeException("Unsupported curveId: " + this.curveId); 
        String str = SupportedEllipticCurvesExtension.getCurveOid(this.curveId);
        if (str == null)
          throw new SSLHandshakeException("Unknown named curve: " + this.curveId); 
        eCParameterSpec = JsseJce.getECParameterSpec(str);
        if (eCParameterSpec == null)
          throw new SSLHandshakeException("Unsupported curve: " + str); 
      } else {
        throw new SSLHandshakeException("Unsupported ECCurveType: " + i);
      } 
      this.pointBytes = param1HandshakeInStream.getBytes8();
      ECPoint eCPoint = JsseJce.decodePoint(this.pointBytes, eCParameterSpec.getCurve());
      KeyFactory keyFactory = JsseJce.getKeyFactory("EC");
      this.publicKey = (ECPublicKey)keyFactory.generatePublic(new ECPublicKeySpec(eCPoint, eCParameterSpec));
      if (param1PublicKey == null)
        return; 
      if (param1ProtocolVersion.v >= ProtocolVersion.TLS12.v) {
        int j = param1HandshakeInStream.getInt8();
        int k = param1HandshakeInStream.getInt8();
        this
          .preferableSignatureAlgorithm = SignatureAndHashAlgorithm.valueOf(j, k, 0);
        if (!param1Collection.contains(this.preferableSignatureAlgorithm))
          throw new SSLHandshakeException("Unsupported SignatureAndHashAlgorithm in ServerKeyExchange message"); 
      } 
      this.signatureBytes = param1HandshakeInStream.getBytes16();
      if (param1ProtocolVersion.v >= ProtocolVersion.TLS12.v) {
        signature = JsseJce.getSignature(this.preferableSignatureAlgorithm
            .getAlgorithmName());
      } else {
        signature = getSignature(param1PublicKey.getAlgorithm());
      } 
      signature.initVerify(param1PublicKey);
      updateSignature(signature, param1ArrayOfbyte1, param1ArrayOfbyte2);
      if (!signature.verify(this.signatureBytes))
        throw new SSLKeyException("Invalid signature on ECDH server key exchange message"); 
    }
    
    ECPublicKey getPublicKey() {
      return this.publicKey;
    }
    
    private static Signature getSignature(String param1String) throws NoSuchAlgorithmException {
      switch (param1String) {
        case "EC":
          return JsseJce.getSignature("SHA1withECDSA");
        case "RSA":
          return RSASignature.getInstance();
      } 
      throw new NoSuchAlgorithmException("neither an RSA or a EC key");
    }
    
    private void updateSignature(Signature param1Signature, byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2) throws SignatureException {
      param1Signature.update(param1ArrayOfbyte1);
      param1Signature.update(param1ArrayOfbyte2);
      param1Signature.update((byte)3);
      param1Signature.update((byte)(this.curveId >> 8));
      param1Signature.update((byte)this.curveId);
      param1Signature.update((byte)this.pointBytes.length);
      param1Signature.update(this.pointBytes);
    }
    
    int messageLength() {
      int i = 0;
      if (this.signatureBytes != null) {
        i = 2 + this.signatureBytes.length;
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v)
          i += SignatureAndHashAlgorithm.sizeInRecord(); 
      } 
      return 4 + this.pointBytes.length + i;
    }
    
    void send(HandshakeOutStream param1HandshakeOutStream) throws IOException {
      param1HandshakeOutStream.putInt8(3);
      param1HandshakeOutStream.putInt16(this.curveId);
      param1HandshakeOutStream.putBytes8(this.pointBytes);
      if (this.signatureBytes != null) {
        if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
          param1HandshakeOutStream.putInt8(this.preferableSignatureAlgorithm.getHashValue());
          param1HandshakeOutStream.putInt8(this.preferableSignatureAlgorithm.getSignatureValue());
        } 
        param1HandshakeOutStream.putBytes16(this.signatureBytes);
      } 
    }
    
    void print(PrintStream param1PrintStream) throws IOException {
      param1PrintStream.println("*** ECDH ServerKeyExchange");
      if (debug != null && Debug.isOn("verbose")) {
        if (this.signatureBytes == null) {
          param1PrintStream.println("Anonymous");
        } else if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
          param1PrintStream.println("Signature Algorithm " + this.preferableSignatureAlgorithm
              .getAlgorithmName());
        } 
        param1PrintStream.println("Server key: " + this.publicKey);
      } 
    }
  }
  
  static final class HandshakeMessage {}
  
  static final class HandshakeMessage {}
  
  static final class ServerHelloDone extends HandshakeMessage {
    int messageType() {
      return 14;
    }
    
    ServerHelloDone() {}
    
    ServerHelloDone(HandshakeInStream param1HandshakeInStream) {}
    
    int messageLength() {
      return 0;
    }
    
    void send(HandshakeOutStream param1HandshakeOutStream) throws IOException {}
    
    void print(PrintStream param1PrintStream) throws IOException {
      param1PrintStream.println("*** ServerHelloDone");
    }
  }
  
  static final class HandshakeMessage {}
  
  static final class Finished extends HandshakeMessage {
    static final int CLIENT = 1;
    
    static final int SERVER = 2;
    
    private static final byte[] SSL_CLIENT = new byte[] { 67, 76, 78, 84 };
    
    private static final byte[] SSL_SERVER = new byte[] { 83, 82, 86, 82 };
    
    private byte[] verifyData;
    
    private ProtocolVersion protocolVersion;
    
    private CipherSuite cipherSuite;
    
    Finished(ProtocolVersion param1ProtocolVersion, HandshakeHash param1HandshakeHash, int param1Int, SecretKey param1SecretKey, CipherSuite param1CipherSuite) {
      this.protocolVersion = param1ProtocolVersion;
      this.cipherSuite = param1CipherSuite;
      this.verifyData = getFinished(param1HandshakeHash, param1Int, param1SecretKey);
    }
    
    Finished(ProtocolVersion param1ProtocolVersion, HandshakeInStream param1HandshakeInStream, CipherSuite param1CipherSuite) throws IOException {
      this.protocolVersion = param1ProtocolVersion;
      this.cipherSuite = param1CipherSuite;
      byte b = (param1ProtocolVersion.v >= ProtocolVersion.TLS10.v) ? 12 : 36;
      this.verifyData = new byte[b];
      param1HandshakeInStream.read(this.verifyData);
    }
    
    boolean verify(HandshakeHash param1HandshakeHash, int param1Int, SecretKey param1SecretKey) {
      byte[] arrayOfByte = getFinished(param1HandshakeHash, param1Int, param1SecretKey);
      return MessageDigest.isEqual(arrayOfByte, this.verifyData);
    }
    
    private byte[] getFinished(HandshakeHash param1HandshakeHash, int param1Int, SecretKey param1SecretKey) {
      byte[] arrayOfByte1;
      String str;
      if (param1Int == 1) {
        arrayOfByte1 = SSL_CLIENT;
        str = "client finished";
      } else if (param1Int == 2) {
        arrayOfByte1 = SSL_SERVER;
        str = "server finished";
      } else {
        throw new RuntimeException("Invalid sender: " + param1Int);
      } 
      if (this.protocolVersion.v >= ProtocolVersion.TLS10.v)
        try {
          byte[] arrayOfByte;
          String str1;
          CipherSuite.PRF pRF;
          if (this.protocolVersion.v >= ProtocolVersion.TLS12.v) {
            arrayOfByte = param1HandshakeHash.getFinishedHash();
            str1 = "SunTls12Prf";
            pRF = this.cipherSuite.prfAlg;
          } else {
            MessageDigest messageDigest3 = param1HandshakeHash.getMD5Clone();
            MessageDigest messageDigest4 = param1HandshakeHash.getSHAClone();
            arrayOfByte = new byte[36];
            messageDigest3.digest(arrayOfByte, 0, 16);
            messageDigest4.digest(arrayOfByte, 16, 20);
            str1 = "SunTlsPrf";
            pRF = CipherSuite.PRF.P_NONE;
          } 
          String str2 = pRF.getPRFHashAlg();
          int i = pRF.getPRFHashLength();
          int j = pRF.getPRFBlockSize();
          TlsPrfParameterSpec tlsPrfParameterSpec = new TlsPrfParameterSpec(param1SecretKey, str, arrayOfByte, 12, str2, i, j);
          KeyGenerator keyGenerator = JsseJce.getKeyGenerator(str1);
          keyGenerator.init(tlsPrfParameterSpec);
          SecretKey secretKey = keyGenerator.generateKey();
          if (!"RAW".equals(secretKey.getFormat()))
            throw new ProviderException("Invalid PRF output, format must be RAW"); 
          return secretKey.getEncoded();
        } catch (GeneralSecurityException generalSecurityException) {
          throw new RuntimeException("PRF failed", generalSecurityException);
        }  
      MessageDigest messageDigest1 = param1HandshakeHash.getMD5Clone();
      MessageDigest messageDigest2 = param1HandshakeHash.getSHAClone();
      updateDigest(messageDigest1, arrayOfByte1, MD5_pad1, MD5_pad2, param1SecretKey);
      updateDigest(messageDigest2, arrayOfByte1, SHA_pad1, SHA_pad2, param1SecretKey);
      byte[] arrayOfByte2 = new byte[36];
      try {
        messageDigest1.digest(arrayOfByte2, 0, 16);
        messageDigest2.digest(arrayOfByte2, 16, 20);
      } catch (DigestException digestException) {
        throw new RuntimeException("Digest failed", digestException);
      } 
      return arrayOfByte2;
    }
    
    private static void updateDigest(MessageDigest param1MessageDigest, byte[] param1ArrayOfbyte1, byte[] param1ArrayOfbyte2, byte[] param1ArrayOfbyte3, SecretKey param1SecretKey) {
      param1MessageDigest.update(param1ArrayOfbyte1);
      HandshakeMessage.CertificateVerify.access$000(param1MessageDigest, param1ArrayOfbyte2, param1ArrayOfbyte3, param1SecretKey);
    }
    
    byte[] getVerifyData() {
      return this.verifyData;
    }
    
    int messageType() {
      return 20;
    }
    
    int messageLength() {
      return this.verifyData.length;
    }
    
    void send(HandshakeOutStream param1HandshakeOutStream) throws IOException {
      param1HandshakeOutStream.write(this.verifyData);
    }
    
    void print(PrintStream param1PrintStream) throws IOException {
      param1PrintStream.println("*** Finished");
      if (debug != null && Debug.isOn("verbose")) {
        Debug.println(param1PrintStream, "verify_data", this.verifyData);
        param1PrintStream.println("***");
      } 
    }
  }
}
