package notthatuwu.xyz.mythrecode.modules.display;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.Module.Info;
import notthatuwu.xyz.mythrecode.api.module.ModuleManager;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.AddonSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ColorSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;
import notthatuwu.xyz.mythrecode.api.utils.ColorUtil;
import notthatuwu.xyz.mythrecode.api.utils.font.CFontRenderer;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import notthatuwu.xyz.mythrecode.api.utils.render.StringUtil;
import notthatuwu.xyz.mythrecode.api.utils.shader.list.DropShadowUtil;
import notthatuwu.xyz.mythrecode.events.Event2D;
import notthatuwu.xyz.mythrecode.manager.InstanceManager;
import notthatuwu.xyz.mythrecode.modules.visuals.Blur;

@Info(name = "HUD", description = "Show you enabled modules cool stuff", category = Category.DISPLAY)
public class HUD extends Module {
  public final ModeSetting watermarkStyle = new ModeSetting("Watermark", this, new String[] { "Basic", "Old Moon", "Skeet", "Rainbow", "Simple", "Japan" }, "Rainbow");
  
  public final ModeSetting arrayListStyle = new ModeSetting("Arraylist", this, new String[] { "Old", "Gato", "Chill", "Basic", "Rofl potato" }, "Old");
  
  public final AddonSetting outlineAddons = new AddonSetting("Outline", this, "Right", new String[] { "Right", "Left", "Bottom", "Top" });
  
  public final ModeSetting cape = new ModeSetting("Cape", this, new String[] { "None", "Hentai", "Hentai 2", "Hentai 3" }, "None");
  
  public final ModeSetting fontMode = new ModeSetting("Font", this, new String[] { "Normal", "Brains", "Chill", "Sand" }, "Sand");
  
  public final ModeSetting arrayListColorMode = new ModeSetting("Color", this, new String[] { "Rainbow", "Myth", "Client" }, "Myth");
  
  public final AddonSetting addons = new AddonSetting("Addons", this, "BPS", new String[] { "BPS", "FPS", "Coords", "Text Shadow", "Lowercase" });
  
  public final BooleanSetting arrayListBackground = new BooleanSetting("Background", this, true, () -> Boolean.valueOf((this.arrayListStyle.is("Chill") || this.arrayListStyle.is("Rofl potato"))));
  
  public final NumberSetting arrayListBackgroundOpacity = new NumberSetting("Background Alpha", this, 70.0D, 0.0D, 255.0D, true, () -> Boolean.valueOf(((this.arrayListBackground.getValue().booleanValue() && this.arrayListStyle.getValue().equalsIgnoreCase("Old")) || (this.arrayListStyle.is("Chill") && this.arrayListBackground.getValue().booleanValue()))));
  
  public final NumberSetting arrayListColorDelay = new NumberSetting("Color Delay", this, 150.0D, 0.0D, 1000.0D, true);
  
  public final ColorSetting colorSetting = new ColorSetting("Client Color", this, new Color(120, 10, 120), () -> Boolean.valueOf(this.arrayListColorMode.is("Client")));
  
  public final BooleanSetting hideVisuals = new BooleanSetting("Hide Visuals", this, false);
  
  @EventTarget
  public void onRender(Event2D event) {
    FontRenderer fr = (Minecraft.getMinecraft()).fontRendererObj;
    if (this.arrayListStyle.getValue().equals("Basic"));
    renderArrayList(this.arrayListStyle.getValue());
    renderBlurInterface(this.arrayListStyle.getValue());
    renderHUD();
    renderWatermarks(this.watermarkStyle.getValue());
  }
  
