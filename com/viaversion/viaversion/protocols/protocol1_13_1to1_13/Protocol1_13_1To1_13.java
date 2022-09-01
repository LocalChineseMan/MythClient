package com.viaversion.viaversion.protocols.protocol1_13_1to1_13;

import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.MappingDataBase;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.RegistryType;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_13Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.protocols.protocol1_13_1to1_13.metadata.MetadataRewriter1_13_1To1_13;
import com.viaversion.viaversion.protocols.protocol1_13_1to1_13.packets.EntityPackets;
import com.viaversion.viaversion.protocols.protocol1_13_1to1_13.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_13_1to1_13.packets.WorldPackets;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;
import com.viaversion.viaversion.rewriter.TagRewriter;

public class Protocol1_13_1To1_13 extends AbstractProtocol<ClientboundPackets1_13, ClientboundPackets1_13, ServerboundPackets1_13, ServerboundPackets1_13> {
  public static final MappingData MAPPINGS = (MappingData)new MappingDataBase("1.13", "1.13.2", true);
  
  private final EntityRewriter entityRewriter = (EntityRewriter)new MetadataRewriter1_13_1To1_13(this);
  
  private final ItemRewriter itemRewriter = (ItemRewriter)new InventoryPackets(this);
  
  public Protocol1_13_1To1_13() {
    super(ClientboundPackets1_13.class, ClientboundPackets1_13.class, ServerboundPackets1_13.class, ServerboundPackets1_13.class);
  }
  
  protected void registerPackets() {
    this.entityRewriter.register();
    this.itemRewriter.register();
    EntityPackets.register(this);
    WorldPackets.register((Protocol)this);
    registerServerbound((ServerboundPacketType)ServerboundPackets1_13.TAB_COMPLETE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.STRING, new ValueTransformer<String, String>(Type.STRING) {
                  public String transform(PacketWrapper wrapper, String inputValue) {
                    return inputValue.startsWith("/") ? inputValue.substring(1) : inputValue;
                  }
                });
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_13.EDIT_BOOK, new PacketRemapper() {
          public void registerMap() {
            map(Type.FLAT_ITEM);
            map((Type)Type.BOOLEAN);
            handler(wrapper -> {
                  Item item = (Item)wrapper.get(Type.FLAT_ITEM, 0);
                  Protocol1_13_1To1_13.this.itemRewriter.handleItemToServer(item);
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int hand = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    if (hand == 1)
                      wrapper.cancel(); 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_13.TAB_COMPLETE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int start = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                    wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(start + 1));
                    int count = ((Integer)wrapper.get((Type)Type.VAR_INT, 3)).intValue();
                    for (int i = 0; i < count; i++) {
                      wrapper.passthrough(Type.STRING);
                      boolean hasTooltip = ((Boolean)wrapper.passthrough((Type)Type.BOOLEAN)).booleanValue();
                      if (hasTooltip)
                        wrapper.passthrough(Type.STRING); 
                    } 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_13.BOSSBAR, new PacketRemapper() {
          public void registerMap() {
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (action == 0) {
                      wrapper.passthrough(Type.COMPONENT);
                      wrapper.passthrough((Type)Type.FLOAT);
                      wrapper.passthrough((Type)Type.VAR_INT);
                      wrapper.passthrough((Type)Type.VAR_INT);
                      short flags = (short)((Byte)wrapper.read((Type)Type.BYTE)).byteValue();
                      if ((flags & 0x2) != 0)
                        flags = (short)(flags | 0x4); 
                      wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf(flags));
                    } 
                  }
                });
          }
        });
    (new TagRewriter((Protocol)this)).register((ClientboundPacketType)ClientboundPackets1_13.TAGS, RegistryType.ITEM);
    (new StatisticsRewriter((Protocol)this)).register((ClientboundPacketType)ClientboundPackets1_13.STATISTICS);
  }
  
  public void init(UserConnection userConnection) {
    userConnection.addEntityTracker(getClass(), (EntityTracker)new EntityTrackerBase(userConnection, (EntityType)Entity1_13Types.EntityType.PLAYER));
    if (!userConnection.has(ClientWorld.class))
      userConnection.put((StorableObject)new ClientWorld(userConnection)); 
  }
  
  public MappingData getMappingData() {
    return MAPPINGS;
  }
  
  public EntityRewriter getEntityRewriter() {
    return this.entityRewriter;
  }
  
  public ItemRewriter getItemRewriter() {
    return this.itemRewriter;
  }
}
