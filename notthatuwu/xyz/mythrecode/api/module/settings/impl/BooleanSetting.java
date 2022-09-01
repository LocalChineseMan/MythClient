package notthatuwu.xyz.mythrecode.api.module.settings.impl;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.settings.Setting;

public class BooleanSetting extends Setting {
  private boolean value;
  
  public BooleanSetting(String name, Module parent, boolean defaultValue) {
    super(name, parent);
    this.value = defaultValue;
    setVisible(() -> Boolean.valueOf(true));
  }
  
  public BooleanSetting(String name, Module parent, boolean defaultValue, Supplier<Boolean> supplier) {
    super(name, parent);
    this.value = defaultValue;
    setVisible(supplier);
  }
  
  public Boolean getValue() {
    return Boolean.valueOf(this.value);
  }
  
  public void setValue(Boolean value) {
    this.value = value.booleanValue();
  }
  
  public void toggle() {
    this.value = !this.value;
  }
  
  public void save(JsonObject object) {
    object.addProperty(getName(), getValue());
  }
  
  public void load(JsonObject object) {
    setValue(Boolean.valueOf(object.get(getName()).getAsBoolean()));
  }
}
