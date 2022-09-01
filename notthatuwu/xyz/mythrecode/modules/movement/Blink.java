package notthatuwu.xyz.mythrecode.modules.movement;

import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.events.EventSendPacket;

@Info(name = "Blink", description = "Blink", category = Category.MOVEMENT)
public class Blink extends Module {
  private ConcurrentLinkedQueue<Packet<? extends INetHandler>> packetQueue = new ConcurrentLinkedQueue<>();
  
  @EventTarget
  public void onSend(EventSendPacket event) {
    Packet<? extends INetHandler> packet = event.getPacket();
    if (MC.isSingleplayer())
      return; 
    if (!(packet instanceof net.minecraft.network.handshake.client.C00Handshake) && !(packet instanceof net.minecraft.network.login.client.C00PacketLoginStart) && !(packet instanceof net.minecraft.network.status.client.C00PacketServerQuery) && !(packet instanceof net.minecraft.network.status.client.C01PacketPing) && !(packet instanceof net.minecraft.network.login.client.C01PacketEncryptionResponse)) {
      this.packetQueue.add(packet);
      event.setCancelled(true);
    } 
  }
  
  public void onDisable() {
    super.onDisable();
    if (!this.packetQueue.isEmpty()) {
      this.packetQueue.forEach(this::sendPacketUnlogged);
      this.packetQueue.clear();
    } 
  }
}
