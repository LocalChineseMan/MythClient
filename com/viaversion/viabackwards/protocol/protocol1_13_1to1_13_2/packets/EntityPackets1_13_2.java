package com.viaversion.viabackwards.protocol.protocol1_13_1to1_13_2.packets;

import com.viaversion.viabackwards.protocol.protocol1_13_1to1_13_2.Protocol1_13_1To1_13_2;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_13;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_13_2;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_13;
import com.viaversion.viaversion.api.type.types.version.Types1_13_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;

public class EntityPackets1_13_2 {
  public static void register(Protocol1_13_1To1_13_2 protocol) {
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_MOB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map(Types1_13_2.METADATA_LIST, Types1_13.METADATA_LIST);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    for (Metadata metadata : wrapper.get(Types1_13.METADATA_LIST, 0)) {
                      if (metadata.metaType() == MetaType1_13_2.Slot)
                        metadata.setMetaType((MetaType)MetaType1_13.Slot); 
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map(Types1_13_2.METADATA_LIST, Types1_13.METADATA_LIST);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    for (Metadata metadata : wrapper.get(Types1_13.METADATA_LIST, 0)) {
                      if (metadata.metaType() == MetaType1_13_2.Slot)
                        metadata.setMetaType((MetaType)MetaType1_13.Slot); 
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_13.ENTITY_METADATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Types1_13_2.METADATA_LIST, Types1_13.METADATA_LIST);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    for (Metadata metadata : wrapper.get(Types1_13.METADATA_LIST, 0)) {
                      if (metadata.metaType() == MetaType1_13_2.Slot)
                        metadata.setMetaType((MetaType)MetaType1_13.Slot); 
                    } 
                  }
                });
          }
        });
  }
}
