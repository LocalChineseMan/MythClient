package notthatuwu.xyz.mythrecode.api.utils.server;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class PacketUtil {
  public static Minecraft mc = Minecraft.getMinecraft();
  
  public static void sendPacketNoEvent(Packet packet) {
    mc.getNetHandler().addToSendQueueNoEvent(packet);
  }
  
  public static void sendPacketSilent(Packet packet) {
    mc.getNetHandler().getNetworkManager().sendPacket(packet);
  }
  
  public static void sendPacket(Packet packet) {
    mc.getNetHandler().addToSendQueue(packet);
  }
  
  public static void sendFunnyPacket() {
    mc.getNetHandler().addToSendQueueSilent((Packet)new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), EnumFacing.UP.getIndex(), null, 0.0F, (float)(Math.random() / 5.0D), 0.0F));
  }
  
  public static void sendPacketDelayed(Packet packet, long delay) {
    try {
      (new Object(delay, packet))
        
        .start();
    } catch (Exception exception) {}
  }
}
