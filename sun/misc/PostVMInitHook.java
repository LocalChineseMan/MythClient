package sun.misc;

import java.io.File;
import sun.usagetracker.UsageTrackerClient;

public class PostVMInitHook {
  public static void run() {
    trackJavaUsage();
  }
  
  private static void trackJavaUsage() {
    String str1 = System.getProperty("java.home") + File.separator + "lib" + File.separator + "management" + File.separator + "usagetracker.properties";
    String str2 = System.getProperty("com.oracle.usagetracker.config.file", str1);
    if ((new File(str2)).exists()) {
      UsageTrackerClient usageTrackerClient = new UsageTrackerClient();
      usageTrackerClient.run("VM start", System.getProperty("sun.java.command"));
    } 
  }
}
