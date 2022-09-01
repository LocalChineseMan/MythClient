package notthatuwu.xyz.mythrecode.commands;

import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.command.Command;
import notthatuwu.xyz.mythrecode.api.utils.ChatUtil;

public class ReloadCommand implements Command {
  public boolean run(String[] args) {
    Client.INSTANCE.scriptManager.reloadScripts();
    ChatUtil.sendChatMessageWPrefix("Reloaded Scripts");
    return false;
  }
  
  public String usage() {
    return "reloadscripts";
  }
}
