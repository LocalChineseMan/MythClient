package com.viaversion.viaversion.protocols.protocol1_11to1_10.metadata;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_11Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_9;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.EntityIdRewriter;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.Protocol1_11To1_10;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.storage.EntityTracker1_11;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import java.util.List;
import java.util.Optional;

public class MetadataRewriter1_11To1_10 extends EntityRewriter<Protocol1_11To1_10> {
  public MetadataRewriter1_11To1_10(Protocol1_11To1_10 protocol) {
    super((Protocol)protocol);
  }
  
  protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) {
    if (metadata.getValue() instanceof com.viaversion.viaversion.api.minecraft.item.DataItem)
      EntityIdRewriter.toClientItem((Item)metadata.getValue()); 
    if (type == null)
      return; 
    if (type.is((EntityType)Entity1_11Types.EntityType.ELDER_GUARDIAN) || type.is((EntityType)Entity1_11Types.EntityType.GUARDIAN)) {
      int oldid = metadata.id();
      if (oldid == 12) {
        boolean val = ((((Byte)metadata.getValue()).byteValue() & 0x2) == 2);
        metadata.setTypeAndValue((MetaType)MetaType1_9.Boolean, Boolean.valueOf(val));
      } 
    } 
    if (type.isOrHasParent((EntityType)Entity1_11Types.EntityType.ABSTRACT_SKELETON)) {
      int oldid = metadata.id();
      if (oldid == 12)
        metadatas.remove(metadata); 
      if (oldid == 13)
        metadata.setId(12); 
    } 
    if (type.isOrHasParent((EntityType)Entity1_11Types.EntityType.ZOMBIE))
      if (type.is(new EntityType[] { (EntityType)Entity1_11Types.EntityType.ZOMBIE, (EntityType)Entity1_11Types.EntityType.HUSK }) && metadata.id() == 14) {
        metadatas.remove(metadata);
      } else if (metadata.id() == 15) {
        metadata.setId(14);
      } else if (metadata.id() == 14) {
        metadata.setId(15);
      }  
    if (type.isOrHasParent((EntityType)Entity1_11Types.EntityType.ABSTRACT_HORSE)) {
      int oldid = metadata.id();
      if (oldid == 14)
        metadatas.remove(metadata); 
      if (oldid == 16)
        metadata.setId(14); 
      if (oldid == 17)
        metadata.setId(16); 
      if (!type.is((EntityType)Entity1_11Types.EntityType.HORSE))
        if (metadata.id() == 15 || metadata.id() == 16)
          metadatas.remove(metadata);  
      if (type.is(new EntityType[] { (EntityType)Entity1_11Types.EntityType.DONKEY, (EntityType)Entity1_11Types.EntityType.MULE }))
        if (metadata.id() == 13)
          if ((((Byte)metadata.getValue()).byteValue() & 0x8) == 8) {
            metadatas.add(new Metadata(15, (MetaType)MetaType1_9.Boolean, Boolean.valueOf(true)));
          } else {
            metadatas.add(new Metadata(15, (MetaType)MetaType1_9.Boolean, Boolean.valueOf(false)));
          }   
    } 
    if (type.is((EntityType)Entity1_11Types.EntityType.ARMOR_STAND) && Via.getConfig().isHologramPatch()) {
      Metadata flags = metaByIndex(11, metadatas);
      Metadata customName = metaByIndex(2, metadatas);
      Metadata customNameVisible = metaByIndex(3, metadatas);
      if (metadata.id() == 0 && flags != null && customName != null && customNameVisible != null) {
        byte data = ((Byte)metadata.getValue()).byteValue();
        if ((data & 0x20) == 32 && (((Byte)flags.getValue()).byteValue() & 0x1) == 1 && 
          !((String)customName.getValue()).isEmpty() && ((Boolean)customNameVisible.getValue()).booleanValue()) {
          EntityTracker1_11 tracker = (EntityTracker1_11)tracker(connection);
          if (!tracker.isHologram(entityId)) {
            tracker.addHologram(entityId);
            try {
              PacketWrapper wrapper = PacketWrapper.create((PacketType)ClientboundPackets1_9_3.ENTITY_POSITION, null, connection);
              wrapper.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
              wrapper.write((Type)Type.SHORT, Short.valueOf((short)0));
              wrapper.write((Type)Type.SHORT, Short.valueOf((short)(int)(128.0D * -Via.getConfig().getHologramYOffset() * 32.0D)));
              wrapper.write((Type)Type.SHORT, Short.valueOf((short)0));
              wrapper.write((Type)Type.BOOLEAN, Boolean.valueOf(true));
              wrapper.send(Protocol1_11To1_10.class);
            } catch (Exception e) {
              e.printStackTrace();
            } 
          } 
        } 
      } 
    } 
  }
  
  public EntityType typeFromId(int type) {
    return (EntityType)Entity1_11Types.getTypeFromId(type, false);
  }
  
  public EntityType objectTypeFromId(int type) {
    return (EntityType)Entity1_11Types.getTypeFromId(type, true);
  }
  
  public static Entity1_11Types.EntityType rewriteEntityType(int numType, List<Metadata> metadata) {
    Optional<Entity1_11Types.EntityType> optType = Entity1_11Types.EntityType.findById(numType);
    if (!optType.isPresent()) {
      Via.getManager().getPlatform().getLogger().severe("Error: could not find Entity type " + numType + " with metadata: " + metadata);
      return null;
    } 
    Entity1_11Types.EntityType type = optType.get();
    try {
      if (type.is((EntityType)Entity1_11Types.EntityType.GUARDIAN)) {
        Optional<Metadata> options = getById(metadata, 12);
        if (options.isPresent() && ((
          (Byte)((Metadata)options.get()).getValue()).byteValue() & 0x4) == 4)
          return Entity1_11Types.EntityType.ELDER_GUARDIAN; 
      } 
      if (type.is((EntityType)Entity1_11Types.EntityType.SKELETON)) {
        Optional<Metadata> options = getById(metadata, 12);
        if (options.isPresent()) {
          if (((Integer)((Metadata)options.get()).getValue()).intValue() == 1)
            return Entity1_11Types.EntityType.WITHER_SKELETON; 
          if (((Integer)((Metadata)options.get()).getValue()).intValue() == 2)
            return Entity1_11Types.EntityType.STRAY; 
        } 
      } 
      if (type.is((EntityType)Entity1_11Types.EntityType.ZOMBIE)) {
        Optional<Metadata> options = getById(metadata, 13);
        if (options.isPresent()) {
          int value = ((Integer)((Metadata)options.get()).getValue()).intValue();
          if (value > 0 && value < 6) {
            metadata.add(new Metadata(16, (MetaType)MetaType1_9.VarInt, Integer.valueOf(value - 1)));
            return Entity1_11Types.EntityType.ZOMBIE_VILLAGER;
          } 
          if (value == 6)
            return Entity1_11Types.EntityType.HUSK; 
        } 
      } 
      if (type.is((EntityType)Entity1_11Types.EntityType.HORSE)) {
        Optional<Metadata> options = getById(metadata, 14);
        if (options.isPresent()) {
          if (((Integer)((Metadata)options.get()).getValue()).intValue() == 0)
            return Entity1_11Types.EntityType.HORSE; 
          if (((Integer)((Metadata)options.get()).getValue()).intValue() == 1)
            return Entity1_11Types.EntityType.DONKEY; 
          if (((Integer)((Metadata)options.get()).getValue()).intValue() == 2)
            return Entity1_11Types.EntityType.MULE; 
          if (((Integer)((Metadata)options.get()).getValue()).intValue() == 3)
            return Entity1_11Types.EntityType.ZOMBIE_HORSE; 
          if (((Integer)((Metadata)options.get()).getValue()).intValue() == 4)
            return Entity1_11Types.EntityType.SKELETON_HORSE; 
        } 
      } 
    } catch (Exception e) {
      if (!Via.getConfig().isSuppressMetadataErrors() || Via.getManager().isDebug()) {
        Via.getPlatform().getLogger().warning("An error occurred with entity type rewriter");
        Via.getPlatform().getLogger().warning("Metadata: " + metadata);
        e.printStackTrace();
      } 
    } 
    return type;
  }
  
  public static Optional<Metadata> getById(List<Metadata> metadatas, int id) {
    for (Metadata metadata : metadatas) {
      if (metadata.id() == id)
        return Optional.of(metadata); 
    } 
    return Optional.empty();
  }
}
