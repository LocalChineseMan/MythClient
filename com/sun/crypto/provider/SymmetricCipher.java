package com.sun.crypto.provider;

import java.security.InvalidKeyException;

abstract class SymmetricCipher {
  abstract int getBlockSize();
  
  abstract void init(boolean paramBoolean, String paramString, byte[] paramArrayOfbyte) throws InvalidKeyException;
  
  abstract void encryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2);
  
  abstract void decryptBlock(byte[] paramArrayOfbyte1, int paramInt1, byte[] paramArrayOfbyte2, int paramInt2);
}
