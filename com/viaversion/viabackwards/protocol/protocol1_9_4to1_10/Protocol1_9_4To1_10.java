package com.viaversion.viabackwards.protocol.protocol1_9_4to1_10;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viabackwards.api.rewriters.SoundRewriter;
import com.viaversion.viabackwards.protocol.protocol1_9_4to1_10.packets.BlockItemPackets1_10;
import com.viaversion.viabackwards.protocol.protocol1_9_4to1_10.packets.EntityPackets1_10;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.remapper.ValueTransformer;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;

public class Protocol1_9_4To1_10 extends BackwardsProtocol<ClientboundPackets1_9_3, ClientboundPackets1_9_3, ServerboundPackets1_9_3, ServerboundPackets1_9_3> {
  public static final BackwardsMappings MAPPINGS = new BackwardsMappings("1.10", "1.9.4", null, true);
  
  private static final ValueTransformer<Float, Short> TO_OLD_PITCH = new ValueTransformer<Float, Short>((Type)Type.UNSIGNED_BYTE) {
      public Short transform(PacketWrapper packetWrapper, Float inputValue) throws Exception {
        return Short.valueOf((short)Math.round(inputValue.floatValue() * 63.5F));
      }
    };
  
  private final EntityPackets1_10 entityPackets = new EntityPackets1_10(this);
  
  private final BlockItemPackets1_10 blockItemPackets = new BlockItemPackets1_10(this);
  
  public Protocol1_9_4To1_10() {
    super(ClientboundPackets1_9_3.class, ClientboundPackets1_9_3.class, ServerboundPackets1_9_3.class, ServerboundPackets1_9_3.class);
  }
  
  protected void registerPackets() {
    this.entityPackets.register();
    this.blockItemPackets.register();
    final SoundRewriter soundRewriter = new SoundRewriter(this);
    registerClientbound((ClientboundPacketType)ClientboundPackets1_9_3.NAMED_SOUND, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING);
            map((Type)Type.VAR_INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.INT);
            map((Type)Type.FLOAT);
            map((Type)Type.FLOAT, Protocol1_9_4To1_10.TO_OLD_PITCH);
            handler(soundRewriter.getNamedSoundHandler());
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
            map((Type)Type.FLOAT, Protocol1_9_4To1_10.TO_OLD_PITCH);
            handler(soundRewriter.getSoundHandler());
          }
        });
    registerServerbound((ServerboundPacketType)ServerboundPackets1_9_3.RESOURCE_PACK_STATUS, new PacketRemapper() {
          public void registerMap() {
            map(Type.STRING, (Type)Type.NOTHING);
            map((Type)Type.VAR_INT);
          }
        });
  }
  
  public void init(UserConnection user) {
    if (!user.has(ClientWorld.class))
      user.put((StorableObject)new ClientWorld(user)); 
    user.addEntityTracker(getClass(), (EntityTracker)new EntityTrackerBase(user, (EntityType)Entity1_10Types.EntityType.PLAYER));
  }
  
  public BackwardsMappings getMappingData() {
    return MAPPINGS;
  }
  
  public EntityPackets1_10 getEntityRewriter() {
    return this.entityPackets;
  }
  
  public BlockItemPackets1_10 getItemRewriter() {
    return this.blockItemPackets;
  }
  
  public boolean hasMappingDataToLoad() {
    return true;
  }
}
