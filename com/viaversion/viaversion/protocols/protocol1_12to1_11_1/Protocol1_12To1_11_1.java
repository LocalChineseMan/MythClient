package com.viaversion.viaversion.protocols.protocol1_12to1_11_1;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_12Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.platform.providers.Provider;
import com.viaversion.viaversion.api.platform.providers.ViaProviders;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_12;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.metadata.MetadataRewriter1_12To1_11_1;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.providers.InventoryQuickMoveProvider;
import com.viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import com.viaversion.viaversion.rewriter.SoundRewriter;

public class Protocol1_12To1_11_1 extends AbstractProtocol<ClientboundPackets1_9_3, ClientboundPackets1_12, ServerboundPackets1_9_3, ServerboundPackets1_12> {
  private final EntityRewriter metadataRewriter = (EntityRewriter)new MetadataRewriter1_12To1_11_1(this);
  
  private final ItemRewriter itemRewriter = (ItemRewriter)new InventoryPackets(this);
  
  public Protocol1_12To1_11_1() {
    super(ClientboundPackets1_9_3.class, ClientboundPackets1_12.class, ServerboundPackets1_9_3.class, ServerboundPackets1_12.class);
  }
  
  protected void registerPackets() {
    this.metadataRewriter.register();
    this.itemRewriter.register();
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.BYTE);
            handler(Protocol1_12To1_11_1.this.metadataRewriter.objectTrackerHandler());
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_MOB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.VAR_INT);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map(Types1_12.METADATA_LIST);
            handler(Protocol1_12To1_11_1.this.metadataRewriter.trackerAndRewriterHandler(Types1_12.METADATA_LIST));
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.CHAT_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    if (!Via.getConfig().is1_12NBTArrayFix())
                      return; 
                    try {
                      JsonElement obj = (JsonElement)Protocol1_9To1_8.FIX_JSON.transform(null, ((JsonElement)wrapper.passthrough(Type.COMPONENT)).toString());
                      TranslateRewriter.toClient(obj, wrapper.user());
                      ChatItemRewriter.toClient(obj, wrapper.user());
                      wrapper.set(Type.COMPONENT, 0, obj);
                    } catch (Exception e) {
                      e.printStackTrace();
                    } 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    Chunk1_9_3_4Type type = new Chunk1_9_3_4Type(clientWorld);
                    Chunk chunk = (Chunk)wrapper.passthrough((Type)type);
                    for (int i = 0; i < (chunk.getSections()).length; i++) {
                      ChunkSection section = chunk.getSections()[i];
                      if (section != null)
                        for (int y = 0; y < 16; y++) {
                          for (int z = 0; z < 16; z++) {
                            for (int x = 0; x < 16; x++) {
                              int block = section.getBlockWithoutData(x, y, z);
                              if (block == 26) {
                                CompoundTag tag = new CompoundTag();
                                tag.put("color", (Tag)new IntTag(14));
                                tag.put("x", (Tag)new IntTag(x + (chunk.getX() << 4)));
                                tag.put("y", (Tag)new IntTag(y + (i << 4)));
                                tag.put("z", (Tag)new IntTag(z + (chunk.getZ() << 4)));
                                tag.put("id", (Tag)new StringTag("minecraft:bed"));
                                chunk.getBlockEntities().add(tag);
                              } 
                            } 
                          } 
                        }  
                    } 
                  }
                });
          }
        });
    this.metadataRewriter.registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_9_3.DESTROY_ENTITIES);
    this.metadataRewriter.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_METADATA, Types1_12.METADATA_LIST);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.INT);
            handler(wrapper -> {
                  ClientWorld clientChunks = (ClientWorld)wrapper.user().get(ClientWorld.class);
                  int dimensionId = ((Integer)wrapper.get((Type)Type.INT, 1)).intValue();
                  clientChunks.setEnvironment(dimensionId);
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            handler(wrapper -> {
                  ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                  int dimensionId = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                  clientWorld.setEnvironment(dimensionId);
                });
          }
        });
    (new SoundRewriter((Protocol)this, this::getNewSoundId)).registerSound((ClientboundPacketType)ClientboundPackets1_9_3.SOUND);
    cancelServerbound(ServerboundPackets1_12.PREPARE_CRAFTING_GRID);
    registerServerbound(ServerboundPackets1_12.CLIENT_SETTINGS, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map((Type)Type.BYTE);
            map((Type)Type.VAR_INT);
            map((Type)Type.BOOLEAN);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String locale = (String)wrapper.get(Type.STRING, 0);
                    if (locale.length() > 7)
                      wrapper.set(Type.STRING, 0, locale.substring(0, 7)); 
                  }
                });
          }
        });
    cancelServerbound(ServerboundPackets1_12.RECIPE_BOOK_DATA);
    cancelServerbound(ServerboundPackets1_12.ADVANCEMENT_TAB);
  }
  
  private int getNewSoundId(int id) {
    int newId = id;
    if (id >= 26)
      newId += 2; 
    if (id >= 70)
      newId += 4; 
    if (id >= 74)
      newId++; 
    if (id >= 143)
      newId += 3; 
    if (id >= 185)
      newId++; 
    if (id >= 263)
      newId += 7; 
    if (id >= 301)
      newId += 33; 
    if (id >= 317)
      newId += 2; 
    if (id >= 491)
      newId += 3; 
    return newId;
  }
  
  public void register(ViaProviders providers) {
    providers.register(InventoryQuickMoveProvider.class, (Provider)new InventoryQuickMoveProvider());
  }
  
  public void init(UserConnection userConnection) {
    userConnection.addEntityTracker(getClass(), (EntityTracker)new EntityTrackerBase(userConnection, (EntityType)Entity1_12Types.EntityType.PLAYER));
    if (!userConnection.has(ClientWorld.class))
      userConnection.put((StorableObject)new ClientWorld(userConnection)); 
  }
  
  public EntityRewriter getEntityRewriter() {
    return this.metadataRewriter;
  }
  
  public ItemRewriter getItemRewriter() {
    return this.itemRewriter;
  }
}
