package notthatuwu.xyz.mythrecode.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.command.Command;
import notthatuwu.xyz.mythrecode.api.utils.ChatUtil;

public class HelpCommand implements Command {
  public boolean run(String[] args) {
    ChatUtil.sendChatMessageWPrefix("§7§lList of commands:");
    for (Command command : Client.INSTANCE.commandManager.getCommands().values())
      ChatUtil.sendChatMessageWPrefix(command.usage()); 
    return true;
  }
  
  public String usage() {
    return ChatFormatting.WHITE + "help <lists commands>";
  }
}
