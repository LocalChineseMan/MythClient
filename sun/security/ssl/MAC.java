package sun.security.ssl;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

final class MAC extends Authenticator {
  static final MAC NULL = new MAC();
  
  private static final byte[] nullMAC = new byte[0];
  
  private final CipherSuite.MacAlg macAlg;
  
  private final Mac mac;
  
  private MAC() {
    this.macAlg = CipherSuite.M_NULL;
    this.mac = null;
  }
  
  MAC(CipherSuite.MacAlg paramMacAlg, ProtocolVersion paramProtocolVersion, SecretKey paramSecretKey) throws NoSuchAlgorithmException, InvalidKeyException {
    super(paramProtocolVersion);
    String str;
    this.macAlg = paramMacAlg;
    boolean bool = (paramProtocolVersion.v >= ProtocolVersion.TLS10.v) ? true : false;
    if (paramMacAlg == CipherSuite.M_MD5) {
      str = bool ? "HmacMD5" : "SslMacMD5";
    } else if (paramMacAlg == CipherSuite.M_SHA) {
      str = bool ? "HmacSHA1" : "SslMacSHA1";
    } else if (paramMacAlg == CipherSuite.M_SHA256) {
      str = "HmacSHA256";
    } else if (paramMacAlg == CipherSuite.M_SHA384) {
      str = "HmacSHA384";
    } else {
      throw new RuntimeException("Unknown Mac " + paramMacAlg);
    } 
    this.mac = JsseJce.getMac(str);
    this.mac.init(paramSecretKey);
  }
  
  int MAClen() {
    return this.macAlg.size;
  }
  
  int hashBlockLen() {
    return this.macAlg.hashBlockSize;
  }
  
  int minimalPaddingLen() {
    return this.macAlg.minimalPaddingSize;
  }
  
  final byte[] compute(byte paramByte, byte[] paramArrayOfbyte, int paramInt1, int paramInt2, boolean paramBoolean) {
    if (this.macAlg.size == 0)
      return nullMAC; 
    if (!paramBoolean) {
      byte[] arrayOfByte = acquireAuthenticationBytes(paramByte, paramInt2);
      this.mac.update(arrayOfByte);
    } 
    this.mac.update(paramArrayOfbyte, paramInt1, paramInt2);
    return this.mac.doFinal();
  }
  
  final byte[] compute(byte paramByte, ByteBuffer paramByteBuffer, boolean paramBoolean) {
    if (this.macAlg.size == 0)
      return nullMAC; 
    if (!paramBoolean) {
      byte[] arrayOfByte = acquireAuthenticationBytes(paramByte, paramByteBuffer.remaining());
      this.mac.update(arrayOfByte);
    } 
    this.mac.update(paramByteBuffer);
    return this.mac.doFinal();
  }
}
