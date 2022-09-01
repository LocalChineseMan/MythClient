package notthatuwu.xyz.mythrecode.modules.visuals;

import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.events.EventReceivePacket;

@Info(name = "Notifications", category = Category.VISUAL)
public class Notifications extends Module {
  public BooleanSetting onToggle = new BooleanSetting("On Module Toggle", this, false);
  
  public BooleanSetting onFlag = new BooleanSetting("On Flag", this, false);
  
  @EventTarget
  public void onReceivedPacket(EventReceivePacket event) {
    if (event.getPacket() instanceof net.minecraft.network.play.server.S08PacketPlayerPosLook && 
      this.onFlag.getValue().booleanValue())
      Client.INSTANCE.notificationManager.sendNotification("You got Flagged!", "Info"); 
  }
}
