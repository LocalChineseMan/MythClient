package com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.packets;

import com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.Protocol1_11_1To1_12;
import com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.data.AdvancementTranslations;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.ClientboundPackets1_12;
import com.viaversion.viaversion.rewriter.ComponentRewriter;

public class ChatPackets1_12 extends RewriterBase<Protocol1_11_1To1_12> {
  private final ComponentRewriter componentRewriter = new ComponentRewriter() {
      protected void handleTranslate(JsonObject object, String translate) {
        String text = AdvancementTranslations.get(translate);
        if (text != null)
          object.addProperty("translate", text); 
      }
    };
  
  public ChatPackets1_12(Protocol1_11_1To1_12 protocol) {
    super((Protocol)protocol);
  }
  
  protected void registerPackets() {
    ((Protocol1_11_1To1_12)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_12.CHAT_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  JsonElement element = (JsonElement)wrapper.passthrough(Type.COMPONENT);
                  ChatPackets1_12.this.componentRewriter.processText(element);
                });
          }
        });
  }
}
