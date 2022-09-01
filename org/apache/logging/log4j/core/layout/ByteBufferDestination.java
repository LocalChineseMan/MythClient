package org.apache.logging.log4j.core.layout;

import java.nio.ByteBuffer;

public interface ByteBufferDestination {
  ByteBuffer getByteBuffer();
  
  ByteBuffer drain(ByteBuffer paramByteBuffer);
  
  void writeBytes(ByteBuffer paramByteBuffer);
  
  void writeBytes(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
}
