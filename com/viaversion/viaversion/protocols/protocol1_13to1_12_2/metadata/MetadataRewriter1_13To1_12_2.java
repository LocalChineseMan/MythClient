package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.metadata;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_13Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.minecraft.metadata.MetaType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.minecraft.metadata.types.MetaType1_13;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.EntityTypeRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.ParticleRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets.WorldPackets;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import java.util.List;

public class MetadataRewriter1_13To1_12_2 extends EntityRewriter<Protocol1_13To1_12_2> {
  public MetadataRewriter1_13To1_12_2(Protocol1_13To1_12_2 protocol) {
    super((Protocol)protocol);
  }
  
  protected void handleMetadata(int entityId, EntityType type, Metadata metadata, List<Metadata> metadatas, UserConnection connection) throws Exception {
    if (metadata.metaType().typeId() > 4) {
      metadata.setMetaType((MetaType)MetaType1_13.byId(metadata.metaType().typeId() + 1));
    } else {
      metadata.setMetaType((MetaType)MetaType1_13.byId(metadata.metaType().typeId()));
    } 
    if (metadata.id() == 2)
      if (metadata.getValue() != null && !((String)metadata.getValue()).isEmpty()) {
        metadata.setTypeAndValue((MetaType)MetaType1_13.OptChat, ChatRewriter.legacyTextToJson((String)metadata.getValue()));
      } else {
        metadata.setTypeAndValue((MetaType)MetaType1_13.OptChat, null);
      }  
    if (type == Entity1_13Types.EntityType.ENDERMAN && metadata.id() == 12) {
      int stateId = ((Integer)metadata.getValue()).intValue();
      int id = stateId & 0xFFF;
      int data = stateId >> 12 & 0xF;
      metadata.setValue(Integer.valueOf(id << 4 | data & 0xF));
    } 
    if (metadata.metaType() == MetaType1_13.Slot) {
      metadata.setMetaType((MetaType)MetaType1_13.Slot);
      ((Protocol1_13To1_12_2)this.protocol).getItemRewriter().handleItemToClient((Item)metadata.getValue());
    } else if (metadata.metaType() == MetaType1_13.BlockID) {
      metadata.setValue(Integer.valueOf(WorldPackets.toNewId(((Integer)metadata.getValue()).intValue())));
    } 
    if (type == null)
      return; 
    if (type == Entity1_13Types.EntityType.WOLF && metadata.id() == 17)
      metadata.setValue(Integer.valueOf(15 - ((Integer)metadata.getValue()).intValue())); 
    if (type.isOrHasParent((EntityType)Entity1_13Types.EntityType.ZOMBIE) && 
      metadata.id() > 14)
      metadata.setId(metadata.id() + 1); 
    if (type.isOrHasParent((EntityType)Entity1_13Types.EntityType.MINECART_ABSTRACT) && metadata.id() == 9) {
      int oldId = ((Integer)metadata.getValue()).intValue();
      int combined = (oldId & 0xFFF) << 4 | oldId >> 12 & 0xF;
      int newId = WorldPackets.toNewId(combined);
      metadata.setValue(Integer.valueOf(newId));
    } 
    if (type == Entity1_13Types.EntityType.AREA_EFFECT_CLOUD) {
      if (metadata.id() == 9) {
        int particleId = ((Integer)metadata.getValue()).intValue();
        Metadata parameter1Meta = metaByIndex(10, metadatas);
        Metadata parameter2Meta = metaByIndex(11, metadatas);
        int parameter1 = (parameter1Meta != null) ? ((Integer)parameter1Meta.getValue()).intValue() : 0;
        int parameter2 = (parameter2Meta != null) ? ((Integer)parameter2Meta.getValue()).intValue() : 0;
        Particle particle = ParticleRewriter.rewriteParticle(particleId, new Integer[] { Integer.valueOf(parameter1), Integer.valueOf(parameter2) });
        if (particle != null && particle.getId() != -1)
          metadatas.add(new Metadata(9, (MetaType)MetaType1_13.PARTICLE, particle)); 
      } 
      if (metadata.id() >= 9)
        metadatas.remove(metadata); 
    } 
    if (metadata.id() == 0)
      metadata.setValue(Byte.valueOf((byte)(((Byte)metadata.getValue()).byteValue() & 0xFFFFFFEF))); 
  }
  
  public int newEntityId(int id) {
    return EntityTypeRewriter.getNewId(id);
  }
  
  public EntityType typeFromId(int type) {
    return (EntityType)Entity1_13Types.getTypeFromId(type, false);
  }
  
  public EntityType objectTypeFromId(int type) {
    return (EntityType)Entity1_13Types.getTypeFromId(type, true);
  }
}
