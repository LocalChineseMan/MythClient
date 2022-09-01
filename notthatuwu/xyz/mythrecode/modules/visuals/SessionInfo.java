package notthatuwu.xyz.mythrecode.modules.visuals;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.util.ResourceLocation;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import notthatuwu.xyz.mythrecode.api.utils.shader.list.BlurShader;
import notthatuwu.xyz.mythrecode.api.utils.shader.list.DropShadowUtil;
import notthatuwu.xyz.mythrecode.events.Event2D;
import notthatuwu.xyz.mythrecode.events.EventMove;
import notthatuwu.xyz.mythrecode.events.EventReceivePacket;
import org.lwjgl.opengl.GL11;

@Info(name = "SessionInfo", category = Category.VISUAL)
public class SessionInfo extends Module {
  int kills = 0;
  
  int deaths = 0;
  
  int gamesPlayed = 0;
  
  int Wins = 0;
  
  public int playTimeSeconds = 0;
  
  public int playTimeMinutes = 0;
  
  public int playTimeHours = 0;
  
  private TimeHelper timeHelper = new TimeHelper();
  
  private ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
  
  public ModeSetting type = new ModeSetting("Type", this, new String[] { "1", "2", "3" }, "1");
  
  public NumberSetting Y = new NumberSetting("Y", this, 13.0D, 0.0D, 500.0D, true);
  
  public NumberSetting X = new NumberSetting("X", this, 0.0D, 0.0D, (this.sr.getScaledWidth() * 2), true);
  
  private double x;
  
  private double y;
  
  public BlurShader blurShader = new BlurShader();
  
