package com.viaversion.viaversion.api.protocol.remapper;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.exception.CancelException;
import com.viaversion.viaversion.exception.InformativeException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class PacketRemapper {
  private final List<PacketHandler> valueRemappers = new ArrayList<>();
  
  protected PacketRemapper() {
    registerMap();
  }
  
  public void map(Type type) {
    handler(wrapper -> wrapper.write(type, wrapper.read(type)));
  }
  
  public void map(Type oldType, Type newType) {
    handler(wrapper -> wrapper.write(newType, wrapper.read(oldType)));
  }
  
  public <T1, T2> void map(Type<T1> oldType, Type<T2> newType, Function<T1, T2> transformer) {
    map(oldType, (ValueTransformer<T1, ?>)new Object(this, newType, transformer));
  }
  
  public <T1, T2> void map(ValueTransformer<T1, T2> transformer) {
    if (transformer.getInputType() == null)
      throw new IllegalArgumentException("Use map(Type<T1>, ValueTransformer<T1, T2>) for value transformers without specified input type!"); 
    map(transformer.getInputType(), transformer);
  }
  
  public <T1, T2> void map(Type<T1> oldType, ValueTransformer<T1, T2> transformer) {
    map(new TypeRemapper<>(oldType), transformer);
  }
  
  public <T> void map(ValueReader<T> inputReader, ValueWriter<T> outputWriter) {
    handler(wrapper -> outputWriter.write(wrapper, inputReader.read(wrapper)));
  }
  
  public void handler(PacketHandler handler) {
    this.valueRemappers.add(handler);
  }
  
  public <T> void create(Type<T> type, T value) {
    handler(wrapper -> wrapper.write(type, value));
  }
  
  public void read(Type type) {
    handler(wrapper -> wrapper.read(type));
  }
  
  public void remap(PacketWrapper packetWrapper) throws Exception {
    try {
      for (PacketHandler handler : this.valueRemappers)
        handler.handle(packetWrapper); 
    } catch (CancelException e) {
      throw e;
    } catch (InformativeException e) {
      e.addSource(getClass());
      throw e;
    } catch (Exception e) {
      InformativeException ex = new InformativeException(e);
      ex.addSource(getClass());
      throw ex;
    } 
  }
  
  public abstract void registerMap();
}
