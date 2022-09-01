package com.viaversion.viaversion.api.protocol.remapper;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;

public class TypeRemapper<T> implements ValueReader<T>, ValueWriter<T> {
  private final Type<T> type;
  
  public TypeRemapper(Type<T> type) {
    this.type = type;
  }
  
  public T read(PacketWrapper wrapper) throws Exception {
    return (T)wrapper.read(this.type);
  }
  
  public void write(PacketWrapper output, T inputValue) {
    output.write(this.type, inputValue);
  }
}
