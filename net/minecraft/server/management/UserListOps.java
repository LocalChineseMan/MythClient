package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListOps extends UserList<GameProfile, UserListOpsEntry> {
  public UserListOps(File saveFile) {
    super(saveFile);
  }
  
  protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
    return (UserListEntry<GameProfile>)new UserListOpsEntry(entryData);
  }
  
  public String[] getKeys() {
    String[] astring = new String[getValues().size()];
    int i = 0;
    for (UserListOpsEntry userlistopsentry : getValues().values())
      astring[i++] = ((GameProfile)userlistopsentry.getValue()).getName(); 
    return astring;
  }
  
  public boolean func_183026_b(GameProfile p_183026_1_) {
    UserListOpsEntry userlistopsentry = getEntry(p_183026_1_);
    return (userlistopsentry != null && userlistopsentry.func_183024_b());
  }
  
  protected String getObjectKey(GameProfile obj) {
    return obj.getId().toString();
  }
  
  public GameProfile getGameProfileFromName(String username) {
    for (UserListOpsEntry userlistopsentry : getValues().values()) {
      if (username.equalsIgnoreCase(((GameProfile)userlistopsentry.getValue()).getName()))
        return (GameProfile)userlistopsentry.getValue(); 
    } 
    return null;
  }
}
