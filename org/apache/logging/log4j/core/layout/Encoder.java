package org.apache.logging.log4j.core.layout;

public interface Encoder<T> {
  void encode(T paramT, ByteBufferDestination paramByteBufferDestination);
}
