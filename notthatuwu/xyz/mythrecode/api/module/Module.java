package notthatuwu.xyz.mythrecode.api.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import notthatuwu.xyz.mythrecode.Client;
import notthatuwu.xyz.mythrecode.api.event.EventManager;
import notthatuwu.xyz.mythrecode.api.interfaces.IMethods;
import notthatuwu.xyz.mythrecode.api.module.settings.Serializable;
import notthatuwu.xyz.mythrecode.api.module.settings.Setting;
import notthatuwu.xyz.mythrecode.modules.visuals.Notifications;

public class Module implements Serializable, IMethods {
  public static Minecraft mc = Minecraft.getMinecraft();
  
  protected String name;
  
  protected String description;
  
  protected String suffix;
  
  protected Category category;
  
  private int keyCode;
  
  private boolean enabled;
  
  private boolean isShow = true;
  
  public ArrayList<Setting> settings = new ArrayList<>();
  
  public Module() {
    Info info = getClass().<Info>getAnnotation(Info.class);
    this.name = info.name();
    this.description = info.description();
    this.category = info.category();
    this.keyCode = info.keyCode();
    this.suffix = "";
  }
  
  public Module(String name, String description, Category category, int keyCode) {
    this.name = name;
    this.description = description;
    this.category = category;
    this.keyCode = keyCode;
    this.suffix = "";
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public String getSuffix() {
    return this.suffix;
  }
  
  public Boolean hasSuffix() {
    return Boolean.valueOf((this.suffix != null));
  }
  
  public void setSuffix(Object obj) {
    String suffix = obj.toString();
    if (suffix.isEmpty()) {
      this.suffix = suffix;
    } else {
      this.suffix = String.format("%s§7", new Object[] { EnumChatFormatting.GRAY + suffix });
    } 
  }
  
  public Category getCategory() {
    return this.category;
  }
  
  public void setCategory(Category category) {
    this.category = category;
  }
  
  public int getKeyCode() {
    return this.keyCode;
  }
  
  public void setKeyCode(int keyCode) {
    this.keyCode = keyCode;
  }
  
  public String getDisplayName() {
    return getName() + ((hasSuffix().booleanValue() && !getSuffix().equals("")) ? (" " + getSuffix()) : getSuffix());
  }
  
  public boolean isEnabled() {
    return this.enabled;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    if (enabled) {
      EventManager.register(this);
      if (mc.theWorld != null)
        onEnable(); 
    } else {
      EventManager.unregister(this);
      if (mc.theWorld != null)
        onDisable(); 
    } 
  }
  
  public void toggle() {
    setEnabled(!isEnabled());
  }
  
  public void onDisable() {
    Notifications notifications = Client.INSTANCE.moduleManager.<Notifications>getModuleByClass(Notifications.class);
    if (notifications.isEnabled() && notifications.onToggle.getValue().booleanValue() && !this.name.equalsIgnoreCase("ClickGui"))
      Client.INSTANCE.notificationManager.sendNotification("Disabled " + this.name, "Module"); 
  }
  
  public void onEnable() {
    Notifications notifications = Client.INSTANCE.moduleManager.<Notifications>getModuleByClass(Notifications.class);
    if (notifications.isEnabled() && notifications.onToggle.getValue().booleanValue() && !this.name.equalsIgnoreCase("ClickGui"))
      Client.INSTANCE.notificationManager.sendNotification("Enabled " + this.name, "Module"); 
  }
  
  public boolean isShow() {
    return this.isShow;
  }
  
  public void setShow(boolean show) {
    this.isShow = show;
  }
  
  public void reflectSettings() {
    try {
      for (Field field : getClass().getDeclaredFields()) {
        try {
          boolean isAssignable = field.get(this) instanceof Setting;
          if (isAssignable) {
            if (!field.isAccessible())
              field.setAccessible(true); 
            try {
              this.settings.add((Setting)field.get(this));
            } catch (IllegalAccessException illegalAccessException) {}
          } 
        } catch (IllegalAccessException illegalAccessException) {}
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public JsonObject save() {
    JsonObject object = new JsonObject();
    object.addProperty("toggled", Boolean.valueOf(isEnabled()));
    object.addProperty("key", Integer.valueOf(getKeyCode()));
    List<Setting> properties = Client.INSTANCE.settingsManager.getSettingsFromModule(this);
    if (properties != null && !properties.isEmpty()) {
      JsonObject propertiesObject = new JsonObject();
      for (Setting settings : properties)
        settings.save(propertiesObject); 
      object.add("Properties", (JsonElement)propertiesObject);
    } 
    return object;
  }
  
  public void load(JsonObject object, boolean loadKeyBind) {
    if (object.has("toggled"))
      setEnabled(object.get("toggled").getAsBoolean()); 
    if (object.has("key") && loadKeyBind)
      setKeyCode(object.get("key").getAsInt()); 
    List<Setting> properties = Client.INSTANCE.settingsManager.getSettingsFromModule(this);
    if (object.has("Properties")) {
      JsonObject propertiesObject = object.getAsJsonObject("Properties");
      for (Setting settings : properties) {
        if (propertiesObject.has(settings.getName()))
          settings.load(propertiesObject); 
      } 
    } 
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE})
  public static @interface Info {
    String name();
    
    String description() default "";
    
    Category category();
    
    int keyCode() default 0;
  }
}
