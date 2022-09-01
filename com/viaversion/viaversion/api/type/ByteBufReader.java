package com.viaversion.viaversion.api.type;

import io.netty.buffer.ByteBuf;

public interface ByteBufReader<T> {
  T read(ByteBuf paramByteBuf) throws Exception;
}
