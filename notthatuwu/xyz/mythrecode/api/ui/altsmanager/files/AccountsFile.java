package notthatuwu.xyz.mythrecode.api.ui.altsmanager.files;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.io.File;
import java.util.Arrays;
import notthatuwu.xyz.mythrecode.api.module.file.IFile;
import notthatuwu.xyz.mythrecode.api.ui.altsmanager.Alt;
import notthatuwu.xyz.mythrecode.api.ui.altsmanager.AltManager;

public class AccountsFile implements IFile {
  private File file;
  
  public void save(Gson gson) {
    JsonArray array = new JsonArray();
    AltManager.getAlts().forEach(alt -> {
          JsonObject object = new JsonObject();
          object.addProperty("mask", alt.getMask());
          object.addProperty("username", alt.getUsername());
          object.addProperty("password", alt.getPassword());
          object.addProperty("type", alt.getType());
          array.add((JsonElement)object);
        });
    writeFile(gson.toJson((JsonElement)array), this.file);
  }
  
  public void load(Gson gson) {
    if (!this.file.exists())
      return; 
    Account[] accounts = (Account[])gson.fromJson(readFile(this.file), Account[].class);
    if (accounts != null)
      Arrays.<Account>stream(accounts).forEach(account -> AltManager.getAlts().add(new Alt(account.username, account.password, account.type))); 
  }
  
  public void setFile(File root) {
    this.file = new File(root, "/accounts.mythclient");
  }
  
  final class Account {
    @SerializedName("mask")
    final String mask;
    
    @SerializedName("username")
    final String username;
    
    @SerializedName("password")
    final String password;
    
    @SerializedName("type")
    final String type;
    
    Account(String mask, String username, String password, String type) {
      this.mask = mask;
      this.username = username;
      this.password = password;
      this.type = type;
    }
  }
}
