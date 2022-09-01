package com.viaversion.viaversion.rewriter;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParser;
import com.viaversion.viaversion.libs.gson.JsonPrimitive;
import com.viaversion.viaversion.libs.gson.JsonSyntaxException;

public class ComponentRewriter {
  protected final Protocol protocol;
  
  public ComponentRewriter(Protocol protocol) {
    this.protocol = protocol;
  }
  
  public ComponentRewriter() {
    this.protocol = null;
  }
  
  public void registerComponentPacket(ClientboundPacketType packetType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> ComponentRewriter.this.processText((JsonElement)wrapper.passthrough(Type.COMPONENT)));
          }
        });
  }
  
  public void registerChatMessage(ClientboundPacketType packetType) {
    registerComponentPacket(packetType);
  }
  
  public void registerBossBar(ClientboundPacketType packetType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  if (action == 0 || action == 3)
                    ComponentRewriter.this.processText((JsonElement)wrapper.passthrough(Type.COMPONENT)); 
                });
          }
        });
  }
  
  public void registerCombatEvent(ClientboundPacketType packetType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  if (((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue() == 2) {
                    wrapper.passthrough((Type)Type.VAR_INT);
                    wrapper.passthrough((Type)Type.INT);
                    ComponentRewriter.this.processText((JsonElement)wrapper.passthrough(Type.COMPONENT));
                  } 
                });
          }
        });
  }
  
  public void registerTitle(ClientboundPacketType packetType) {
    this.protocol.registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int action = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  if (action >= 0 && action <= 2)
                    ComponentRewriter.this.processText((JsonElement)wrapper.passthrough(Type.COMPONENT)); 
                });
          }
        });
  }
  
  public JsonElement processText(String value) {
    try {
      JsonElement root = JsonParser.parseString(value);
      processText(root);
      return root;
    } catch (JsonSyntaxException e) {
      if (Via.getManager().isDebug()) {
        Via.getPlatform().getLogger().severe("Error when trying to parse json: " + value);
        throw e;
      } 
      return (JsonElement)new JsonPrimitive(value);
    } 
  }
  
  public void processText(JsonElement element) {
    if (element == null || element.isJsonNull())
      return; 
    if (element.isJsonArray()) {
      processAsArray(element);
      return;
    } 
    if (element.isJsonPrimitive()) {
      handleText(element.getAsJsonPrimitive());
      return;
    } 
    JsonObject object = element.getAsJsonObject();
    JsonPrimitive text = object.getAsJsonPrimitive("text");
    if (text != null)
      handleText(text); 
    JsonElement translate = object.get("translate");
    if (translate != null) {
      handleTranslate(object, translate.getAsString());
      JsonElement with = object.get("with");
      if (with != null)
        processAsArray(with); 
    } 
    JsonElement extra = object.get("extra");
    if (extra != null)
      processAsArray(extra); 
    JsonObject hoverEvent = object.getAsJsonObject("hoverEvent");
    if (hoverEvent != null)
      handleHoverEvent(hoverEvent); 
  }
  
  protected void handleText(JsonPrimitive text) {}
  
  protected void handleTranslate(JsonObject object, String translate) {}
  
  protected void handleHoverEvent(JsonObject hoverEvent) {
    String action = hoverEvent.getAsJsonPrimitive("action").getAsString();
    if (action.equals("show_text")) {
      JsonElement value = hoverEvent.get("value");
      processText((value != null) ? value : hoverEvent.get("contents"));
    } else if (action.equals("show_entity")) {
      JsonObject contents = hoverEvent.getAsJsonObject("contents");
      if (contents != null)
        processText(contents.get("name")); 
    } 
  }
  
  private void processAsArray(JsonElement element) {
    for (JsonElement jsonElement : element.getAsJsonArray())
      processText(jsonElement); 
  }
  
  public <T extends Protocol> T getProtocol() {
    return (T)this.protocol;
  }
}
