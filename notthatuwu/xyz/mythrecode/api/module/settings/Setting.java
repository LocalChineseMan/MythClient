package notthatuwu.xyz.mythrecode.api.module.settings;

import com.google.gson.JsonObject;
import java.util.function.Supplier;
import notthatuwu.xyz.mythrecode.api.module.Module;

public class Setting {
  public String name;
  
  private Supplier<Boolean> visible;
  
  public Setting(String name, Module parent) {
    this.name = name;
  }
  
  public Object getValue() {
    return null;
  }
  
  public String getName() {
    return this.name;
  }
  
  public boolean isVisible() {
    return ((Boolean)this.visible.get()).booleanValue();
  }
  
  public void setVisible(Supplier<Boolean> visibility) {
    this.visible = visibility;
  }
  
  public void save(JsonObject object) {}
  
  public void load(JsonObject object) {}
}
