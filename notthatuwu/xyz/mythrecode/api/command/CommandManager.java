package notthatuwu.xyz.mythrecode.api.command;

import java.util.HashMap;
import notthatuwu.xyz.mythrecode.api.utils.ChatUtil;
import notthatuwu.xyz.mythrecode.commands.BindCommand;
import notthatuwu.xyz.mythrecode.commands.ConfigCommand;
import notthatuwu.xyz.mythrecode.commands.HClipCommand;
import notthatuwu.xyz.mythrecode.commands.HelpCommand;
import notthatuwu.xyz.mythrecode.commands.ReloadCommand;
import notthatuwu.xyz.mythrecode.commands.ToggleCommand;
import notthatuwu.xyz.mythrecode.commands.VClipCommand;

public class CommandManager {
  private final HashMap<String[], Command> commands;
  
  private final String prefix;
  
  public CommandManager() {
    this.commands = (HashMap)new HashMap<>();
    this.prefix = ".";
    registerCommands();
  }
  
  private void registerCommands() {
    this.commands.put(new String[] { "help", "h" }, new HelpCommand());
    this.commands.put(new String[] { "bind", "b" }, new BindCommand());
    this.commands.put(new String[] { "toggle", "t" }, new ToggleCommand());
    this.commands.put(new String[] { "config", "c" }, new ConfigCommand());
    this.commands.put(new String[] { "hclip", "hc" }, new HClipCommand());
    this.commands.put(new String[] { "vclip", "vc" }, new VClipCommand());
    this.commands.put(new String[] { "reloadscripts", "rs" }, new ReloadCommand());
  }
  
  public boolean processCommand(String rawMessage) {
    if (!rawMessage.startsWith(this.prefix))
      return false; 
    String beheaded = rawMessage.substring(1);
    String[] args = beheaded.split(" ");
    Command command = getCommand(args[0]);
    if (command != null) {
      if (!command.run(args))
        ChatUtil.sendChatMessageWPrefix(command.usage()); 
    } else {
      ChatUtil.sendChatMessageWPrefix("Try " + this.prefix + "help.");
    } 
    return true;
  }
  
  private Command getCommand(String name) {
    for (String[] keys : this.commands.keySet()) {
      for (String key : keys) {
        if (key.equalsIgnoreCase(name))
          return this.commands.get(keys); 
      } 
    } 
    return null;
  }
  
  public HashMap<String[], Command> getCommands() {
    return this.commands;
  }
}
