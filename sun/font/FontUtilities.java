package sun.font;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.plaf.FontUIResource;
import sun.util.logging.PlatformLogger;

public final class FontUtilities {
  public static boolean isSolaris;
  
  public static boolean isLinux;
  
  public static boolean isMacOSX;
  
  public static boolean isSolaris8;
  
  public static boolean isSolaris9;
  
  public static boolean isOpenSolaris;
  
  public static boolean useT2K;
  
  public static boolean isWindows;
  
  public static boolean isOpenJDK;
  
  static final String LUCIDA_FILE_NAME = "LucidaSansRegular.ttf";
  
  private static boolean debugFonts = false;
  
  private static PlatformLogger logger = null;
  
  private static boolean logging;
  
  public static final int MIN_LAYOUT_CHARCODE = 768;
  
  public static final int MAX_LAYOUT_CHARCODE = 8303;
  
  static {
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            String str1 = System.getProperty("os.name", "unknownOS");
            FontUtilities.isSolaris = str1.startsWith("SunOS");
            FontUtilities.isLinux = str1.startsWith("Linux");
            FontUtilities.isMacOSX = str1.contains("OS X");
            String str2 = System.getProperty("sun.java2d.font.scaler");
            if (str2 != null) {
              FontUtilities.useT2K = "t2k".equals(str2);
            } else {
              FontUtilities.useT2K = false;
            } 
            if (FontUtilities.isSolaris) {
              String str = System.getProperty("os.version", "0.0");
              FontUtilities.isSolaris8 = str.startsWith("5.8");
              FontUtilities.isSolaris9 = str.startsWith("5.9");
              float f = Float.parseFloat(str);
              if (f > 5.1F) {
                File file1 = new File("/etc/release");
                String str6 = null;
                try {
                  FileInputStream fileInputStream = new FileInputStream(file1);
                  InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "ISO-8859-1");
                  BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                  str6 = bufferedReader.readLine();
                  fileInputStream.close();
                } catch (Exception exception) {}
                if (str6 != null && str6.indexOf("OpenSolaris") >= 0) {
                  FontUtilities.isOpenSolaris = true;
                } else {
                  FontUtilities.isOpenSolaris = false;
                } 
              } else {
                FontUtilities.isOpenSolaris = false;
              } 
            } else {
              FontUtilities.isSolaris8 = false;
              FontUtilities.isSolaris9 = false;
              FontUtilities.isOpenSolaris = false;
            } 
            FontUtilities.isWindows = str1.startsWith("Windows");
            String str3 = System.getProperty("java.home", "") + File.separator + "lib";
            String str4 = str3 + File.separator + "fonts";
            File file = new File(str4 + File.separator + "LucidaSansRegular.ttf");
            FontUtilities.isOpenJDK = !file.exists();
            String str5 = System.getProperty("sun.java2d.debugfonts");
            if (str5 != null && !str5.equals("false")) {
              FontUtilities.debugFonts = true;
              FontUtilities.logger = PlatformLogger.getLogger("sun.java2d");
              if (str5.equals("warning")) {
                FontUtilities.logger.setLevel(PlatformLogger.Level.WARNING);
              } else if (str5.equals("severe")) {
                FontUtilities.logger.setLevel(PlatformLogger.Level.SEVERE);
              } 
            } 
            if (FontUtilities.debugFonts) {
              FontUtilities.logger = PlatformLogger.getLogger("sun.java2d");
              FontUtilities.logging = FontUtilities.logger.isEnabled();
            } 
            return null;
          }
        });
  }
  
  public static Font2D getFont2D(Font paramFont) {
    return FontAccess.getFontAccess().getFont2D(paramFont);
  }
  
  public static boolean isComplexText(char[] paramArrayOfchar, int paramInt1, int paramInt2) {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (paramArrayOfchar[i] >= '̀')
        if (isNonSimpleChar(paramArrayOfchar[i]))
          return true;  
    } 
    return false;
  }
  
  public static boolean isNonSimpleChar(char paramChar) {
    return (
      isComplexCharCode(paramChar) || (paramChar >= '?' && paramChar <= '?'));
  }
  
  public static boolean isComplexCharCode(int paramInt) {
    if (paramInt < 768 || paramInt > 8303)
      return false; 
    if (paramInt <= 879)
      return true; 
    if (paramInt < 1424)
      return false; 
    if (paramInt <= 1791)
      return true; 
    if (paramInt < 2304)
      return false; 
    if (paramInt <= 3711)
      return true; 
    if (paramInt < 3840)
      return false; 
    if (paramInt <= 4095)
      return true; 
    if (paramInt < 4352)
      return false; 
    if (paramInt < 4607)
      return true; 
    if (paramInt < 6016)
      return false; 
    if (paramInt <= 6143)
      return true; 
    if (paramInt < 8204)
      return false; 
    if (paramInt <= 8205)
      return true; 
    if (paramInt >= 8234 && paramInt <= 8238)
      return true; 
    if (paramInt >= 8298 && paramInt <= 8303)
      return true; 
    return false;
  }
  
  public static PlatformLogger getLogger() {
    return logger;
  }
  
  public static boolean isLogging() {
    return logging;
  }
  
  public static boolean debugFonts() {
    return debugFonts;
  }
  
  public static boolean fontSupportsDefaultEncoding(Font paramFont) {
    return getFont2D(paramFont) instanceof CompositeFont;
  }
  
  private static volatile SoftReference<ConcurrentHashMap<PhysicalFont, CompositeFont>> compMapRef = new SoftReference<>(null);
  
  public static FontUIResource getCompositeFontUIResource(Font paramFont) {
    FontUIResource fontUIResource = new FontUIResource(paramFont);
    Font2D font2D1 = getFont2D(paramFont);
    if (!(font2D1 instanceof PhysicalFont))
      return fontUIResource; 
    FontManager fontManager = FontManagerFactory.getInstance();
    Font2D font2D2 = fontManager.findFont2D("dialog", paramFont.getStyle(), 0);
    if (font2D2 == null || !(font2D2 instanceof CompositeFont))
      return fontUIResource; 
    CompositeFont compositeFont1 = (CompositeFont)font2D2;
    PhysicalFont physicalFont = (PhysicalFont)font2D1;
    ConcurrentHashMap<Object, Object> concurrentHashMap = (ConcurrentHashMap)compMapRef.get();
    if (concurrentHashMap == null) {
      concurrentHashMap = new ConcurrentHashMap<>();
      compMapRef = new SoftReference(concurrentHashMap);
    } 
    CompositeFont compositeFont2 = (CompositeFont)concurrentHashMap.get(physicalFont);
    if (compositeFont2 == null) {
      compositeFont2 = new CompositeFont(physicalFont, compositeFont1);
      concurrentHashMap.put(physicalFont, compositeFont2);
    } 
    FontAccess.getFontAccess().setFont2D(fontUIResource, compositeFont2.handle);
    FontAccess.getFontAccess().setCreatedFont(fontUIResource);
    return fontUIResource;
  }
  
  private static final String[][] nameMap = new String[][] { { "sans", "sansserif" }, { "sans-serif", "sansserif" }, { "serif", "serif" }, { "monospace", "monospaced" } };
  
  public static String mapFcName(String paramString) {
    for (byte b = 0; b < nameMap.length; b++) {
      if (paramString.equals(nameMap[b][0]))
        return nameMap[b][1]; 
    } 
    return null;
  }
  
  public static FontUIResource getFontConfigFUIR(String paramString, int paramInt1, int paramInt2) {
    FontUIResource fontUIResource;
    String str = mapFcName(paramString);
    if (str == null)
      str = "sansserif"; 
    FontManager fontManager = FontManagerFactory.getInstance();
    if (fontManager instanceof SunFontManager) {
      SunFontManager sunFontManager = (SunFontManager)fontManager;
      fontUIResource = sunFontManager.getFontConfigFUIR(str, paramInt1, paramInt2);
    } else {
      fontUIResource = new FontUIResource(str, paramInt1, paramInt2);
    } 
    return fontUIResource;
  }
  
  public static boolean textLayoutIsCompatible(Font paramFont) {
    Font2D font2D = getFont2D(paramFont);
    if (font2D instanceof TrueTypeFont) {
      TrueTypeFont trueTypeFont = (TrueTypeFont)font2D;
      return (trueTypeFont
        .getDirectoryEntry(1196643650) == null || trueTypeFont
        .getDirectoryEntry(1196445523) != null);
    } 
    return false;
  }
}
