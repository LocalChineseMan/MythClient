package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FireConnectionHandler extends ConnectionHandler {
  private static final String[] WOOD_TYPES = new String[] { "oak", "spruce", "birch", "jungle", "acacia", "dark_oak" };
  
  private static final Map<Byte, Integer> connectedBlocks = new HashMap<>();
  
  private static final Set<Integer> flammableBlocks = new HashSet<>();
  
  private static void addWoodTypes(Set<String> set, String suffix) {
    for (String woodType : WOOD_TYPES)
      set.add("minecraft:" + woodType + suffix); 
  }
  
  static ConnectionData.ConnectorInitAction init() {
    Set<String> flammabeIds = new HashSet<>();
    flammabeIds.add("minecraft:tnt");
    flammabeIds.add("minecraft:vine");
    flammabeIds.add("minecraft:bookshelf");
    flammabeIds.add("minecraft:hay_block");
    flammabeIds.add("minecraft:deadbush");
    addWoodTypes(flammabeIds, "_slab");
    addWoodTypes(flammabeIds, "_log");
    addWoodTypes(flammabeIds, "_planks");
    addWoodTypes(flammabeIds, "_leaves");
    addWoodTypes(flammabeIds, "_fence");
    addWoodTypes(flammabeIds, "_fence_gate");
    addWoodTypes(flammabeIds, "_stairs");
    FireConnectionHandler connectionHandler = new FireConnectionHandler();
    return blockData -> {
        String key = blockData.getMinecraftKey();
        if (key.contains("_wool") || key.contains("_carpet") || flammabeIds.contains(key)) {
          flammableBlocks.add(Integer.valueOf(blockData.getSavedBlockStateId()));
        } else if (key.equals("minecraft:fire")) {
          int id = blockData.getSavedBlockStateId();
          connectedBlocks.put(Byte.valueOf(getStates(blockData)), Integer.valueOf(id));
          ConnectionData.connectionHandlerMap.put(id, connectionHandler);
        } 
      };
  }
  
  private static byte getStates(WrappedBlockData blockData) {
    byte states = 0;
    if (blockData.getValue("east").equals("true"))
      states = (byte)(states | 0x1); 
    if (blockData.getValue("north").equals("true"))
      states = (byte)(states | 0x2); 
    if (blockData.getValue("south").equals("true"))
      states = (byte)(states | 0x4); 
    if (blockData.getValue("up").equals("true"))
      states = (byte)(states | 0x8); 
    if (blockData.getValue("west").equals("true"))
      states = (byte)(states | 0x10); 
    return states;
  }
  
  public int connect(UserConnection user, Position position, int blockState) {
    byte states = 0;
    if (flammableBlocks.contains(Integer.valueOf(getBlockData(user, position.getRelative(BlockFace.EAST)))))
      states = (byte)(states | 0x1); 
    if (flammableBlocks.contains(Integer.valueOf(getBlockData(user, position.getRelative(BlockFace.NORTH)))))
      states = (byte)(states | 0x2); 
    if (flammableBlocks.contains(Integer.valueOf(getBlockData(user, position.getRelative(BlockFace.SOUTH)))))
      states = (byte)(states | 0x4); 
    if (flammableBlocks.contains(Integer.valueOf(getBlockData(user, position.getRelative(BlockFace.TOP)))))
      states = (byte)(states | 0x8); 
    if (flammableBlocks.contains(Integer.valueOf(getBlockData(user, position.getRelative(BlockFace.WEST)))))
      states = (byte)(states | 0x10); 
    return ((Integer)connectedBlocks.get(Byte.valueOf(states))).intValue();
  }
}
