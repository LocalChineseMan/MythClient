package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.shader.list.BlurShader;
import notthatuwu.xyz.mythrecode.modules.visuals.BetterChat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiNewChat extends Gui {
  private static final Logger logger = LogManager.getLogger();
  
  private final Minecraft mc;
  
  private final List<String> sentMessages = Lists.newArrayList();
  
  private final List<ChatLine> chatLines = Lists.newArrayList();
  
  private final List<ChatLine> field_146253_i = Lists.newArrayList();
  
  private int scrollPos;
  
  private boolean isScrolled;
  
  private BlurShader blurShader = new BlurShader();
  
  public GuiNewChat(Minecraft mcIn) {
    this.mc = mcIn;
  }
  
  public void drawChat(int p_146230_1_, boolean rectangle) {
    if (this.mc.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN) {
      BetterChat betterChat = (BetterChat)Client.INSTANCE.moduleManager.getModuleByClass(BetterChat.class);
      int i = getLineCount();
      boolean flag = false;
      int j = 0;
      int k = this.field_146253_i.size();
      float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
      if (k > 0) {
        if (getChatOpen())
          flag = true; 
        float f1 = getChatScale();
        int l = MathHelper.ceiling_float_int(getChatWidth() / f1);
        GlStateManager.pushMatrix();
        GlStateManager.translate(2.0F, 20.0F, 0.0F);
        GlStateManager.scale(f1, f1, 1.0F);
        for (int i1 = 0; i1 + this.scrollPos < this.field_146253_i.size() && i1 < i; i1++) {
          ChatLine chatline = this.field_146253_i.get(i1 + this.scrollPos);
          if (chatline != null) {
            int j1 = p_146230_1_ - chatline.getUpdatedCounter();
            if (j1 < 200 || flag) {
              double d0 = j1 / 200.0D;
              d0 = 1.0D - d0;
              d0 *= 10.0D;
              d0 = MathHelper.clamp_double(d0, 0.0D, 1.0D);
              d0 *= d0;
              int l1 = (int)(255.0D * d0);
              if (flag)
                l1 = 255; 
              l1 = (int)(l1 * f);
              j++;
              if (l1 > 3) {
                int i2 = 0;
                int j2 = -i1 * 9;
                if (rectangle)
                  Gui.drawRect(i2, (j2 - 9), (i2 + l + 4), j2, l1 / 2 << 24); 
                String s = chatline.getChatComponent().getFormattedText();
                GlStateManager.enableBlend();
                if (!rectangle)
                  if (betterChat.customFont.getValue().booleanValue() && betterChat.isEnabled()) {
                    FontLoaders.Sfui19.drawString(s, i2, (j2 - 8), 16777215 + (l1 << 24));
                  } else {
                    this.mc.fontRendererObj.drawStringWithShadow(s, i2, (j2 - 8), 16777215 + (l1 << 24));
                  }  
                GlStateManager.disableAlpha();
                GlStateManager.disableBlend();
              } 
            } 
          } 
        } 
        if (flag) {
          int k2 = this.mc.fontRendererObj.FONT_HEIGHT;
          GlStateManager.translate(-3.0F, 0.0F, 0.0F);
          int l2 = k * k2 + k;
          int i3 = j * k2 + j;
          int j3 = this.scrollPos * i3 / k;
          int k1 = i3 * i3 / l2;
          if (l2 != i3) {
            int k3 = (j3 > 0) ? 170 : 96;
            int l3 = this.isScrolled ? 13382451 : 3355562;
            drawRect(0.0D, -j3, 2.0D, (-j3 - k1), l3 + (k3 << 24));
            drawRect(2.0D, -j3, 1.0D, (-j3 - k1), 13421772 + (k3 << 24));
          } 
        } 
        GlStateManager.popMatrix();
      } 
    } 
  }
  
  public void clearChatMessages() {
    this.field_146253_i.clear();
    this.chatLines.clear();
    this.sentMessages.clear();
  }
  
  public void printChatMessage(IChatComponent p_146227_1_) {
    printChatMessageWithOptionalDeletion(p_146227_1_, 0);
  }
  
  public void printChatMessageWithOptionalDeletion(IChatComponent p_146234_1_, int p_146234_2_) {
    setChatLine(p_146234_1_, p_146234_2_, this.mc.ingameGUI.getUpdateCounter(), false);
    logger.info("[CHAT] " + p_146234_1_.getUnformattedText());
  }
  
  private void setChatLine(IChatComponent p_146237_1_, int p_146237_2_, int p_146237_3_, boolean p_146237_4_) {
    if (p_146237_2_ != 0)
      deleteChatLine(p_146237_2_); 
    int i = MathHelper.floor_float(getChatWidth() / getChatScale());
    List<IChatComponent> list = GuiUtilRenderComponents.func_178908_a(p_146237_1_, i, this.mc.fontRendererObj, false, false);
    boolean flag = getChatOpen();
    for (IChatComponent ichatcomponent : list) {
      if (flag && this.scrollPos > 0) {
        this.isScrolled = true;
        scroll(1);
      } 
      this.field_146253_i.add(0, new ChatLine(p_146237_3_, ichatcomponent, p_146237_2_));
    } 
    while (this.field_146253_i.size() > 100)
      this.field_146253_i.remove(this.field_146253_i.size() - 1); 
    if (!p_146237_4_) {
      this.chatLines.add(0, new ChatLine(p_146237_3_, p_146237_1_, p_146237_2_));
      while (this.chatLines.size() > 100)
        this.chatLines.remove(this.chatLines.size() - 1); 
    } 
  }
  
  public void refreshChat() {
    this.field_146253_i.clear();
    resetScroll();
    for (int i = this.chatLines.size() - 1; i >= 0; i--) {
      ChatLine chatline = this.chatLines.get(i);
      setChatLine(chatline.getChatComponent(), chatline.getChatLineID(), chatline.getUpdatedCounter(), true);
    } 
  }
  
  public List<String> getSentMessages() {
    return this.sentMessages;
  }
  
  public void addToSentMessages(String p_146239_1_) {
    if (this.sentMessages.isEmpty() || !((String)this.sentMessages.get(this.sentMessages.size() - 1)).equals(p_146239_1_))
      this.sentMessages.add(p_146239_1_); 
  }
  
  public void resetScroll() {
    this.scrollPos = 0;
    this.isScrolled = false;
  }
  
  public void scroll(int p_146229_1_) {
    this.scrollPos += p_146229_1_;
    int i = this.field_146253_i.size();
    if (this.scrollPos > i - getLineCount())
      this.scrollPos = i - getLineCount(); 
    if (this.scrollPos <= 0) {
      this.scrollPos = 0;
      this.isScrolled = false;
    } 
  }
  
  public IChatComponent getChatComponent(int p_146236_1_, int p_146236_2_) {
    if (!getChatOpen())
      return null; 
    ScaledResolution scaledresolution = new ScaledResolution(this.mc);
    int i = scaledresolution.getScaleFactor();
    float f = getChatScale();
    int j = p_146236_1_ / i - 3;
    int k = p_146236_2_ / i - 27;
    j = MathHelper.floor_float(j / f);
    k = MathHelper.floor_float(k / f);
    if (j >= 0 && k >= 0) {
      int l = Math.min(getLineCount(), this.field_146253_i.size());
      if (j <= MathHelper.floor_float(getChatWidth() / getChatScale()) && k < this.mc.fontRendererObj.FONT_HEIGHT * l + l) {
        int i1 = k / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;
        if (i1 >= 0 && i1 < this.field_146253_i.size()) {
          ChatLine chatline = this.field_146253_i.get(i1);
          int j1 = 0;
          for (IChatComponent ichatcomponent : chatline.getChatComponent()) {
            if (ichatcomponent instanceof ChatComponentText) {
              j1 += this.mc.fontRendererObj.getStringWidth(GuiUtilRenderComponents.func_178909_a(((ChatComponentText)ichatcomponent).getChatComponentText_TextValue(), false));
              if (j1 > j)
                return ichatcomponent; 
            } 
          } 
        } 
        return null;
      } 
      return null;
    } 
    return null;
  }
  
  public boolean getChatOpen() {
    return this.mc.currentScreen instanceof GuiChat;
  }
  
  public void deleteChatLine(int p_146242_1_) {
    Iterator<ChatLine> iterator = this.field_146253_i.iterator();
    while (iterator.hasNext()) {
      ChatLine chatline = iterator.next();
      if (chatline.getChatLineID() == p_146242_1_)
        iterator.remove(); 
    } 
    iterator = this.chatLines.iterator();
    while (iterator.hasNext()) {
      ChatLine chatline1 = iterator.next();
      if (chatline1.getChatLineID() == p_146242_1_) {
        iterator.remove();
        break;
      } 
    } 
  }
  
  public int getChatWidth() {
    return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
  }
  
  public int getChatHeight() {
    return calculateChatboxHeight(getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
  }
  
  public float getChatScale() {
    return this.mc.gameSettings.chatScale;
  }
  
  public static int calculateChatboxWidth(float p_146233_0_) {
    int i = 320;
    int j = 40;
    return MathHelper.floor_float(p_146233_0_ * (i - j) + j);
  }
  
  public static int calculateChatboxHeight(float p_146243_0_) {
    int i = 180;
    int j = 20;
    return MathHelper.floor_float(p_146243_0_ * (i - j) + j);
  }
  
  public int getLineCount() {
    return getChatHeight() / 9;
  }
}
