package sun.security.ssl;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class CloneableDigest extends MessageDigest implements Cloneable {
  private final MessageDigest[] digests;
  
  private CloneableDigest(MessageDigest paramMessageDigest, int paramInt, String paramString) throws NoSuchAlgorithmException {
    super(paramString);
    this.digests = new MessageDigest[paramInt];
    this.digests[0] = paramMessageDigest;
    for (byte b = 1; b < paramInt; b++)
      this.digests[b] = JsseJce.getMessageDigest(paramString); 
  }
  
  static MessageDigest getDigest(String paramString, int paramInt) throws NoSuchAlgorithmException {
    MessageDigest messageDigest = JsseJce.getMessageDigest(paramString);
    try {
      messageDigest.clone();
      return messageDigest;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      return new CloneableDigest(messageDigest, paramInt, paramString);
    } 
  }
  
  private void checkState() {}
  
  protected int engineGetDigestLength() {
    checkState();
    return this.digests[0].getDigestLength();
  }
  
  protected void engineUpdate(byte paramByte) {
    checkState();
    for (byte b = 0; b < this.digests.length && this.digests[b] != null; b++)
      this.digests[b].update(paramByte); 
  }
  
  protected void engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    checkState();
    for (byte b = 0; b < this.digests.length && this.digests[b] != null; b++)
      this.digests[b].update(paramArrayOfbyte, paramInt1, paramInt2); 
  }
  
  protected byte[] engineDigest() {
    checkState();
    byte[] arrayOfByte = this.digests[0].digest();
    digestReset();
    return arrayOfByte;
  }
  
  protected int engineDigest(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws DigestException {
    checkState();
    int i = this.digests[0].digest(paramArrayOfbyte, paramInt1, paramInt2);
    digestReset();
    return i;
  }
  
  private void digestReset() {
    for (byte b = 1; b < this.digests.length && this.digests[b] != null; b++)
      this.digests[b].reset(); 
  }
  
  protected void engineReset() {
    checkState();
    for (byte b = 0; b < this.digests.length && this.digests[b] != null; b++)
      this.digests[b].reset(); 
  }
  
  public Object clone() {
    checkState();
    for (int i = this.digests.length - 1; i >= 0; i--) {
      if (this.digests[i] != null) {
        MessageDigest messageDigest = this.digests[i];
        this.digests[i] = null;
        return messageDigest;
      } 
    } 
    throw new InternalError();
  }
}
