package com.viaversion.viabackwards.protocol.protocol1_16_1to1_16_2.packets;

import com.google.common.collect.Sets;
import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.EntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_16_1to1_16_2.Protocol1_16_1To1_16_2;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16_2Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_16;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_16;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.packets.EntityPackets;
import java.util.Set;

public class EntityPackets1_16_2 extends EntityRewriter<Protocol1_16_1To1_16_2> {
  private final Set<String> oldDimensions = Sets.newHashSet((Object[])new String[] { "minecraft:overworld", "minecraft:the_nether", "minecraft:the_end" });
  
  public EntityPackets1_16_2(Protocol1_16_1To1_16_2 protocol) {
    super((BackwardsProtocol)protocol);
  }
  
  protected void registerPackets() {
    registerTrackerWithData((ClientboundPacketType)ClientboundPackets1_16_2.SPAWN_ENTITY, (EntityType)Entity1_16_2Types.FALLING_BLOCK);
    registerSpawnTracker((ClientboundPacketType)ClientboundPackets1_16_2.SPAWN_MOB);
    registerTracker((ClientboundPacketType)ClientboundPackets1_16_2.SPAWN_EXPERIENCE_ORB, (EntityType)Entity1_16_2Types.EXPERIENCE_ORB);
    registerTracker((ClientboundPacketType)ClientboundPackets1_16_2.SPAWN_PAINTING, (EntityType)Entity1_16_2Types.PAINTING);
    registerTracker((ClientboundPacketType)ClientboundPackets1_16_2.SPAWN_PLAYER, (EntityType)Entity1_16_2Types.PLAYER);
    registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_16_2.DESTROY_ENTITIES);
    registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_16_2.ENTITY_METADATA, Types1_16.METADATA_LIST);
    ((Protocol1_16_1To1_16_2)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            handler(wrapper -> {
                  boolean hardcore = ((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue();
                  short gamemode = ((Short)wrapper.read((Type)Type.UNSIGNED_BYTE)).shortValue();
                  if (hardcore)
                    gamemode = (short)(gamemode | 0x8); 
                  wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf(gamemode));
                });
            map((Type)Type.BYTE);
            map(Type.STRING_ARRAY);
            handler(wrapper -> {
                  wrapper.read(Type.NBT);
                  wrapper.write(Type.NBT, EntityPackets.DIMENSIONS_TAG);
                  CompoundTag dimensionData = (CompoundTag)wrapper.read(Type.NBT);
                  wrapper.write(Type.STRING, EntityPackets1_16_2.this.getDimensionFromData(dimensionData));
                });
            map(Type.STRING);
            map((Type)Type.LONG);
            handler(wrapper -> {
                  int maxPlayers = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)Math.max(maxPlayers, 255)));
                });
            handler(EntityPackets1_16_2.this.getTrackerHandler((EntityType)Entity1_16_2Types.PLAYER, (Type)Type.INT));
          }
        });
    ((Protocol1_16_1To1_16_2)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_16_2.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  CompoundTag dimensionData = (CompoundTag)wrapper.read(Type.NBT);
                  wrapper.write(Type.STRING, EntityPackets1_16_2.this.getDimensionFromData(dimensionData));
                });
          }
        });
  }
  
  private String getDimensionFromData(CompoundTag dimensionData) {
    StringTag effectsLocation = (StringTag)dimensionData.get("effects");
    return (effectsLocation != null && this.oldDimensions.contains(effectsLocation.getValue())) ? effectsLocation.getValue() : "minecraft:overworld";
  }
  
  protected void registerRewrites() {
    registerMetaTypeHandler((MetaType)MetaType1_16.ITEM, (MetaType)MetaType1_16.BLOCK_STATE, (MetaType)MetaType1_16.PARTICLE, (MetaType)MetaType1_16.OPT_COMPONENT);
    mapTypes((EntityType[])Entity1_16_2Types.values(), Entity1_16Types.class);
    mapEntityTypeWithData((EntityType)Entity1_16_2Types.PIGLIN_BRUTE, (EntityType)Entity1_16_2Types.PIGLIN).jsonName("Piglin Brute");
    filter().filterFamily((EntityType)Entity1_16_2Types.ABSTRACT_PIGLIN).index(15).toIndex(16);
    filter().filterFamily((EntityType)Entity1_16_2Types.ABSTRACT_PIGLIN).index(16).toIndex(15);
  }
  
  public EntityType typeFromId(int typeId) {
    return Entity1_16_2Types.getTypeFromId(typeId);
  }
}
