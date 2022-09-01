package notthatuwu.xyz.mythrecode.modules.combat;

import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.AddonSetting;
import notthatuwu.xyz.mythrecode.events.EventReceivePacket;
import notthatuwu.xyz.mythrecode.events.EventUpdate;

@Info(name = "AntiBot", category = Category.COMBAT)
public class AntiBot extends Module {
  public AddonSetting addons = new AddonSetting("Checks", this, "TabList", new String[] { "TabList", "Smart", "Easy", "Hypixel" });
  
  static final ArrayList<Entity> bots = new ArrayList<>();
  
  @EventTarget
  public void onUpdate(EventUpdate event) {
    if (this.addons.isEnabled("Smart"))
      for (EntityPlayer player : (getWorld()).playerEntities) {
        if (player.getHealth() > 20.0F || player.getHealth() <= 0.0F)
          bots.add(player); 
      }  
    if (this.addons.isEnabled("Easy"))
      for (EntityPlayer player : (getWorld()).playerEntities) {
        if (getPlayer().getDistanceToEntity((Entity)player) > 5.0F || 
          Float.isNaN(player.getHealth()) || player == 
          getPlayer() || player
          .getHealth() >= 20.0D || player
          .getHealth() == 1.0D)
          continue; 
        bots.add(player);
      }  
    if (this.addons.isEnabled("TabList")) {
      if ((getPlayer()).ticksExisted % 10 == 0)
        bots.clear(); 
      for (EntityPlayer player : (getWorld()).playerEntities) {
        if (player == getPlayer())
          continue; 
        if (!GuiPlayerTabOverlay.getPlayers().contains(player))
          bots.add(player); 
      } 
    } 
  }
  
  @EventTarget
  public void onPacket(EventReceivePacket event) {
    Packet<? extends INetHandler> packet = event.getPacket();
    if (this.addons.isEnabled("Hypixel") && 
      packet instanceof S0CPacketSpawnPlayer) {
      S0CPacketSpawnPlayer s0CPacketSpawnPlayer = (S0CPacketSpawnPlayer)packet;
      UUID uid = s0CPacketSpawnPlayer.getPlayer();
      if (uid != getPlayer().getUniqueID()) {
        NetworkPlayerInfo networkPlayerInfo = MC.getNetHandler().getPlayerInfo(uid);
        if (networkPlayerInfo == null || networkPlayerInfo.getResponseTime() != 1)
          bots.add(getWorld().getPlayerEntityByUUID(uid)); 
      } 
    } 
  }
  
  public static boolean isBot(EntityLivingBase player) {
    if (!((AntiBot)Client.INSTANCE.moduleManager.getModuleByClass(AntiBot.class)).isEnabled())
      return false; 
    return (player instanceof EntityPlayer && bots.contains(player));
  }
}
