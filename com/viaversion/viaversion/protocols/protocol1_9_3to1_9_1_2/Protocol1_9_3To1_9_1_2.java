package com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2;

import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.chunks.FakeTileEntity;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.types.Chunk1_9_1_2Type;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import java.util.List;

public class Protocol1_9_3To1_9_1_2 extends AbstractProtocol<ClientboundPackets1_9, ClientboundPackets1_9_3, ServerboundPackets1_9, ServerboundPackets1_9_3> {
  public static final ValueTransformer<Short, Short> ADJUST_PITCH = new ValueTransformer<Short, Short>((Type)Type.UNSIGNED_BYTE, (Type)Type.UNSIGNED_BYTE) {
      public Short transform(PacketWrapper wrapper, Short inputValue) throws Exception {
        return Short.valueOf((short)Math.round(inputValue.shortValue() / 63.5F * 63.0F));
      }
    };
  
  public Protocol1_9_3To1_9_1_2() {
    super(ClientboundPackets1_9.class, ClientboundPackets1_9_3.class, ServerboundPackets1_9.class, ServerboundPackets1_9_3.class);
  }
  
  protected void registerPackets() {
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9.UPDATE_SIGN, null, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    Position position = (Position)wrapper.read(Type.POSITION);
                    JsonElement[] lines = new JsonElement[4];
                    for (int i = 0; i < 4; i++)
                      lines[i] = (JsonElement)wrapper.read(Type.COMPONENT); 
                    wrapper.clearInputBuffer();
                    wrapper.setId(9);
                    wrapper.write(Type.POSITION, position);
                    wrapper.write((Type)Type.UNSIGNED_BYTE, Short.valueOf((short)9));
                    CompoundTag tag = new CompoundTag();
                    tag.put("id", (Tag)new StringTag("Sign"));
                    tag.put("x", (Tag)new IntTag(position.getX()));
                    tag.put("y", (Tag)new IntTag(position.getY()));
                    tag.put("z", (Tag)new IntTag(position.getZ()));
                    for (int j = 0; j < lines.length; j++)
                      tag.put("Text" + (j + 1), (Tag)new StringTag(lines[j].toString())); 
                    wrapper.write(Type.NBT, tag);
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9.CHUNK_DATA, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    Chunk1_9_1_2Type type = new Chunk1_9_1_2Type(clientWorld);
                    Chunk chunk = (Chunk)wrapper.passthrough((Type)type);
                    List<CompoundTag> tags = chunk.getBlockEntities();
                    for (int i = 0; i < (chunk.getSections()).length; i++) {
                      ChunkSection section = chunk.getSections()[i];
                      if (section != null)
                        for (int y = 0; y < 16; y++) {
                          for (int z = 0; z < 16; z++) {
                            for (int x = 0; x < 16; x++) {
                              int block = section.getBlockWithoutData(x, y, z);
                              if (FakeTileEntity.hasBlock(block))
                                tags.add(FakeTileEntity.getFromBlock(x + (chunk.getX() << 4), y + (i << 4), z + (chunk.getZ() << 4), block)); 
                            } 
                          } 
                        }  
                    } 
                    wrapper.write(Type.NBT_ARRAY, chunk.getBlockEntities().toArray((Object[])new CompoundTag[0]));
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9.JOIN_GAME, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientChunks = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    int dimensionId = ((Integer)wrapper.get((Type)Type.INT, 1)).intValue();
                    clientChunks.setEnvironment(dimensionId);
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9.RESPAWN, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.INT);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ClientWorld clientWorld = (ClientWorld)wrapper.user().get(ClientWorld.class);
                    int dimensionId = ((Integer)wrapper.get((Type)Type.INT, 0)).intValue();
                    clientWorld.setEnvironment(dimensionId);
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9.SOUND, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.FLOAT);
            map(Protocol1_9_3To1_9_1_2.ADJUST_PITCH);
          }
        });
  }
  
  public void init(UserConnection user) {
    if (!user.has(ClientWorld.class))
      user.put((StorableObject)new ClientWorld(user)); 
  }
}
