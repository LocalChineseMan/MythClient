package notthatuwu.xyz.mythrecode.api.module.settings;

import com.google.gson.JsonObject;

public interface Serializable {
  JsonObject save();
  
  void load(JsonObject paramJsonObject, boolean paramBoolean);
}
