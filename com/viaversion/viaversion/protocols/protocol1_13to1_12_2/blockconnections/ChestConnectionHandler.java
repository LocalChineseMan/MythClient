package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

class ChestConnectionHandler extends ConnectionHandler {
  private static final Map<Integer, BlockFace> chestFacings = new HashMap<>();
  
  private static final Map<Byte, Integer> connectedStates = new HashMap<>();
  
  private static final Set<Integer> trappedChests = new HashSet<>();
  
  static ConnectionData.ConnectorInitAction init() {
    ChestConnectionHandler connectionHandler = new ChestConnectionHandler();
    return blockData -> {
        if (!blockData.getMinecraftKey().equals("minecraft:chest") && !blockData.getMinecraftKey().equals("minecraft:trapped_chest"))
          return; 
        if (blockData.getValue("waterlogged").equals("true"))
          return; 
        chestFacings.put(Integer.valueOf(blockData.getSavedBlockStateId()), BlockFace.valueOf(blockData.getValue("facing").toUpperCase(Locale.ROOT)));
        if (blockData.getMinecraftKey().equalsIgnoreCase("minecraft:trapped_chest"))
          trappedChests.add(Integer.valueOf(blockData.getSavedBlockStateId())); 
        connectedStates.put(getStates(blockData), Integer.valueOf(blockData.getSavedBlockStateId()));
        ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), connectionHandler);
      };
  }
  
  private static Byte getStates(WrappedBlockData blockData) {
    byte states = 0;
    String type = blockData.getValue("type");
    if (type.equals("left"))
      states = (byte)(states | 0x1); 
    if (type.equals("right"))
      states = (byte)(states | 0x2); 
    states = (byte)(states | BlockFace.valueOf(blockData.getValue("facing").toUpperCase(Locale.ROOT)).ordinal() << 2);
    if (blockData.getMinecraftKey().equals("minecraft:trapped_chest"))
      states = (byte)(states | 0x10); 
    return Byte.valueOf(states);
  }
  
  public int connect(UserConnection user, Position position, int blockState) {
    BlockFace facing = chestFacings.get(Integer.valueOf(blockState));
    byte states = 0;
    states = (byte)(states | facing.ordinal() << 2);
    boolean trapped = trappedChests.contains(Integer.valueOf(blockState));
    if (trapped)
      states = (byte)(states | 0x10); 
    int relative;
    if (chestFacings.containsKey(Integer.valueOf(relative = getBlockData(user, position.getRelative(BlockFace.NORTH)))) && trapped == trappedChests.contains(Integer.valueOf(relative))) {
      states = (byte)(states | ((facing == BlockFace.WEST) ? 1 : 2));
    } else if (chestFacings.containsKey(Integer.valueOf(relative = getBlockData(user, position.getRelative(BlockFace.SOUTH)))) && trapped == trappedChests.contains(Integer.valueOf(relative))) {
      states = (byte)(states | ((facing == BlockFace.EAST) ? 1 : 2));
    } else if (chestFacings.containsKey(Integer.valueOf(relative = getBlockData(user, position.getRelative(BlockFace.WEST)))) && trapped == trappedChests.contains(Integer.valueOf(relative))) {
      states = (byte)(states | ((facing == BlockFace.NORTH) ? 2 : 1));
    } else if (chestFacings.containsKey(Integer.valueOf(relative = getBlockData(user, position.getRelative(BlockFace.EAST)))) && trapped == trappedChests.contains(Integer.valueOf(relative))) {
      states = (byte)(states | ((facing == BlockFace.SOUTH) ? 2 : 1));
    } 
    Integer newBlockState = connectedStates.get(Byte.valueOf(states));
    return (newBlockState == null) ? blockState : newBlockState.intValue();
  }
}