  public void renderWatermarks(String mode) {
    String string;
    SimpleDateFormat format;
    String hudString;
    int posX;
    String text, rateString, timeString;
    int i, posY;
    float width;
    AtomicLong time;
    long currentPing;
    int height, xCharRainbow;
    AtomicInteger count;
    int j;
    char[] by;
    String textAlready;
    int k, xChar;
    FontRenderer fr = MC.fontRendererObj;
    switch (mode) {
      case "Basic":
        fr.drawStringWithShadow("Myth 1.3", 3.0F, 3.0F, -1);
        string = "FPS: " + Minecraft.getDebugFPS() + " / ";
        fr.drawStringWithShadow(string, 3.0F, 4.0F + fr.FONT_HEIGHT, -1);
        rateString = "(" + ((MC.gameSettings.limitFramerate == 260) ? "Unlimited" : (String)Integer.valueOf(MC.gameSettings.limitFramerate)) + ")";
        time = new AtomicLong(Minecraft.getSystemTime());
        count = new AtomicInteger(1);
        textAlready = "";
        for (char c : rateString.toCharArray()) {
          int color = ColorUtil.getColor(this.arrayListColorMode.getValue(), (int)time.get());
          fr.drawStringWithShadow(String.valueOf(c), 3.0F + fr.getStringWidth(string) + fr.getStringWidth(textAlready), 4.0F + fr.FONT_HEIGHT, color);
          textAlready = textAlready + c;
          count.set(count.get() + 1);
          time.set(time.get() - 300L);
        } 
        break;
      case "Old Moon":
        format = new SimpleDateFormat("hh:mm aa");
        timeString = format.format(new Date());
        currentPing = 0L;
        if (mc.getCurrentServerData() != null)
          currentPing = (mc.getCurrentServerData()).pingToServer; 
        fr.drawStringWithShadow("Myth " + ChatFormatting.GRAY + "(" + timeString + ")", 2.0F, 2.0F, -1);
        fr.drawStringWithShadow(ChatFormatting.GRAY + "FPS: " + Minecraft.getDebugFPS() + " - MS: " + currentPing, 2.0F, (2 + fr.FONT_HEIGHT), -1);
        break;
      case "Skeet":
        hudString = "Myth | " + mc.thePlayer.getName() + " | " + (mc.isSingleplayer() ? "SinglePlayer     " : ((mc.getCurrentServerData()).serverIP + " | " + Minecraft.debugFPS + "fps | " + mc.thePlayer.sendQueue.getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime() + "ms     "));
        i = FontLoaders.Sfui16.getStringWidth(hudString);
        height = getHudFont().getStringHeight(hudString);
        j = 5;
        k = 5;
        Gui.drawRect(5.0D, 5.0D, (5 + i), (5 + height), (new Color(5, 5, 5, 255)).getRGB());
        RenderUtils.drawBorderedRect(5.5D, 5.5D, (5 + i) - 0.5D, (5 + height) - 0.5D, 0.5D, (new Color(40, 40, 40, 255)).getRGB(), (new Color(60, 60, 60, 255)).getRGB());
        RenderUtils.drawBorderedRect(7.0D, 7.0D, (5 + i - 2), (5 + height - 2), 0.5D, (new Color(22, 22, 22, 255)).getRGB(), (new Color(60, 60, 60, 255)).getRGB());
        Gui.drawRect(7.5D, 7.5D, (5 + i) - 2.5D, 9.5D, (new Color(9, 9, 9, 255)).getRGB());
        Gui.drawGradientSideways(8.0D, 8.0D, (5 + i / 3), 9.0D, (new Color(81, 149, 219, 255)).getRGB(), (new Color(180, 49, 218, 255)).getRGB());
        Gui.drawGradientSideways((5 + i / 3), 8.0D, (5 + i / 3 * 2), 9.0D, (new Color(180, 49, 218, 255)).getRGB(), (new Color(236, 93, 128, 255)).getRGB());
        Gui.drawGradientSideways((5 + i / 3 * 2), 8.0D, (5 + i / 3 * 3 - 3), 9.0D, (new Color(236, 93, 128, 255)).getRGB(), (new Color(167, 171, 90, 255)).getRGB());
        FontLoaders.Sfui16.drawString(this.addons.isEnabled("Lowercase") ? ColorUtil.translateColor(hudString).toLowerCase(Locale.ROOT) : ColorUtil.translateColor(hudString), 10.0F, 13.0F, -1);
        break;
      case "Rainbow":
        posX = 5;
        posY = 5;
        xCharRainbow = 0;
        by = "Myth".toCharArray();
        for (xChar = 0; xChar < "Myth".length(); xChar++) {
          if (this.addons.isEnabled("Text Shadow")) {
            getHudFont().drawStringWithShadow(this.addons.isEnabled("Lowercase") ? String.valueOf(by[xChar]).toLowerCase(Locale.ROOT) : String.valueOf(by[xChar]), (xCharRainbow + 5), 5.0D, ColorUtil.rainbow(xChar * 80).getRGB());
          } else {
            getHudFont().drawString(this.addons.isEnabled("Lowercase") ? String.valueOf(by[xChar]).toLowerCase(Locale.ROOT) : String.valueOf(by[xChar]), (xCharRainbow + 5), 5.0F, ColorUtil.rainbow(xChar * 80).getRGB());
          } 
          xCharRainbow += getHudFont().getStringWidth(this.addons.isEnabled("Lowercase") ? String.valueOf(by[xChar]).toLowerCase(Locale.ROOT) : String.valueOf(by[xChar]));
        } 
        break;
      case "Japan":
        FontLoaders.realjapan.drawString("M" + EnumChatFormatting.WHITE + "YTH", 6.0F, 6.0F, ColorUtil.getColor(this.arrayListColorMode.getValue(), this.arrayListColorDelay.getValueInt()));
        break;
      case "Simple":
        text = "Myth - " + StringUtil.upperSnakeCaseToPascal(Client.INSTANCE.discordRPC.getUser());
        width = (FontLoaders.Sfui18.getStringWidth(text) + 6);
        Gui.drawRect(2.0D, 2.0D, width, 3.0D, ColorUtil.getColor(this.arrayListColorMode.getValue(), this.arrayListColorDelay.getValueInt() * 3));
        Gui.drawRect(2.0D, 3.0D, width, 13.0D, (new Color(0, 0, 0, 100)).getRGB());
        FontLoaders.Sfui18.drawString(this.addons.isEnabled("Lowercase") ? text.toLowerCase(Locale.ROOT) : text, 4.0F, 5.3F, -1);
        break;
    } 
  }
  
