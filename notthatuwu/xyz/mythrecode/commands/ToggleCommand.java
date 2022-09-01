package notthatuwu.xyz.mythrecode.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import notthatuwu.xyz.mythrecode.api.command.Command;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.ModuleManager;
import notthatuwu.xyz.mythrecode.api.utils.ChatUtil;

public class ToggleCommand implements Command {
  public boolean run(String[] args) {
    if (args.length == 2) {
      try {
        Module m = ModuleManager.getModuleByName(args[1]);
        if (args[1].equalsIgnoreCase(m.getName()))
          m.toggle(); 
        ChatUtil.sendChatMessageWPrefix(m
            .getName() + (m.isEnabled() ? " has been §2enabled" : " has been §4disabled"));
      } catch (Exception e) {
        ChatUtil.sendChatMessageWPrefix("Module not found.");
      } 
      return true;
    } 
    return false;
  }
  
  public String usage() {
    return ChatFormatting.WHITE + "toggle <module>";
  }
}
