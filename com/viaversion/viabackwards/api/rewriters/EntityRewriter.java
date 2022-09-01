package com.viaversion.viabackwards.api.rewriters;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_14;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;

public abstract class EntityRewriter<T extends BackwardsProtocol> extends EntityRewriterBase<T> {
  protected EntityRewriter(T protocol) {
    this(protocol, (MetaType)MetaType1_14.OptChat, (MetaType)MetaType1_14.Boolean);
  }
  
  protected EntityRewriter(T protocol, MetaType displayType, MetaType displayVisibilityType) {
    super(protocol, displayType, 2, displayVisibilityType, 3);
  }
  
  public void registerTrackerWithData(ClientboundPacketType packetType, final EntityType fallingBlockType) {
    ((BackwardsProtocol)this.protocol).registerClientbound(packetType, new PacketRemapper() {
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
            handler(EntityRewriter.this.getSpawnTrackerWithDataHandler(fallingBlockType));
          }
        });
  }
  
  public PacketHandler getSpawnTrackerWithDataHandler(EntityType fallingBlockType) {
    return wrapper -> {
        EntityType entityType = setOldEntityId(wrapper);
        if (entityType == fallingBlockType) {
          int blockState = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
          wrapper.set((Type)Type.INT, 0, Integer.valueOf(((BackwardsProtocol)this.protocol).getMappingData().getNewBlockStateId(blockState)));
        } 
      };
  }
  
  public void registerSpawnTracker(ClientboundPacketType packetType) {
    ((BackwardsProtocol)this.protocol).registerClientbound(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            handler(wrapper -> EntityRewriter.this.setOldEntityId(wrapper));
          }
        });
  }
  
  private EntityType setOldEntityId(PacketWrapper wrapper) throws Exception {
    int typeId = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
    EntityType entityType = typeFromId(typeId);
    tracker(wrapper.user()).addEntity(((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue(), entityType);
    int mappedTypeId = newEntityId(entityType.getId());
    if (typeId != mappedTypeId)
      wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(mappedTypeId)); 
    return entityType;
  }
}
