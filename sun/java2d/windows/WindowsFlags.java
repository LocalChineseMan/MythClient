package sun.java2d.windows;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.awt.windows.WToolkit;
import sun.java2d.opengl.WGLGraphicsConfig;

public class WindowsFlags {
  private static boolean gdiBlitEnabled;
  
  private static boolean d3dEnabled;
  
  private static boolean d3dVerbose;
  
  private static boolean d3dSet;
  
  private static boolean d3dOnScreenEnabled;
  
  private static boolean oglEnabled;
  
  private static boolean oglVerbose;
  
  private static boolean offscreenSharingEnabled;
  
  private static boolean accelReset;
  
  private static boolean checkRegistry;
  
  private static boolean disableRegistry;
  
  private static boolean magPresent;
  
  private static boolean setHighDPIAware;
  
  private static String javaVersion;
  
  static {
    WToolkit.loadLibraries();
    initJavaFlags();
    initNativeFlags();
  }
  
  public static void initFlags() {}
  
  private static boolean getBooleanProp(String paramString, boolean paramBoolean) {
    String str = System.getProperty(paramString);
    boolean bool = paramBoolean;
    if (str != null)
      if (str.equals("true") || str
        .equals("t") || str
        .equals("True") || str
        .equals("T") || str
        .equals("")) {
        bool = true;
      } else if (str.equals("false") || str
        .equals("f") || str
        .equals("False") || str
        .equals("F")) {
        bool = false;
      }  
    return bool;
  }
  
  private static boolean isBooleanPropTrueVerbose(String paramString) {
    String str = System.getProperty(paramString);
    if (str != null && (
      str.equals("True") || str
      .equals("T")))
      return true; 
    return false;
  }
  
  private static int getIntProp(String paramString, int paramInt) {
    String str = System.getProperty(paramString);
    int i = paramInt;
    if (str != null)
      try {
        i = Integer.parseInt(str);
      } catch (NumberFormatException numberFormatException) {} 
    return i;
  }
  
  private static boolean getPropertySet(String paramString) {
    String str = System.getProperty(paramString);
    return (str != null);
  }
  
  private static void initJavaFlags() {
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            WindowsFlags.magPresent = WindowsFlags.getBooleanProp("javax.accessibility.screen_magnifier_present", false);
            boolean bool = !WindowsFlags.getBooleanProp("sun.java2d.noddraw", WindowsFlags.magPresent) ? true : false;
            boolean bool1 = WindowsFlags.getBooleanProp("sun.java2d.ddoffscreen", bool);
            WindowsFlags.d3dEnabled = WindowsFlags.getBooleanProp("sun.java2d.d3d", (bool && bool1));
            WindowsFlags.d3dOnScreenEnabled = WindowsFlags.getBooleanProp("sun.java2d.d3d.onscreen", WindowsFlags.d3dEnabled);
            WindowsFlags.oglEnabled = WindowsFlags.getBooleanProp("sun.java2d.opengl", false);
            if (WindowsFlags.oglEnabled) {
              WindowsFlags.oglVerbose = WindowsFlags.isBooleanPropTrueVerbose("sun.java2d.opengl");
              if (WGLGraphicsConfig.isWGLAvailable()) {
                WindowsFlags.d3dEnabled = false;
              } else {
                if (WindowsFlags.oglVerbose)
                  System.out.println("Could not enable OpenGL pipeline (WGL not available)"); 
                WindowsFlags.oglEnabled = false;
              } 
            } 
            WindowsFlags.gdiBlitEnabled = WindowsFlags.getBooleanProp("sun.java2d.gdiBlit", true);
            WindowsFlags.d3dSet = WindowsFlags.getPropertySet("sun.java2d.d3d");
            if (WindowsFlags.d3dSet)
              WindowsFlags.d3dVerbose = WindowsFlags.isBooleanPropTrueVerbose("sun.java2d.d3d"); 
            WindowsFlags.offscreenSharingEnabled = WindowsFlags.getBooleanProp("sun.java2d.offscreenSharing", false);
            WindowsFlags.accelReset = WindowsFlags.getBooleanProp("sun.java2d.accelReset", false);
            WindowsFlags.checkRegistry = WindowsFlags.getBooleanProp("sun.java2d.checkRegistry", false);
            WindowsFlags.disableRegistry = WindowsFlags.getBooleanProp("sun.java2d.disableRegistry", false);
            WindowsFlags.javaVersion = System.getProperty("java.version");
            if (WindowsFlags.javaVersion == null) {
              WindowsFlags.javaVersion = "default";
            } else {
              int i = WindowsFlags.javaVersion.indexOf('-');
              if (i >= 0)
                WindowsFlags.javaVersion = WindowsFlags.javaVersion.substring(0, i); 
            } 
            String str = System.getProperty("sun.java2d.dpiaware");
            if (str != null) {
              WindowsFlags.setHighDPIAware = str.equalsIgnoreCase("true");
            } else {
              String str1 = System.getProperty("sun.java.launcher", "unknown");
              WindowsFlags.setHighDPIAware = str1
                .equalsIgnoreCase("SUN_STANDARD");
            } 
            return null;
          }
        });
  }
  
  public static boolean isD3DEnabled() {
    return d3dEnabled;
  }
  
  public static boolean isD3DSet() {
    return d3dSet;
  }
  
  public static boolean isD3DOnScreenEnabled() {
    return d3dOnScreenEnabled;
  }
  
  public static boolean isD3DVerbose() {
    return d3dVerbose;
  }
  
  public static boolean isGdiBlitEnabled() {
    return gdiBlitEnabled;
  }
  
  public static boolean isTranslucentAccelerationEnabled() {
    return d3dEnabled;
  }
  
  public static boolean isOffscreenSharingEnabled() {
    return offscreenSharingEnabled;
  }
  
  public static boolean isMagPresent() {
    return magPresent;
  }
  
  public static boolean isOGLEnabled() {
    return oglEnabled;
  }
  
  public static boolean isOGLVerbose() {
    return oglVerbose;
  }
  
  private static native boolean initNativeFlags();
}
