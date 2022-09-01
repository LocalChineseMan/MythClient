package net.minecraft.client.gui;

import java.io.IOException;
import java.util.List;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;
import notthatuwu.xyz.mythrecode.Client;

public class GuiDisconnected extends GuiScreen {
  private final String reason;
  
  private final IChatComponent message;
  
  private List<String> multilineMessage;
  
  private final GuiScreen parentScreen;
  
  private int field_175353_i;
  
  private GuiButton reconnectButton;
  
  private ServerData lastServer;
  
  public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp) {
    this.parentScreen = screen;
    this.reason = I18n.format(reasonLocalizationKey, new Object[0]);
    this.message = chatComp;
  }
  
  protected void keyTyped(char typedChar, int keyCode) throws IOException {}
  
  public void initGui() {
    this.buttonList.clear();
    this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
    this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
    int x = this.width / 2 - 100;
    int y = this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT;
    this.buttonList.add(new GuiButton(0, x, y, I18n.format("gui.toMenu", new Object[0])));
    this.buttonList.add(this.reconnectButton = new GuiButton(1, x, y + 20, "Reconnect"));
    this.lastServer = Client.INSTANCE.lastServer;
    this.reconnectButton.enabled = (this.lastServer != null);
  }
  
  protected void actionPerformed(GuiButton button) throws IOException {
    switch (button.id) {
      case 0:
        this.mc.displayGuiScreen(this.parentScreen);
        break;
      case 1:
        reconnect();
        break;
    } 
  }
  
  private void reconnect() {
    this.mc.displayGuiScreen((GuiScreen)new GuiConnecting(this.parentScreen, this.mc, this.lastServer));
  }
  
  private void setEnabledLogInButtons(boolean b) {
    this.reconnectButton.enabled = b;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
    int i = this.height / 2 - this.field_175353_i / 2;
    if (this.multilineMessage != null)
      for (String s : this.multilineMessage) {
        drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
        i += this.fontRendererObj.FONT_HEIGHT;
      }  
    int y = this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT;
    if (this.mc.session != null && this.lastServer != null)
      this.fontRendererObj.drawString(this.mc.session.getUsername() + " - " + this.lastServer.serverIP, this.width / 2 - this.fontRendererObj.getStringWidth(this.mc.session.getUsername() + " - " + this.lastServer.serverIP) / 2, y + 85, -1); 
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
}
