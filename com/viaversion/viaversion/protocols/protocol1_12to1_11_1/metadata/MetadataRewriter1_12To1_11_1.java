package com.viaversion.viaversion.protocols.protocol1_12to1_11_1.metadata;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_12Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.BedRewriter;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.Protocol1_12To1_11_1;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import java.util.List;

public class MetadataRewriter1_12To1_11_1 extends EntityRewriter<Protocol1_12To1_11_1> {
  public MetadataRewriter1_12To1_11_1(Protocol1_12To1_11_1 protocol) {
    super((Protocol)protocol);
  }
  
  protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) {
    if (metadata.getValue() instanceof com.viaversion.viaversion.api.minecraft.item.DataItem)
      BedRewriter.toClientItem((Item)metadata.getValue()); 
    if (type == null)
      return; 
    if (type == Entity1_12Types.EntityType.EVOCATION_ILLAGER && 
      metadata.id() == 12)
      metadata.setId(13); 
  }
  
  public EntityType typeFromId(int type) {
    return (EntityType)Entity1_12Types.getTypeFromId(type, false);
  }
  
  public EntityType objectTypeFromId(int type) {
    return (EntityType)Entity1_12Types.getTypeFromId(type, true);
  }
}
