package notthatuwu.xyz.mythrecode.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import notthatuwu.xyz.mythrecode.api.command.Command;
import notthatuwu.xyz.mythrecode.api.utils.ChatUtil;

public class HClipCommand implements Command {
  public Minecraft mc = Minecraft.getMinecraft();
  
  public boolean run(String[] args) {
    if (args.length == 2)
      try {
        double posMod = Double.parseDouble(args[1]);
        if (this.mc.thePlayer.getHorizontalFacing() == EnumFacing.SOUTH)
          this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ + posMod); 
        if (this.mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST)
          this.mc.thePlayer.setPosition(this.mc.thePlayer.posX + -posMod, this.mc.thePlayer.posY, this.mc.thePlayer.posZ); 
        if (this.mc.thePlayer.getHorizontalFacing() == EnumFacing.EAST)
          this.mc.thePlayer.setPosition(this.mc.thePlayer.posX + posMod, this.mc.thePlayer.posY, this.mc.thePlayer.posZ); 
        if (this.mc.thePlayer.getHorizontalFacing() == EnumFacing.NORTH)
          this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ + -posMod); 
        ChatUtil.sendChatMessageWPrefix("Teleported " + posMod + " blocks horizontally.");
        return true;
      } catch (Exception e) {
        ChatUtil.sendChatMessageWOutPrefix(ChatFormatting.RED + e.getMessage());
      }  
    return false;
  }
  
  public String usage() {
    return ChatFormatting.WHITE + "hclip <value>";
  }
}
