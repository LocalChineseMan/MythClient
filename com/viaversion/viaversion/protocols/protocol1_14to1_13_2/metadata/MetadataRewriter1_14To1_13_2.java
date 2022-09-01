package com.viaversion.viaversion.protocols.protocol1_14to1_13_2.metadata;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.VillagerData;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_13Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_14Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_14;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import java.util.List;

public class MetadataRewriter1_14To1_13_2 extends EntityRewriter<Protocol1_14To1_13_2> {
  public MetadataRewriter1_14To1_13_2(Protocol1_14To1_13_2 protocol) {
    super((Protocol)protocol);
    mapTypes((EntityType[])Entity1_13Types.EntityType.values(), Entity1_14Types.class);
    mapEntityType((EntityType)Entity1_13Types.EntityType.OCELOT, (EntityType)Entity1_14Types.CAT);
  }
  
  protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) throws Exception {
    metadata.setMetaType((MetaType)MetaType1_14.byId(metadata.metaType().typeId()));
    EntityTracker1_14 tracker = (EntityTracker1_14)tracker(connection);
    if (metadata.metaType() == MetaType1_14.Slot) {
      ((Protocol1_14To1_13_2)this.protocol).getItemRewriter().handleItemToClient((Item)metadata.getValue());
    } else if (metadata.metaType() == MetaType1_14.BlockID) {
      int data = ((Integer)metadata.getValue()).intValue();
      metadata.setValue(Integer.valueOf(((Protocol1_14To1_13_2)this.protocol).getMappingData().getNewBlockStateId(data)));
    } else if (metadata.metaType() == MetaType1_14.PARTICLE) {
      rewriteParticle((Particle)metadata.getValue());
    } 
    if (type == null)
      return; 
    if (metadata.id() > 5)
      metadata.setId(metadata.id() + 1); 
    if (metadata.id() == 8 && type.isOrHasParent((EntityType)Entity1_14Types.LIVINGENTITY)) {
      float v = ((Number)metadata.getValue()).floatValue();
      if (Float.isNaN(v) && Via.getConfig().is1_14HealthNaNFix())
        metadata.setValue(Float.valueOf(1.0F)); 
    } 
    if (metadata.id() > 11 && type.isOrHasParent((EntityType)Entity1_14Types.LIVINGENTITY))
      metadata.setId(metadata.id() + 1); 
    if (type.isOrHasParent((EntityType)Entity1_14Types.ABSTRACT_INSENTIENT) && 
      metadata.id() == 13) {
      tracker.setInsentientData(entityId, 
          (byte)(((Number)metadata.getValue()).byteValue() & 0xFFFFFFFB | tracker.getInsentientData(entityId) & 0x4));
      metadata.setValue(Byte.valueOf(tracker.getInsentientData(entityId)));
    } 
    if (type.isOrHasParent((EntityType)Entity1_14Types.PLAYER)) {
      if (entityId != tracker.clientEntityId()) {
        if (metadata.id() == 0) {
          byte flags = ((Number)metadata.getValue()).byteValue();
          tracker.setEntityFlags(entityId, flags);
        } else if (metadata.id() == 7) {
          tracker.setRiptide(entityId, ((((Number)metadata.getValue()).byteValue() & 0x4) != 0));
        } 
        if (metadata.id() == 0 || metadata.id() == 7)
          metadatas.add(new Metadata(6, (MetaType)MetaType1_14.Pose, Integer.valueOf(recalculatePlayerPose(entityId, tracker)))); 
      } 
    } else if (type.isOrHasParent((EntityType)Entity1_14Types.ZOMBIE)) {
      if (metadata.id() == 16) {
        tracker.setInsentientData(entityId, 
            (byte)(tracker.getInsentientData(entityId) & 0xFFFFFFFB | (((Boolean)metadata.getValue()).booleanValue() ? 4 : 0)));
        metadatas.remove(metadata);
        metadatas.add(new Metadata(13, (MetaType)MetaType1_14.Byte, Byte.valueOf(tracker.getInsentientData(entityId))));
      } else if (metadata.id() > 16) {
        metadata.setId(metadata.id() - 1);
      } 
    } 
    if (type.isOrHasParent((EntityType)Entity1_14Types.MINECART_ABSTRACT)) {
      if (metadata.id() == 10) {
        int data = ((Integer)metadata.getValue()).intValue();
        metadata.setValue(Integer.valueOf(((Protocol1_14To1_13_2)this.protocol).getMappingData().getNewBlockStateId(data)));
      } 
    } else if (type.is((EntityType)Entity1_14Types.HORSE)) {
      if (metadata.id() == 18) {
        DataItem dataItem;
        metadatas.remove(metadata);
        int armorType = ((Integer)metadata.getValue()).intValue();
        Item armorItem = null;
        if (armorType == 1) {
          dataItem = new DataItem(((Protocol1_14To1_13_2)this.protocol).getMappingData().getNewItemId(727), (byte)1, (short)0, null);
        } else if (armorType == 2) {
          dataItem = new DataItem(((Protocol1_14To1_13_2)this.protocol).getMappingData().getNewItemId(728), (byte)1, (short)0, null);
        } else if (armorType == 3) {
          dataItem = new DataItem(((Protocol1_14To1_13_2)this.protocol).getMappingData().getNewItemId(729), (byte)1, (short)0, null);
        } 
        PacketWrapper equipmentPacket = PacketWrapper.create((PacketType)ClientboundPackets1_14.ENTITY_EQUIPMENT, null, connection);
        equipmentPacket.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
        equipmentPacket.write((Type)Type.VAR_INT, Integer.valueOf(4));
        equipmentPacket.write(Type.FLAT_VAR_INT_ITEM, dataItem);
        equipmentPacket.scheduleSend(Protocol1_14To1_13_2.class);
      } 
    } else if (type.is((EntityType)Entity1_14Types.VILLAGER)) {
      if (metadata.id() == 15)
        metadata.setTypeAndValue((MetaType)MetaType1_14.VillagerData, new VillagerData(2, getNewProfessionId(((Integer)metadata.getValue()).intValue()), 0)); 
    } else if (type.is((EntityType)Entity1_14Types.ZOMBIE_VILLAGER)) {
      if (metadata.id() == 18)
        metadata.setTypeAndValue((MetaType)MetaType1_14.VillagerData, new VillagerData(2, getNewProfessionId(((Integer)metadata.getValue()).intValue()), 0)); 
    } else if (type.isOrHasParent((EntityType)Entity1_14Types.ABSTRACT_ARROW)) {
      if (metadata.id() >= 9)
        metadata.setId(metadata.id() + 1); 
    } else if (type.is((EntityType)Entity1_14Types.FIREWORK_ROCKET)) {
      if (metadata.id() == 8) {
        metadata.setMetaType((MetaType)MetaType1_14.OptVarInt);
        if (metadata.getValue().equals(Integer.valueOf(0)))
          metadata.setValue(null); 
      } 
    } else if (type.isOrHasParent((EntityType)Entity1_14Types.ABSTRACT_SKELETON) && 
      metadata.id() == 14) {
      tracker.setInsentientData(entityId, 
          (byte)(tracker.getInsentientData(entityId) & 0xFFFFFFFB | (((Boolean)metadata.getValue()).booleanValue() ? 4 : 0)));
      metadatas.remove(metadata);
      metadatas.add(new Metadata(13, (MetaType)MetaType1_14.Byte, Byte.valueOf(tracker.getInsentientData(entityId))));
    } 
    if (type.isOrHasParent((EntityType)Entity1_14Types.ABSTRACT_ILLAGER_BASE) && 
      metadata.id() == 14) {
      tracker.setInsentientData(entityId, 
          (byte)(tracker.getInsentientData(entityId) & 0xFFFFFFFB | ((((Number)metadata.getValue()).byteValue() != 0) ? 4 : 0)));
      metadatas.remove(metadata);
      metadatas.add(new Metadata(13, (MetaType)MetaType1_14.Byte, Byte.valueOf(tracker.getInsentientData(entityId))));
    } 
    if ((type.is((EntityType)Entity1_14Types.WITCH) || type.is((EntityType)Entity1_14Types.RAVAGER) || type.isOrHasParent((EntityType)Entity1_14Types.ABSTRACT_ILLAGER_BASE)) && 
      metadata.id() >= 14)
      metadata.setId(metadata.id() + 1); 
  }
  
  public EntityType typeFromId(int type) {
    return Entity1_14Types.getTypeFromId(type);
  }
  
  private static boolean isSneaking(byte flags) {
    return ((flags & 0x2) != 0);
  }
  
  private static boolean isSwimming(byte flags) {
    return ((flags & 0x10) != 0);
  }
  
  private static int getNewProfessionId(int old) {
    switch (old) {
      case 0:
        return 5;
      case 1:
        return 9;
      case 2:
        return 4;
      case 3:
        return 1;
      case 4:
        return 2;
      case 5:
        return 11;
    } 
    return 0;
  }
  
  private static boolean isFallFlying(int entityFlags) {
    return ((entityFlags & 0x80) != 0);
  }
  
  public static int recalculatePlayerPose(int entityId, EntityTracker1_14 tracker) {
    byte flags = tracker.getEntityFlags(entityId);
    int pose = 0;
    if (isFallFlying(flags)) {
      pose = 1;
    } else if (tracker.isSleeping(entityId)) {
      pose = 2;
    } else if (isSwimming(flags)) {
      pose = 3;
    } else if (tracker.isRiptide(entityId)) {
      pose = 4;
    } else if (isSneaking(flags)) {
      pose = 5;
    } 
    return pose;
  }
}
