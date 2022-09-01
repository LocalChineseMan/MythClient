package notthatuwu.xyz.mythrecode.modules.combat;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MovingObjectPosition;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.ModuleManager;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.MoveUtil;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.events.EventSendPacket;
import notthatuwu.xyz.mythrecode.events.EventUpdate;

@Info(name = "Criticals", category = Category.COMBAT)
public class Criticals extends Module {
  public ModeSetting mode = new ModeSetting("Mode", this, new String[] { "Packet", "LowHop", "NCP" }, "Packet");
  
  public NumberSetting criticalDelay = new NumberSetting("Critical Delay", this, 500.0D, 0.0D, 2000.0D, true);
  
  int groundTicks;
  
  final TimeHelper timeHelper = new TimeHelper();
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    setSuffix(this.mode.getValue());
    if (event.isPre())
      this.groundTicks = MoveUtil.isOnGround() ? (this.groundTicks + 1) : 0; 
  }
  
  @EventTarget
  public void onSendPacket(EventSendPacket event) {
    Packet<? extends INetHandler> packet = event.getPacket();
    if (packet instanceof net.minecraft.network.play.client.C0APacketAnimation && ableToCritical()) {
      double[] watchdogOffsets;
      switch (this.mode.getValue()) {
        case "Packet":
          sendPacketUnlogged((Packet)new C03PacketPlayer.C04PacketPlayerPosition(getX(), getY() + 0.15000000596046448D, getZ(), false));
          sendPacketUnlogged((Packet)new C03PacketPlayer.C04PacketPlayerPosition(getX(), getY(), getZ(), false));
          break;
        case "LowHop":
          (getPlayer()).motionY = 0.015D;
          break;
        case "NCP":
          watchdogOffsets = new double[] { 0.11D, 0.1100013579D, 1.3579E-6D };
          for (double i : watchdogOffsets)
            sendPacket((Packet)new C03PacketPlayer.C04PacketPlayerPosition(getX(), getY() + i, getZ(), false)); 
          break;
      } 
      this.timeHelper.reset();
    } 
  }
  
  private boolean ableToCritical() {
    return (hasTarget() && this.timeHelper
      .hasReached(this.criticalDelay.getValueLong()) && 
      !MC.gameSettings.keyBindJump.isKeyDown() && 
      !(getPlayer()).isInWeb && 
      !ModuleManager.getModuleByName("Fly").isEnabled() && (
      !ModuleManager.getModuleByName("Speed").isEnabled() || 
      !MoveUtil.isMoving()) && 
      !MoveUtil.isInLiquid() && 
      !getPlayer().isOnLadder() && 
      (getPlayer()).fallDistance == 0.0F && this.groundTicks > 1);
  }
  
  private boolean hasTarget() {
    if (KillAura.target != null)
      return true; 
    MovingObjectPosition target = mc.objectMouseOver;
    return (target != null && target.entityHit != null);
  }
}
