package sun.nio.fs;

import sun.misc.Unsafe;

class NativeBuffers {
  private static final Unsafe unsafe = Unsafe.getUnsafe();
  
  private static final int TEMP_BUF_POOL_SIZE = 3;
  
  private static ThreadLocal<NativeBuffer[]> threadLocal = (ThreadLocal)new ThreadLocal<>();
  
  static NativeBuffer allocNativeBuffer(int paramInt) {
    if (paramInt < 2048)
      paramInt = 2048; 
    return new NativeBuffer(paramInt);
  }
  
  static NativeBuffer getNativeBufferFromCache(int paramInt) {
    NativeBuffer[] arrayOfNativeBuffer = threadLocal.get();
    if (arrayOfNativeBuffer != null)
      for (byte b = 0; b < 3; b++) {
        NativeBuffer nativeBuffer = arrayOfNativeBuffer[b];
        if (nativeBuffer != null && nativeBuffer.size() >= paramInt) {
          arrayOfNativeBuffer[b] = null;
          return nativeBuffer;
        } 
      }  
    return null;
  }
  
  static NativeBuffer getNativeBuffer(int paramInt) {
    NativeBuffer nativeBuffer = getNativeBufferFromCache(paramInt);
    if (nativeBuffer != null) {
      nativeBuffer.setOwner(null);
      return nativeBuffer;
    } 
    return allocNativeBuffer(paramInt);
  }
  
  static void releaseNativeBuffer(NativeBuffer paramNativeBuffer) {
    NativeBuffer[] arrayOfNativeBuffer = threadLocal.get();
    if (arrayOfNativeBuffer == null) {
      arrayOfNativeBuffer = new NativeBuffer[3];
      arrayOfNativeBuffer[0] = paramNativeBuffer;
      threadLocal.set(arrayOfNativeBuffer);
      return;
    } 
    byte b;
    for (b = 0; b < 3; b++) {
      if (arrayOfNativeBuffer[b] == null) {
        arrayOfNativeBuffer[b] = paramNativeBuffer;
        return;
      } 
    } 
    for (b = 0; b < 3; b++) {
      NativeBuffer nativeBuffer = arrayOfNativeBuffer[b];
      if (nativeBuffer.size() < paramNativeBuffer.size()) {
        nativeBuffer.cleaner().clean();
        arrayOfNativeBuffer[b] = paramNativeBuffer;
        return;
      } 
    } 
    paramNativeBuffer.cleaner().clean();
  }
  
  static void copyCStringToNativeBuffer(byte[] paramArrayOfbyte, NativeBuffer paramNativeBuffer) {
    long l1 = Unsafe.ARRAY_BYTE_BASE_OFFSET;
    long l2 = paramArrayOfbyte.length;
    assert paramNativeBuffer.size() >= l2 + 1L;
    unsafe.copyMemory(paramArrayOfbyte, l1, null, paramNativeBuffer.address(), l2);
    unsafe.putByte(paramNativeBuffer.address() + l2, (byte)0);
  }
  
  static NativeBuffer asNativeBuffer(byte[] paramArrayOfbyte) {
    NativeBuffer nativeBuffer = getNativeBuffer(paramArrayOfbyte.length + 1);
    copyCStringToNativeBuffer(paramArrayOfbyte, nativeBuffer);
    return nativeBuffer;
  }
}