  public void renderHUD() {
    ScaledResolution sr = new ScaledResolution(mc);
    if (this.addons.isEnabled("FPS")) {
      float y = sr.getScaledHeight() - (this.addons.isEnabled("Coords") ? (this.addons.isEnabled("BPS") ? (getHudFont().getHeight() * 3.2F + 3.0F) : (getHudFont().getHeight() * 2.2F + 1.0F)) : (this.addons.isEnabled("BPS") ? (getHudFont().getHeight() * 2.2F + 2.0F) : (getHudFont().getHeight() * 1.2F + 1.0F)));
      String fpsFont = EnumChatFormatting.RESET + "FPS: " + EnumChatFormatting.WHITE + Minecraft.getDebugFPS();
      getHudFont().drawString(fpsFont, 1.0F, y, ColorUtil.getColor(this.arrayListColorMode.getValue(), 3 * this.arrayListColorDelay.getValueInt()));
    } 
    if (this.addons.isEnabled("BPS")) {
      float y = sr.getScaledHeight() - (this.addons.isEnabled("Coords") ? (getHudFont().getHeight() * 2.2F + 2.0F) : (getHudFont().getHeight() * 1.2F + 1.0F));
      String bps = String.format("%.2f", new Object[] { Double.valueOf(Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed * mc.timer.ticksPerSecond) });
      getHudFont().drawString("BPS: " + EnumChatFormatting.WHITE + bps, 1.0F, y, ColorUtil.getColor(this.arrayListColorMode.getValue(), 3 * this.arrayListColorDelay.getValueInt()));
    } 
    if (this.addons.isEnabled("Coords")) {
      String xyz = String.format("§rXYZ:§f %.0f %.0f %.0f", new Object[] { Double.valueOf(mc.thePlayer.posX), Double.valueOf((mc.thePlayer.getEntityBoundingBox()).minY), Double.valueOf(mc.thePlayer.posZ) });
      getHudFont().drawString(xyz, 1.0F, sr.getScaledHeight() - getHudFont().getHeight() * 1.2F, ColorUtil.getColor(this.arrayListColorMode.getValue(), 3 * this.arrayListColorDelay.getValueInt()));
    } 
    String build = "040822";
    String text = ChatFormatting.GRAY + "Developer Build - " + ChatFormatting.WHITE + build + ChatFormatting.GRAY + " - " + Client.INSTANCE.discordRPC.getUser();
    getHudFont().drawStringWithShadow(text, (sr.getScaledWidth() - getHudFont().getStringWidth(text) - 1.0F), (sr.getScaledHeight() - getHudFont().getHeight() * 1.2F), -1);
    Client.INSTANCE.notificationManager.drawNotifications();
  }
  
