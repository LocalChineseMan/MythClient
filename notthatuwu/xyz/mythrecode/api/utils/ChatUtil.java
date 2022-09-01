package notthatuwu.xyz.mythrecode.api.utils;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class ChatUtil {
  public static void sendChatMessageWPrefix(Object msg) {
    (Minecraft.getMinecraft()).thePlayer.addChatMessage((IChatComponent)new ChatComponentText(ChatFormatting.AQUA + "Myth: " + ChatFormatting.WHITE + msg.toString()));
  }
  
  public static void sendChatMessageWOutPrefix(Object msg) {
    (Minecraft.getMinecraft()).thePlayer.addChatMessage((IChatComponent)new ChatComponentText(msg.toString()));
  }
}