  @EventTarget
  public void onRender2D(Event2D event) {
    Blur blur = (Blur)Client.INSTANCE.moduleManager.getModuleByClass(Blur.class);
    GlStateManager.disableBlend();
    setX(this.X.getValue().doubleValue());
    setY(this.Y.getValue().doubleValue());
    if (mc.currentScreen instanceof net.minecraft.client.gui.GuiMainMenu) {
      this.kills = 0;
      this.Wins = 0;
      this.gamesPlayed = 0;
      this.deaths = 0;
    } 
    if (!(mc.currentScreen instanceof net.minecraft.client.gui.GuiMainMenu) || !(mc.currentScreen instanceof net.minecraft.client.gui.GuiMultiplayer) || !(mc.currentScreen instanceof net.minecraft.client.gui.GuiOptions)) {
      GL11.glPushMatrix();
      switch (this.type.getValue()) {
        case "1":
          GlStateManager.disableBlend();
          if (blur.isEnabled() && blur.modules.isEnabled("SessionInfo") && blur.shadow.getValue().booleanValue()) {
            DropShadowUtil.start();
            RenderUtils.drawRoundedRect(getX() + 4.0D, 10.0D + getY(), 145.0D, 60.0D, 7.0D, (new Color(0, 0, 0, 120)).getRGB());
            DropShadowUtil.stop();
          } 
          if (blur.isEnabled() && blur.isEnabled() && blur.isEnabled() && blur.modules.isEnabled("SessionInfo"))
            this.blurShader.startBlur(); 
          RenderUtils.drawRoundedRect(getX() + 4.0D, 10.0D + getY(), 145.0D, 60.0D, 7.0D, (new Color(0, 0, 0, 120)).getRGB());
          if (blur.isEnabled() && blur.isEnabled() && blur.isEnabled() && blur.modules.isEnabled("SessionInfo"))
            this.blurShader.stopBlur(blur.sigma.getValueInt(), blur.radius.getValueInt(), 1); 
          FontLoaders.Sfui20.drawString(" Session Info ", (float)(getX() + 40.0D), (float)(15.0D + getY()), -1);
          FontLoaders.Sfui16.drawString("Kills: " + this.kills, (float)(getX() + 6.0D), (float)(47.0D + getY()), -1);
          FontLoaders.Sfui16.drawString("Wins: " + this.Wins, (float)(getX() + 6.0D), (float)(37.0D + getY()), -1);
          FontLoaders.Sfui16.drawString("Time: " + this.playTimeHours + "h, " + this.playTimeMinutes + "m, " + this.playTimeSeconds + "s", (float)(getX() + 6.0D), (float)(27.0D + getY()), -1);
          break;
        case "2":
          GlStateManager.disableBlend();
          if (blur.isEnabled() && blur.isEnabled() && blur.isEnabled() && blur.modules.isEnabled("SessionInfo"))
            this.blurShader.startBlur(); 
          RenderUtils.drawRoundedRect(getX() + 4.0D + 10.0D, 10.0D + getY(), 175.0D, 60.0D, 7.0D, (new Color(0, 0, 0, 120)).getRGB());
          RenderUtils.drawRoundedRect(getX() + 4.0D + 10.0D, 10.0D + getY(), 50.0D, 60.0D, 7.0D, (new Color(0, 0, 0, 60)).getRGB());
          if (blur.isEnabled() && blur.isEnabled() && blur.isEnabled() && blur.modules.isEnabled("SessionInfo"))
            this.blurShader.stopBlur(blur.sigma.getValueInt(), blur.radius.getValueInt(), 1); 
          if (blur.isEnabled() && blur.modules.isEnabled("SessionInfo") && blur.shadow.getValue().booleanValue()) {
            DropShadowUtil.start();
            RenderUtils.drawRoundedRect(getX() + 4.0D + 10.0D, 10.0D + getY(), 175.0D, 60.0D, 7.0D, (new Color(0, 0, 0, 120)).getRGB());
            RenderUtils.drawRoundedRect(getX() + 4.0D + 10.0D, 10.0D + getY(), 50.0D, 60.0D, 7.0D, (new Color(0, 0, 0, 60)).getRGB());
            DropShadowUtil.stop();
          } 
          RenderUtils.drawImage((int)(getX() + 25.0D), (int)(27.0D + getY()), 24, 24, new ResourceLocation("myth/Icons/clock.png"));
          GlStateManager.disableBlend();
          FontLoaders.Sfui18.drawString("Kills: " + this.kills, (float)(getX() + 70.0D), (float)(47.0D + getY()), -1);
          FontLoaders.Sfui18.drawString("Wins: " + this.Wins, (float)(getX() + 70.0D), (float)(37.0D + getY()), -1);
          FontLoaders.Sfui18.drawString("Play Time: " + this.playTimeHours + "h, " + this.playTimeMinutes + "m, " + this.playTimeSeconds + "s", (float)(getX() + 70.0D), (float)(27.0D + getY()), -1);
          break;
        case "3":
          GlStateManager.disableBlend();
          if (blur.isEnabled() && blur.isEnabled() && blur.isEnabled() && blur.modules.isEnabled("SessionInfo"))
            this.blurShader.startBlur(); 
          RenderUtils.drawRoundedRect(getX() + 4.0D + 10.0D, 10.0D + getY(), 155.0D, 65.0D, 7.0D, (new Color(0, 0, 0, 120)).getRGB());
          RenderUtils.drawRoundedRect(getX() + 4.0D + 10.0D, 10.0D + getY(), 30.0D, 65.0D, 7.0D, (new Color(0, 0, 0, 60)).getRGB());
          if (blur.isEnabled() && blur.isEnabled() && blur.isEnabled() && blur.modules.isEnabled("SessionInfo"))
            this.blurShader.stopBlur(blur.sigma.getValueInt(), blur.radius.getValueInt(), 1); 
          if (blur.isEnabled() && blur.modules.isEnabled("SessionInfo") && blur.shadow.getValue().booleanValue()) {
            DropShadowUtil.start();
            RenderUtils.drawRoundedRect(getX() + 4.0D + 10.0D, 10.0D + getY(), 155.0D, 65.0D, 7.0D, (new Color(0, 0, 0, 120)).getRGB());
            RenderUtils.drawRoundedRect(getX() + 4.0D + 10.0D, 10.0D + getY(), 30.0D, 65.0D, 7.0D, (new Color(0, 0, 0, 60)).getRGB());
            DropShadowUtil.stop();
          } 
          RenderUtils.drawImage((int)(getX() + 25.0D), (int)(53.0D + getY()), 16, 16, new ResourceLocation("myth/Icons/sword.png"));
          RenderUtils.drawImage((int)(getX() + 25.0D), (int)(17.0D + getY()), 16, 16, new ResourceLocation("myth/Icons/time.png"));
          RenderUtils.drawImage((int)(getX() + 25.0D), (int)(34.0D + getY()), 16, 16, new ResourceLocation("myth/Icons/played.png"));
          GlStateManager.disableBlend();
          FontLoaders.Sfui22.drawString("- " + this.kills, (float)(getX() + 45.0D), (float)(57.0D + getY()), -1);
          FontLoaders.Sfui22.drawString("- " + this.Wins, (float)(getX() + 45.0D), (float)(37.0D + getY()), -1);
          FontLoaders.Sfui22.drawString("- " + this.playTimeHours + "h, " + this.playTimeMinutes + "m, " + this.playTimeSeconds + "s", (float)(getX() + 45.0D), (float)(19.0D + getY()), -1);
          break;
      } 
      GL11.glPopMatrix();
    } 
  }
  
