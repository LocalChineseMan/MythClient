package com.sun.xml.internal.stream.util;

import java.lang.ref.SoftReference;

public class ThreadLocalBufferAllocator {
  private static ThreadLocal tlba = new ThreadLocal();
  
  public static BufferAllocator getBufferAllocator() {
    SoftReference<BufferAllocator> bAllocatorRef = tlba.get();
    if (bAllocatorRef == null || bAllocatorRef.get() == null) {
      bAllocatorRef = new SoftReference<>(new BufferAllocator());
      tlba.set(bAllocatorRef);
    } 
    return bAllocatorRef.get();
  }
}
