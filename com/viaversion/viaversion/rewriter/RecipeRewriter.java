package com.viaversion.viaversion.rewriter;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import java.util.HashMap;
import java.util.Map;

public abstract class RecipeRewriter {
  protected final Protocol protocol;
  
  protected final Map<String, RecipeConsumer> recipeHandlers = new HashMap<>();
  
  protected RecipeRewriter(Protocol protocol) {
    this.protocol = protocol;
  }
  
  public void handle(PacketWrapper wrapper, String type) throws Exception {
    RecipeConsumer handler = this.recipeHandlers.get(type);
    if (handler != null)
      handler.accept(wrapper); 
  }
  
  public void registerDefaultHandler(ClientboundPacketType packetType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int size = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  for (int i = 0; i < size; i++) {
                    String type = ((String)wrapper.passthrough(Type.STRING)).replace("minecraft:", "");
                    String id = (String)wrapper.passthrough(Type.STRING);
                    RecipeRewriter.this.handle(wrapper, type);
                  } 
                });
          }
        });
  }
  
  @FunctionalInterface
  public static interface RecipeConsumer {
    void accept(PacketWrapper param1PacketWrapper) throws Exception;
  }
  
  protected void rewrite(Item item) {
    if (this.protocol.getItemRewriter() != null)
      this.protocol.getItemRewriter().handleItemToClient(item); 
  }
}
