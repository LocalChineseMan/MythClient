package net.arikia.dev.drpc;

import com.sun.jna.Library;
import com.sun.jna.Native;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class DiscordRPC {
  private static final String DLL_VERSION = "3.4.0";
  
  private static final String LIB_VERSION = "1.6.2";
  
  static {
    loadDLL();
  }
  
  public static void discordInitialize(String applicationId, DiscordEventHandlers handlers, boolean autoRegister) {
    DLL.INSTANCE.Discord_Initialize(applicationId, handlers, autoRegister ? 1 : 0, null);
  }
  
  public static void discordRegister(String applicationId, String command) {
    DLL.INSTANCE.Discord_Register(applicationId, command);
  }
  
  public static void discordInitialize(String applicationId, DiscordEventHandlers handlers, boolean autoRegister, String steamId) {
    DLL.INSTANCE.Discord_Initialize(applicationId, handlers, autoRegister ? 1 : 0, steamId);
  }
  
  public static void discordRegisterSteam(String applicationId, String steamId) {
    DLL.INSTANCE.Discord_RegisterSteamGame(applicationId, steamId);
  }
  
  public static void discordUpdateEventHandlers(DiscordEventHandlers handlers) {
    DLL.INSTANCE.Discord_UpdateHandlers(handlers);
  }
  
  public static void discordShutdown() {
    DLL.INSTANCE.Discord_Shutdown();
  }
  
  public static void discordRunCallbacks() {
    DLL.INSTANCE.Discord_RunCallbacks();
  }
  
  public static void discordUpdatePresence(DiscordRichPresence presence) {
    DLL.INSTANCE.Discord_UpdatePresence(presence);
  }
  
  public static void discordClearPresence() {
    DLL.INSTANCE.Discord_ClearPresence();
  }
  
  public static void discordRespond(String userId, DiscordReply reply) {
    DLL.INSTANCE.Discord_Respond(userId, reply.reply);
  }
  
  private static void loadDLL() {
    String tempPath, dir, name = System.mapLibraryName("discord-rpc");
    OSUtil osUtil = new OSUtil();
    if (osUtil.isMac()) {
      File homeDir = new File(System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support" + File.separator);
      dir = "darwin";
      tempPath = homeDir + File.separator + "discord-rpc" + File.separator + name;
    } else if (osUtil.isWindows()) {
      File homeDir = new File(System.getenv("TEMP"));
      boolean is64bit = System.getProperty("sun.arch.data.model").equals("64");
      dir = is64bit ? "win-x64" : "win-x86";
      tempPath = homeDir + File.separator + "discord-rpc" + File.separator + name;
    } else {
      File homeDir = new File(System.getProperty("user.home"), ".discord-rpc");
      dir = "linux";
      tempPath = homeDir + File.separator + name;
    } 
    String finalPath = "/" + dir + "/" + name;
    File f = new File(tempPath);
    try {
      InputStream in = DiscordRPC.class.getResourceAsStream(finalPath);
      try {
        OutputStream out = openOutputStream(f);
        try {
          copyFile(in, out);
          f.deleteOnExit();
          if (out != null)
            out.close(); 
        } catch (Throwable throwable) {
          if (out != null)
            try {
              out.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
        if (in != null)
          in.close(); 
      } catch (Throwable throwable) {
        if (in != null)
          try {
            in.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
    } catch (IOException e) {
      e.printStackTrace();
    } 
    System.load(f.getAbsolutePath());
  }
  
  private static void copyFile(InputStream input, OutputStream output) throws IOException {
    byte[] buffer = new byte[4096];
    int n;
    while (-1 != (n = input.read(buffer)))
      output.write(buffer, 0, n); 
  }
  
  private static FileOutputStream openOutputStream(File file) throws IOException {
    if (file.exists()) {
      if (file.isDirectory())
        throw new IOException("File '" + file + "' exists but is a directory"); 
      if (!file.canWrite())
        throw new IOException("File '" + file + "' cannot be written to"); 
    } else {
      File parent = file.getParentFile();
      if (parent != null && 
        !parent.mkdirs() && !parent.isDirectory())
        throw new IOException("Directory '" + parent + "' could not be created"); 
    } 
    return new FileOutputStream(file);
  }
  
  private static interface DLL extends Library {
    public static final DLL INSTANCE = (DLL)Native.loadLibrary("discord-rpc", DLL.class);
    
    void Discord_Initialize(String param1String1, DiscordEventHandlers param1DiscordEventHandlers, int param1Int, String param1String2);
    
    void Discord_Register(String param1String1, String param1String2);
    
    void Discord_RegisterSteamGame(String param1String1, String param1String2);
    
    void Discord_UpdateHandlers(DiscordEventHandlers param1DiscordEventHandlers);
    
    void Discord_Shutdown();
    
    void Discord_RunCallbacks();
    
    void Discord_UpdatePresence(DiscordRichPresence param1DiscordRichPresence);
    
    void Discord_ClearPresence();
    
    void Discord_Respond(String param1String, int param1Int);
  }
  
  public enum DiscordRPC {
  
  }
}