  public void renderArrayList(String mode) {
    ScaledResolution sr;
    float time;
    CFontRenderer cFontRenderer;
    FontRenderer fr;
    int y;
    List<Module> showedModules;
    int count;
    AtomicLong atomicLong;
    List<Module> list1;
    AtomicReference<Float> yPos;
    AtomicInteger atomicInteger;
    switch (mode) {
      case "Rofl potato":
        sr = new ScaledResolution(MC);
        cFontRenderer = getHudFont();
        showedModules = (List<Module>)ModuleManager.getModules().stream().filter(module -> (module.isEnabled() && (!this.hideVisuals.getValue().booleanValue() || (module.getCategory() != Category.VISUAL && module.getCategory() != Category.DISPLAY)))).collect(Collectors.toList());
        showedModules.sort((feature0, feature1) -> (fr.getStringWidth(feature0.getDisplayName()) > fr.getStringWidth(feature1.getDisplayName())) ? -1 : 1);
        if (showedModules.isEmpty())
          return; 
        atomicLong = new AtomicLong(Minecraft.getSystemTime());
        yPos = new AtomicReference<>(Float.valueOf(3.0F));
        atomicInteger = new AtomicInteger(1);
        showedModules.forEach(module -> {
              int color = ColorUtil.getColor(this.arrayListColorMode.getValue(), (int)time.get());
              if (this.arrayListBackground.getValue().booleanValue())
                Gui.drawRect(((sr.getScaledWidth() - fr.getStringWidth(module.getDisplayName())) - 4.0F), (((Float)yPos.get()).floatValue() - 1.0F), (sr.getScaledWidth() - 1.5F), (((Float)yPos.get()).floatValue() + fr.getStringHeight(module.getDisplayName()) + 0.5F), (new Color(0, 0, 0, this.arrayListBackgroundOpacity.getValueInt())).getRGB()); 
              if (this.outlineAddons.isEnabled("Left"))
                Gui.drawRect(((sr.getScaledWidth() - fr.getStringWidth(module.getDisplayName())) - 5.0F), (((Float)yPos.get()).floatValue() - 1.0F), ((sr.getScaledWidth() - fr.getStringWidth(module.getDisplayName())) - 4.0F), (((Float)yPos.get()).floatValue() + fr.getStringHeight(module.getDisplayName()) + 0.5F), color); 
              if (this.outlineAddons.isEnabled("Top") && showedModules.indexOf(module) == 0)
                Gui.drawRect(((sr.getScaledWidth() - fr.getStringWidth(module.getDisplayName())) - 5.0F), (((Float)yPos.get()).floatValue() - 2.0F), (sr.getScaledWidth() - 0.5F), (((Float)yPos.get()).floatValue() - 1.0F), color); 
              if (this.outlineAddons.isEnabled("Bottom") && showedModules.indexOf(module) == showedModules.size() - 1)
                Gui.drawRect(((sr.getScaledWidth() - fr.getStringWidth(module.getDisplayName())) - 5.0F), (((Float)yPos.get()).floatValue() + fr.getStringHeight(module.getDisplayName()) + 0.5F), (sr.getScaledWidth() - 0.5F), (((Float)yPos.get()).floatValue() + fr.getStringHeight(module.getDisplayName()) + 1.5F), color); 
              if (this.outlineAddons.isEnabled("Right"))
                Gui.drawRect((sr.getScaledWidth() - 1.5F), (((Float)yPos.get()).floatValue() - 1.0F), (sr.getScaledWidth() - 0.5F), (((Float)yPos.get()).floatValue() + fr.getStringHeight(module.getDisplayName()) + 0.5F), color); 
              if (this.outlineAddons.isEnabled("Left"))
                try {
                  Module nextModule = showedModules.get(showedModules.indexOf(module) + 1);
                  Gui.drawRect(((sr.getScaledWidth() - fr.getStringWidth(module.getDisplayName())) - 5.0F), (((Float)yPos.get()).floatValue() + fr.getStringHeight(nextModule.getDisplayName()) + 0.5F), ((sr.getScaledWidth() - fr.getStringWidth(nextModule.getDisplayName())) - 5.0F), (((Float)yPos.get()).floatValue() + fr.getStringHeight(nextModule.getDisplayName()) + 1.5F), color);
                } catch (Exception exception) {} 
              if (this.addons.isEnabled("Text Shadow")) {
                fr.drawStringWithShadow(module.getDisplayName(), ((sr.getScaledWidth() - fr.getStringWidth(module.getDisplayName())) - 3.0F), ((Float)yPos.get()).floatValue(), color);
              } else {
                fr.drawString(module.getDisplayName(), (sr.getScaledWidth() - fr.getStringWidth(module.getDisplayName())) - 3.0F, ((Float)yPos.get()).floatValue(), color);
              } 
              count.set(count.get() + 1);
              yPos.set(Float.valueOf(((Float)yPos.get()).floatValue() + fr.getStringHeight(module.getDisplayName()) + 1.5F));
              time.set(time.get() - 300L);
            });
        break;
      case "Basic":
        sr = new ScaledResolution(MC);
        fr = mc.fontRendererObj;
        showedModules = (List<Module>)ModuleManager.getModules().stream().filter(module -> (module.isEnabled() && (!this.hideVisuals.getValue().booleanValue() || (module.getCategory() != Category.VISUAL && module.getCategory() != Category.DISPLAY)))).collect(Collectors.toList());
        showedModules.sort((feature0, feature1) -> (fr.getStringWidth(feature0.getDisplayName()) > fr.getStringWidth(feature1.getDisplayName())) ? -1 : 1);
        atomicLong = new AtomicLong(Minecraft.getSystemTime());
        yPos = new AtomicReference<>(Float.valueOf(3.0F));
        atomicInteger = new AtomicInteger(1);
        showedModules.forEach(module -> {
              int color = ColorUtil.getColor(this.arrayListColorMode.getValue(), (int)time.get());
              if (this.addons.isEnabled("Text Shadow")) {
                fr.drawStringWithShadow(module.getDisplayName(), (sr.getScaledWidth() - fr.getStringWidth(module.getDisplayName())) - 3.0F, ((Float)yPos.get()).floatValue(), color);
              } else {
                fr.drawString(module.getDisplayName(), (int)((sr.getScaledWidth() - fr.getStringWidth(module.getDisplayName())) - 3.0F), ((Float)yPos.get()).intValue(), color);
              } 
              count.set(count.get() + 1);
              yPos.set(Float.valueOf(((Float)yPos.get()).floatValue() + fr.FONT_HEIGHT));
              time.set(time.get() - 300L);
            });
        break;
      case "Gato":
        time = (float)Minecraft.getSystemTime();
        y = 0;
        count = 0;
        list1 = (List<Module>)ModuleManager.getModules().stream().filter(module -> (module.isEnabled() && (!this.hideVisuals.getValue().booleanValue() || (module.getCategory() != Category.VISUAL && module.getCategory() != Category.DISPLAY)))).collect(Collectors.toList());
        list1.sort((feature0, feature1) -> (getHudFont().getStringWidth(feature0.getDisplayName()) > getHudFont().getStringWidth(feature1.getDisplayName())) ? -1 : 1);
        for (Module m : list1) {
          if (m.isEnabled()) {
            String name = this.addons.isEnabled("Lowercase") ? (m.getSuffix().isEmpty() ? m.getName().toLowerCase(Locale.ROOT) : String.format("%s" + EnumChatFormatting.GRAY + " - %s", new Object[] { m.getName().toLowerCase(Locale.ROOT), m.getSuffix() }).toLowerCase(Locale.ROOT)) : (m.getSuffix().isEmpty() ? m.getName() : String.format("%s" + EnumChatFormatting.GRAY + " - %s", new Object[] { m.getName(), m.getSuffix() }));
            float x = 0.0F;
            x = (width() - getHudFont().getStringWidth(name));
            if (this.addons.isEnabled("Text Shadow")) {
              getHudFont().drawStringWithShadow(name, (x - 5.0F), (y + 5), ColorUtil.gatoPulseBrightness(Color.CYAN, 10, 10).getRGB());
            } else {
              getHudFont().drawString(name, x - 5.0F, (y + 5), ColorUtil.gatoPulseBrightness(Color.CYAN, 10, 10).getRGB());
            } 
            y += 10;
            time -= 300.0F;
            count++;
          } 
        } 
        break;
    } 
  }
  
