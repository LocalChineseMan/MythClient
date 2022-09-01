package com.sun.crypto.provider;

import java.security.ProviderException;
import java.util.Arrays;

final class GHASH {
  private static final byte P128 = -31;
  
  private final byte[] subkeyH;
  
  private byte[] state;
  
  private static boolean getBit(byte[] paramArrayOfbyte, int paramInt) {
    int i = paramInt / 8;
    paramInt %= 8;
    int j = paramArrayOfbyte[i] >>> 7 - paramInt & 0x1;
    return (j != 0);
  }
  
  private static void shift(byte[] paramArrayOfbyte) {
    byte b = 0;
    for (byte b1 = 0; b1 < paramArrayOfbyte.length; b1++) {
      byte b2 = (byte)((paramArrayOfbyte[b1] & 0x1) << 7);
      paramArrayOfbyte[b1] = (byte)((paramArrayOfbyte[b1] & 0xFF) >>> 1);
      paramArrayOfbyte[b1] = (byte)(paramArrayOfbyte[b1] | b);
      b = b2;
    } 
  }
  
  private static byte[] blockMult(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (paramArrayOfbyte1.length != 16 || paramArrayOfbyte2.length != 16)
      throw new RuntimeException("illegal input sizes"); 
    byte[] arrayOfByte1 = new byte[16];
    byte[] arrayOfByte2 = (byte[])paramArrayOfbyte2.clone();
    byte b;
    for (b = 0; b < 127; b++) {
      if (getBit(paramArrayOfbyte1, b))
        for (byte b1 = 0; b1 < arrayOfByte1.length; b1++)
          arrayOfByte1[b1] = (byte)(arrayOfByte1[b1] ^ arrayOfByte2[b1]);  
      boolean bool = getBit(arrayOfByte2, 127);
      shift(arrayOfByte2);
      if (bool)
        arrayOfByte2[0] = (byte)(arrayOfByte2[0] ^ 0xFFFFFFE1); 
    } 
    if (getBit(paramArrayOfbyte1, 127))
      for (b = 0; b < arrayOfByte1.length; b++)
        arrayOfByte1[b] = (byte)(arrayOfByte1[b] ^ arrayOfByte2[b]);  
    return arrayOfByte1;
  }
  
  private byte[] stateSave = null;
  
  GHASH(byte[] paramArrayOfbyte) throws ProviderException {
    if (paramArrayOfbyte == null || paramArrayOfbyte.length != 16)
      throw new ProviderException("Internal error"); 
    this.subkeyH = paramArrayOfbyte;
    this.state = new byte[16];
  }
  
  void reset() {
    Arrays.fill(this.state, (byte)0);
  }
  
  void save() {
    this.stateSave = (byte[])this.state.clone();
  }
  
  void restore() {
    this.state = this.stateSave;
  }
  
  private void processBlock(byte[] paramArrayOfbyte, int paramInt) {
    if (paramArrayOfbyte.length - paramInt < 16)
      throw new RuntimeException("need complete block"); 
    for (byte b = 0; b < this.state.length; b++)
      this.state[b] = (byte)(this.state[b] ^ paramArrayOfbyte[paramInt + b]); 
    this.state = blockMult(this.state, this.subkeyH);
  }
  
  void update(byte[] paramArrayOfbyte) {
    update(paramArrayOfbyte, 0, paramArrayOfbyte.length);
  }
  
  void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    if (paramInt2 - paramInt1 > paramArrayOfbyte.length)
      throw new RuntimeException("input length out of bound"); 
    if (paramInt2 % 16 != 0)
      throw new RuntimeException("input length unsupported"); 
    for (int i = paramInt1; i < paramInt1 + paramInt2; i += 16)
      processBlock(paramArrayOfbyte, i); 
  }
  
  byte[] digest() {
    try {
      return (byte[])this.state.clone();
    } finally {
      reset();
    } 
  }
}
