package sun.security.ssl;

import java.security.AlgorithmConstraints;
import java.security.CryptoPrimitive;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.EnumSet;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLHandshakeException;

final class ECDHCrypt {
  private PrivateKey privateKey;
  
  private ECPublicKey publicKey;
  
  ECDHCrypt(PrivateKey paramPrivateKey, PublicKey paramPublicKey) {
    this.privateKey = paramPrivateKey;
    this.publicKey = (ECPublicKey)paramPublicKey;
  }
  
  ECDHCrypt(String paramString, SecureRandom paramSecureRandom) {
    try {
      KeyPairGenerator keyPairGenerator = JsseJce.getKeyPairGenerator("EC");
      ECGenParameterSpec eCGenParameterSpec = new ECGenParameterSpec(paramString);
      keyPairGenerator.initialize(eCGenParameterSpec, paramSecureRandom);
      KeyPair keyPair = keyPairGenerator.generateKeyPair();
      this.privateKey = keyPair.getPrivate();
      this.publicKey = (ECPublicKey)keyPair.getPublic();
    } catch (GeneralSecurityException generalSecurityException) {
      throw new RuntimeException("Could not generate DH keypair", generalSecurityException);
    } 
  }
  
  ECDHCrypt(ECParameterSpec paramECParameterSpec, SecureRandom paramSecureRandom) {
    try {
      KeyPairGenerator keyPairGenerator = JsseJce.getKeyPairGenerator("EC");
      keyPairGenerator.initialize(paramECParameterSpec, paramSecureRandom);
      KeyPair keyPair = keyPairGenerator.generateKeyPair();
      this.privateKey = keyPair.getPrivate();
      this.publicKey = (ECPublicKey)keyPair.getPublic();
    } catch (GeneralSecurityException generalSecurityException) {
      throw new RuntimeException("Could not generate DH keypair", generalSecurityException);
    } 
  }
  
  PublicKey getPublicKey() {
    return this.publicKey;
  }
  
  SecretKey getAgreedSecret(PublicKey paramPublicKey) throws SSLHandshakeException {
    try {
      KeyAgreement keyAgreement = JsseJce.getKeyAgreement("ECDH");
      keyAgreement.init(this.privateKey);
      keyAgreement.doPhase(paramPublicKey, true);
      return keyAgreement.generateSecret("TlsPremasterSecret");
    } catch (GeneralSecurityException generalSecurityException) {
      throw (SSLHandshakeException)(new SSLHandshakeException("Could not generate secret"))
        .initCause(generalSecurityException);
    } 
  }
  
  SecretKey getAgreedSecret(byte[] paramArrayOfbyte) throws SSLHandshakeException {
    try {
      ECParameterSpec eCParameterSpec = this.publicKey.getParams();
      ECPoint eCPoint = JsseJce.decodePoint(paramArrayOfbyte, eCParameterSpec.getCurve());
      KeyFactory keyFactory = JsseJce.getKeyFactory("EC");
      ECPublicKeySpec eCPublicKeySpec = new ECPublicKeySpec(eCPoint, eCParameterSpec);
      PublicKey publicKey = keyFactory.generatePublic(eCPublicKeySpec);
      return getAgreedSecret(publicKey);
    } catch (GeneralSecurityException|java.io.IOException generalSecurityException) {
      throw (SSLHandshakeException)(new SSLHandshakeException("Could not generate secret"))
        .initCause(generalSecurityException);
    } 
  }
  
  void checkConstraints(AlgorithmConstraints paramAlgorithmConstraints, byte[] paramArrayOfbyte) throws SSLHandshakeException {
    try {
      ECParameterSpec eCParameterSpec = this.publicKey.getParams();
      ECPoint eCPoint = JsseJce.decodePoint(paramArrayOfbyte, eCParameterSpec.getCurve());
      ECPublicKeySpec eCPublicKeySpec = new ECPublicKeySpec(eCPoint, eCParameterSpec);
      KeyFactory keyFactory = JsseJce.getKeyFactory("EC");
      ECPublicKey eCPublicKey = (ECPublicKey)keyFactory.generatePublic(eCPublicKeySpec);
      if (!paramAlgorithmConstraints.permits(
          EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), eCPublicKey))
        throw new SSLHandshakeException("ECPublicKey does not comply to algorithm constraints"); 
    } catch (GeneralSecurityException|java.io.IOException generalSecurityException) {
      throw (SSLHandshakeException)(new SSLHandshakeException("Could not generate ECPublicKey"))
        .initCause(generalSecurityException);
    } 
  }
}
