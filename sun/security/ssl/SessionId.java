package sun.security.ssl;

import java.security.SecureRandom;

final class SessionId {
  private byte[] sessionId;
  
  SessionId(boolean paramBoolean, SecureRandom paramSecureRandom) {
    if (paramBoolean) {
      this.sessionId = (new RandomCookie(paramSecureRandom)).random_bytes;
    } else {
      this.sessionId = new byte[0];
    } 
  }
  
  SessionId(byte[] paramArrayOfbyte) {
    this.sessionId = paramArrayOfbyte;
  }
  
  int length() {
    return this.sessionId.length;
  }
  
  byte[] getId() {
    return (byte[])this.sessionId.clone();
  }
  
  public String toString() {
    int i = this.sessionId.length;
    StringBuffer stringBuffer = new StringBuffer(10 + 2 * i);
    stringBuffer.append("{");
    for (byte b = 0; b < i; b++) {
      stringBuffer.append(0xFF & this.sessionId[b]);
      if (b != i - 1)
        stringBuffer.append(", "); 
    } 
    stringBuffer.append("}");
    return stringBuffer.toString();
  }
  
  public int hashCode() {
    int i = 0;
    for (byte b = 0; b < this.sessionId.length; b++)
      i += this.sessionId[b]; 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof SessionId))
      return false; 
    SessionId sessionId = (SessionId)paramObject;
    byte[] arrayOfByte = sessionId.getId();
    if (arrayOfByte.length != this.sessionId.length)
      return false; 
    for (byte b = 0; b < this.sessionId.length; b++) {
      if (arrayOfByte[b] != this.sessionId[b])
        return false; 
    } 
    return true;
  }
}
