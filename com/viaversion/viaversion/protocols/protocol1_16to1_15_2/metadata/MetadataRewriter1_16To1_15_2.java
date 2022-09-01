package com.viaversion.viaversion.protocols.protocol1_16to1_15_2.metadata;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_15Types;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_16Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_16;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.Protocol1_16To1_15_2;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import java.util.List;

public class MetadataRewriter1_16To1_15_2 extends EntityRewriter<Protocol1_16To1_15_2> {
  public MetadataRewriter1_16To1_15_2(Protocol1_16To1_15_2 protocol) {
    super((Protocol)protocol);
    mapEntityType((EntityType)Entity1_15Types.ZOMBIE_PIGMAN, (EntityType)Entity1_16Types.ZOMBIFIED_PIGLIN);
    mapTypes((EntityType[])Entity1_15Types.values(), Entity1_16Types.class);
  }
  
  public void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) throws Exception {
    metadata.setMetaType((MetaType)MetaType1_16.byId(metadata.metaType().typeId()));
    if (metadata.metaType() == MetaType1_16.ITEM) {
      ((Protocol1_16To1_15_2)this.protocol).getItemRewriter().handleItemToClient((Item)metadata.getValue());
    } else if (metadata.metaType() == MetaType1_16.BLOCK_STATE) {
      int data = ((Integer)metadata.getValue()).intValue();
      metadata.setValue(Integer.valueOf(((Protocol1_16To1_15_2)this.protocol).getMappingData().getNewBlockStateId(data)));
    } else if (metadata.metaType() == MetaType1_16.PARTICLE) {
      rewriteParticle((Particle)metadata.getValue());
    } 
    if (type == null)
      return; 
    if (type.isOrHasParent((EntityType)Entity1_16Types.MINECART_ABSTRACT) && metadata
      .id() == 10) {
      int data = ((Integer)metadata.getValue()).intValue();
      metadata.setValue(Integer.valueOf(((Protocol1_16To1_15_2)this.protocol).getMappingData().getNewBlockStateId(data)));
    } 
    if (type.isOrHasParent((EntityType)Entity1_16Types.ABSTRACT_ARROW))
      if (metadata.id() == 8) {
        metadatas.remove(metadata);
      } else if (metadata.id() > 8) {
        metadata.setId(metadata.id() - 1);
      }  
  }
  
  public EntityType typeFromId(int type) {
    return Entity1_16Types.getTypeFromId(type);
  }
}
