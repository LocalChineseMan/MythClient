package com.sun.crypto.provider;

import javax.crypto.ShortBufferException;

interface Padding {
  void padWithLen(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws ShortBufferException;
  
  int unpad(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  int padLength(int paramInt);
}
