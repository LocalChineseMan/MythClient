package notthatuwu.xyz.mythrecode.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import notthatuwu.xyz.mythrecode.api.command.Command;
import notthatuwu.xyz.mythrecode.api.utils.ChatUtil;

public class VClipCommand implements Command {
  public Minecraft mc = Minecraft.getMinecraft();
  
  public boolean run(String[] args) {
    if (args.length == 2)
      try {
        double blocks = Double.parseDouble(args[1]);
        this.mc.thePlayer.setEntityBoundingBox(this.mc.thePlayer.getEntityBoundingBox().offset(0.0D, blocks, 0.0D));
        ChatUtil.sendChatMessageWPrefix("Teleported " + blocks + " blocks.");
        return true;
      } catch (Exception e) {
        ChatUtil.sendChatMessageWOutPrefix(ChatFormatting.RED + e.getMessage());
      }  
    return false;
  }
  
  public String usage() {
    return ChatFormatting.WHITE + "vclip <value>";
  }
}