  @EventTarget
  public void onPacket(EventReceivePacket ep) {
    if (ep.getPacket() instanceof S02PacketChat) {
      S02PacketChat s02 = (S02PacketChat)ep.getPacket();
      String xd = s02.getChatComponent().getUnformattedText();
      this;
      if (xd.contains(" was killed by " + mc.thePlayer.getName()))
        this.kills++; 
      if (xd.contains(String.valueOf("Sending you to")))
        this.gamesPlayed++; 
      this;
      if (!mc.thePlayer.isEntityAlive())
        this.deaths++; 
      this;
      if (xd.contains(String.valueOf("Winner - " + mc.thePlayer.getName())))
        this.Wins++; 
    } 
    if (ep.getPacket() instanceof S45PacketTitle) {
      S45PacketTitle packet = (S45PacketTitle)ep.getPacket();
      if (packet.getMessage() == null)
        return; 
      if (packet.getMessage().getUnformattedText().equals("YOU DIED!") || packet.getMessage().getUnformattedText().equals("You are now a spectator!"))
        this.deaths++; 
      if (packet.getMessage().getUnformattedText().equals("GAME END") || packet.getMessage().getUnformattedText().equals("YOU DIED!") || packet.getMessage().getUnformattedText().equals("VICTORY!") || packet.getMessage().getUnformattedText().equals("You are now a spectator!"))
        this.gamesPlayed++; 
      if (packet.getMessage().getUnformattedText().equals("VICTORY!"))
        this.Wins++; 
    } 
  }
  
  @EventTarget
  public void onUpdate(EventMove e) {
    boolean shouldRevert = (mc.getCurrentServerData() == null || (mc.getCurrentServerData()).serverIP == null);
    if (shouldRevert) {
      this.playTimeSeconds = 0;
      this.playTimeMinutes = 0;
      this.playTimeHours = 0;
    } 
    if (this.timeHelper.hasReached(1000L)) {
      this.playTimeSeconds++;
      this.timeHelper.reset();
    } 
    if (this.playTimeSeconds == 60) {
      this.playTimeSeconds = 0;
      this.playTimeMinutes++;
    } 
    if (this.playTimeMinutes == 60) {
      this.playTimeMinutes = 0;
      this.playTimeHours = 1;
    } 
  }
  
  public void reset() {
    this.playTimeMinutes = 0;
    this.playTimeHours = 0;
    this.playTimeMinutes = 0;
  }
  
  public void onEnable() {
    super.onEnable();
    float startTime = (float)System.currentTimeMillis();
  }
  
  public static boolean hovered(float left, float top, float right, float bottom, int mouseX, int mouseY) {
    return (mouseX >= left && mouseY >= top && mouseX < right && mouseY < bottom);
  }
  
  public static Color getGradientOffset(Color color1, Color color2, double offset) {
    if (offset > 1.0D) {
      double left = offset % 1.0D;
      int off = (int)offset;
      offset = (off % 2 == 0) ? left : (1.0D - left);
    } 
    double inverse_percent = 1.0D - offset;
    int redPart = (int)(color1.getRed() * inverse_percent + color2.getRed() * offset);
    int greenPart = (int)(color1.getGreen() * inverse_percent + color2.getGreen() * offset);
    int bluePart = (int)(color1.getBlue() * inverse_percent + color2.getBlue() * offset);
    return new Color(redPart, greenPart, bluePart);
  }
  
  public void setX(double x) {
    this.x = x;
  }
  
  public void setY(double y) {
    this.y = y;
  }
  
  public double getX() {
    return this.x;
  }
  
  public double getY() {
    return this.y;
  }
}
