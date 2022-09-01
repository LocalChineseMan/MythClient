package javax.crypto;

import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

public abstract class CipherSpi {
  protected abstract void engineSetMode(String paramString) throws NoSuchAlgorithmException;
  
  protected abstract void engineSetPadding(String paramString) throws NoSuchPaddingException;
  
  protected abstract int engineGetBlockSize();
  
  protected abstract int engineGetOutputSize(int paramInt);
  
  protected abstract byte[] engineGetIV();
  
  protected abstract AlgorithmParameters engineGetParameters();
  
  protected abstract void engineInit(int paramInt, Key paramKey, SecureRandom paramSecureRandom) throws InvalidKeyException;
  
  protected abstract void engineInit(int paramInt, Key paramKey, AlgorithmParameterSpec paramAlgorithmParameterSpec, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException;
  
  protected abstract void engineInit(int paramInt, Key paramKey, AlgorithmParameters paramAlgorithmParameters, SecureRandom paramSecureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException;
  
  protected abstract byte[] engineUpdate(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  protected abstract int engineUpdate(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException;
  
  protected int engineUpdate(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2) throws ShortBufferException {
    try {
      return bufferCrypt(paramByteBuffer1, paramByteBuffer2, true);
    } catch (IllegalBlockSizeException illegalBlockSizeException) {
      throw new ProviderException("Internal error in update()");
    } catch (BadPaddingException badPaddingException) {
      throw new ProviderException("Internal error in update()");
    } 
  }
  
  protected abstract byte[] engineDoFinal(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IllegalBlockSizeException, BadPaddingException;
  
  protected abstract int engineDoFinal(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException;
  
  protected int engineDoFinal(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
    return bufferCrypt(paramByteBuffer1, paramByteBuffer2, false);
  }
  
  static int getTempArraySize(int paramInt) {
    return Math.min(4096, paramInt);
  }
  
  private int bufferCrypt(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2, boolean paramBoolean) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
    byte[] arrayOfByte1;
    if (paramByteBuffer1 == null || paramByteBuffer2 == null)
      throw new NullPointerException("Input and output buffers must not be null"); 
    int i = paramByteBuffer1.position();
    int j = paramByteBuffer1.limit();
    int k = j - i;
    if (paramBoolean && k == 0)
      return 0; 
    int m = engineGetOutputSize(k);
    if (paramByteBuffer2.remaining() < m)
      throw new ShortBufferException("Need at least " + m + " bytes of space in output buffer"); 
    boolean bool1 = paramByteBuffer1.hasArray();
    boolean bool2 = paramByteBuffer2.hasArray();
    if (bool1 && bool2) {
      int i5;
      arrayOfByte1 = paramByteBuffer1.array();
      int i2 = paramByteBuffer1.arrayOffset() + i;
      byte[] arrayOfByte = paramByteBuffer2.array();
      int i3 = paramByteBuffer2.position();
      int i4 = paramByteBuffer2.arrayOffset() + i3;
      if (paramBoolean) {
        i5 = engineUpdate(arrayOfByte1, i2, k, arrayOfByte, i4);
      } else {
        i5 = engineDoFinal(arrayOfByte1, i2, k, arrayOfByte, i4);
      } 
      paramByteBuffer1.position(j);
      paramByteBuffer2.position(i3 + i5);
      return i5;
    } 
    if (!bool1 && bool2) {
      int i2 = paramByteBuffer2.position();
      byte[] arrayOfByte3 = paramByteBuffer2.array();
      int i3 = paramByteBuffer2.arrayOffset() + i2;
      byte[] arrayOfByte4 = new byte[getTempArraySize(k)];
      int i4 = 0;
      while (true) {
        int i6, i5 = Math.min(k, arrayOfByte4.length);
        if (i5 > 0)
          paramByteBuffer1.get(arrayOfByte4, 0, i5); 
        if (paramBoolean || k != i5) {
          i6 = engineUpdate(arrayOfByte4, 0, i5, arrayOfByte3, i3);
        } else {
          i6 = engineDoFinal(arrayOfByte4, 0, i5, arrayOfByte3, i3);
        } 
        i4 += i6;
        i3 += i6;
        k -= i5;
        if (k <= 0) {
          paramByteBuffer2.position(i2 + i4);
          return i4;
        } 
      } 
    } 
    if (bool1) {
      arrayOfByte1 = paramByteBuffer1.array();
      int i2 = paramByteBuffer1.arrayOffset() + i;
    } else {
      arrayOfByte1 = new byte[getTempArraySize(k)];
      boolean bool3 = false;
    } 
    byte[] arrayOfByte2 = new byte[getTempArraySize(m)];
    int n = arrayOfByte2.length;
    int i1 = 0;
    boolean bool = false;
    while (true) {
      int i2, i3 = Math.min(k, (n == 0) ? arrayOfByte1.length : n);
      if (!bool1 && !bool && i3 > 0) {
        paramByteBuffer1.get(arrayOfByte1, 0, i3);
        i2 = 0;
      } 
      try {
        int i4;
        if (paramBoolean || k != i3) {
          i4 = engineUpdate(arrayOfByte1, i2, i3, arrayOfByte2, 0);
        } else {
          i4 = engineDoFinal(arrayOfByte1, i2, i3, arrayOfByte2, 0);
        } 
        bool = false;
        i2 += i3;
        k -= i3;
        if (i4 > 0) {
          paramByteBuffer2.put(arrayOfByte2, 0, i4);
          i1 += i4;
        } 
      } catch (ShortBufferException shortBufferException) {
        if (bool)
          throw (ProviderException)(new ProviderException("Could not determine buffer size"))
            .initCause(shortBufferException); 
        bool = true;
        n = engineGetOutputSize(i3);
        arrayOfByte2 = new byte[n];
      } 
      if (k <= 0) {
        if (bool1)
          paramByteBuffer1.position(j); 
        return i1;
      } 
    } 
  }
  
  protected byte[] engineWrap(Key paramKey) throws IllegalBlockSizeException, InvalidKeyException {
    throw new UnsupportedOperationException();
  }
  
  protected Key engineUnwrap(byte[] paramArrayOfbyte, String paramString, int paramInt) throws InvalidKeyException, NoSuchAlgorithmException {
    throw new UnsupportedOperationException();
  }
  
  protected int engineGetKeySize(Key paramKey) throws InvalidKeyException {
    throw new UnsupportedOperationException();
  }
  
  protected void engineUpdateAAD(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) {
    throw new UnsupportedOperationException("The underlying Cipher implementation does not support this method");
  }
  
  protected void engineUpdateAAD(ByteBuffer paramByteBuffer) {
    throw new UnsupportedOperationException("The underlying Cipher implementation does not support this method");
  }
}
