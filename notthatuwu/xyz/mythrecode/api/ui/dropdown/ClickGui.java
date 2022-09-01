package notthatuwu.xyz.mythrecode.api.ui.dropdown;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.module.Category;
import notthatuwu.xyz.mythrecode.api.ui.config.Config;
import notthatuwu.xyz.mythrecode.api.ui.config.ConfigTab;
import notthatuwu.xyz.mythrecode.api.ui.config.OnlineConfigs;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.Component;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.ExpandableComponent;
import notthatuwu.xyz.mythrecode.api.ui.dropdown.component.impl.panel.CategoryPanel;
import notthatuwu.xyz.mythrecode.api.utils.TimeHelper;
import notthatuwu.xyz.mythrecode.api.utils.animation.Animation;
import notthatuwu.xyz.mythrecode.api.utils.animation.Easings;
import notthatuwu.xyz.mythrecode.api.utils.font.FontLoaders;
import notthatuwu.xyz.mythrecode.api.utils.render.RenderUtils;
import notthatuwu.xyz.mythrecode.manager.InstanceManager;
import notthatuwu.xyz.mythrecode.modules.display.ClickGuiMod;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public final class ClickGui extends GuiScreen {
  public static boolean escapeKeyInUse;
  
  public static boolean isInGui;
  
  public static boolean loaded;
  
  public static ClickGui instance;
  
  public List<Component> components;
  
  public Component selectedPanel;
  
  public TimeHelper timeHelper = new TimeHelper();
  
  public static int x;
  
  public static int y;
  
  public static int configscount;
  
  public static int onlineconfigscount;
  
  public float modsRole;
  
  public float modsRoleNow;
  
  public static ArrayList<Config> onlineConfigs = new ArrayList<>();
  
  public static ArrayList<Config> localConfigs = new ArrayList<>();
  
  public ConfigTab configTab = ConfigTab.ONLINE;
  
  public Animation animation = new Animation();
  
  public boolean closing;
  
  public void initGui() {
    super.initGui();
    this.closing = false;
    this.animation.setValue(0.0D);
    this.animation.animate(1.0D, 1000.0D, Easings.ELASTIC_OUT);
  }
  
  public ClickGui() {
    this.components = new ArrayList<>();
    instance = this;
    isInGui = false;
    int panelX = 2;
    for (Category category : Category.values()) {
      CategoryPanel panel = new CategoryPanel(category, panelX, 4);
      this.components.add(panel);
      panelX += panel.getWidth() + 7;
      this.selectedPanel = (Component)panel;
    } 
  }
  
  public static ClickGui getInstance() {
    return instance;
  }
  
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    if (this.animation.updateAnimation()) {
      GlStateManager.translate(this.width / 2.0F, this.height / 2.0F, 0.0F);
      GlStateManager.scale(this.animation.getValue(), this.animation.getValue(), 0.0D);
      GlStateManager.translate(-this.width / 2.0F, -this.height / 2.0F, 0.0F);
    } else if (this.closing) {
      this.mc.displayGuiScreen(null);
      if (this.mc.currentScreen == null)
        this.mc.setIngameFocus(); 
      return;
    } 
    ScaledResolution sr = new ScaledResolution(this.mc);
    ClickGuiMod clickGui = (ClickGuiMod)Client.INSTANCE.moduleManager.getModuleByClass(ClickGuiMod.class);
    drawAnime();
    if (clickGui.addons.isEnabled("Blur"))
      InstanceManager.BLUR_UTIL.blur(clickGui.blurradius.getValue().intValue()); 
    if (clickGui.addons.isEnabled("Gardient"))
      RenderUtils.drawGradientRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), clickGui.gardient.getColor(), (new Color(0, 0, 0, 30)).getRGB()); 
    drawConfig(mouseX, mouseY);
    if (!isInGui)
      for (Component component : this.components)
        component.renderComponent(sr, mouseX, mouseY);  
  }
  
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (!isInGui) {
      this.selectedPanel.onKeyPress(keyCode);
      if (!escapeKeyInUse && 
        keyCode == 1 && !this.closing) {
        this.animation.animate(0.0D, 300.0D, Easings.BACK_IN);
        this.closing = true;
      } 
    } else if (keyCode == 1) {
      isInGui = false;
    } 
    escapeKeyInUse = false;
  }
  
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    if (!isInGui)
      for (int i = this.components.size() - 1; i >= 0; i--) {
        Component component = this.components.get(i);
        int x = component.getX();
        int y = component.getY();
        int cHeight = component.getHeight();
        if (component instanceof ExpandableComponent) {
          ExpandableComponent expandableComponent = (ExpandableComponent)component;
          if (expandableComponent.isExpanded())
            cHeight = expandableComponent.getExpandedHeight(); 
        } 
        if (mouseX > x && mouseY > y && mouseX < x + component.getWidth() && mouseY < y + cHeight) {
          (this.selectedPanel = component).onMouseClick(mouseX, mouseY, mouseButton);
          break;
        } 
      }  
  }
  
  protected void mouseReleased(int mouseX, int mouseY, int state) {
    if (!isInGui)
      this.selectedPanel.onMouseRelease(state); 
  }
  
  public void drawConfig(int mouseX, int mouseY) {
    ScaledResolution sr = new ScaledResolution(this.mc);
    boolean isHoveridus = GuiMainMenu.isHovered(5.0F, (sr.getScaledHeight() - 30), 30.0F, (sr.getScaledHeight() - 5), mouseX, mouseY);
    RenderUtils.drawRoundedRect2(5.0D, (sr.getScaledHeight() - 30), 30.0D, (sr.getScaledHeight() - 5), 4.0D, (new Color(0, 0, 0, 120)).getRGB());
    RenderUtils.drawImage(5, sr.getScaledHeight() - 30, 25, 25, new ResourceLocation("myth/Icons/file.png"), Color.WHITE);
    if (isHoveridus && Mouse.isButtonDown(0) && this.timeHelper.hasReached(500L)) {
      if (isInGui) {
        isInGui = false;
      } else {
        onlineConfigs.clear();
        localConfigs.clear();
        configscount = 0;
        onlineconfigscount = 0;
        OnlineConfigs.loadOnlineConfigs();
        File folder = new File("Myth/Configs");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
          if (listOfFiles[i].isFile()) {
            String[] s = listOfFiles[i].getName().split(".myth");
            localConfigs.add(new Config(listOfFiles[i].getName()));
            configscount++;
          } 
        } 
        isInGui = true;
      } 
      this.timeHelper.reset();
    } 
    if (isInGui) {
      float x = 320.0F, y = 130.0F;
      float width = 700.0F, height = 360.0F;
      RenderUtils.drawRoundedRect2(x, y, width, height, 5.0D, (new Color(0, 0, 0, 120)).getRGB());
      FontLoaders.Sfui22.drawCenteredString("Online Configs", (x + 120.0F), (y + 20.0F), -1);
      FontLoaders.Sfui22.drawCenteredString("Local Configs", (x + 220.0F), (y + 20.0F), -1);
      if (this.configTab == ConfigTab.ONLINE)
        Gui.drawRect((x + 120.0F - 33.0F), (y + 20.0F + 9.0F), (x + 120.0F + 33.0F), (y + 20.0F + 10.0F), -1); 
      if (this.configTab == ConfigTab.LOCAL)
        Gui.drawRect((x + 220.0F - 33.0F), (y + 20.0F + 9.0F), (x + 220.0F + 33.0F), (y + 20.0F + 10.0F), -1); 
      if (GuiMainMenu.isHovered(x + 120.0F - 33.0F, y + 20.0F, x + 120.0F + 33.0F, y + 20.0F + 10.0F, mouseX, mouseY) && Mouse.isButtonDown(0) && this.timeHelper.hasReached(500L)) {
        this.configTab = ConfigTab.ONLINE;
        this.timeHelper.reset();
      } else if (GuiMainMenu.isHovered(x + 220.0F - 33.0F, y + 20.0F, x + 220.0F + 33.0F, y + 20.0F + 10.0F, mouseX, mouseY) && Mouse.isButtonDown(0) && this.timeHelper.hasReached(500L)) {
        this.configTab = ConfigTab.LOCAL;
        this.timeHelper.reset();
      } 
      int dWheel2 = Mouse.getDWheel();
      if (dWheel2 < 0 && Math.abs(this.modsRole) + 220.0F < (onlineconfigscount * 40))
        this.modsRole -= 32.0F; 
      if (dWheel2 > 0 && this.modsRole < 0.0F)
        this.modsRole += 32.0F; 
      if (this.modsRoleNow != this.modsRole) {
        this.modsRoleNow += (this.modsRole - this.modsRoleNow) / 20.0F;
        this.modsRoleNow = (int)this.modsRoleNow;
      } 
      if (this.configTab == ConfigTab.LOCAL) {
        float configx = x + 5.0F;
        float configy = y + 70.0F + this.modsRoleNow;
        RenderUtils.scissor(x, (y + 70.0F), (configx + 340.0F), (configy + 20.0F - this.modsRoleNow - 70.0F));
        GL11.glEnable(3089);
        for (Config config : localConfigs) {
          String[] configname = config.getName().split(".myth");
          RenderUtils.drawRoundedRect2(configx, configy, (configx + 340.0F), (configy + 20.0F), 5.0D, GuiMainMenu.isHovered(configx, configy, configx + 340.0F, configy + 20.0F, mouseX, mouseY) ? (new Color(0, 0, 0, 140)).getRGB() : (new Color(0, 0, 0, 120)).getRGB());
          FontLoaders.Sfui16.drawString(configname[0], configx + 10.0F, configy + 7.0F, -1);
          if (GuiMainMenu.isHovered(configx, configy, configx + 340.0F, configy + 20.0F, mouseX, mouseY) && Mouse.isButtonDown(0))
            Client.INSTANCE.configUtil.load(configname[0]); 
          configy += 25.0F;
        } 
        GL11.glDisable(3089);
      } 
      if (this.configTab == ConfigTab.ONLINE) {
        float configx = x + 5.0F;
        float configy = y + 70.0F + this.modsRoleNow;
        RenderUtils.scissor(x, (y + 70.0F), (configx + 340.0F), (configy + 20.0F - this.modsRoleNow - 70.0F));
        GL11.glEnable(3089);
        for (Config config : onlineConfigs) {
          RenderUtils.drawRoundedRect2(configx, configy, (configx + 340.0F), (configy + 20.0F), 5.0D, GuiMainMenu.isHovered(configx, configy, configx + 340.0F, configy + 20.0F, mouseX, mouseY) ? (new Color(0, 0, 0, 140)).getRGB() : (new Color(0, 0, 0, 120)).getRGB());
          FontLoaders.Sfui16.drawString(config.getName() + " (" + EnumChatFormatting.YELLOW + config.getAuthor() + EnumChatFormatting.RESET + ")", configx + 10.0F, configy + 7.0F, -1);
          if (GuiMainMenu.isHovered(configx, configy, configx + 340.0F, configy + 20.0F, mouseX, mouseY) && 
            config.safe.booleanValue())
            FontLoaders.Sfui16.drawString("Safe", configx + 300.0F, configy + 7.0F, Color.GREEN.getRGB()); 
          FontLoaders.Sfui15.drawString(config.getDescription(), configx + 40.0F + (config.getAuthor().length() * 6) + 25.0F, configy + 7.0F, Color.GRAY.getRGB());
          if (GuiMainMenu.isHovered(configx, configy, configx + 340.0F, configy + 20.0F, mouseX, mouseY) && Mouse.isButtonDown(0)) {
            System.out.println(config.getName());
            OnlineConfigs.downloadOnlineConfigAndLoad(config.getName());
          } 
          configy += 25.0F;
        } 
        GL11.glDisable(3089);
      } 
    } 
  }
  
  public void drawAnime() {
    ClickGuiMod clickGui = (ClickGuiMod)Client.INSTANCE.moduleManager.getModuleByClass(ClickGuiMod.class);
    if (clickGui.addons.isEnabled("Anime"))
      switch (clickGui.anime.getValue()) {
        case "Rem":
          RenderUtils.drawImage(577, 130, 366, 489, new ResourceLocation("myth/waifu/rem.png"));
          break;
        case "Rem2":
          RenderUtils.drawImage(577, 100, 369, 676, new ResourceLocation("myth/waifu/rem2.png"));
          break;
        case "Asna":
          RenderUtils.drawImage(577, 130, 435, 574, new ResourceLocation("myth/waifu/Asna.png"));
          break;
        case "SchoolGirl":
          RenderUtils.drawImage(577, 130, 422, 591, new ResourceLocation("myth/waifu/SchoolGirl.png"));
          break;
        case "Kirigaya":
          RenderUtils.drawImage(577, 130, 422, 591, new ResourceLocation("myth/waifu/Kirigaya.png"));
          break;
        case "Miku":
          RenderUtils.drawImage(577, 130, 369, 676, new ResourceLocation("myth/waifu/Miku.png"));
          break;
        case "Shiina Mashiro":
          RenderUtils.drawImage(577, 130, 369, 676, new ResourceLocation("myth/waifu/Shiina Mashiro.png"));
          break;
        case "Akeno":
          RenderUtils.drawImage(577, 130, 422, 591, new ResourceLocation("myth/waifu/Akeno.png"));
          break;
        case "Misaka":
          RenderUtils.drawImage(577, 130, 422, 591, new ResourceLocation("myth/waifu/Misaka.png"));
          break;
        case "Astolfo":
          RenderUtils.drawImage(577, 130, 422, 591, new ResourceLocation("myth/waifu/Astolfo.png"));
          break;
      }  
  }
}
