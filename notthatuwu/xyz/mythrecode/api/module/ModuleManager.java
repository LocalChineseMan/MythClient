package notthatuwu.xyz.mythrecode.api.module;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.modules.combat.AntiBot;
import notthatuwu.xyz.mythrecode.modules.combat.Criticals;
import notthatuwu.xyz.mythrecode.modules.combat.KillAura;
import notthatuwu.xyz.mythrecode.modules.combat.TargetStrafe;
import notthatuwu.xyz.mythrecode.modules.combat.Velocity;
import notthatuwu.xyz.mythrecode.modules.display.ClickGuiMod;
import notthatuwu.xyz.mythrecode.modules.display.HUD;
import notthatuwu.xyz.mythrecode.modules.display.TargetHUD;
import notthatuwu.xyz.mythrecode.modules.exploit.Disabler;
import notthatuwu.xyz.mythrecode.modules.exploit.Phase;
import notthatuwu.xyz.mythrecode.modules.exploit.PingSpoof;
import notthatuwu.xyz.mythrecode.modules.movement.Blink;
import notthatuwu.xyz.mythrecode.modules.movement.ClickTP;
import notthatuwu.xyz.mythrecode.modules.movement.DamageBoost;
import notthatuwu.xyz.mythrecode.modules.movement.Fly;
import notthatuwu.xyz.mythrecode.modules.movement.InvMove;
import notthatuwu.xyz.mythrecode.modules.movement.LongJump;
import notthatuwu.xyz.mythrecode.modules.movement.NoSlow;
import notthatuwu.xyz.mythrecode.modules.movement.Speed;
import notthatuwu.xyz.mythrecode.modules.movement.Sprint;
import notthatuwu.xyz.mythrecode.modules.movement.Timer;
import notthatuwu.xyz.mythrecode.modules.player.AntiVoid;
import notthatuwu.xyz.mythrecode.modules.player.AutoDisable;
import notthatuwu.xyz.mythrecode.modules.player.AutoTool;
import notthatuwu.xyz.mythrecode.modules.player.Breaker;
import notthatuwu.xyz.mythrecode.modules.player.ChestStealer;
import notthatuwu.xyz.mythrecode.modules.player.InvManager;
import notthatuwu.xyz.mythrecode.modules.player.NoFall;
import notthatuwu.xyz.mythrecode.modules.player.NoRot;
import notthatuwu.xyz.mythrecode.modules.player.Scaffold;
import notthatuwu.xyz.mythrecode.modules.player.Teams;
import notthatuwu.xyz.mythrecode.modules.visuals.Ambience;
import notthatuwu.xyz.mythrecode.modules.visuals.Animations;
import notthatuwu.xyz.mythrecode.modules.visuals.BetterChat;
import notthatuwu.xyz.mythrecode.modules.visuals.Blur;
import notthatuwu.xyz.mythrecode.modules.visuals.ChestESP;
import notthatuwu.xyz.mythrecode.modules.visuals.ChinaHat;
import notthatuwu.xyz.mythrecode.modules.visuals.ESP;
import notthatuwu.xyz.mythrecode.modules.visuals.JumpCircle;
import notthatuwu.xyz.mythrecode.modules.visuals.Keystrokes;
import notthatuwu.xyz.mythrecode.modules.visuals.Notifications;
import notthatuwu.xyz.mythrecode.modules.visuals.PlayerList;
import notthatuwu.xyz.mythrecode.modules.visuals.Radar;
import notthatuwu.xyz.mythrecode.modules.visuals.Scoreboard;
import notthatuwu.xyz.mythrecode.modules.visuals.SessionInfo;

public class ModuleManager {
  private final HashMap<Class<? extends Module>, Module> moduleHashMap = new HashMap<>();
  
  public void registerNormal() {
    ArrayList<Class<? extends Module>> modules = new ArrayList<>();
    modules.add(AntiBot.class);
    modules.add(Velocity.class);
    modules.add(KillAura.class);
    modules.add(Criticals.class);
    modules.add(TargetStrafe.class);
    modules.add(Fly.class);
    modules.add(Speed.class);
    modules.add(InvMove.class);
    modules.add(NoSlow.class);
    modules.add(Sprint.class);
    modules.add(LongJump.class);
    modules.add(DamageBoost.class);
    modules.add(ClickTP.class);
    modules.add(Timer.class);
    modules.add(Blink.class);
    modules.add(Teams.class);
    modules.add(ChestStealer.class);
    modules.add(InvManager.class);
    modules.add(AutoDisable.class);
    modules.add(NoRot.class);
    modules.add(NoFall.class);
    modules.add(Scaffold.class);
    modules.add(AntiVoid.class);
    modules.add(Breaker.class);
    modules.add(AutoTool.class);
    modules.add(HUD.class);
    modules.add(Scoreboard.class);
    modules.add(ClickGuiMod.class);
    modules.add(Animations.class);
    modules.add(ChestESP.class);
    modules.add(Ambience.class);
    modules.add(TargetHUD.class);
    modules.add(ESP.class);
    modules.add(PlayerList.class);
    modules.add(Radar.class);
    modules.add(SessionInfo.class);
    modules.add(JumpCircle.class);
    modules.add(BetterChat.class);
    modules.add(ChinaHat.class);
    modules.add(Keystrokes.class);
    modules.add(Blur.class);
    modules.add(Notifications.class);
    modules.add(Disabler.class);
    modules.add(Phase.class);
    modules.add(PingSpoof.class);
    modules.forEach(aClass -> {
          try {
            this.moduleHashMap.put(aClass, aClass.getConstructor(new Class[0]).newInstance(new Object[0]));
          } catch (Exception e) {
            e.printStackTrace();
          } 
        });
    try {
      Client.INSTANCE.configUtil.loadKey(this);
    } catch (IOException e) {
      e.printStackTrace();
    } 
    this.moduleHashMap.forEach((aClass, module) -> module.reflectSettings());
  }
  
  public static ArrayList<Module> getModules() {
    return new ArrayList<>(Client.INSTANCE.moduleManager.moduleHashMap.values());
  }
  
  public <T extends Module> T getModuleByClass(Class<T> clazz) {
    return (T)this.moduleHashMap.get(clazz);
  }
  
  public List<Module> getModulesByCategory(Category c) {
    List<Module> modules = new ArrayList<>();
    this.moduleHashMap.values().forEach(module -> {
          if (module.getCategory() == c)
            modules.add(module); 
        });
    return modules;
  }
  
  public static Module getModuleByName(String name) {
    return getModules().stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
  }
  
  public HashMap<Class<? extends Module>, Module> getModuleHashMap() {
    return this.moduleHashMap;
  }
}
