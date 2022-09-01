package notthatuwu.xyz.mythrecode.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import notthatuwu.xyz.mythrecode.api.command.Command;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.ModuleManager;
import notthatuwu.xyz.mythrecode.api.utils.ChatUtil;
import org.lwjgl.input.Keyboard;

public class BindCommand implements Command {
  public boolean run(String[] args) {
    if (args.length == 3) {
      Module m = ModuleManager.getModuleByName(args[1]);
      if (m != null) {
        m.setKeyCode(Keyboard.getKeyIndex(args[2].toUpperCase()));
        ChatUtil.sendChatMessageWPrefix(m.getName() + " has been bound to " + Keyboard.getKeyName(m.getKeyCode()) + ".");
        return true;
      } 
    } else if (args.length == 2 && 
      args[1].equalsIgnoreCase("clear")) {
      ModuleManager.getModules().forEach(module -> module.setKeyCode(0));
      ChatUtil.sendChatMessageWPrefix("All binds have been cleared.");
      return true;
    } 
    return false;
  }
  
  public String usage() {
    return ChatFormatting.WHITE + "bind <clear> <module> <key>";
  }
}
