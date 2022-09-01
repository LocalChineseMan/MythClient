package notthatuwu.xyz.mythrecode.modules.player;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.utils.server.PacketUtil;
import notthatuwu.xyz.mythrecode.events.EventReceivePacket;
import notthatuwu.xyz.mythrecode.events.EventUpdate;

@Info(name = "NoRot", category = Category.PLAYER)
public class NoRot extends Module {
  public final ModeSetting mode = new ModeSetting("Mode", this, new String[] { "Vanilla", "Packet", "Spoof" }, "Vanilla");
  
  private float lastYaw;
  
  private float lastPitch;
  
  private boolean set;
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    if (!event.isPre())
      return; 
    if (this.mode.is("spoof") && 
      this.set) {
      event.setYaw(this.lastYaw);
      event.setPitch(this.lastPitch);
      this.set = false;
    } 
  }
  
  @EventTarget
  public void onPacket(EventReceivePacket event) {
    if (mc.thePlayer == null)
      return; 
    if (event.getPacket() instanceof S08PacketPlayerPosLook) {
      S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)event.getPacket();
      this.lastYaw = packet.yaw;
      this.lastPitch = packet.pitch;
      packet.yaw = mc.thePlayer.rotationYaw;
      packet.pitch = mc.thePlayer.rotationPitch;
      if (this.mode.is("spoof"))
        this.set = true; 
      if (this.mode.is("packet"))
        PacketUtil.sendPacketNoEvent((Packet)new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, packet.getYaw(), packet.getPitch(), mc.thePlayer.onGround)); 
    } 
  }
}
