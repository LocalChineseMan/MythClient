package notthatuwu.xyz.mythrecode.api.utils;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.ModuleManager;
import notthatuwu.xyz.mythrecode.api.module.settings.Setting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.AddonSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.BooleanSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ColorSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.ModeSetting;
import notthatuwu.xyz.mythrecode.api.module.settings.impl.NumberSetting;

public class ConfigUtil {
  private File dir;
  
  private File dataFile;
  
  public void save(String name) {
    this.dir = new File(String.valueOf(Client.INSTANCE.dir));
    if (!this.dir.exists())
      this.dir.mkdir(); 
    this.dataFile = new File(this.dir, name + ".myth");
    if (!this.dataFile.exists())
      try {
        this.dataFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }  
    ArrayList<String> toSave = new ArrayList<>();
    for (Module m : ModuleManager.getModules()) {
      toSave.add("Module:" + m.getName() + ":" + m.isEnabled() + ":" + m.getKeyCode() + ":" + '\001');
      for (Setting s : Client.INSTANCE.settingsManager.getSettingsFromModule(m)) {
        if (s instanceof NumberSetting) {
          NumberSetting set = (NumberSetting)s;
          toSave.add("Number:" + m.getName() + ":" + set.name + ":" + set.getValue());
          continue;
        } 
        if (s instanceof BooleanSetting) {
          BooleanSetting set = (BooleanSetting)s;
          toSave.add("Boolean:" + m.getName() + ":" + set.name + ":" + set.getValue());
          continue;
        } 
        if (s instanceof ModeSetting) {
          ModeSetting set = (ModeSetting)s;
          toSave.add("Mode:" + m.getName() + ":" + set.name + ":" + set.getValue());
          continue;
        } 
        if (s instanceof AddonSetting) {
          AddonSetting set = (AddonSetting)s;
          if (!set.toggled.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (String s1 : set.toggled)
              builder.append(s1).append(":"); 
            toSave.add("AddonSetting:" + m.getName() + ":" + set.name + ":" + builder);
            continue;
          } 
          toSave.add("AddonSetting:" + m.getName() + ":" + set.name + ": ");
          continue;
        } 
        if (s instanceof ColorSetting) {
          ColorSetting set = (ColorSetting)s;
          toSave.add("ColorSetting:" + m.getName() + ":" + set.name + ":" + set.getRed() + ":" + set.getGreen() + ":" + set.getBlue());
        } 
      } 
    } 
    try {
      PrintWriter pw = new PrintWriter(this.dataFile);
      for (String str : toSave)
        pw.println(str); 
      pw.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } 
  }
  
