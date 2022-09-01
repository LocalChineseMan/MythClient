package notthatuwu.xyz.mythrecode.api.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import notthatuwu.xyz.mythrecode.api.utils.MoveUtil;
import notthatuwu.xyz.mythrecode.api.utils.server.PacketUtil;

public interface IMethods {
  public static final Minecraft MC = Minecraft.getMinecraft();
  
  public static final Minecraft mc = Minecraft.getMinecraft();
  
  default EntityPlayerSP getPlayer() {
    return MC.thePlayer;
  }
  
  default WorldClient getWorld() {
    return MC.theWorld;
  }
  
  default double getX() {
    return MC.thePlayer.posX;
  }
  
  default double getY() {
    return MC.thePlayer.posY;
  }
  
  default double getZ() {
    return MC.thePlayer.posZ;
  }
  
  default float getYaw() {
    return MC.thePlayer.rotationYaw;
  }
  
  default float getPitch() {
    return MC.thePlayer.rotationPitch;
  }
  
  default void setTimer(float timer) {
    MC.timer.timerSpeed = timer;
  }
  
  default GameSettings getGameSettings() {
    return MC.gameSettings;
  }
  
  default void setSpeed(double speed) {
    MoveUtil.setSpeed(speed);
  }
  
  default void setPosition(double x, double y, double z) {
    float yaw = MoveUtil.getDirection();
    MC.thePlayer.setPosition(MC.thePlayer.posX - Math.sin(yaw) * x, MC.thePlayer.posY + y, MC.thePlayer.posZ + 
        Math.cos(yaw) * z);
  }
  
  default void sendPacketUnlogged(Packet<? extends INetHandler> packet) {
    PacketUtil.sendPacketNoEvent(packet);
  }
  
  default void sendPacket(Packet<? extends INetHandler> packet) {
    PacketUtil.sendPacket(packet);
  }
}
