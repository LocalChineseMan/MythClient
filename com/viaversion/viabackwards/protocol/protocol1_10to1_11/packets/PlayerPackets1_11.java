package com.viaversion.viabackwards.protocol.protocol1_10to1_11.packets;

import com.viaversion.viabackwards.protocol.protocol1_10to1_11.Protocol1_10To1_11;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import com.viaversion.viaversion.libs.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;

public class PlayerPackets1_11 {
  private static final ValueTransformer<Short, Float> TO_NEW_FLOAT = new ValueTransformer<Short, Float>((Type)Type.FLOAT) {
      public Float transform(PacketWrapper wrapper, Short inputValue) throws Exception {
        return Float.valueOf(inputValue.shortValue() / 15.0F);
      }
    };
  
  public void register(Protocol1_10To1_11 protocol) {
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.TITLE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  if (action == 2) {
                    JsonElement message = (JsonElement)wrapper.read(Type.COMPONENT);
                    wrapper.clearPacket();
                    wrapper.setId(ClientboundPackets1_9_3.CHAT_MESSAGE.ordinal());
                    String legacy = LegacyComponentSerializer.legacySection().serialize(GsonComponentSerializer.gson().deserialize(message.toString()));
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.getAsJsonObject().addProperty("text", legacy);
                    wrapper.write(Type.COMPONENT, jsonObject);
                    wrapper.write((Type)Type.BYTE, Byte.valueOf((byte)2));
                  } else if (action > 2) {
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(action - 1));
                  } 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.COLLECT_ITEM, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            handler(wrapper -> wrapper.read((Type)Type.VAR_INT));
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_9_3.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map((Type)Type.UNSIGNED_BYTE, PlayerPackets1_11.TO_NEW_FLOAT);
            map((Type)Type.UNSIGNED_BYTE, PlayerPackets1_11.TO_NEW_FLOAT);
            map((Type)Type.UNSIGNED_BYTE, PlayerPackets1_11.TO_NEW_FLOAT);
          }
        });
  }
}
