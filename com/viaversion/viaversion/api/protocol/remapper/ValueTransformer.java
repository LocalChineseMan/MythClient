package com.viaversion.viaversion.api.protocol.remapper;

import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.exception.InformativeException;

public abstract class ValueTransformer<T1, T2> implements ValueWriter<T1> {
  private final Type<T1> inputType;
  
  private final Type<T2> outputType;
  
  protected ValueTransformer(Type<T1> inputType, Type<T2> outputType) {
    this.inputType = inputType;
    this.outputType = outputType;
  }
  
  protected ValueTransformer(Type<T2> outputType) {
    this(null, outputType);
  }
  
  public abstract T2 transform(PacketWrapper paramPacketWrapper, T1 paramT1) throws Exception;
  
  public void write(PacketWrapper writer, T1 inputValue) throws Exception {
    try {
      writer.write(this.outputType, transform(writer, inputValue));
    } catch (InformativeException e) {
      e.addSource(getClass());
      throw e;
    } 
  }
  
  public Type<T1> getInputType() {
    return this.inputType;
  }
  
  public Type<T2> getOutputType() {
    return this.outputType;
  }
}
