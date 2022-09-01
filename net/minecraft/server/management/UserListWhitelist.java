package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListWhitelist extends UserList<GameProfile, UserListWhitelistEntry> {
  public UserListWhitelist(File p_i1132_1_) {
    super(p_i1132_1_);
  }
  
  protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
    return (UserListEntry<GameProfile>)new UserListWhitelistEntry(entryData);
  }
  
  public String[] getKeys() {
    String[] astring = new String[getValues().size()];
    int i = 0;
    for (UserListWhitelistEntry userlistwhitelistentry : getValues().values())
      astring[i++] = ((GameProfile)userlistwhitelistentry.getValue()).getName(); 
    return astring;
  }
  
  protected String getObjectKey(GameProfile obj) {
    return obj.getId().toString();
  }
  
  public GameProfile func_152706_a(String p_152706_1_) {
    for (UserListWhitelistEntry userlistwhitelistentry : getValues().values()) {
      if (p_152706_1_.equalsIgnoreCase(((GameProfile)userlistwhitelistentry.getValue()).getName()))
        return (GameProfile)userlistwhitelistentry.getValue(); 
    } 
    return null;
  }
}
