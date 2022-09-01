package com.sun.crypto.provider;

import javax.crypto.ShortBufferException;

final class PKCS5Padding implements Padding {
  private int blockSize;
  
  PKCS5Padding(int paramInt) {
    this.blockSize = paramInt;
  }
  
  public void padWithLen(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws ShortBufferException {
    if (paramArrayOfbyte == null)
      return; 
    if (paramInt1 + paramInt2 > paramArrayOfbyte.length)
      throw new ShortBufferException("Buffer too small to hold padding"); 
    byte b = (byte)(paramInt2 & 0xFF);
    for (byte b1 = 0; b1 < paramInt2; b1++)
      paramArrayOfbyte[b1 + paramInt1] = b; 
  }
  
  public int unpad(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramArrayOfbyte == null || paramInt2 == 0)
      return 0; 
    byte b = paramArrayOfbyte[paramInt1 + paramInt2 - 1];
    int i = b & 0xFF;
    if (i < 1 || i > this.blockSize)
      return -1; 
    int j = paramInt1 + paramInt2 - (b & 0xFF);
    if (j < paramInt1)
      return -1; 
    for (byte b1 = 0; b1 < (b & 0xFF); b1++) {
      if (paramArrayOfbyte[j + b1] != b)
        return -1; 
    } 
    return j;
  }
  
  public int padLength(int paramInt) {
    return this.blockSize - paramInt % this.blockSize;
  }
}
