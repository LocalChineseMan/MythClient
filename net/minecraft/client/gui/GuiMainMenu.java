package net.minecraft.client.gui;

import java.awt.Color;
import java.io.IOException;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.ui.altsmanager.GuiAltManager;
import notthatuwu.xyz.mythrecode.api.ui.mainmenu.GuiCredits;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import notthatuwu.xyz.mythrecode.api.utils.shader.list.BlurShader;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {
  public static String shaderName;
  
  public TimeHelper timeHelper = new TimeHelper();
  
  public BlurShader blurShader = new BlurShader();
  
  public void initGui() {
    Client.INSTANCE.discordRPC.update("Myth 1.3 - 12", "Main Menu");
    if (!Client.firstTimeAnimation) {
      Client.logoMove = 20;
      Client.buttonFade = 0;
      Client.buttonFadeBG = 0;
      Client.firstTimeAnimation = true;
    } 
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    ScaledResolution sr = new ScaledResolution(this.mc);
    shaderName = "rainbow";
    if (Client.logoMove < 100)
      Client.logoMove += 4; 
    if (Client.buttonFadeBG == 70 && Client.buttonFade < 255 && Client.logoMove == 100)
      Client.buttonFade += 5; 
    if (Client.buttonFadeBG < 70 && Client.logoMove == 100)
      Client.buttonFadeBG += 5; 
    this.mc.getTextureManager().bindTexture(new ResourceLocation("myth/image/" + shaderName + ".png"));
    Gui.drawScaledCustomSizeModalRect(0, 0, 0.0F, 0.0F, sr.getScaledWidth(), sr.getScaledHeight(), sr.getScaledWidth(), sr.getScaledHeight(), sr.getScaledWidth(), sr.getScaledHeight());
    GL11.glBegin(7);
    GL11.glVertex2f(-1.0F, -1.0F);
    GL11.glVertex2f(-1.0F, 1.0F);
    GL11.glVertex2f(1.0F, 1.0F);
    GL11.glVertex2f(1.0F, -1.0F);
    GL11.glEnd();
    GL20.glUseProgram(0);
    GL11.glEnable(3553);
    GL11.glEnable(3008);
    this.blurShader.startBlur();
    RenderUtils.drawRoundedRect2((sr.getScaledWidth() / 2 - 120), (sr.getScaledHeight() / 2 - 20 - Client.logoMove), (sr.getScaledWidth() / 2 + 120), (sr.getScaledHeight() / 2 - 15 - Client.logoMove + 230), 5.0D, (new Color(0, 0, 0, 120)).getRGB());
    this.blurShader.stopBlur();
    RenderUtils.drawUnfilledRoundedRect((sr.getScaledWidth() / 2 - 120), (sr.getScaledHeight() / 2 - 20 - Client.logoMove), 240.0F, (15 - Client.logoMove + 320), 5.0F, -1);
    FontLoaders.Sfui40.drawCenteredString("Myth", (sr.getScaledWidth() / 2 - 3), (sr.getScaledHeight() / 2 - 15 - Client.logoMove), (new Color(255, 255, 255)).getRGB());
    FontLoaders.Sfui16.drawCenteredString("made by Auxy, Codeman, $kush", (sr.getScaledWidth() / 2 - 3), (sr.getScaledHeight() / 2 - 15 - Client.logoMove + 205), (new Color(255, 255, 255)).getRGB());
    String[] S = { "SinglePlayer", "MultiPlayer", "Alts Manager", "Settings", "Quit" };
    for (int i = 0; i < 5; i++) {
      RenderUtils.drawRoundedRect2((int)(sr.getScaledWidth_double() / 2.0D - 75.0D), (int)(sr.getScaledHeight_double() / 2.0D - 75.0D + (i * 25) + (i * 5)), ((int)(sr.getScaledWidth_double() / 2.0D) + 75), (int)(sr.getScaledHeight_double() / 2.0D - 50.0D + (i * 25) + (i * 5)), 10.0D, (Client.buttonFadeBG == 70) ? (isHovered((int)(sr.getScaledWidth_double() / 2.0D - 75.0D), (int)(sr.getScaledHeight_double() / 2.0D - 75.0D + (i * 25) + (i * 5)), ((int)(sr.getScaledWidth_double() / 2.0D) + 75), (int)(sr.getScaledHeight_double() / 2.0D - 50.0D + (i * 25) + (i * 5)), mouseX, mouseY) ? (new Color(0, 0, 0, 179)).getRGB() : (new Color(0, 0, 0, 77)).getRGB()) : (new Color(0, 0, 0, Client.buttonFadeBG)).getRGB());
      if (Client.buttonFade > 0)
        FontLoaders.Sfui20.drawCenteredString(S[i], ((float)sr.getScaledWidth_double() / 2.0F), ((float)sr.getScaledHeight_double() / 2.0F - 66.0F + (i * 30)), (new Color(255, 255, 255, Client.buttonFade)).getRGB()); 
    } 
    int fontHeight = FontLoaders.Sfui16.getHeight();
    boolean isCreditsHovered = isHoveredNotGay((FontLoaders.Sfui16.getStringWidth("Release 1.1") + 2 + FontLoaders.Sfui16.getStringWidth("|") + 2), (sr.getScaledHeight() - fontHeight), FontLoaders.Sfui16.getStringWidth("Credits"), fontHeight, mouseX, mouseY);
    FontLoaders.Sfui16.drawString("Release 1.1", 2.0F, (sr.getScaledHeight() - fontHeight), Color.GRAY.getRGB());
    FontLoaders.Sfui16.drawString("|", (FontLoaders.Sfui16.getStringWidth("Release 1.1") + 2), (sr.getScaledHeight() - fontHeight), -1);
    FontLoaders.Sfui16.drawString("Credits", (FontLoaders.Sfui16.getStringWidth("Release 1.1") + 2 + FontLoaders.Sfui16.getStringWidth("|") + 2), (sr.getScaledHeight() - fontHeight), isCreditsHovered ? -1 : Color.GRAY.getRGB());
    if (isCreditsHovered && Mouse.isButtonDown(0) && this.timeHelper.hasReached(500L))
      this.mc.displayGuiScreen((GuiScreen)new GuiCredits()); 
    GlStateManager.color(1.0F, 1.0F, 1.0F);
  }
  
  public static boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
    return (mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2);
  }
  
  public static boolean isHoveredNotGay(float x, float y, float width, float height, int mouseX, int mouseY) {
    return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
  }
  
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    ScaledResolution sr = new ScaledResolution(this.mc);
    for (int i = 0; i < 5; i++) {
      if (isHovered((int)(sr.getScaledWidth_double() / 2.0D - 75.0D), (int)(sr.getScaledHeight_double() / 2.0D - 75.0D + (i * 25) + (i * 5)), ((int)(sr.getScaledWidth_double() / 2.0D) + 75), (int)(sr.getScaledHeight_double() / 2.0D - 50.0D + (i * 25) + (i * 5)), mouseX, mouseY))
        switch (i) {
          case 0:
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
            break;
          case 1:
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
            break;
          case 2:
            this.mc.displayGuiScreen((GuiScreen)new GuiAltManager());
            break;
          case 3:
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
            break;
          case 4:
            this.mc.shutdown();
            break;
        }  
    } 
    super.mouseClicked(mouseX, mouseY, mouseButton);
  }
  
  protected void keyTyped(char typedChar, int keyCode) throws IOException {}
  
  public boolean hovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
    return (mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2);
  }
  
  public void onGuiClosed() {}
}
