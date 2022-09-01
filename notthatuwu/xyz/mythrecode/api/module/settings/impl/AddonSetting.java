package notthatuwu.xyz.mythrecode.api.module.settings.impl;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.settings.Setting;

public class AddonSetting extends Setting {
  public int index;
  
  public List<String> addons;
  
  public List<String> toggled = new ArrayList<>();
  
  private String selected;
  
  public String getSelected() {
    return this.selected;
  }
  
  public AddonSetting(String name, Module parent, String defaultMode, Supplier<Boolean> supplier, String... addons) {
    super(name, parent);
    this.name = name;
    this.addons = Arrays.asList(addons);
    this.selected = this.addons.get(this.index);
    setVisible(supplier);
    if (!this.toggled.contains(defaultMode))
      this.toggled.add(defaultMode); 
  }
  
  public AddonSetting(String name, Module parent, String defaultMode, String... addons) {
    this(name, parent, defaultMode, () -> Boolean.valueOf(true), addons);
    this.name = name;
    this.addons = Arrays.asList(addons);
    setVisible(() -> Boolean.valueOf(true));
    this.selected = this.addons.get(this.index);
    if (!this.toggled.contains(defaultMode))
      this.toggled.add(defaultMode); 
  }
  
  public boolean isEnabled(String mode) {
    return this.toggled.contains(mode);
  }
  
  public void toggle(String selected) {
    if (!this.toggled.contains(selected)) {
      this.toggled.add(selected);
    } else {
      this.toggled.remove(selected);
    } 
  }
  
  public List<String> getAddons() {
    return this.addons;
  }
  
  public void save(JsonObject object) {
    for (String enabled : this.toggled)
      object.addProperty(getName(), enabled); 
  }
  
  public void load(JsonObject object) {
    for (String en : this.addons) {
      if (object.get(getName()).getAsString().contains(en))
        this.toggled.add(en); 
    } 
  }
}
