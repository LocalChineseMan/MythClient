package notthatuwu.xyz.mythrecode.modules.player;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.ModuleManager;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.MoveUtil;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.events.EventReceivePacket;
import notthatuwu.xyz.mythrecode.events.EventSendPacket;
import notthatuwu.xyz.mythrecode.events.EventUpdate;

@Info(name = "AntiVoid", category = Category.PLAYER)
public class AntiVoid extends Module {
  public TimeHelper timeHelper = new TimeHelper();
  
  public ModeSetting mode = new ModeSetting("Mode", this, new String[] { "Normal", "Jump", "Blink" }, "Normal");
  
  public NumberSetting fallDistance = new NumberSetting("Distance", this, 4.0D, 1.0D, 10.0D, false);
  
  private double x;
  
  private double y;
  
  private double z;
  
  private boolean falling;
  
  private List<Packet> packets = new CopyOnWriteArrayList<>();
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    setSuffix(this.mode.getValue());
    if (!MoveUtil.isBlockUnder() && mc.thePlayer.fallDistance > this.fallDistance.getValue().doubleValue())
      switch (this.mode.getValue()) {
        case "Normal":
          mc.thePlayer.fallDistance = 5.0E-17F;
          mc.getNetHandler().getNetworkManager().sendPacket((Packet)new C03PacketPlayer.C04PacketPlayerPosition(Math.random() * 5.0E-14D, -Math.random() * 50.0D, Math.random() * 1.73E-12D, false));
          mc.thePlayer.fallDistance = 0.0F;
          break;
        case "Jump":
          mc.thePlayer.jump();
          break;
      }  
    if (this.mode.getValue().equalsIgnoreCase("Blink")) {
      if (MoveUtil.isBlockUnder() && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
        this.x = mc.thePlayer.posX;
        this.y = mc.thePlayer.posY;
        this.z = mc.thePlayer.posZ;
      } 
      if (this.falling && mc.thePlayer.fallDistance > this.fallDistance.getValue().doubleValue()) {
        this.packets.clear();
        mc.thePlayer.setPositionAndUpdate(this.x, this.y, this.z);
        event.setX(this.x);
        event.setY(this.y);
        event.setZ(this.z);
      } 
    } 
  }
  
  @EventTarget
  public void onSendPacket(EventSendPacket event) {
    if (this.mode.getValue().equalsIgnoreCase("Blink") && 
      event.getPacket() instanceof C03PacketPlayer && !ModuleManager.getModuleByName("Fly").isEnabled())
      if (!MoveUtil.isBlockUnder()) {
        this.packets.add(event.getPacket());
        event.setCancelled(true);
        this.falling = true;
      } else if (mc.thePlayer.fallDistance < this.fallDistance.getValue().doubleValue() && 
        this.falling) {
        if (!this.packets.isEmpty()) {
          for (Packet packet : this.packets)
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packet); 
          this.packets.clear();
        } 
        this.falling = false;
      }  
  }
  
  @EventTarget
  public void onReceivePacket(EventReceivePacket event) {
    if (this.mode.getValue().equalsIgnoreCase("Blink") && 
      event.getPacket() instanceof S08PacketPlayerPosLook) {
      S08PacketPlayerPosLook posLook = (S08PacketPlayerPosLook)event.getPacket();
      for (Packet packet : this.packets) {
        C03PacketPlayer packetPlayer = (C03PacketPlayer)packet;
        if (packetPlayer.getPositionX() == posLook.getX() && packetPlayer.getPositionY() == posLook.getY() && packetPlayer.getPositionZ() == posLook.getZ()) {
          this.packets.clear();
          this.falling = false;
        } 
      } 
    } 
  }
  
  public void onDisable() {
    this.timeHelper.reset();
    super.onDisable();
  }
}
