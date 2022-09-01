package notthatuwu.xyz.mythrecode.events;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import notthatuwu.xyz.mythrecode.api.event.Event;

public class EventReceivePacket extends Event {
  private Packet<? extends INetHandler> packet;
  
  public void setPacket(Packet<? extends INetHandler> packet) {
    this.packet = packet;
  }
  
  public Packet<? extends INetHandler> getPacket() {
    return this.packet;
  }
  
  public EventReceivePacket(Packet<? extends INetHandler> packet) {
    this.packet = packet;
  }
}
