package notthatuwu.xyz.mythrecode.api.module.settings.impl;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.settings.Setting;

public class ModeSetting extends Setting {
  private String value;
  
  public boolean focused;
  
  public List<String> parentModes;
  
  public ModeSetting parent;
  
  private String[] options;
  
  public ModeSetting(String name, String[] options, String defaultValue) {
    this(name, (Module)null, options, defaultValue);
  }
  
  public ModeSetting(String name, Module parent, String[] options, String defaultValue) {
    super(name, parent);
    this.options = options;
    this.value = defaultValue;
    setVisible(() -> Boolean.valueOf(true));
  }
  
  public ModeSetting(String name, Module parent, String[] options, String defaultValue, Supplier<Boolean> supplier) {
    super(name, parent);
    this.options = options;
    this.value = defaultValue;
    setVisible(supplier);
  }
  
  public void addParent(ModeSetting parent, String... parentmodes) {
    this.parentModes = Arrays.asList(parentmodes);
    this.parent = parent;
  }
  
  public String[] getOptions() {
    return this.options;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public boolean is(String value) {
    return value.equalsIgnoreCase(getValue());
  }
  
  public void setValue(String value) {
    this.value = value;
  }
  
  public void save(JsonObject object) {
    object.addProperty(getName(), getValue());
  }
  
  public void load(JsonObject object) {
    setValue(object.get(getName()).getAsString());
  }
}
