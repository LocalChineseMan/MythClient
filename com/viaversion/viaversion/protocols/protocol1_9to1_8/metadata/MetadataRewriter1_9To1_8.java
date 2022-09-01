package com.viaversion.viaversion.protocols.protocol1_9to1_8.metadata;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.EulerAngle;
import com.viaversion.viaversion.api.minecraft.Vector;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_8;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_9;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import java.util.List;
import java.util.UUID;

public class MetadataRewriter1_9To1_8 extends EntityRewriter<Protocol1_9To1_8> {
  public MetadataRewriter1_9To1_8(Protocol1_9To1_8 protocol) {
    super((Protocol)protocol);
  }
  
  protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) throws Exception {
    String owner;
    UUID toWrite;
    Vector vector;
    EulerAngle angle;
    MetaIndex metaIndex = MetaIndex.searchIndex(type, metadata.id());
    if (metaIndex == null)
      throw new Exception("Could not find valid metadata"); 
    if (metaIndex.getNewType() == null) {
      metadatas.remove(metadata);
      return;
    } 
    metadata.setId(metaIndex.getNewIndex());
    metadata.setMetaTypeUnsafe((MetaType)metaIndex.getNewType());
    Object value = metadata.getValue();
    switch (null.$SwitchMap$com$viaversion$viaversion$api$minecraft$metadata$types$MetaType1_9[metaIndex.getNewType().ordinal()]) {
      case 1:
        if (metaIndex.getOldType() == MetaType1_8.Byte)
          metadata.setValue(value); 
        if (metaIndex.getOldType() == MetaType1_8.Int)
          metadata.setValue(Byte.valueOf(((Integer)value).byteValue())); 
        if (metaIndex == MetaIndex.ENTITY_STATUS && type == Entity1_10Types.EntityType.PLAYER) {
          Byte val = Byte.valueOf((byte)0);
          if ((((Byte)value).byteValue() & 0x10) == 16)
            val = Byte.valueOf((byte)1); 
          int newIndex = MetaIndex.PLAYER_HAND.getNewIndex();
          MetaType1_9 metaType1_9 = MetaIndex.PLAYER_HAND.getNewType();
          metadatas.add(new Metadata(newIndex, (MetaType)metaType1_9, val));
        } 
        return;
      case 2:
        owner = (String)value;
        toWrite = null;
        if (!owner.isEmpty())
          try {
            toWrite = UUID.fromString(owner);
          } catch (Exception exception) {} 
        metadata.setValue(toWrite);
        return;
      case 3:
        if (metaIndex.getOldType() == MetaType1_8.Byte)
          metadata.setValue(Integer.valueOf(((Byte)value).intValue())); 
        if (metaIndex.getOldType() == MetaType1_8.Short)
          metadata.setValue(Integer.valueOf(((Short)value).intValue())); 
        if (metaIndex.getOldType() == MetaType1_8.Int)
          metadata.setValue(value); 
        return;
      case 4:
        metadata.setValue(value);
        return;
      case 5:
        metadata.setValue(value);
        return;
      case 6:
        if (metaIndex == MetaIndex.AGEABLE_AGE) {
          metadata.setValue(Boolean.valueOf((((Byte)value).byteValue() < 0)));
        } else {
          metadata.setValue(Boolean.valueOf((((Byte)value).byteValue() != 0)));
        } 
        return;
      case 7:
        metadata.setValue(value);
        ItemRewriter.toClient((Item)metadata.getValue());
        return;
      case 8:
        vector = (Vector)value;
        metadata.setValue(vector);
        return;
      case 9:
        angle = (EulerAngle)value;
        metadata.setValue(angle);
        return;
      case 10:
        value = Protocol1_9To1_8.fixJson(value.toString());
        metadata.setValue(value);
        return;
      case 11:
        metadata.setValue(Integer.valueOf(((Number)value).intValue()));
        return;
    } 
    metadatas.remove(metadata);
    throw new Exception("Unhandled MetaDataType: " + metaIndex.getNewType());
  }
  
  public EntityType typeFromId(int type) {
    return (EntityType)Entity1_10Types.getTypeFromId(type, false);
  }
  
  public EntityType objectTypeFromId(int type) {
    return (EntityType)Entity1_10Types.getTypeFromId(type, true);
  }
}
