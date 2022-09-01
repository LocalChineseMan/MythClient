package com.viaversion.viaversion.api.minecraft;

import java.util.HashMap;
import java.util.Map;

public enum BlockFace {
  NORTH((byte)0, (byte)0, (byte)-1, EnumAxis.Z),
  SOUTH((byte)0, (byte)0, (byte)1, EnumAxis.Z),
  EAST((byte)1, (byte)0, (byte)0, EnumAxis.X),
  WEST((byte)-1, (byte)0, (byte)0, EnumAxis.X),
  TOP((byte)0, (byte)1, (byte)0, EnumAxis.Y),
  BOTTOM((byte)0, (byte)-1, (byte)0, EnumAxis.Y);
  
  private static final Map<BlockFace, BlockFace> opposites;
  
  private final byte modX;
  
  private final byte modY;
  
  private final byte modZ;
  
  private final EnumAxis axis;
  
  static {
    opposites = new HashMap<>();
    opposites.put(NORTH, SOUTH);
    opposites.put(SOUTH, NORTH);
    opposites.put(EAST, WEST);
    opposites.put(WEST, EAST);
    opposites.put(TOP, BOTTOM);
    opposites.put(BOTTOM, TOP);
  }
  
  BlockFace(byte modX, byte modY, byte modZ, EnumAxis axis) {
    this.modX = modX;
    this.modY = modY;
    this.modZ = modZ;
    this.axis = axis;
  }
  
  public BlockFace opposite() {
    return opposites.get(this);
  }
  
  public byte getModX() {
    return this.modX;
  }
  
  public byte getModY() {
    return this.modY;
  }
  
  public byte getModZ() {
    return this.modZ;
  }
  
  public EnumAxis getAxis() {
    return this.axis;
  }
  
  public enum EnumAxis {
    X, Y, Z;
  }
}
