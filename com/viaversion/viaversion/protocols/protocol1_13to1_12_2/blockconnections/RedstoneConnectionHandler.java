package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;
import java.util.HashSet;
import java.util.Set;

public class RedstoneConnectionHandler extends ConnectionHandler {
  private static final Set<Integer> redstone = new HashSet<>();
  
  private static final Int2IntMap connectedBlockStates = (Int2IntMap)new Int2IntOpenHashMap(1296);
  
  private static final Int2IntMap powerMappings = (Int2IntMap)new Int2IntOpenHashMap(1296);
  
  static ConnectionData.ConnectorInitAction init() {
    RedstoneConnectionHandler connectionHandler = new RedstoneConnectionHandler();
    String redstoneKey = "minecraft:redstone_wire";
    return blockData -> {
        if (!"minecraft:redstone_wire".equals(blockData.getMinecraftKey()))
          return; 
        redstone.add(Integer.valueOf(blockData.getSavedBlockStateId()));
        ConnectionData.connectionHandlerMap.put(blockData.getSavedBlockStateId(), connectionHandler);
        connectedBlockStates.put(getStates(blockData), blockData.getSavedBlockStateId());
        powerMappings.put(blockData.getSavedBlockStateId(), Integer.parseInt(blockData.getValue("power")));
      };
  }
  
  private static short getStates(WrappedBlockData data) {
    short b = 0;
    b = (short)(b | getState(data.getValue("east")));
    b = (short)(b | getState(data.getValue("north")) << 2);
    b = (short)(b | getState(data.getValue("south")) << 4);
    b = (short)(b | getState(data.getValue("west")) << 6);
    b = (short)(b | Integer.parseInt(data.getValue("power")) << 8);
    return b;
  }
  
  private static int getState(String value) {
    switch (value) {
      case "none":
        return 0;
      case "side":
        return 1;
      case "up":
        return 2;
    } 
    return 0;
  }
  
  public int connect(UserConnection user, Position position, int blockState) {
    short b = 0;
    b = (short)(b | connects(user, position, BlockFace.EAST));
    b = (short)(b | connects(user, position, BlockFace.NORTH) << 2);
    b = (short)(b | connects(user, position, BlockFace.SOUTH) << 4);
    b = (short)(b | connects(user, position, BlockFace.WEST) << 6);
    b = (short)(b | powerMappings.get(blockState) << 8);
    return connectedBlockStates.getOrDefault(b, blockState);
  }
  
  private int connects(UserConnection user, Position position, BlockFace side) {
    Position relative = position.getRelative(side);
    int blockState = getBlockData(user, relative);
    if (connects(side, blockState))
      return 1; 
    int up = getBlockData(user, relative.getRelative(BlockFace.TOP));
    if (redstone.contains(Integer.valueOf(up)) && !ConnectionData.occludingStates.contains(getBlockData(user, position.getRelative(BlockFace.TOP))))
      return 2; 
    int down = getBlockData(user, relative.getRelative(BlockFace.BOTTOM));
    if (redstone.contains(Integer.valueOf(down)) && !ConnectionData.occludingStates.contains(getBlockData(user, relative)))
      return 1; 
    return 0;
  }
  
  private boolean connects(BlockFace side, int blockState) {
    BlockData blockData = (BlockData)ConnectionData.blockConnectionData.get(blockState);
    return (blockData != null && blockData.connectsTo("redstoneConnections", side.opposite(), false));
  }
}