  private void renderBlurInterface(String mode) {
    Blur blur = (Blur)Client.INSTANCE.moduleManager.getModuleByClass(Blur.class);
    if (blur.isEnabled() && blur.modules.isEnabled("Arraylist")) {
      if (blur.isEnabled() && blur.shadow.getValue().booleanValue()) {
        DropShadowUtil.start();
        if (mode.equalsIgnoreCase("Old") && blur.isEnabled() && blur.modules.isEnabled("Arraylist"))
          doRenderOldArraylistBase(true); 
        if (mode.equalsIgnoreCase("Chill") && blur.isEnabled() && blur.modules.isEnabled("Arraylist"))
          drawChillArrayList(true); 
        DropShadowUtil.stop();
      } 
      InstanceManager.BLUR_SHADER.startBlur();
      if (mode.equalsIgnoreCase("Old") && blur.isEnabled() && blur.modules.isEnabled("Arraylist"))
        doRenderOldArraylistBase(true); 
      if (mode.equalsIgnoreCase("Chill") && blur.isEnabled() && blur.modules.isEnabled("Arraylist"))
        drawChillArrayList(true); 
      InstanceManager.BLUR_SHADER.stopBlur(blur.sigma.getValueInt(), blur.radius.getValueInt(), 1);
      if (mode.equalsIgnoreCase("Old") && blur.isEnabled() && blur.modules.isEnabled("Arraylist"))
        doRenderOldArraylistBase(false); 
      if (mode.equalsIgnoreCase("Chill") && blur.isEnabled() && blur.modules.isEnabled("Arraylist"))
        drawChillArrayList(false); 
    } else {
      if (mode.equalsIgnoreCase("Old"))
        doRenderOldArraylistBase(true); 
      if (mode.equalsIgnoreCase("Chill"))
        drawChillArrayList(true); 
    } 
  }
  
