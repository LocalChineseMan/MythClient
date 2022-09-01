package notthatuwu.xyz.mythrecode.modules.player;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.utils.MoveUtil;
import notthatuwu.xyz.mythrecode.api.utils.server.PacketUtil;
import notthatuwu.xyz.mythrecode.events.EventUpdate;

@Info(name = "NoFall", category = Category.PLAYER)
public class NoFall extends Module {
  public ModeSetting mode = new ModeSetting("Mode", this, new String[] { "Packet", "Spoof", "Collison", "Collison Silent" }, "Packet");
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    switch (this.mode.getValue()) {
      case "Packet":
        if (mc.thePlayer.fallDistance >= 2.75D)
          PacketUtil.sendPacketSilent((Packet)new C03PacketPlayer(true)); 
        break;
      case "Spoof":
        if (mc.thePlayer.fallDistance >= 2.75D && event.isPre())
          PacketUtil.sendPacketSilent((Packet)new C03PacketPlayer(true)); 
        break;
      case "Collison":
        if (mc.thePlayer.fallDistance - mc.thePlayer.motionY >= 3.0D && !MoveUtil.isOverVoid()) {
          mc.thePlayer.motionY = 0.0D;
          mc.thePlayer.fallDistance = 0.0F;
          event.setOnGround(true);
        } 
        break;
      case "Collison Silent":
        if (mc.thePlayer.fallDistance > 2.0F) {
          PacketUtil.sendPacket((Packet)new C03PacketPlayer.C06PacketPlayerPosLook((mc.thePlayer.posX + mc.thePlayer.lastTickPosX) / 2.0D, mc.thePlayer.posY - mc.thePlayer.posY % 0.015625D, (mc.thePlayer.posZ + mc.thePlayer.lastTickPosZ) / 2.0D, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
          mc.thePlayer.fallDistance = 0.0F;
        } 
        break;
    } 
  }
}
