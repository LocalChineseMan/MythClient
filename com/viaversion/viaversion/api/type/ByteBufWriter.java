package com.viaversion.viaversion.api.type;

import io.netty.buffer.ByteBuf;

public interface ByteBufWriter<T> {
  void write(ByteBuf paramByteBuf, T paramT) throws Exception;
}
