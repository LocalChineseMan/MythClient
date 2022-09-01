package notthatuwu.xyz.mythrecode.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.io.File;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.command.Command;
import notthatuwu.xyz.mythrecode.api.utils.ChatUtil;

public final class ConfigCommand implements Command {
  public boolean run(String[] args) {
    if (args.length >= 2) {
      String upperCaseFunction = args[1].toUpperCase();
      if (args.length == 3) {
        switch (upperCaseFunction) {
          case "LOAD":
            Client.INSTANCE.configUtil.load(args[2]);
            ChatUtil.sendChatMessageWPrefix("Successfully loaded config: '" + args[2] + "'");
            break;
          case "SAVE":
            Client.INSTANCE.configUtil.save(args[2]);
            ChatUtil.sendChatMessageWPrefix("Successfully saved config: '" + args[2] + "'");
            break;
          case "DELETE":
            Client.INSTANCE.configUtil.delete(args[2]);
            ChatUtil.sendChatMessageWPrefix("Successfully deleted config: '" + args[2] + "'");
            break;
        } 
        return true;
      } 
      if (args.length == 2 && upperCaseFunction.equalsIgnoreCase("LIST")) {
        File folder = new File("Myth/Configs");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
          if (listOfFiles[i].isFile())
            ChatUtil.sendChatMessageWPrefix(listOfFiles[i].getName()); 
        } 
        return true;
      } 
    } 
    return false;
  }
  
  public String usage() {
    return ChatFormatting.WHITE + "config <load/save/delete/list> <config>";
  }
}
