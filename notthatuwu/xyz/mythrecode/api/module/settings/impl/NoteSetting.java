package notthatuwu.xyz.mythrecode.api.module.settings.impl;

import java.util.function.Supplier;
import notthatuwu.xyz.mythrecode.api.module.Module;
import notthatuwu.xyz.mythrecode.api.module.settings.Setting;

public class NoteSetting extends Setting {
  private String text;
  
  public NoteSetting(String name, Module parent) {
    super(name, parent);
    setVisible(() -> Boolean.valueOf(true));
  }
  
  public NoteSetting(String name, Module parent, Supplier<Boolean> supplier) {
    super(name, parent);
    setVisible(supplier);
  }
  
  public String getValue() {
    return this.text;
  }
}
