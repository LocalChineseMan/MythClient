package io.netty.buffer;

import io.netty.util.internal.PlatformDependent;

public final class UnpooledByteBufAllocator extends AbstractByteBufAllocator {
  public static final UnpooledByteBufAllocator DEFAULT = new UnpooledByteBufAllocator(PlatformDependent.directBufferPreferred());
  
  public UnpooledByteBufAllocator(boolean preferDirect) {
    super(preferDirect);
  }
  
  protected ByteBuf newHeapBuffer(int initialCapacity, int maxCapacity) {
    return new UnpooledHeapByteBuf(this, initialCapacity, maxCapacity);
  }
  
  protected ByteBuf newDirectBuffer(int initialCapacity, int maxCapacity) {
    UnpooledDirectByteBuf unpooledDirectByteBuf;
    if (PlatformDependent.hasUnsafe()) {
      ByteBuf buf = new UnpooledUnsafeDirectByteBuf(this, initialCapacity, maxCapacity);
    } else {
      unpooledDirectByteBuf = new UnpooledDirectByteBuf(this, initialCapacity, maxCapacity);
    } 
    return toLeakAwareBuffer((ByteBuf)unpooledDirectByteBuf);
  }
  
  public boolean isDirectBufferPooled() {
    return false;
  }
}
