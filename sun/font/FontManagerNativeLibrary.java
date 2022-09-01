package sun.font;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class FontManagerNativeLibrary {
  static {
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            System.loadLibrary("awt");
            if (FontUtilities.isOpenJDK && 
              System.getProperty("os.name").startsWith("Windows"))
              System.loadLibrary("freetype"); 
            System.loadLibrary("fontmanager");
            return null;
          }
        });
  }
  
  public static void load() {}
}
