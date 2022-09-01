package com.viaversion.viaversion.protocols.protocol1_14to1_13_2;

import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ServerboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.data.CommandRewriter1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.data.ComponentRewriter1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.data.MappingData;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.metadata.MetadataRewriter1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.EntityPackets;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.PlayerPackets;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets.WorldPackets;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.SoundRewriter;
import com.viaversion.viaversion.rewriter.StatisticsRewriter;

public class Protocol1_14To1_13_2 extends AbstractProtocol<ClientboundPackets1_13, ClientboundPackets1_14, ServerboundPackets1_13, ServerboundPackets1_14> {
  public static final MappingData MAPPINGS = new MappingData();
  
  private final EntityRewriter metadataRewriter = (EntityRewriter)new MetadataRewriter1_14To1_13_2(this);
  
  private final ItemRewriter itemRewriter = (ItemRewriter)new InventoryPackets(this);
  
  public Protocol1_14To1_13_2() {
    super(ClientboundPackets1_13.class, ClientboundPackets1_14.class, ServerboundPackets1_13.class, ServerboundPackets1_14.class);
  }
  
  protected void registerPackets() {
    this.metadataRewriter.register();
    this.itemRewriter.register();
    EntityPackets.register(this);
    WorldPackets.register(this);
    PlayerPackets.register((Protocol)this);
    (new SoundRewriter((Protocol)this)).registerSound((ClientboundPacketType)ClientboundPackets1_13.SOUND);
    (new StatisticsRewriter((Protocol)this)).register((ClientboundPacketType)ClientboundPackets1_13.STATISTICS);
    ComponentRewriter1_14 componentRewriter1_14 = new ComponentRewriter1_14((Protocol)this);
    componentRewriter1_14.registerChatMessage((ClientboundPacketType)ClientboundPackets1_13.CHAT_MESSAGE);
    CommandRewriter1_14 commandRewriter = new CommandRewriter1_14((Protocol)this);
    commandRewriter.registerDeclareCommands((ClientboundPacketType)ClientboundPackets1_13.DECLARE_COMMANDS);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_13.TAGS, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int blockTagsSize = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(blockTagsSize + 6));
                    for (int i = 0; i < blockTagsSize; i++) {
                      wrapper.passthrough(Type.STRING);
                      int[] blockIds = (int[])wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);
                      for (int m = 0; m < blockIds.length; m++)
                        blockIds[m] = Protocol1_14To1_13_2.this.getMappingData().getNewBlockId(blockIds[m]); 
                    } 
                    wrapper.write(Type.STRING, "minecraft:signs");
                    wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[] { this.this$1.this$0
                          .getMappingData().getNewBlockId(150), this.this$1.this$0.getMappingData().getNewBlockId(155) });
                    wrapper.write(Type.STRING, "minecraft:wall_signs");
                    wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[] { this.this$1.this$0
                          .getMappingData().getNewBlockId(155) });
                    wrapper.write(Type.STRING, "minecraft:standing_signs");
                    wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[] { this.this$1.this$0
                          .getMappingData().getNewBlockId(150) });
                    wrapper.write(Type.STRING, "minecraft:fences");
                    wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[] { 189, 248, 472, 473, 474, 475 });
                    wrapper.write(Type.STRING, "minecraft:walls");
                    wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[] { 271, 272 });
                    wrapper.write(Type.STRING, "minecraft:wooden_fences");
                    wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[] { 189, 472, 473, 474, 475 });
                    int itemTagsSize = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(itemTagsSize + 2));
                    for (int j = 0; j < itemTagsSize; j++) {
                      wrapper.passthrough(Type.STRING);
                      int[] itemIds = (int[])wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);
                      for (int m = 0; m < itemIds.length; m++)
                        itemIds[m] = Protocol1_14To1_13_2.this.getMappingData().getNewItemId(itemIds[m]); 
                    } 
                    wrapper.write(Type.STRING, "minecraft:signs");
                    wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[] { this.this$1.this$0
                          .getMappingData().getNewItemId(541) });
                    wrapper.write(Type.STRING, "minecraft:arrows");
                    wrapper.write(Type.VAR_INT_ARRAY_PRIMITIVE, new int[] { 526, 825, 826 });
                    int fluidTagsSize = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                    for (int k = 0; k < fluidTagsSize; k++) {
                      wrapper.passthrough(Type.STRING);
                      wrapper.passthrough(Type.VAR_INT_ARRAY_PRIMITIVE);
                    } 
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(0));
                  }
                });
          }
        });
    cancelServerbound(ServerboundPackets1_14.SET_DIFFICULTY);
    cancelServerbound(ServerboundPackets1_14.LOCK_DIFFICULTY);
    cancelServerbound(ServerboundPackets1_14.UPDATE_JIGSAW_BLOCK);
  }
  
  protected void onMappingDataLoaded() {
    WorldPackets.air = getMappingData().getBlockStateMappings().getNewId(0);
    WorldPackets.voidAir = getMappingData().getBlockStateMappings().getNewId(8591);
    WorldPackets.caveAir = getMappingData().getBlockStateMappings().getNewId(8592);
  }
  
  public void init(UserConnection userConnection) {
    userConnection.addEntityTracker(getClass(), (EntityTracker)new EntityTracker1_14(userConnection));
    if (!userConnection.has(ClientWorld.class))
      userConnection.put((StorableObject)new ClientWorld(userConnection)); 
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
