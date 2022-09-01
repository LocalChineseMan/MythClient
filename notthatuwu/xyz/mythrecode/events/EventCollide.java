package notthatuwu.xyz.mythrecode.events;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import notthatuwu.xyz.mythrecode.api.event.Event;

public class EventCollide extends Event {
  private AxisAlignedBB axisAlignedBB;
  
  private final Block block;
  
  private final Entity collidingEntity;
  
  private final int x;
  
  private final int z;
  
  public int y;
  
  public AxisAlignedBB getAxisAlignedBB() {
    return this.axisAlignedBB;
  }
  
  public void setAxisAlignedBB(AxisAlignedBB axisAlignedBB) {
    this.axisAlignedBB = axisAlignedBB;
  }
  
  public Block getBlock() {
    return this.block;
  }
  
  public Entity getCollidingEntity() {
    return this.collidingEntity;
  }
  
  public int getX() {
    return this.x;
  }
  
  public int getZ() {
    return this.z;
  }
  
  public int getY() {
    return this.y;
  }
  
  public EventCollide(Entity collidingEntity, int x, int y, int z, AxisAlignedBB axisAlignedBB, Block block) {
    this.collidingEntity = collidingEntity;
    this.x = x;
    this.y = y;
    this.z = z;
    this.axisAlignedBB = axisAlignedBB;
    this.block = block;
  }
}
