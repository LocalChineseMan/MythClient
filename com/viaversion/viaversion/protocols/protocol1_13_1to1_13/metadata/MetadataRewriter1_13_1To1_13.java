package com.viaversion.viaversion.protocols.protocol1_13_1to1_13.metadata;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_13Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_13;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.protocols.protocol1_13_1to1_13.Protocol1_13_1To1_13;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import java.util.List;

public class MetadataRewriter1_13_1To1_13 extends EntityRewriter<Protocol1_13_1To1_13> {
  public MetadataRewriter1_13_1To1_13(Protocol1_13_1To1_13 protocol) {
    super((Protocol)protocol);
  }
  
  protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) {
    if (metadata.metaType() == MetaType1_13.Slot) {
      ((Protocol1_13_1To1_13)this.protocol).getItemRewriter().handleItemToClient((Item)metadata.getValue());
    } else if (metadata.metaType() == MetaType1_13.BlockID) {
      int data = ((Integer)metadata.getValue()).intValue();
      metadata.setValue(Integer.valueOf(((Protocol1_13_1To1_13)this.protocol).getMappingData().getNewBlockStateId(data)));
    } else if (metadata.metaType() == MetaType1_13.PARTICLE) {
      rewriteParticle((Particle)metadata.getValue());
    } 
    if (type == null)
      return; 
    if (type.isOrHasParent((EntityType)Entity1_13Types.EntityType.MINECART_ABSTRACT) && metadata.id() == 9) {
      int data = ((Integer)metadata.getValue()).intValue();
      metadata.setValue(Integer.valueOf(((Protocol1_13_1To1_13)this.protocol).getMappingData().getNewBlockStateId(data)));
    } else if (type.isOrHasParent((EntityType)Entity1_13Types.EntityType.ABSTRACT_ARROW) && metadata.id() >= 7) {
      metadata.setId(metadata.id() + 1);
    } 
  }
  
  public EntityType typeFromId(int type) {
    return (EntityType)Entity1_13Types.getTypeFromId(type, false);
  }
  
  public EntityType objectTypeFromId(int type) {
    return (EntityType)Entity1_13Types.getTypeFromId(type, true);
  }
}
