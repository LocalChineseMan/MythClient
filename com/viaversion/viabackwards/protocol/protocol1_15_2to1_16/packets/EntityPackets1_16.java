package com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.packets;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.rewriters.EntityRewriter;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.Protocol1_15_2To1_16;
import com.viaversion.viabackwards.protocol.protocol1_15_2to1_16.data.WorldNameTracker;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_15Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_14;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.api.type.types.version.Types1_14;
import com.viaversion.viaversion.api.type.types.version.Types1_16;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.ClientboundPackets1_15;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.ClientboundPackets1_16;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.meta.MetaHandlerEvent;

public class EntityPackets1_16 extends EntityRewriter<Protocol1_15_2To1_16> {
  private final ValueTransformer<String, Integer> dimensionTransformer = new ValueTransformer<String, Integer>(Type.STRING, (Type)Type.INT) {
      public Integer transform(PacketWrapper wrapper, String input) throws Exception {
        switch (input) {
          case "minecraft:the_nether":
            return Integer.valueOf(-1);
          default:
            return Integer.valueOf(0);
          case "minecraft:the_end":
            break;
        } 
        return Integer.valueOf(1);
      }
    };
  
  public EntityPackets1_16(Protocol1_15_2To1_16 protocol) {
    super((BackwardsProtocol)protocol);
  }
  