  private void doRenderOldArraylistBase(boolean rect) {
    int y = 0;
    float time = (float)Minecraft.getSystemTime();
    int count = 0;
    List<Module> showedModules = (List<Module>)ModuleManager.getModules().stream().filter(module -> (module.isEnabled() && (!this.hideVisuals.getValue().booleanValue() || (module.getCategory() != Category.VISUAL && module.getCategory() != Category.DISPLAY)))).collect(Collectors.toList());
    showedModules.sort((feature0, feature1) -> (getHudFont().getStringWidth(feature0.getDisplayName()) > getHudFont().getStringWidth(feature1.getDisplayName())) ? -1 : 1);
    for (Module m : showedModules) {
      if (m.isEnabled()) {
        String name = this.addons.isEnabled("Lowercase") ? (m.getSuffix().isEmpty() ? m.getName().toLowerCase(Locale.ROOT) : String.format("%s %s", new Object[] { m.getName().toLowerCase(Locale.ROOT), m.getSuffix() }).toLowerCase(Locale.ROOT)) : (m.getSuffix().isEmpty() ? m.getName() : String.format("%s %s", new Object[] { m.getName(), m.getSuffix() }));
        float x = 0.0F;
        x = (width() - getHudFont().getStringWidth(name));
        if (this.arrayListBackground.getValue().booleanValue()) {
          if (rect) {
            Gui.drawRect((x - 3.0F), y, (new ScaledResolution(mc)).getScaledWidth(), (y + getHudFont().getHeight() + 2), (new Color(0, 0, 0, this.arrayListBackgroundOpacity.getValueInt())).getRGB());
            Gui.drawRect(((new ScaledResolution(mc)).getScaledWidth() - 1), y, (new ScaledResolution(mc)).getScaledWidth(), (y + getHudFont().getHeight() + 4), this.arrayListColorMode.is("Client") ? ColorUtil.getColor(this.arrayListColorMode.getValue(), (int)time) : ColorUtil.getColor(this.arrayListColorMode.getValue(), count * this.arrayListColorDelay.getValueInt()));
          } 
          getHudFont().drawString(name, x - 2.0F, (y + 2), this.arrayListColorMode.is("Client") ? ColorUtil.getColor(this.arrayListColorMode.getValue(), (int)time) : ColorUtil.getColor(this.arrayListColorMode.getValue(), count * this.arrayListColorDelay.getValueInt()));
        } else if (!rect) {
          if (this.addons.isEnabled("Text Shadow")) {
            getHudFont().drawStringWithShadow(name, (x - 2.0F), (y + 2), this.arrayListColorMode.is("Client") ? ColorUtil.getColor(this.arrayListColorMode.getValue(), (int)time) : ColorUtil.getColor(this.arrayListColorMode.getValue(), count * this.arrayListColorDelay.getValueInt()));
          } else {
            getHudFont().drawString(name, x - 2.0F, (y + 2), this.arrayListColorMode.is("Client") ? ColorUtil.getColor(this.arrayListColorMode.getValue(), (int)time) : ColorUtil.getColor(this.arrayListColorMode.getValue(), count * this.arrayListColorDelay.getValueInt()));
          } 
        } 
        time -= 300.0F;
        y += 10;
        count++;
      } 
    } 
  }
  
