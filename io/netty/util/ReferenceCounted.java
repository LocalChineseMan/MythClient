package io.netty.util;

public interface ReferenceCounted {
  int refCnt();
  
  ReferenceCounted retain();
  
  ReferenceCounted retain(int paramInt);
  
  boolean release();
  
  boolean release(int paramInt);
}
