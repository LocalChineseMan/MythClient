package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.providers;

import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.block_entity_handlers.BannerHandler;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.block_entity_handlers.BedHandler;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.block_entity_handlers.FlowerPotHandler;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.block_entity_handlers.PistonHandler;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.block_entity_handlers.SkullHandler;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.block_entity_handlers.SpawnerHandler;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.storage.BackwardsBlockStorage;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.platform.providers.Provider;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import java.util.HashMap;
import java.util.Map;

public class BackwardsBlockEntityProvider implements Provider {
  private final Map<String, BackwardsBlockEntityHandler> handlers = new HashMap<>();
  
  public BackwardsBlockEntityProvider() {
    this.handlers.put("minecraft:flower_pot", new FlowerPotHandler());
    this.handlers.put("minecraft:bed", new BedHandler());
    this.handlers.put("minecraft:banner", new BannerHandler());
    this.handlers.put("minecraft:skull", new SkullHandler());
    this.handlers.put("minecraft:mob_spawner", new SpawnerHandler());
    this.handlers.put("minecraft:piston", new PistonHandler());
  }
  
  public boolean isHandled(String key) {
    return this.handlers.containsKey(key);
  }
  
  public CompoundTag transform(UserConnection user, Position position, CompoundTag tag) throws Exception {
    String id = (String)tag.get("id").getValue();
    BackwardsBlockEntityHandler handler = this.handlers.get(id);
    if (handler == null) {
      if (Via.getManager().isDebug())
        ViaBackwards.getPlatform().getLogger().warning("Unhandled BlockEntity " + id + " full tag: " + tag); 
      return tag;
    } 
    BackwardsBlockStorage storage = (BackwardsBlockStorage)user.get(BackwardsBlockStorage.class);
    Integer blockId = storage.get(position);
    if (blockId == null) {
      if (Via.getManager().isDebug())
        ViaBackwards.getPlatform().getLogger().warning("Handled BlockEntity does not have a stored block :( " + id + " full tag: " + tag); 
      return tag;
    } 
    return handler.transform(user, blockId.intValue(), tag);
  }
  
  public CompoundTag transform(UserConnection user, Position position, String id) throws Exception {
    CompoundTag tag = new CompoundTag();
    tag.put("id", (Tag)new StringTag(id));
    tag.put("x", (Tag)new IntTag(Math.toIntExact(position.getX())));
    tag.put("y", (Tag)new IntTag(Math.toIntExact(position.getY())));
    tag.put("z", (Tag)new IntTag(Math.toIntExact(position.getZ())));
    return transform(user, position, tag);
  }
  
  public static interface BackwardsBlockEntityHandler {
    CompoundTag transform(UserConnection param1UserConnection, int param1Int, CompoundTag param1CompoundTag);
  }
}
