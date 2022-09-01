package notthatuwu.xyz.mythrecode.api.command;

public interface Command {
  boolean run(String[] paramArrayOfString);
  
  String usage();
}
