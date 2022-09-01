package com.viaversion.viaversion.protocols.protocol1_10to1_9_3;

import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_9;
import com.viaversion.viaversion.protocols.protocol1_10to1_9_3.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_10to1_9_3.storage.ResourcePackTracker;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Protocol1_10To1_9_3_4 extends AbstractProtocol<ClientboundPackets1_9_3, ClientboundPackets1_9_3, ServerboundPackets1_9_3, ServerboundPackets1_9_3> {
  public static final ValueTransformer<Short, Float> TO_NEW_PITCH = new ValueTransformer<Short, Float>((Type)Type.FLOAT) {
      public Float transform(PacketWrapper wrapper, Short inputValue) throws Exception {
        return Float.valueOf(inputValue.shortValue() / 63.0F);
      }
    };
  
  public static final ValueTransformer<List<Metadata>, List<Metadata>> TRANSFORM_METADATA = new ValueTransformer<List<Metadata>, List<Metadata>>(Types1_9.METADATA_LIST) {
      public List<Metadata> transform(PacketWrapper wrapper, List<Metadata> inputValue) throws Exception {
        List<Metadata> metaList = new CopyOnWriteArrayList<>(inputValue);
        for (Metadata m : metaList) {
          if (m.id() >= 5)
            m.setId(m.id() + 1); 
        } 
        return metaList;
      }
    };
  
  private final ItemRewriter itemRewriter = (ItemRewriter)new InventoryPackets(this);
  
  public Protocol1_10To1_9_3_4() {
    super(ClientboundPackets1_9_3.class, ClientboundPackets1_9_3.class, ServerboundPackets1_9_3.class, ServerboundPackets1_9_3.class);
  }
  
  protected void registerPackets() {
    this.itemRewriter.register();
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.NAMED_SOUND, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map((Type)Type.VAR_INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.FLOAT);
            map((Type)Type.UNSIGNED_BYTE, Protocol1_10To1_9_3_4.TO_NEW_PITCH);
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SOUND, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map((Type)Type.VAR_INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.FLOAT);
            map((Type)Type.UNSIGNED_BYTE, Protocol1_10To1_9_3_4.TO_NEW_PITCH);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    int id = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(Protocol1_10To1_9_3_4.this.getNewSoundId(id)));
                  }
                });
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.ENTITY_METADATA, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Types1_9.METADATA_LIST, Protocol1_10To1_9_3_4.TRANSFORM_METADATA);
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_MOB, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.UNSIGNED_BYTE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map((Type)Type.SHORT);
            map(Types1_9.METADATA_LIST, Protocol1_10To1_9_3_4.TRANSFORM_METADATA);
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.SPAWN_PLAYER, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            map(Type.UUID);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.DOUBLE);
            map((Type)Type.BYTE);
            map((Type)Type.BYTE);
            map(Types1_9.METADATA_LIST, Protocol1_10To1_9_3_4.TRANSFORM_METADATA);
          }
        });
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.RESOURCE_PACK, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map(Type.STRING);
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ResourcePackTracker tracker = (ResourcePackTracker)wrapper.user().get(ResourcePackTracker.class);
                    tracker.setLastHash((String)wrapper.get(Type.STRING, 1));
                  }
                });
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_9_3.RESOURCE_PACK_STATUS, new PacketRemapper() {
          public void registerMap() {
            handler(new PacketHandler() {
                  public void handle(PacketWrapper wrapper) throws Exception {
                    ResourcePackTracker tracker = (ResourcePackTracker)wrapper.user().get(ResourcePackTracker.class);
                    wrapper.write(Type.STRING, tracker.getLastHash());
                    wrapper.write((Type)Type.VAR_INT, wrapper.read((Type)Type.VAR_INT));
                  }
                });
          }
        });
  }
  
  public int getNewSoundId(int id) {
    int newId = id;
    if (id >= 24)
      newId++; 
    if (id >= 248)
      newId += 4; 
    if (id >= 296)
      newId += 6; 
    if (id >= 354)
      newId += 4; 
    if (id >= 372)
      newId += 4; 
    return newId;
  }
  
  public void init(UserConnection userConnection) {
    userConnection.put((StorableObject)new ResourcePackTracker());
  }
  
  public ItemRewriter getItemRewriter() {
    return this.itemRewriter;
  }
}