  public void load(String name) {
    this.dir = new File(String.valueOf(Client.INSTANCE.dir));
    if (!this.dir.exists())
      this.dir.mkdir(); 
    this.dataFile = new File(this.dir, name + ".myth");
    for (Module m : ModuleManager.getModules()) {
      if (m.isEnabled())
        m.toggle(); 
    } 
    ArrayList<String> lines = new ArrayList<>();
    try {
      BufferedReader reader = new BufferedReader(new FileReader(this.dataFile));
      String line = reader.readLine();
      while (line != null) {
        lines.add(line);
        line = reader.readLine();
      } 
      reader.close();
    } catch (Exception e) {
      e.printStackTrace();
    } 
    try {
      for (String s : lines) {
        String[] args = s.split(":");
        if (s.toLowerCase().startsWith("module:"))
          for (Module m : ModuleManager.getModules()) {
            if (m.getName().equalsIgnoreCase(args[1])) {
              boolean shouldEnable = Boolean.parseBoolean(args[2]);
              if (shouldEnable && !m.isEnabled())
                m.setEnabled(true); 
              if (args.length > 4);
            } 
          }  
        if (s.toLowerCase().startsWith("number:"))
          for (Module m : ModuleManager.getModules()) {
            if (m.getName().equalsIgnoreCase(args[1]))
              for (Setting setting : Client.INSTANCE.settingsManager.getSettingsFromModule(m)) {
                if (!(setting instanceof NumberSetting))
                  continue; 
                if (setting.name.equalsIgnoreCase(args[2])) {
                  NumberSetting setting1 = (NumberSetting)setting;
                  setting1.setValue(Double.parseDouble(args[3]));
                } 
              }  
          }  
        if (s.toLowerCase().startsWith("boolean:"))
          for (Module m : ModuleManager.getModules()) {
            if (m.getName().equalsIgnoreCase(args[1]))
              for (Setting setting : Client.INSTANCE.settingsManager.getSettingsFromModule(m)) {
                if (!(setting instanceof BooleanSetting))
                  continue; 
                if (setting.name.equalsIgnoreCase(args[2])) {
                  BooleanSetting setting1 = (BooleanSetting)setting;
                  setting1.setValue(Boolean.valueOf(Boolean.parseBoolean(args[3])));
                } 
              }  
          }  
        if (s.toLowerCase().startsWith("mode:"))
          for (Module m : ModuleManager.getModules()) {
            if (m.getName().equalsIgnoreCase(args[1]))
              for (Setting setting : Client.INSTANCE.settingsManager.getSettingsFromModule(m)) {
                if (!(setting instanceof ModeSetting))
                  continue; 
                for (String str : ((ModeSetting)setting).getOptions()) {
                  if (setting.name.equalsIgnoreCase(args[2]) && args[3]
                    .equalsIgnoreCase(str)) {
                    ModeSetting setting1 = (ModeSetting)setting;
                    setting1.setValue(args[3]);
                  } 
                } 
              }  
          }  
        if (s.toLowerCase().startsWith("addonsetting:"))
          for (Module m : ModuleManager.getModules()) {
            if (m.getName().equalsIgnoreCase(args[1]))
              for (Setting setting : Client.INSTANCE.settingsManager.getSettingsFromModule(m)) {
                if (!(setting instanceof AddonSetting) || !setting.getName().equalsIgnoreCase(args[2]))
                  continue; 
                AddonSetting setting1 = (AddonSetting)setting;
                setting1.toggled.clear();
                if (args.length < 4)
                  return; 
                for (int i = 3; i < args.length; i++) {
                  if (setting1.addons.contains(args[i]))
                    setting1.toggled.add(args[i]); 
                } 
              }  
          }  
        if (s.toLowerCase().startsWith("colorsetting:"))
          for (Module m : ModuleManager.getModules()) {
            if (m.getName().equalsIgnoreCase(args[1]))
              for (Setting setting : Client.INSTANCE.settingsManager.getSettingsFromModule(m)) {
                if (!(setting instanceof ColorSetting))
                  continue; 
                ColorSetting setting1 = (ColorSetting)setting;
                setting1.setColor(new Color(Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5])));
              }  
          }  
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public void delete(String name) {
    this.dir = new File(String.valueOf(Client.INSTANCE.dir));
    if (!this.dir.exists())
      this.dir.mkdir(); 
    this.dataFile = new File(this.dir, name + ".myth");
    try {
      this.dataFile.delete();
    } catch (Exception exception) {}
  }
  
  public void loadKey(ModuleManager modules) throws IOException {
    File file = new File((Minecraft.getMinecraft()).mcDataDir + "/Myth", "Keybinds.mythclient");
    if ((new File((Minecraft.getMinecraft()).mcDataDir, "Myth")).mkdir() || !file.exists()) {
      file.createNewFile();
      return;
    } 
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
      bufferedReader.lines().forEach(s -> {
            try {
              ModuleManager.getModuleByName(s.split(":")[0]).setKeyCode(Integer.valueOf(s.split(":")[1]).intValue());
            } catch (Exception exception) {}
          });
    } 
  }
  
  public void saveKey(List<Module> moduleList) {
    File file = new File((Minecraft.getMinecraft()).mcDataDir + "/Myth", "Keybinds.mythclient");
    if ((new File((Minecraft.getMinecraft()).mcDataDir, "Myth")).mkdir() || !file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      } 
      return;
    } 
    try {
      BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
      for (Module module : moduleList) {
        bufferedWriter.write(module.getName() + ":" + module.getKeyCode() + "\n");
        bufferedWriter.flush();
      } 
      bufferedWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
}
