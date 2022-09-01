package com.sun.crypto.provider;

import java.security.InvalidKeyException;

final class ElectronicCodeBook extends FeedbackCipher {
  ElectronicCodeBook(SymmetricCipher paramSymmetricCipher) {
    super(paramSymmetricCipher);
  }
  
  String getFeedback() {
    return "ECB";
  }
  
  void reset() {}
  
  void save() {}
  
  void restore() {}
  
  void init(boolean paramBoolean, String paramString, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws InvalidKeyException {
    if (paramArrayOfbyte1 == null || paramArrayOfbyte2 != null)
      throw new InvalidKeyException("Internal error"); 
    this.embeddedCipher.init(paramBoolean, paramString, paramArrayOfbyte1);
  }
  
  int encrypt(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    int i;
    for (i = paramInt2; i >= this.blockSize; i -= this.blockSize) {
      this.embeddedCipher.encryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt3);
      paramInt1 += this.blockSize;
      paramInt3 += this.blockSize;
    } 
    return paramInt2;
  }
  
  int decrypt(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) {
    int i;
    for (i = paramInt2; i >= this.blockSize; i -= this.blockSize) {
      this.embeddedCipher.decryptBlock(paramArrayOfbyte1, paramInt1, paramArrayOfbyte2, paramInt3);
      paramInt1 += this.blockSize;
      paramInt3 += this.blockSize;
    } 
    return paramInt2;
  }
}
