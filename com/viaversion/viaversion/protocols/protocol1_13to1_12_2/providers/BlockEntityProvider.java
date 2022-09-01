package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.platform.providers.Provider;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.blockentities.BannerHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.blockentities.BedHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.blockentities.CommandBlockHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.blockentities.FlowerPotHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.blockentities.SkullHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.blockentities.SpawnerHandler;
import java.util.HashMap;
import java.util.Map;

public class BlockEntityProvider implements Provider {
  private final Map<String, BlockEntityHandler> handlers = new HashMap<>();
  
  public BlockEntityProvider() {
    this.handlers.put("minecraft:flower_pot", new FlowerPotHandler());
    this.handlers.put("minecraft:bed", new BedHandler());
    this.handlers.put("minecraft:banner", new BannerHandler());
    this.handlers.put("minecraft:skull", new SkullHandler());
    this.handlers.put("minecraft:mob_spawner", new SpawnerHandler());
    this.handlers.put("minecraft:command_block", new CommandBlockHandler());
  }
  
  public int transform(UserConnection user, Position position, CompoundTag tag, boolean sendUpdate) throws Exception {
    Tag idTag = tag.get("id");
    if (idTag == null)
      return -1; 
    String id = (String)idTag.getValue();
    BlockEntityHandler handler = this.handlers.get(id);
    if (handler == null) {
      if (Via.getManager().isDebug())
        Via.getPlatform().getLogger().warning("Unhandled BlockEntity " + id + " full tag: " + tag); 
      return -1;
    } 
    int newBlock = handler.transform(user, tag);
    if (sendUpdate && newBlock != -1)
      sendBlockChange(user, position, newBlock); 
    return newBlock;
  }
  
  private void sendBlockChange(UserConnection user, Position position, int blockId) throws Exception {
    PacketWrapper wrapper = PacketWrapper.create((PacketType)ClientboundPackets1_13.BLOCK_CHANGE, null, user);
    wrapper.write(Type.POSITION, position);
    wrapper.write((Type)Type.VAR_INT, Integer.valueOf(blockId));
    wrapper.send(Protocol1_13To1_12_2.class);
  }
  
  public static interface BlockEntityHandler {
    int transform(UserConnection param1UserConnection, CompoundTag param1CompoundTag);
  }
}
