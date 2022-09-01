package notthatuwu.xyz.mythrecode.modules.player;

import net.minecraft.entity.Entity;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;

@Info(name = "Teams", category = Category.PLAYER)
public class Teams extends Module {
  public static boolean isOnSameTeam(Entity entity) {
    if (!((Teams)Client.INSTANCE.moduleManager.getModuleByClass(Teams.class)).isEnabled())
      return false; 
    if (mc.thePlayer.getDisplayName().getUnformattedText().startsWith("§")) {
      if (mc.thePlayer.getDisplayName().getUnformattedText().length() <= 2 || entity
        .getDisplayName().getUnformattedText().length() <= 2)
        return false; 
      if (mc.thePlayer.getDisplayName().getUnformattedText().substring(0, 2)
        .equals(entity.getDisplayName().getUnformattedText().substring(0, 2)))
        return true; 
    } 
    return false;
  }
}
