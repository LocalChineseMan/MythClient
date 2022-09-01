package com.viaversion.viaversion.rewriter;

import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class CommandRewriter {
  protected final Protocol protocol;
  
  protected final Map<String, CommandArgumentConsumer> parserHandlers = new HashMap<>();
  
  protected CommandRewriter(Protocol protocol) {
    this.protocol = protocol;
    this.parserHandlers.put("brigadier:double", wrapper -> {
          byte propertyFlags = ((Byte)wrapper.passthrough((Type)Type.BYTE)).byteValue();
          if ((propertyFlags & 0x1) != 0)
            wrapper.passthrough((Type)Type.DOUBLE); 
          if ((propertyFlags & 0x2) != 0)
            wrapper.passthrough((Type)Type.DOUBLE); 
        });
    this.parserHandlers.put("brigadier:float", wrapper -> {
          byte propertyFlags = ((Byte)wrapper.passthrough((Type)Type.BYTE)).byteValue();
          if ((propertyFlags & 0x1) != 0)
            wrapper.passthrough((Type)Type.FLOAT); 
          if ((propertyFlags & 0x2) != 0)
            wrapper.passthrough((Type)Type.FLOAT); 
        });
    this.parserHandlers.put("brigadier:integer", wrapper -> {
          byte propertyFlags = ((Byte)wrapper.passthrough((Type)Type.BYTE)).byteValue();
          if ((propertyFlags & 0x1) != 0)
            wrapper.passthrough((Type)Type.INT); 
          if ((propertyFlags & 0x2) != 0)
            wrapper.passthrough((Type)Type.INT); 
        });
    this.parserHandlers.put("brigadier:long", wrapper -> {
          byte propertyFlags = ((Byte)wrapper.passthrough((Type)Type.BYTE)).byteValue();
          if ((propertyFlags & 0x1) != 0)
            wrapper.passthrough((Type)Type.LONG); 
          if ((propertyFlags & 0x2) != 0)
            wrapper.passthrough((Type)Type.LONG); 
        });
    this.parserHandlers.put("brigadier:string", wrapper -> wrapper.passthrough((Type)Type.VAR_INT));
    this.parserHandlers.put("minecraft:entity", wrapper -> wrapper.passthrough((Type)Type.BYTE));
    this.parserHandlers.put("minecraft:score_holder", wrapper -> wrapper.passthrough((Type)Type.BYTE));
  }
  
  public void handleArgument(PacketWrapper wrapper, String argumentType) throws Exception {
    CommandArgumentConsumer handler = this.parserHandlers.get(argumentType);
    if (handler != null)
      handler.accept(wrapper); 
  }
  
  public void registerDeclareCommands(ClientboundPacketType packetType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int size = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  for (int i = 0; i < size; i++) {
                    byte flags = ((Byte)wrapper.passthrough((Type)Type.BYTE)).byteValue();
                    wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);
                    if ((flags & 0x8) != 0)
                      wrapper.passthrough((Type)Type.VAR_INT); 
                    byte nodeType = (byte)(flags & 0x3);
                    if (nodeType == 1 || nodeType == 2)
                      wrapper.passthrough(Type.STRING); 
                    if (nodeType == 2) {
                      String argumentType = (String)wrapper.read(Type.STRING);
                      String newArgumentType = CommandRewriter.this.handleArgumentType(argumentType);
                      if (newArgumentType != null)
                        wrapper.write(Type.STRING, newArgumentType); 
                      CommandRewriter.this.handleArgument(wrapper, argumentType);
                    } 
                    if ((flags & 0x10) != 0)
                      wrapper.passthrough(Type.STRING); 
                  } 
                  wrapper.passthrough((Type)Type.VAR_INT);
                });
          }
        });
  }
  
  @FunctionalInterface
  public static interface CommandArgumentConsumer {
    void accept(PacketWrapper param1PacketWrapper) throws Exception;
  }
  
  protected String handleArgumentType(String argumentType) {
    return argumentType;
  }
}