  private void drawChillArrayList(boolean rect) {
    int y = 0;
    float time = (float)Minecraft.getSystemTime();
    int count = 0;
    List<Module> showedModules = (List<Module>)ModuleManager.getModules().stream().filter(module -> (module.isEnabled() && (!this.hideVisuals.getValue().booleanValue() || (module.getCategory() != Category.VISUAL && module.getCategory() != Category.DISPLAY)))).collect(Collectors.toList());
    showedModules.sort((feature0, feature1) -> (getHudFont().getStringWidth(feature0.getDisplayName()) > getHudFont().getStringWidth(feature1.getDisplayName())) ? -1 : 1);
    for (Module m : showedModules) {
      if (m.isEnabled()) {
        String name = this.addons.isEnabled("Lowercase") ? (m.getSuffix().isEmpty() ? m.getName().toLowerCase(Locale.ROOT) : String.format("%s" + EnumChatFormatting.GRAY + " %s", new Object[] { m.getName().toLowerCase(Locale.ROOT), m.getSuffix().toLowerCase(Locale.ROOT) })) : (m.getSuffix().isEmpty() ? m.getName() : String.format("%s" + EnumChatFormatting.GRAY + " %s", new Object[] { m.getName(), m.getSuffix() }));
        float x = 0.0F;
        x = (width() - getHudFont().getStringWidth(name));
        if (this.addons.isEnabled("Text Shadow")) {
          getHudFont().drawStringWithShadow(name, (x - 5.0F), (y + 5), this.arrayListColorMode.is("Client") ? ColorUtil.getColor(this.arrayListColorMode.getValue(), (int)time) : ColorUtil.getColor(this.arrayListColorMode.getValue(), count * this.arrayListColorDelay.getValueInt()));
        } else {
          float finalX = x;
          int finalY = y;
          float finalTime = time;
          int finalCount = count;
          if (this.arrayListBackground.getValue().booleanValue()) {
            if (rect)
              Gui.drawRect((x - 3.0F - 6.0F), (y + 10), (new ScaledResolution(mc)).getScaledWidth() - 5.5D, (y + getHudFont().getHeight() + 2 + 10), (new Color(0, 0, 0, this.arrayListBackgroundOpacity.getValueInt())).getRGB()); 
            getHudFont().drawString(name, (float)((x - 2.0F) - 5.5D), (float)(y + 1.3D + 10.0D), this.arrayListColorMode.is("Client") ? ColorUtil.getColor(this.arrayListColorMode.getValue(), (int)time) : ColorUtil.getColor(this.arrayListColorMode.getValue(), count * this.arrayListColorDelay.getValueInt()));
          } else {
            getHudFont().drawString(name, finalX - 5.0F, (finalY + 5), this.arrayListColorMode.is("Client") ? ColorUtil.getColor(this.arrayListColorMode.getValue(), (int)finalTime) : ColorUtil.getColor(this.arrayListColorMode.getValue(), finalCount * this.arrayListColorDelay.getValueInt()));
          } 
        } 
        y += 10;
        time -= 300.0F;
        count++;
      } 
    } 
  }
  
  public CFontRenderer getHudFont() {
    return this.fontMode.is("Chill") ? FontLoaders.chill : (this.fontMode.is("Normal") ? FontLoaders.Sfui20 : (this.fontMode.is("Sand") ? FontLoaders.quickSand : FontLoaders.brains));
  }
  
  public static int width() {
    return (new ScaledResolution(Minecraft.getMinecraft())).getScaledWidth();
  }
  
  public static int height() {
    return (new ScaledResolution(Minecraft.getMinecraft())).getScaledHeight();
  }
}
