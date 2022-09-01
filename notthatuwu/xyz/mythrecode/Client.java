package notthatuwu.xyz.mythrecode;

import com.thealtening.auth.service.ServiceSwitcher;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;
import notthatuwu.xyz.mythrecode.api.command.CommandManager;
import notthatuwu.xyz.mythrecode.api.event.EventManager;
import notthatuwu.xyz.mythrecode.api.event.EventTarget;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.ModuleManager;
import notthatuwu.xyz.mythrecode.api.module.file.FileFactory;
import notthatuwu.xyz.mythrecode.api.module.file.IFile;
import notthatuwu.xyz.mythrecode.api.module.settings.SettingsManager;
import notthatuwu.xyz.mythrecode.api.ui.altsmanager.AltManager;
import notthatuwu.xyz.mythrecode.api.ui.altsmanager.files.AccountsFile;
import notthatuwu.xyz.mythrecode.api.ui.notifications.NotificationManager;
import notthatuwu.xyz.mythrecode.api.utils.ConfigUtil;
import notthatuwu.xyz.mythrecode.api.utils.Kernel32;
import notthatuwu.xyz.mythrecode.api.utils.discord.MythDiscordRPC;
import notthatuwu.xyz.mythrecode.api.viamcp.ViaMCP;
import notthatuwu.xyz.mythrecode.events.EventKey;
import notthatuwu.xyz.mythrecode.scripting.ScriptManager;
import org.lwjgl.opengl.Display;

public enum Client {
  INSTANCE;
  
  private final Runnable pingRunnable;
  
  private final OldServerPinger oldServerPinger;
  
  private final ScheduledExecutorService scheduledExecutorService;
  
  public ConfigUtil configUtil;
  
  public NotificationManager notificationManager;
  
  public ScriptManager scriptManager;
  
  public CommandManager commandManager;
  
  public ServiceSwitcher serviceSwitcher;
  
  public FileFactory fileFactory;
  
  public ModuleManager moduleManager;
  
  public SettingsManager settingsManager;
  
  public MythDiscordRPC discordRPC;
  
  public static int buttonFadeBG;
  
  public static int buttonFade;
  
  public static int logoMove;
  
  public static boolean firstTimeAnimation;
  
  public static boolean load;
  
  public ServerData lastServer;
  
  public File mythFolder;
  
  public File dir;
  
  public static String APIKey;
  
  public static final int CLIENT_BUILD = 12;
  
  public static final String CLIENT_VERSION = "1.3";
  
  public static final String CLIENT_NAME = "Myth";
  
  Client() {
    this.notificationManager = new NotificationManager();
    this.configUtil = new ConfigUtil();
    this.scheduledExecutorService = Executors.newScheduledThreadPool(10);
    this.oldServerPinger = new OldServerPinger(true);
    this.pingRunnable = (() -> {
        try {
          Minecraft mc = Minecraft.getMinecraft();
          if (!(mc.currentScreen instanceof net.minecraft.client.gui.GuiMultiplayer) && mc.getCurrentServerData() != null)
            this.oldServerPinger.ping(mc.getCurrentServerData()); 
        } catch (Throwable throwable) {}
      });
  }
  
  static {
    APIKey = "";
    load = false;
  }
  
  public void startClient() {
    if (Kernel32.INSTANCE.IsDebuggerPresent())
      System.exit(420); 
    Minecraft.getMinecraft().drawSplashScreen(0, ".", Minecraft.getMinecraft().getTextureManager(), 20);
    this.discordRPC = new MythDiscordRPC();
    this.discordRPC.start();
    this.discordRPC.update("Myth 1.3 - 12", "Loading up...");
    EventManager.register(this);
    this.settingsManager = new SettingsManager();
    this.moduleManager = new ModuleManager();
    Minecraft.getMinecraft().drawSplashScreen(20, "..", Minecraft.getMinecraft().getTextureManager(), 50);
    this.moduleManager.registerNormal();
    this.fileFactory = new FileFactory();
    this.commandManager = new CommandManager();
    this.serviceSwitcher = new ServiceSwitcher();
    Minecraft.getMinecraft().drawSplashScreen(50, "...", Minecraft.getMinecraft().getTextureManager(), 70);
    this.fileFactory.setupRoot("Myth");
    this.fileFactory.add((IFile)new AccountsFile());
    AltManager.init();
    this.mythFolder = new File((Minecraft.getMinecraft()).mcDataDir, "Myth");
    this.scriptManager = new ScriptManager();
    EventManager.register(this.scriptManager);
    this.dir = new File(this.mythFolder + "/Configs");
    if (!this.dir.exists())
      this.dir.mkdir(); 
    File file = new File(this.mythFolder + "/Configs/Default.myth");
    if (file.exists())
      this.configUtil.load("Default"); 
    this.fileFactory.load();
    Minecraft.getMinecraft().drawSplashScreen(50, ".", Minecraft.getMinecraft().getTextureManager(), 100);
    Display.setTitle("Myth 1.3 - 12 - Cracked - Directleaks.to");
    Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new GuiMainMenu());
    load = true;
    Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownClient));
    this.scheduledExecutorService.scheduleAtFixedRate(this.pingRunnable, 0L, 3L, TimeUnit.SECONDS);
  }
  
  public void startCommon() {
    try {
      firstTimeAnimation = false;
      ViaMCP.getInstance().start();
      ViaMCP.getInstance().initAsyncSlider();
    } catch (Exception exception) {}
  }
  
  public void shutdownClient() {
    try {
      EventManager.unregister(this);
      this.configUtil.saveKey(ModuleManager.getModules());
      this.discordRPC.shutdown();
      System.out.println("Myth 1.3 [ Build:12 ] Shutdown");
      this.configUtil.save("Default");
      this.fileFactory.save();
    } catch (Exception exception) {}
  }
  
  @EventTarget
  public void onKeyUpdate(EventKey event) {
    ModuleManager.getModules().stream().filter(module -> (module.getKeyCode() == event.getKey())).forEach(Module::toggle);
  }
}
