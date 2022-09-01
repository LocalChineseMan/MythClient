package sun.font;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class FontManagerFactory {
  private static FontManager instance = null;
  
  private static final String DEFAULT_CLASS;
  
  static {
    if (FontUtilities.isWindows) {
      DEFAULT_CLASS = "sun.awt.Win32FontManager";
    } else if (FontUtilities.isMacOSX) {
      DEFAULT_CLASS = "sun.font.CFontManager";
    } else {
      DEFAULT_CLASS = "sun.awt.X11FontManager";
    } 
  }
  
  public static synchronized FontManager getInstance() {
    if (instance != null)
      return instance; 
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            try {
              String str = System.getProperty("sun.font.fontmanager", FontManagerFactory
                  .DEFAULT_CLASS);
              ClassLoader classLoader = ClassLoader.getSystemClassLoader();
              Class<?> clazz = Class.forName(str, true, classLoader);
              FontManagerFactory.instance = (FontManager)clazz.newInstance();
            } catch (ClassNotFoundException|InstantiationException|IllegalAccessException classNotFoundException) {
              throw new InternalError(classNotFoundException);
            } 
            return null;
          }
        });
    return instance;
  }
}
