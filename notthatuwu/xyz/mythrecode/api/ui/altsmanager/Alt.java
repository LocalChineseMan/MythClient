package notthatuwu.xyz.mythrecode.api.ui.altsmanager;

import java.util.Date;

public class Alt {
  private String mask;
  
  private String username;
  
  public String type;
  
  private String password;
  
  private boolean banned;
  
  private Date unbannedDate;
  
  public Alt(String username, String password, String type) {
    this(username, password, "", type);
  }
  
  public Alt(String username, String password, String mask, String type) {
    this.username = username;
    this.password = password;
    this.mask = mask;
    this.type = type;
    this.banned = false;
  }
  
  public String getMask() {
    return this.mask;
  }
  
  public String getType() {
    return this.type;
  }
  
  public String getPassword() {
    return this.password;
  }
  
  public String getUsername() {
    return this.username;
  }
  
  public void setMask(String mask) {
    this.mask = mask;
  }
  
  public void setPassword(String password) {
    this.password = password;
  }
  
  public boolean isBanned() {
    return this.banned;
  }
  
  public void setBanned(boolean banned) {
    this.banned = banned;
  }
  
  public Date getUnbannedDate() {
    return this.unbannedDate;
  }
  
  public void setUnbannedDate(Date unbannedDate) {
    this.unbannedDate = unbannedDate;
  }
}
