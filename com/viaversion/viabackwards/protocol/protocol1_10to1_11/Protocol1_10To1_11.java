package com.viaversion.viabackwards.protocol.protocol1_10to1_11;

import com.viaversion.viabackwards.api.BackwardsProtocol;
import com.viaversion.viabackwards.api.data.BackwardsMappings;
import com.viaversion.viabackwards.api.rewriters.SoundRewriter;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.packets.BlockItemPackets1_11;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.packets.EntityPackets1_11;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.packets.PlayerPackets1_11;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.storage.WindowTracker;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.MappingData;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_11Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.rewriter.EntityRewriter;
import com.viaversion.viaversion.api.rewriter.ItemRewriter;
import com.viaversion.viaversion.data.entity.EntityTrackerBase;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;

public class Protocol1_10To1_11 extends BackwardsProtocol<ClientboundPackets1_9_3, ClientboundPackets1_9_3, ServerboundPackets1_9_3, ServerboundPackets1_9_3> {
  public static final BackwardsMappings MAPPINGS = new BackwardsMappings("1.11", "1.10", null, true);
  
  private final EntityPackets1_11 entityPackets = new EntityPackets1_11(this);
  
  private BlockItemPackets1_11 blockItemPackets;
  
  public Protocol1_10To1_11() {
    super(ClientboundPackets1_9_3.class, ClientboundPackets1_9_3.class, ServerboundPackets1_9_3.class, ServerboundPackets1_9_3.class);
  }
  
  protected void registerPackets() {
    (this.blockItemPackets = new BlockItemPackets1_11(this)).register();
    this.entityPackets.register();
    (new PlayerPackets1_11()).register(this);
    SoundRewriter soundRewriter = new SoundRewriter(this);
    soundRewriter.registerNamedSound((ClientboundPacketType)ClientboundPackets1_9_3.NAMED_SOUND);
    soundRewriter.registerSound((ClientboundPacketType)ClientboundPackets1_9_3.SOUND);
  }
  
  public void init(UserConnection user) {
    if (!user.has(ClientWorld.class))
      user.put((StorableObject)new ClientWorld(user)); 
    user.addEntityTracker(getClass(), (EntityTracker)new EntityTrackerBase(user, (EntityType)Entity1_11Types.EntityType.PLAYER, true));
    if (!user.has(WindowTracker.class))
      user.put((StorableObject)new WindowTracker()); 
  }
  
  public BackwardsMappings getMappingData() {
    return MAPPINGS;
  }
  
  public EntityPackets1_11 getEntityRewriter() {
    return this.entityPackets;
  }
  
  public BlockItemPackets1_11 getItemRewriter() {
    return this.blockItemPackets;
  }
  
  public boolean hasMappingDataToLoad() {
    return true;
  }
}
