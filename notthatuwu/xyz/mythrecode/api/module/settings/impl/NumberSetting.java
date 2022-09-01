package notthatuwu.xyz.mythrecode.api.module.settings.impl;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.settings.Setting;

public class NumberSetting extends Setting {
  private double value;
  
  private double min;
  
  private double max;
  
  private boolean onlyInt;
  
  private double inc;
  
  public NumberSetting(String name, Module parent, double defaultValue, double min, double max, boolean onlyInt) {
    super(name, parent);
    this.value = defaultValue;
    this.min = min;
    this.max = max;
    this.onlyInt = onlyInt;
    this.inc = 0.01D;
    setVisible(() -> Boolean.valueOf(true));
  }
  
  public NumberSetting(String name, Module parent, double defaultValue, double min, double max, boolean onlyInt, double inc) {
    super(name, parent);
    this.value = defaultValue;
    this.min = min;
    this.max = max;
    this.onlyInt = onlyInt;
    this.inc = inc;
    setVisible(() -> Boolean.valueOf(true));
  }
  
  public NumberSetting(String name, Module parent, double defaultValue, double min, double max, boolean onlyInt, Supplier<Boolean> supplier) {
    super(name, parent);
    this.value = defaultValue;
    this.min = min;
    this.max = max;
    this.onlyInt = onlyInt;
    this.inc = 0.01D;
    setVisible(supplier);
  }
  
  public NumberSetting(String name, Module parent, double defaultValue, double min, double max, boolean onlyInt, Supplier<Boolean> supplier, double inc) {
    super(name, parent);
    this.value = defaultValue;
    this.min = min;
    this.max = max;
    this.onlyInt = onlyInt;
    this.inc = inc;
    setVisible(supplier);
  }
  
  public Double getValue() {
    if (this.onlyInt)
      this.value = (int)this.value; 
    return Double.valueOf(this.value);
  }
  
  public float getValueFloat() {
    if (this.onlyInt)
      this.value = (int)this.value; 
    return (float)this.value;
  }
  
  public long getValueLong() {
    if (this.onlyInt)
      this.value = (int)this.value; 
    return (long)this.value;
  }
  
  public int getValueInt() {
    return (int)this.value;
  }
  
  public double getInc() {
    return this.inc;
  }
  
  public double getMin() {
    return this.min;
  }
  
  public double getMax() {
    return this.max;
  }
  
  public boolean getOnlyInt() {
    return this.onlyInt;
  }
  
  public void setValue(double value) {
    this.value = value;
  }
  
  public void save(JsonObject object) {
    object.addProperty(getName(), getValue());
  }
  
  public void load(JsonObject object) {
    setValue(object.get(getName()).getAsDouble());
  }
}
