package com.viaversion.viaversion.protocols.protocol1_9to1_8.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk1_8;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.CustomByteType;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.CommandBlockProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.sounds.Effect;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.sounds.SoundEffect;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.ClientChunks;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.types.Chunk1_9to1_8Type;
import io.netty.buffer.ByteBuf;
import java.util.Optional;

public class WorldPackets {
  public static void register(Protocol protocol) {
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.UPDATE_SIGN, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.EFFECT, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map(Type.POSITION);
            map((Type)Type.INT);
            map((Type)Type.BOOLEAN);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    id = Effect.getNewId(id);
                    wrapper.set((Type)Type.INT, 0, Integer.valueOf(id));
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    if (id == 2002) {
                      int data = ((Integer)wrapper.get((Type)Type.INT, 1)).intValue();
                      int newData = ItemRewriter.getNewEffectID(data);
                      wrapper.set((Type)Type.INT, 1, Integer.valueOf(newData));
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.NAMED_SOUND, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    String name = (String)wrapper.get(Type.STRING, 0);
                    SoundEffect effect = SoundEffect.getByName(name);
                    int catid = 0;
                    String newname = name;
                    if (effect != null) {
                      catid = effect.getCategory().getId();
                      newname = effect.getNewName();
                    } 
                    wrapper.set(Type.STRING, 0, newname);
                    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(catid));
                    if (effect != null && effect.isBreaksound()) {
                      EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                      int x = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue();
                      int y = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue();
                      int z = ((Integer)wrapper.passthrough((Type)Type.INT)).intValue();
                      if (tracker.interactedBlockRecently((int)Math.floor(x / 8.0D), (int)Math.floor(y / 8.0D), (int)Math.floor(z / 8.0D)))
                        wrapper.cancel(); 
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientChunks clientChunks = (ClientChunks)wrapper.user().get(ClientChunks.class);
                    Chunk1_9to1_8Type type = new Chunk1_9to1_8Type(clientChunks);
                    Chunk1_8 chunk = (Chunk1_8)wrapper.read((Type)type);
                    if (chunk.isUnloadPacket()) {
                      wrapper.setPacketType((PacketType)ClientboundPackets1_9.UNLOAD_CHUNK);
                      wrapper.write((Type)Type.INT, Integer.valueOf(chunk.getX()));
                      wrapper.write((Type)Type.INT, Integer.valueOf(chunk.getZ()));
                      CommandBlockProvider provider = (CommandBlockProvider)Via.getManager().getProviders().get(CommandBlockProvider.class);
                      provider.unloadChunk(wrapper.user(), chunk.getX(), chunk.getZ());
                    } else {
                      wrapper.write((Type)type, chunk);
                    } 
                    wrapper.read(Type.REMAINING_BYTES);
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.MAP_BULK_CHUNK, null, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  wrapper.cancel();
                  boolean skyLight = ((Boolean)wrapper.read((Type)Type.BOOLEAN)).booleanValue();
                  int count = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                  WorldPackets.ChunkBulkSection[] chunks = new WorldPackets.ChunkBulkSection[count];
                  for (int i = 0; i < count; i++)
                    chunks[i] = new WorldPackets.ChunkBulkSection(wrapper, skyLight); 
                  ClientChunks clientChunks = (ClientChunks)wrapper.user().get(ClientChunks.class);
                  for (WorldPackets.ChunkBulkSection chunk : chunks) {
                    CustomByteType customByteType = new CustomByteType(Integer.valueOf(chunk.getLength()));
                    chunk.setData((byte[])wrapper.read((Type)customByteType));
                    clientChunks.getBulkChunks().add(Long.valueOf(ClientChunks.toLong(chunk.getX(), chunk.getZ())));
                    ByteBuf buffer = null;
                  } 
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.BLOCK_ENTITY_DATA, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.UNSIGNED_BYTE);
            map(Type.NBT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int action = ((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                    if (action == 1) {
                      CompoundTag tag = (CompoundTag)wrapper.get(Type.NBT, 0);
                      if (tag != null)
                        if (tag.contains("EntityId")) {
                          String entity = (String)tag.get("EntityId").getValue();
                          CompoundTag spawn = new CompoundTag();
                          spawn.put("id", (Tag)new StringTag(entity));
                          tag.put("SpawnData", (Tag)spawn);
                        } else {
                          CompoundTag spawn = new CompoundTag();
                          spawn.put("id", (Tag)new StringTag("AreaEffectCloud"));
                          tag.put("SpawnData", (Tag)spawn);
                        }  
                    } 
                    if (action == 2) {
                      CommandBlockProvider provider = (CommandBlockProvider)Via.getManager().getProviders().get(CommandBlockProvider.class);
                      provider.addOrUpdateBlock(wrapper.user(), (Position)wrapper.get(Type.POSITION, 0), (CompoundTag)wrapper.get(Type.NBT, 0));
                      wrapper.cancel();
                    } 
                  }
                });
          }
        });
    protocol.registerClientbound((ClientboundPacketType)ClientboundPackets1_8.BLOCK_CHANGE, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.VAR_INT);
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_9.UPDATE_SIGN, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_9.PLAYER_DIGGING, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT, (Type)Type.UNSIGNED_BYTE);
            map(Type.POSITION);
            map((Type)Type.BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int status = ((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                    if (status == 6)
                      wrapper.cancel(); 
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int status = ((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                    if (status == 5 || status == 4 || status == 3) {
                      EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                      if (entityTracker.isBlocking()) {
                        entityTracker.setBlocking(false);
                        if (!Via.getConfig().isShowShieldWhenSwordInHand())
                          entityTracker.setSecondHand(null); 
                      } 
                    } 
                  }
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_9.USE_ITEM, null, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int hand = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    wrapper.clearInputBuffer();
                    wrapper.setId(8);
                    wrapper.write(Type.POSITION, new Position(-1, (short)-1, -1));
                    wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)255));
                    Item item = Protocol1_9To1_8.getHandItem(wrapper.user());
                    if (Via.getConfig().isShieldBlocking()) {
                      EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                      boolean showShieldWhenSwordInHand = Via.getConfig().isShowShieldWhenSwordInHand();
                      boolean isSword = showShieldWhenSwordInHand ? tracker.hasSwordInHand() : ((item != null && Protocol1_9To1_8.isSword(item.identifier())));
                      if (isSword) {
                        if (hand == 0 && 
                          !tracker.isBlocking()) {
                          tracker.setBlocking(true);
                          if (!showShieldWhenSwordInHand && tracker.getItemInSecondHand() == null) {
                            DataItem dataItem = new DataItem(442, (byte)1, (short)0, null);
                            tracker.setSecondHand((Item)dataItem);
                          } 
                        } 
                        boolean blockUsingMainHand = (Via.getConfig().isNoDelayShieldBlocking() && !showShieldWhenSwordInHand);
                        if ((blockUsingMainHand && hand == 1) || (!blockUsingMainHand && hand == 0))
                          wrapper.cancel(); 
                      } else {
                        if (!showShieldWhenSwordInHand)
                          tracker.setSecondHand(null); 
                        tracker.setBlocking(false);
                      } 
                    } 
                    wrapper.write(Type.ITEM, item);
                    wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)0));
                    wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)0));
                    wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)0));
                  }
                });
          }
        });
    protocol.registerServerbound((ServerboundPacketType)ServerboundPackets1_9.PLAYER_BLOCK_PLACEMENT, new PacketRemapper() {
          public void registerMap() {
            map(Type.POSITION);
            map((Type)Type.VAR_INT, (Type)Type.UNSIGNED_BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int hand = ((Integer)wrapper.read((Type)Type.VAR_INT)).intValue();
                    if (hand != 0)
                      wrapper.cancel(); 
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Item item = Protocol1_9To1_8.getHandItem(wrapper.user());
                    wrapper.write(Type.ITEM, item);
                  }
                });
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.UNSIGNED_BYTE);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int face = ((Short)wrapper.get((Type)Type.UNSIGNED_BYTE, 0)).shortValue();
                    if (face == 255)
                      return; 
                    Position p = (Position)wrapper.get(Type.POSITION, 0);
                    int x = p.getX();
                    int y = p.getY();
                    int z = p.getZ();
                    switch (face) {
                      case 0:
                        y--;
                        break;
                      case 1:
                        y++;
                        break;
                      case 2:
                        z--;
                        break;
                      case 3:
                        z++;
                        break;
                      case 4:
                        x--;
                        break;
                      case 5:
                        x++;
                        break;
                    } 
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.addBlockInteraction(new Position(x, y, z));
                  }
                });
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    CommandBlockProvider provider = (CommandBlockProvider)Via.getManager().getProviders().get(CommandBlockProvider.class);
                    Position pos = (Position)wrapper.get(Type.POSITION, 0);
                    Optional<CompoundTag> tag = provider.get(wrapper.user(), pos);
                    if (tag.isPresent()) {
                      PacketWrapper updateBlockEntity = PacketWrapper.create((PacketType)ClientboundPackets1_9.BLOCK_ENTITY_DATA, null, wrapper.user());
                      updateBlockEntity.write(Type.POSITION, pos);
                      updateBlockEntity.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)2));
                      updateBlockEntity.write(Type.NBT, tag.get());
                      updateBlockEntity.scheduleSend(Protocol1_9To1_8.class);
                    } 
                  }
                });
          }
        });
  }
  
  public static final class WorldPackets {}
}
