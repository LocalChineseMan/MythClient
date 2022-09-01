package notthatuwu.xyz.mythrecode.api.ui.config;

public class Config {
  String name;
  
  String description;
  
  String server;
  
  String author;
  
  public Boolean safe;
  
  public String getName() {
    return this.name;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public String getServer() {
    return this.server;
  }
  
  public String getAuthor() {
    return this.author;
  }
  
  public Config(String name, String description, String server, String author, Boolean safe) {
    this.name = name;
    this.description = description;
    this.server = server;
    this.author = author;
    this.safe = safe;
  }
  
  public Config(String name) {
    this.name = name;
  }
}
