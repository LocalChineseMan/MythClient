package sun.security.ssl;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

final class HandshakeHash {
  private int version = -1;
  
  private ByteArrayOutputStream data = new ByteArrayOutputStream();
  
  private MessageDigest md5;
  
  private MessageDigest sha;
  
  private final int clonesNeeded;
  
  private MessageDigest finMD;
  
  HandshakeHash(boolean paramBoolean) {
    this.clonesNeeded = paramBoolean ? 3 : 2;
  }
  
  void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    switch (this.version) {
      case 1:
        this.md5.update(paramArrayOfbyte, paramInt1, paramInt2);
        this.sha.update(paramArrayOfbyte, paramInt1, paramInt2);
        return;
    } 
    if (this.finMD != null)
      this.finMD.update(paramArrayOfbyte, paramInt1, paramInt2); 
    this.data.write(paramArrayOfbyte, paramInt1, paramInt2);
  }
  
  void reset() {
    if (this.version != -1)
      throw new RuntimeException("reset() can be only be called before protocolDetermined"); 
    this.data.reset();
  }
  
  void protocolDetermined(ProtocolVersion paramProtocolVersion) {
    byte[] arrayOfByte;
    if (this.version != -1)
      return; 
    this.version = (paramProtocolVersion.compareTo(ProtocolVersion.TLS12) >= 0) ? 2 : 1;
    switch (this.version) {
      case 1:
        try {
          this.md5 = CloneableDigest.getDigest("MD5", this.clonesNeeded);
          this.sha = CloneableDigest.getDigest("SHA", this.clonesNeeded);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
          throw new RuntimeException("Algorithm MD5 or SHA not available", noSuchAlgorithmException);
        } 
        arrayOfByte = this.data.toByteArray();
        update(arrayOfByte, 0, arrayOfByte.length);
        break;
    } 
  }
  
  MessageDigest getMD5Clone() {
    if (this.version != 1)
      throw new RuntimeException("getMD5Clone() can be only be called for TLS 1.1"); 
    return cloneDigest(this.md5);
  }
  
  MessageDigest getSHAClone() {
    if (this.version != 1)
      throw new RuntimeException("getSHAClone() can be only be called for TLS 1.1"); 
    return cloneDigest(this.sha);
  }
  
  private static MessageDigest cloneDigest(MessageDigest paramMessageDigest) {
    try {
      return (MessageDigest)paramMessageDigest.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new RuntimeException("Could not clone digest", cloneNotSupportedException);
    } 
  }
  
  private static String normalizeAlgName(String paramString) {
    paramString = paramString.toUpperCase(Locale.US);
    if (paramString.startsWith("SHA")) {
      if (paramString.length() == 3)
        return "SHA-1"; 
      if (paramString.charAt(3) != '-')
        return "SHA-" + paramString.substring(3); 
    } 
    return paramString;
  }
  
  void setFinishedAlg(String paramString) {
    if (paramString == null)
      throw new RuntimeException("setFinishedAlg's argument cannot be null"); 
    if (this.finMD != null)
      return; 
    try {
      this.finMD = CloneableDigest.getDigest(normalizeAlgName(paramString), 2);
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new Error(noSuchAlgorithmException);
    } 
    this.finMD.update(this.data.toByteArray());
  }
  
  byte[] getAllHandshakeMessages() {
    return this.data.toByteArray();
  }
  
  byte[] getFinishedHash() {
    try {
      return cloneDigest(this.finMD).digest();
    } catch (Exception exception) {
      throw new Error("BAD");
    } 
  }
}
