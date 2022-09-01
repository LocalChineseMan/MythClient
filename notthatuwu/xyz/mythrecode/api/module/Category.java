package notthatuwu.xyz.mythrecode.api.module;

public enum Category {
  COMBAT("Combat"),
  MOVEMENT("Movement"),
  PLAYER("Player"),
  VISUAL("Visual"),
  EXPLOIT("Exploit"),
  DISPLAY("Display");
  
  public String name;
  
  public boolean hidden;
  
  public int x;
  
  public int y;
  
  Category(String name) {
    this.name = name;
  }
}
