package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public interface RecvByteBufAllocator {
  Handle newHandle();
  
  public static interface Handle {
    ByteBuf allocate(ByteBufAllocator param1ByteBufAllocator);
    
    int guess();
    
    void record(int param1Int);
  }
}
