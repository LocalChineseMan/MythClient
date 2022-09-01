package com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16_2Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_16;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.Protocol1_16_2To1_16_1;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.metadata.MetadataRewriter1_16_2To1_16_1;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;

public class EntityPackets {
  public static void register(final Protocol1_16_2To1_16_1 protocol) {
    MetadataRewriter1_16_2To1_16_1 metadataRewriter = (MetadataRewriter1_16_2To1_16_1)protocol.get(MetadataRewriter1_16_2To1_16_1.class);
    metadataRewriter.registerTrackerWithData((ClientboundPacketType)ClientboundPackets1_16.SPAWN_ENTITY, (EntityType)Entity1_16_2Types.FALLING_BLOCK);
    metadataRewriter.registerTracker((ClientboundPacketType)ClientboundPackets1_16.SPAWN_MOB);
    metadataRewriter.registerTracker((ClientboundPacketType)ClientboundPackets1_16.SPAWN_PLAYER, (EntityType)Entity1_16_2Types.PLAYER);
    metadataRewriter.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_16.ENTITY_METADATA, Types1_16.METADATA_LIST);
    metadataRewriter.registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_16.DESTROY_ENTITIES);
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_16.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            handler(wrapper -> {
                  short gamemode = ((Short)wrapper.read((Type)Type.UNSIGNED_BYTE)).shortValue();
                  wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(((gamemode & 0x8) != 0)));
                  gamemode = (short)(gamemode & 0xFFFFFFF7);
                  wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf(gamemode));
                });
            map((Type)Type.BYTE);
            map(Type.STRING_ARRAY);
            handler(wrapper -> {
                  wrapper.read(Type.NBT);
                  wrapper.write(Type.NBT, protocol.getMappingData().getDimensionRegistry());
                  String dimensionType = (String)wrapper.read(Type.STRING);
                  wrapper.write(Type.NBT, EntityPackets.getDimensionData(dimensionType));
                });
            map(Type.STRING);
            map((Type)Type.LONG);
            map((Type)Type.UNSIGNED_BYTE, (Type)Type.VAR_INT);
            handler(wrapper -> wrapper.user().getEntityTracker(Protocol1_16_2To1_16_1.class).addEntity(((Integer)wrapper.get((Type)Type.INT, 0)).intValue(), (EntityType)Entity1_16_2Types.PLAYER));
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_16.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  String dimensionType = (String)wrapper.read(Type.STRING);
                  wrapper.write(Type.NBT, EntityPackets.getDimensionData(dimensionType));
                });
          }
        });
  }
  
  public static CompoundTag getDimensionData(String dimensionType) {
    CompoundTag tag = (CompoundTag)Protocol1_16_2To1_16_1.MAPPINGS.getDimensionDataMap().get(dimensionType);
    if (tag == null) {
      Via.getPlatform().getLogger().severe("Could not get dimension data of " + dimensionType);
      throw new NullPointerException("Dimension data for " + dimensionType + " is null!");
    } 
    return tag;
  }
}
