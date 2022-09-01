package java.lang.management;

import java.lang.management.PlatformManagedObject;
import java.util.List;

public interface PlatformLoggingMXBean extends PlatformManagedObject {
  List<String> getLoggerNames();
  
  String getLoggerLevel(String paramString);
  
  void setLoggerLevel(String paramString1, String paramString2);
  
  String getParentLoggerName(String paramString);
}
