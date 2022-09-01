package com.viaversion.viaversion.protocols.protocol1_11to1_10;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_11Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
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
import com.viaversion.viaversion.api.type.types.version.Types1_9;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.data.PotionColorMapping;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.metadata.MetadataRewriter1_11To1_10;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.storage.EntityTracker1_11;
import com.viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.EntityRewriter;
import com.viaversion.viaversion.rewriter.SoundRewriter;
import com.viaversion.viaversion.util.Pair;
import java.util.List;

public class Protocol1_11To1_10 extends AbstractProtocol<ClientboundPackets1_9_3, ClientboundPackets1_9_3, ServerboundPackets1_9_3, ServerboundPackets1_9_3> {
  private static final ValueTransformer<Float, Short> toOldByte = new ValueTransformer<Float, Short>((Type)Type.UNSIGNED_BYTE) {
      public Short transform(PacketWrapper wrapper, Float inputValue) throws Exception {
        return Short.valueOf((short)(int)(inputValue.floatValue() * 16.0F));
      }
    };
  
  private final EntityRewriter entityRewriter = (EntityRewriter)new MetadataRewriter1_11To1_10(this);
  
  private final ItemRewriter itemRewriter = (ItemRewriter)new InventoryPackets(this);
  
  public Protocol1_11To1_10() {
    super(ClientboundPackets1_9_3.class, ClientboundPackets1_9_3.class, ServerboundPackets1_9_3.class, ServerboundPackets1_9_3.class);
  }
  
