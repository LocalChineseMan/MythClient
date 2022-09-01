package com.viaversion.viaversion.protocols.protocol1_15to1_14_4;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_15Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.data.MappingData;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.metadata.MetadataRewriter1_15To1_14_4;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.packets.EntityPackets;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.packets.PlayerPackets;
import com.viaversion.viaversion.protocols.protocol1_15to1_14_4.packets.WorldPackets;
import com.viaversion.viaversion.rewriter.SoundRewriter;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.rewriter.TagRewriter;

public class Protocol1_15To1_14_4 extends AbstractProtocol<ClientboundPackets1_14, ClientboundPackets1_15, ServerboundPackets1_14, ServerboundPackets1_14> {
  public static final MappingData MAPPINGS = new MappingData();
  
  private final EntityRewriter metadataRewriter = (EntityRewriter)new MetadataRewriter1_15To1_14_4(this);
  
  private final ItemRewriter itemRewriter = (ItemRewriter)new InventoryPackets(this);
  
  private TagRewriter tagRewriter;
  
  public Protocol1_15To1_14_4() {
    super(ClientboundPackets1_14.class, ClientboundPackets1_15.class, ServerboundPackets1_14.class, ServerboundPackets1_14.class);
  }
  
  protected void registerPackets() {
    this.metadataRewriter.register();
    this.itemRewriter.register();
    EntityPackets.register(this);
    PlayerPackets.register((Protocol)this);
    WorldPackets.register(this);
    SoundRewriter soundRewriter = new SoundRewriter((Protocol)this);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_14.ENTITY_SOUND);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_14.SOUND);
    (new StatisticsRewriter((Protocol)this)).register((ClientboundPacketType)ClientboundPackets1_14.STATISTICS);
    registerServerbound((ServerboundPacketType)ServerboundPackets1_14.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> Protocol1_15To1_14_4.this.itemRewriter.handleItemToServer((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
          }
        });
    this.tagRewriter = new TagRewriter((Protocol)this);
    this.tagRewriter.register((ClientboundPacketType)ClientboundPackets1_14.TAGS, RegistryType.ENTITY);
  }
  
  protected void onMappingDataLoaded() {
    int[] shulkerBoxes = new int[17];
    int shulkerBoxOffset = 501;
    for (int i = 0; i < 17; i++)
      shulkerBoxes[i] = shulkerBoxOffset + i; 
    this.tagRewriter.addTag(RegistryType.BLOCK, "minecraft:shulker_boxes", shulkerBoxes);
  }
  
  public void init(UserConnection connection) {
    addEntityTracker(connection, (EntityTracker)new EntityTrackerBase(connection, (EntityType)Entity1_15Types.PLAYER));
  }
  
  public MappingData getMappingData() {
    return MAPPINGS;
  }
  
  public EntityRewriter getEntityRewriter() {
    return this.metadataRewriter;
  }
  
  public ItemRewriter getItemRewriter() {
    return this.itemRewriter;
  }
}
