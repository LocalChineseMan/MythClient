package java.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import sun.misc.SharedSecrets;

class DeleteOnExitHook {
  private static LinkedHashSet<String> files = new LinkedHashSet<>();
  
  static {
    SharedSecrets.getJavaLangAccess()
      .registerShutdownHook(2, true, new Runnable() {
          public void run() {
            DeleteOnExitHook.runHooks();
          }
        });
  }
  
  static synchronized void add(String paramString) {
    if (files == null)
      throw new IllegalStateException("Shutdown in progress"); 
    files.add(paramString);
  }
  
  static void runHooks() {
    LinkedHashSet<String> linkedHashSet;
    synchronized (DeleteOnExitHook.class) {
      linkedHashSet = files;
      files = null;
    } 
    ArrayList<String> arrayList = new ArrayList<>(linkedHashSet);
    Collections.reverse(arrayList);
    for (String str : arrayList)
      (new File(str)).delete(); 
  }
}