  protected void registerPackets() {
    this.entityRewriter.register();
    this.itemRewriter.register();
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_ENTITY, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.BYTE);
            handler(Protocol1_11To1_10.this.entityRewriter.objectTrackerHandler());
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_MOB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.UNSIGNED_BYTE, (Type)Type.VAR_INT);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map(Types1_9.METADATA_LIST);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    int type = ((Integer)wrapper.get((Type)Type.VAR_INT, 1)).intValue();
                    Entity1_11Types.EntityType entType = MetadataRewriter1_11To1_10.rewriteEntityType(type, (List)wrapper.get(Types1_9.METADATA_LIST, 0));
                    if (entType != null) {
                      wrapper.set((Type)Type.VAR_INT, 1, Integer.valueOf(entType.getId()));
                      wrapper.user().getEntityTracker(Protocol1_11To1_10.class).addEntity(entityId, (EntityType)entType);
                      Protocol1_11To1_10.this.entityRewriter.handleMetadata(entityId, (List)wrapper.get(Types1_9.METADATA_LIST, 0), wrapper.user());
                    } 
                  }
                });
          }
        });
    (new SoundRewriter((Protocol)this, this::getNewSoundId)).registerSound((ClientboundPacketType)ClientboundPackets1_9_3.SOUND);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.COLLECT_ITEM, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(1));
                  }
                });
          }
        });
    this.entityRewriter.registerMetadataRewriter((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_METADATA, Types1_9.METADATA_LIST);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_TELEPORT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.BOOLEAN);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int entityID = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (Via.getConfig().isHologramPatch()) {
                      EntityTracker1_11 tracker = (EntityTracker1_11)wrapper.user().getEntityTracker(Protocol1_11To1_10.class);
                      if (tracker.isHologram(entityID)) {
                        Double newValue = (Double)wrapper.get((Type)Type.DOUBLE, 1);
                        newValue = Double.valueOf(newValue.doubleValue() - Via.getConfig().getHologramYOffset());
                        wrapper.set((Type)Type.DOUBLE, 1, newValue);
                      } 
                    } 
                  }
                });
          }
        });
    this.entityRewriter.registerRemoveEntities((ClientboundPacketType)ClientboundPackets1_9_3.DESTROY_ENTITIES);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.TITLE, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int action = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    if (action >= 2)
                      wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(action + 1)); 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.BLOCK_ACTION, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.VAR_INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper actionWrapper) throws Exception {
                    if (Via.getConfig().isPistonAnimationPatch()) {
                      int id = ((Integer)actionWrapper.get((Type)Type.VAR_INT, 0)).intValue();
                      if (id == 33 || id == 29)
                        actionWrapper.cancel(); 
                    } 
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.BLOCK_ENTITY_DATA, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.NBT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    CompoundTag tag = (CompoundTag)wrapper.get(Type.NBT, 0);
                    if (((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue() == 1)
                      EntityIdRewriter.toClientSpawner(tag); 
                    if (tag.contains("id"))
                      ((StringTag)tag.get("id")).setValue(BlockEntityRewriter.toNewIdentifier((String)tag.get("id").getValue())); 
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
                    wrapper.clearInputBuffer();
                    if (chunk.getBlockEntities() == null)
                      return; 
                    for (CompoundTag tag : chunk.getBlockEntities()) {
                      if (tag.contains("id")) {
                        String identifier = ((StringTag)tag.get("id")).getValue();
                        if (identifier.equals("MobSpawner"))
                          EntityIdRewriter.toClientSpawner(tag); 
                        ((StringTag)tag.get("id")).setValue(BlockEntityRewriter.toNewIdentifier(identifier));
                      } 
                    } 
                  }
                });
          }
        });
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
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.EFFECT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map(Type.POSITION);
            map((Type)Type.INT);
            map((Type)Type.BOOLEAN);
            handler(packetWrapper -> {
                  int effectID = ((Integer)packetWrapper.get((Type)Type.INT, 0)).intValue();
                  if (effectID == 2002) {
                    int data = ((Integer)packetWrapper.get((Type)Type.INT, 1)).intValue();
                    boolean isInstant = false;
                    Pair<Integer, Boolean> newData = PotionColorMapping.getNewData(data);
                    if (newData == null) {
                      Via.getPlatform().getLogger().warning("Received unknown 1.11 -> 1.10.2 potion data (" + data + ")");
                      data = 0;
                    } else {
                      data = ((Integer)newData.getKey()).intValue();
                      isInstant = ((Boolean)newData.getValue()).booleanValue();
                    } 
                    if (isInstant)
                      packetWrapper.set((Type)Type.INT, 0, Integer.valueOf(2007)); 
                    packetWrapper.set((Type)Type.INT, 1, Integer.valueOf(data));
                  } 
                });
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_9_3.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map((Type)Type.FLOAT, Protocol1_11To1_10.toOldByte);
            map((Type)Type.FLOAT, Protocol1_11To1_10.toOldByte);
            map((Type)Type.FLOAT, Protocol1_11To1_10.toOldByte);
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_9_3.CHAT_MESSAGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String msg = (String)wrapper.get(Type.STRING, 0);
                    if (msg.length() > 100)
                      wrapper.set(Type.STRING, 0, msg.substring(0, 100)); 
                  }
                });
          }
        });
  }
  
  private int getNewSoundId(int id) {
    if (id == 196)
      return -1; 
    if (id >= 85)
      id += 2; 
    if (id >= 176)
      id++; 
    if (id >= 197)
      id += 8; 
    if (id >= 207)
      id--; 
    if (id >= 279)
      id += 9; 
    if (id >= 296)
      id++; 
    if (id >= 390)
      id += 4; 
    if (id >= 400)
      id += 3; 
    if (id >= 450)
      id++; 
    if (id >= 455)
      id++; 
    if (id >= 470)
      id++; 
    return id;
  }
  
  public void init(UserConnection userConnection) {
    userConnection.addEntityTracker(getClass(), (EntityTracker)new EntityTracker1_11(userConnection));
    if (!userConnection.has(ClientWorld.class))
      userConnection.put((StorableObject)new ClientWorld(userConnection)); 
  }
  
  public EntityRewriter getEntityRewriter() {
    return this.entityRewriter;
  }
  
  public ItemRewriter getItemRewriter() {
    return this.itemRewriter;
  }
}