  protected void registerPackets() {
    ((Protocol1_15_2To1_16)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_16.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.INT);
            handler(wrapper -> {
                  EntityType entityType = EntityPackets1_16.this.typeFromId(((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue());
                  if (entityType == Entity1_16Types.LIGHTNING_BOLT) {
                    wrapper.cancel();
                    PacketWrapper spawnLightningPacket = wrapper.create((PacketType)ClientboundPackets1_15.SPAWN_GLOBAL_ENTITY);
                    spawnLightningPacket.write((Type)Type.VAR_INT, wrapper.get((Type)Type.VAR_INT, 0));
                    spawnLightningPacket.write((Type)Type.BYTE, Byte.valueOf((byte)1));
                    spawnLightningPacket.write((Type)Type.DOUBLE, wrapper.get((Type)Type.DOUBLE, 0));
                    spawnLightningPacket.write((Type)Type.DOUBLE, wrapper.get((Type)Type.DOUBLE, 1));
                    spawnLightningPacket.write((Type)Type.DOUBLE, wrapper.get((Type)Type.DOUBLE, 2));
                    spawnLightningPacket.send(Protocol1_15_2To1_16.class);
                  } 
                });
            handler(EntityPackets1_16.this.getSpawnTrackerWithDataHandler((EntityType)Entity1_16Types.FALLING_BLOCK));
          }
        });
    registerSpawnTracker((ClientboundPacketType)ClientboundPackets1_16.SPAWN_MOB);
    ((Protocol1_15_2To1_16)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_16.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map(EntityPackets1_16.this.dimensionTransformer);
            handler(wrapper -> {
                  WorldNameTracker worldNameTracker = (WorldNameTracker)wrapper.user().get(WorldNameTracker.class);
                  String nextWorldName = (String)wrapper.read(Type.STRING);
                  wrapper.passthrough((Type)Type.LONG);
                  wrapper.passthrough((Type)Type.UNSIGNED_BYTE);
                  wrapper.read((Type)Type.BYTE);
                  ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                  int dimension = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                  if (clientWorld.getEnvironment() != null && dimension == clientWorld.getEnvironment().getId() && (wrapper.user().isClientSide() || Via.getPlatform().isProxy() || wrapper.user().getProtocolInfo().getProtocolVersion() <= ProtocolVersion.v1_12_2.getVersion() || !nextWorldName.equals(worldNameTracker.getWorldName()))) {
                    PacketWrapper packet = wrapper.create((PacketType)ClientboundPackets1_15.RESPAWN);
                    packet.write((Type)Type.INT, Integer.valueOf((dimension == 0) ? -1 : 0));
                    packet.write((Type)Type.LONG, Long.valueOf(0L));
                    packet.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)0));
                    packet.write(Type.STRING, "default");
                    packet.send(Protocol1_15_2To1_16.class);
                  } 
                  clientWorld.setEnvironment(dimension);
                  wrapper.write(Type.STRING, "default");
                  wrapper.read((Type)Type.BOOLEAN);
                  if (((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue())
                    wrapper.set(Type.STRING, 0, "flat"); 
                  wrapper.read((Type)Type.BOOLEAN);
                  worldNameTracker.setWorldName(nextWorldName);
                });
          }
        });
    ((Protocol1_15_2To1_16)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_16.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.BYTE, (Type)Type.NOTHING);
            map(Type.STRING_ARRAY, (Type)Type.NOTHING);
            map(Type.NBT, (Type)Type.NOTHING);
            map(EntityPackets1_16.this.dimensionTransformer);
            handler(wrapper -> {
                  WorldNameTracker worldNameTracker = (WorldNameTracker)wrapper.user().get(WorldNameTracker.class);
                  worldNameTracker.setWorldName((String)wrapper.read(Type.STRING));
                });
            map((Type)Type.LONG);
            map((Type)Type.UNSIGNED_BYTE);
            handler(wrapper -> {
                  ClientWorld clientChunks = (ClientWorld)wrapper.user().get(ClientWorld.class);
                  clientChunks.setEnvironment(((Integer)wrapper.get((Type)Type.INT, 1)).intValue());
                  EntityPackets1_16.this.tracker(wrapper.user()).addEntity(((Integer)wrapper.get((Type)Type.INT, 0)).intValue(), (EntityType)Entity1_16Types.PLAYER);
                  wrapper.write(Type.STRING, "default");
                  wrapper.passthrough((Type)Type.VAR_INT);
                  wrapper.passthrough((Type)Type.BOOLEAN);
                  wrapper.passthrough((Type)Type.BOOLEAN);
                  wrapper.read((Type)Type.BOOLEAN);
                  if (((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue())
                    wrapper.set(Type.STRING, 0, "flat"); 
                });
          }
        });
    registerTracker((ClientboundPacketType)ClientboundPackets1_16.SPAWN_EXPERIENCE_ORB, (EntityType)Entity1_16Types.EXPERIENCE_ORB);
    registerTracker((ClientboundPacketType)ClientboundPackets1_16.SPAWN_PAINTING, (EntityType)Entity1_16Types.PAINTING);
    registerTracker((ClientboundPacketType)ClientboundPackets1_16.SPAWN_PLAYER, (EntityType)Entity1_16Types.PLAYER);
    registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_16.DESTROY_ENTITIES);
    registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_16.ENTITY_METADATA, Types1_16.METADATA_LIST, Types1_14.METADATA_LIST);
    ((Protocol1_15_2To1_16)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_16.ENTITY_PROPERTIES, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.passthrough((Type)Type.VAR_INT);
                  int size = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue();
                  for (int i = 0; i < size; i++) {
                    String attributeIdentifier = (String)wrapper.read(Type.STRING);
                    String oldKey = (String)((Protocol1_15_2To1_16)EntityPackets1_16.this.protocol).getMappingData().getAttributeMappings().get(attributeIdentifier);
                    wrapper.write(Type.STRING, (oldKey != null) ? oldKey : attributeIdentifier.replace("minecraft:", ""));
                    wrapper.passthrough((Type)Type.DOUBLE);
                    int modifierSize = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                    for (int j = 0; j < modifierSize; j++) {
                      wrapper.passthrough(Type.UUID);
                      wrapper.passthrough((Type)Type.DOUBLE);
                      wrapper.passthrough((Type)Type.BYTE);
                    } 
                  } 
                });
          }
        });
    ((Protocol1_15_2To1_16)this.protocol).registerClientbound((ClientboundPacketType)ClientboundPackets1_16.PLAYER_INFO, new PacketRemapper() {
          public void registerMap() {
            handler(packetWrapper -> {
                  int action = ((Integer)packetWrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  int playerCount = ((Integer)packetWrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  for (int i = 0; i < playerCount; i++) {
                    packetWrapper.passthrough(Type.UUID);
                    if (action == 0) {
                      packetWrapper.passthrough(Type.STRING);
                      int properties = ((Integer)packetWrapper.passthrough((Type)Type.VAR_INT)).intValue();
                      for (int j = 0; j < properties; j++) {
                        packetWrapper.passthrough(Type.STRING);
                        packetWrapper.passthrough(Type.STRING);
                        if (((Boolean)packetWrapper.passthrough((Type)Type.BOOLEAN)).booleanValue())
                          packetWrapper.passthrough(Type.STRING); 
                      } 
                      packetWrapper.passthrough((Type)Type.VAR_INT);
                      packetWrapper.passthrough((Type)Type.VAR_INT);
                      if (((Boolean)packetWrapper.passthrough((Type)Type.BOOLEAN)).booleanValue())
                        ((Protocol1_15_2To1_16)EntityPackets1_16.this.protocol).getTranslatableRewriter().processText((JsonElement)packetWrapper.passthrough(Type.COMPONENT)); 
                    } else if (action == 1) {
                      packetWrapper.passthrough((Type)Type.VAR_INT);
                    } else if (action == 2) {
                      packetWrapper.passthrough((Type)Type.VAR_INT);
                    } else if (action == 3 && ((Boolean)packetWrapper.passthrough((Type)Type.BOOLEAN)).booleanValue()) {
                      ((Protocol1_15_2To1_16)EntityPackets1_16.this.protocol).getTranslatableRewriter().processText((JsonElement)packetWrapper.passthrough(Type.COMPONENT));
                    } 
                  } 
                });
          }
        });
  }
  
  protected void registerRewrites() {
    filter().handler((event, meta) -> {
          meta.setMetaType((MetaType)MetaType1_14.byId(meta.metaType().typeId()));
          MetaType type = meta.metaType();
          if (type == MetaType1_14.Slot) {
            meta.setValue(((Protocol1_15_2To1_16)this.protocol).getItemRewriter().handleItemToClient((Item)meta.getValue()));
          } else if (type == MetaType1_14.BlockID) {
            meta.setValue(Integer.valueOf(((Protocol1_15_2To1_16)this.protocol).getMappingData().getNewBlockStateId(((Integer)meta.getValue()).intValue())));
          } else if (type == MetaType1_14.PARTICLE) {
            rewriteParticle((Particle)meta.getValue());
          } else if (type == MetaType1_14.OptChat) {
            JsonElement text = (JsonElement)meta.value();
            if (text != null)
              ((Protocol1_15_2To1_16)this.protocol).getTranslatableRewriter().processText(text); 
          } 
        });
    mapEntityType((EntityType)Entity1_16Types.ZOMBIFIED_PIGLIN, (EntityType)Entity1_15Types.ZOMBIE_PIGMAN);
    mapTypes((EntityType[])Entity1_16Types.values(), Entity1_15Types.class);
    mapEntityTypeWithData((EntityType)Entity1_16Types.HOGLIN, (EntityType)Entity1_16Types.COW).jsonName("Hoglin");
    mapEntityTypeWithData((EntityType)Entity1_16Types.ZOGLIN, (EntityType)Entity1_16Types.COW).jsonName("Zoglin");
    mapEntityTypeWithData((EntityType)Entity1_16Types.PIGLIN, (EntityType)Entity1_16Types.ZOMBIFIED_PIGLIN).jsonName("Piglin");
    mapEntityTypeWithData((EntityType)Entity1_16Types.STRIDER, (EntityType)Entity1_16Types.MAGMA_CUBE).jsonName("Strider");
    filter().type((EntityType)Entity1_16Types.ZOGLIN).cancel(16);
    filter().type((EntityType)Entity1_16Types.HOGLIN).cancel(15);
    filter().type((EntityType)Entity1_16Types.PIGLIN).cancel(16);
    filter().type((EntityType)Entity1_16Types.PIGLIN).cancel(17);
    filter().type((EntityType)Entity1_16Types.PIGLIN).cancel(18);
    filter().type((EntityType)Entity1_16Types.STRIDER).index(15).handler((event, meta) -> {
          boolean baby = ((Boolean)meta.value()).booleanValue();
          meta.setTypeAndValue((MetaType)MetaType1_14.VarInt, Integer.valueOf(baby ? 1 : 3));
        });
    filter().type((EntityType)Entity1_16Types.STRIDER).cancel(16);
    filter().type((EntityType)Entity1_16Types.STRIDER).cancel(17);
    filter().type((EntityType)Entity1_16Types.STRIDER).cancel(18);
    filter().type((EntityType)Entity1_16Types.FISHING_BOBBER).cancel(8);
    filter().filterFamily((EntityType)Entity1_16Types.ABSTRACT_ARROW).cancel(8);
    filter().filterFamily((EntityType)Entity1_16Types.ABSTRACT_ARROW).handler((event, meta) -> {
          if (event.index() >= 8)
            event.setIndex(event.index() + 1); 
        });
  }
  
  public EntityType typeFromId(int typeId) {
    return Entity1_16Types.getTypeFromId(typeId);
  }
}
