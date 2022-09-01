package notthatuwu.xyz.mythrecode.modules.player;

import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.ModuleManager;
import notthatuwu.xyz.mythrecode.api.utils.ChatUtil;
import notthatuwu.xyz.mythrecode.api.utils.PlayerUtil;
import notthatuwu.xyz.mythrecode.events.EventReceivePacket;
import notthatuwu.xyz.mythrecode.events.EventUpdate;

@Info(name = "AutoDisable", category = Category.PLAYER)
public class AutoDisable extends Module {
  @EventTarget
  public void onUpdate(EventUpdate event) {
    if (PlayerUtil.onHypixel() && 
      ModuleManager.getModuleByName("Speed").isEnabled() && ModuleManager.getModuleByName("Scaffold").isEnabled()) {
      ModuleManager.getModuleByName("Speed").setEnabled(false);
      ChatUtil.sendChatMessageWPrefix("Speed module disabled due to module conflicting.");
    } 
  }
  
  @EventTarget
  public void onReceivedPacket(EventReceivePacket event) {
    if (event.getPacket() instanceof net.minecraft.network.play.server.S08PacketPlayerPosLook && mc.thePlayer.ticksExisted > 50) {
      ModuleManager.getModuleByName("Speed").setEnabled(false);
      ModuleManager.getModuleByName("Fly").setEnabled(false);
    } 
  }
}
