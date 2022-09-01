package notthatuwu.xyz.mythrecode.modules.movement;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.utils.MoveUtil;
import notthatuwu.xyz.mythrecode.events.EventReceivePacket;
import notthatuwu.xyz.mythrecode.events.EventUpdate;
import notthatuwu.xyz.mythrecode.modules.combat.KillAura;
import org.apache.commons.lang3.RandomUtils;

@Info(name = "NoSlow", category = Category.MOVEMENT)
public class NoSlow extends Module {
  public ModeSetting mode = new ModeSetting("Mode", this, new String[] { "Vanilla", "NCP", "Watchdog", "AAC5" }, "Vanilla");
  
  boolean reSwitch = false;
  
  boolean reBlock = false;
  
  int fakeSlot;
  
  @EventTarget
  public void onUpdate(EventUpdate e) {
    setSuffix(this.mode.getValue());
    switch (this.mode.getValue()) {
      case "NCP":
        if (!getPlayer().isBlocking() || !getPlayer().isMoving() || !MoveUtil.isOnGround(0.42D) || KillAura.target != null)
          return; 
        if (e.isPre()) {
          sendPacket((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(
                  RandomUtils.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE), RandomUtils.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE), RandomUtils.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE)), EnumFacing.DOWN));
          break;
        } 
        sendPacket((Packet)new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, getPlayer().getHeldItem(), 0.0F, 0.0F, 0.0F));
        break;
      case "Watchdog":
        if (!getPlayer().isBlocking() || !getPlayer().isMoving() || !MoveUtil.isOnGround(0.42D) || KillAura.target != null)
          return; 
        if (e.isPre()) {
          sendPacket((Packet)new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(
                  RandomUtils.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE), 
                  RandomUtils.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE), 
                  RandomUtils.nextDouble(Double.MIN_VALUE, Double.MAX_VALUE)), EnumFacing.DOWN));
          break;
        } 
        if (this.fakeSlot == (getPlayer()).inventory.currentItem && getPlayer().isUsingItem()) {
          sendPacket((Packet)new C08PacketPlayerBlockPlacement());
          if ((getPlayer()).ticksExisted % 3 == 0 && this.reBlock) {
            sendPacket((Packet)new C08PacketPlayerBlockPlacement());
            this.reBlock = false;
          } 
        } 
        if (getPlayer().isUsingItem() && !this.reSwitch) {
          sendPacket((Packet)new C09PacketHeldItemChange(airSlot()));
          this.reSwitch = true;
        } 
        if (this.reSwitch && getPlayer().isUsingItem()) {
          sendPacket((Packet)new C09PacketHeldItemChange((getPlayer()).inventory.currentItem));
          if ((getPlayer()).ticksExisted % 4 == 0 && this.reBlock) {
            sendPacket((Packet)new C08PacketPlayerBlockPlacement());
            this.reBlock = false;
          } 
          this.reSwitch = false;
        } 
        if (this.reBlock) {
          sendPacket((Packet)new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, 
                (getPlayer()).inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
          this.reBlock = false;
        } 
        break;
      case "AAC5":
        if (!e.isPost() || (!getPlayer().isUsingItem() && !getPlayer().isBlocking()))
          return; 
        sendPacket((Packet)new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, 
              (getPlayer()).inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
        break;
    } 
  }
  
  @EventTarget
  public void onPacket(EventReceivePacket event) {
    Packet<? extends INetHandler> packet = event.getPacket();
    if (this.mode.is("Watchdog") && 
      packet instanceof C09PacketHeldItemChange) {
      C09PacketHeldItemChange c09 = (C09PacketHeldItemChange)packet;
      if (c09.getSlotId() == this.fakeSlot)
        event.setCancelled(true); 
      this.fakeSlot = c09.getSlotId();
      if (!this.reBlock)
        this.reBlock = true; 
    } 
  }
  
  public void onEnable() {
    this.reBlock = false;
    this.fakeSlot = 0;
    this.reSwitch = false;
  }
  
  private int airSlot() {
    int[] intArray = { 4 };
    return intArray[ThreadLocalRandom.current().nextInt(1, 9)];
  }
}
