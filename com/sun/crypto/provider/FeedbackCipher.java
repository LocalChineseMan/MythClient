package com.sun.crypto.provider;

import java.security.InvalidKeyException;
import javax.crypto.AEADBadTagException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

abstract class FeedbackCipher {
  final SymmetricCipher embeddedCipher;
  
  final int blockSize;
  
  byte[] iv;
  
  FeedbackCipher(SymmetricCipher paramSymmetricCipher) {
    this.embeddedCipher = paramSymmetricCipher;
    this.blockSize = paramSymmetricCipher.getBlockSize();
  }
  
  final SymmetricCipher getEmbeddedCipher() {
    return this.embeddedCipher;
  }
  
  final int getBlockSize() {
    return this.blockSize;
  }
  
  abstract String getFeedback();
  
  abstract void save();
  
  abstract void restore();
  
  abstract void init(boolean paramBoolean, String paramString, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws InvalidKeyException;
  
  final byte[] getIV() {
    return this.iv;
  }
  
  abstract void reset();
  
  abstract int encrypt(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3);
  
  int encryptFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws IllegalBlockSizeException, ShortBufferException {
    return encrypt(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3);
  }
  
  abstract int decrypt(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3);
  
  int decryptFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws IllegalBlockSizeException, AEADBadTagException, ShortBufferException {
    return decrypt(paramArrayOfbyte1, paramInt1, paramInt2, paramArrayOfbyte2, paramInt3);
  }
  
  void updateAAD(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    throw new IllegalStateException("No AAD accepted");
  }
  
  int getBufferedLength() {
    return 0;
  }
}
