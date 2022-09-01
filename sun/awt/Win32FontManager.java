package sun.awt;

import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import sun.awt.windows.WFontConfiguration;
import sun.font.SunFontManager;
import sun.font.TrueTypeFont;

public final class Win32FontManager extends SunFontManager {
  private static TrueTypeFont eudcFont;
  
  static {
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            String str = Win32FontManager.getEUDCFontFile();
            if (str != null)
              try {
                Win32FontManager.eudcFont = new TrueTypeFont(str, null, 0, true);
              } catch (FontFormatException fontFormatException) {} 
            return null;
          }
        });
  }
  
  public TrueTypeFont getEUDCFont() {
    return eudcFont;
  }
  
  public Win32FontManager() {
    AccessController.doPrivileged(new PrivilegedAction() {
          public Object run() {
            Win32FontManager.this.registerJREFontsWithPlatform(SunFontManager.jreFontDirName);
            return null;
          }
        });
  }
  
  protected boolean useAbsoluteFontFileNames() {
    return false;
  }
  
  protected void registerFontFile(String paramString, String[] paramArrayOfString, int paramInt, boolean paramBoolean) {
    boolean bool1;
    if (this.registeredFontFiles.contains(paramString))
      return; 
    this.registeredFontFiles.add(paramString);
    if (getTrueTypeFilter().accept(null, paramString)) {
      bool1 = false;
    } else if (getType1Filter().accept(null, paramString)) {
      bool1 = true;
    } else {
      return;
    } 
    if (this.fontPath == null)
      this.fontPath = getPlatformFontPath(noType1Font); 
    String str = jreFontDirName + File.pathSeparator + this.fontPath;
    StringTokenizer stringTokenizer = new StringTokenizer(str, File.pathSeparator);
    boolean bool2 = false;
    try {
      while (!bool2 && stringTokenizer.hasMoreTokens()) {
        String str1 = stringTokenizer.nextToken();
        boolean bool = str1.equals(jreFontDirName);
        File file = new File(str1, paramString);
        if (file.canRead()) {
          bool2 = true;
          String str2 = file.getAbsolutePath();
          if (paramBoolean) {
            registerDeferredFont(paramString, str2, paramArrayOfString, bool1, bool, paramInt);
            break;
          } 
          registerFontFile(str2, paramArrayOfString, bool1, bool, paramInt);
          break;
        } 
      } 
    } catch (NoSuchElementException noSuchElementException) {
      System.err.println(noSuchElementException);
    } 
    if (!bool2)
      addToMissingFontFileList(paramString); 
  }
  
  protected FontConfiguration createFontConfiguration() {
    WFontConfiguration wFontConfiguration = new WFontConfiguration((SunFontManager)this);
    wFontConfiguration.init();
    return wFontConfiguration;
  }
  
  public FontConfiguration createFontConfiguration(boolean paramBoolean1, boolean paramBoolean2) {
    return new WFontConfiguration((SunFontManager)this, paramBoolean1, paramBoolean2);
  }
  
  protected void populateFontFileNameMap(HashMap<String, String> paramHashMap1, HashMap<String, String> paramHashMap2, HashMap<String, ArrayList<String>> paramHashMap, Locale paramLocale) {
    populateFontFileNameMap0(paramHashMap1, paramHashMap2, paramHashMap, paramLocale);
  }
  
  protected String[] getDefaultPlatformFont() {
    String[] arrayOfString1 = new String[2];
    arrayOfString1[0] = "Arial";
    arrayOfString1[1] = "c:\\windows\\fonts";
    String[] arrayOfString2 = getPlatformFontDirs(true);
    if (arrayOfString2.length > 1) {
      String str = AccessController.<String>doPrivileged((PrivilegedAction<String>)new Object(this, arrayOfString2));
      if (str != null)
        arrayOfString1[1] = str; 
    } else {
      arrayOfString1[1] = arrayOfString2[0];
    } 
    arrayOfString1[1] = arrayOfString1[1] + File.separator + "arial.ttf";
    return arrayOfString1;
  }
  
  static String fontsForPrinting = null;
  
  protected void registerJREFontsWithPlatform(String paramString) {
    fontsForPrinting = paramString;
  }
  
  public static void registerJREFontsForPrinting() {
    String str;
    synchronized (Win32GraphicsEnvironment.class) {
      GraphicsEnvironment.getLocalGraphicsEnvironment();
      if (fontsForPrinting == null)
        return; 
      str = fontsForPrinting;
      fontsForPrinting = null;
    } 
    AccessController.doPrivileged((PrivilegedAction<?>)new Object(str));
  }
  
  public HashMap<String, SunFontManager.FamilyDescription> populateHardcodedFileNameMap() {
    HashMap<Object, Object> hashMap = new HashMap<>();
    SunFontManager.FamilyDescription familyDescription = new SunFontManager.FamilyDescription();
    familyDescription.familyName = "Segoe UI";
    familyDescription.plainFullName = "Segoe UI";
    familyDescription.plainFileName = "segoeui.ttf";
    familyDescription.boldFullName = "Segoe UI Bold";
    familyDescription.boldFileName = "segoeuib.ttf";
    familyDescription.italicFullName = "Segoe UI Italic";
    familyDescription.italicFileName = "segoeuii.ttf";
    familyDescription.boldItalicFullName = "Segoe UI Bold Italic";
    familyDescription.boldItalicFileName = "segoeuiz.ttf";
    hashMap.put("segoe", familyDescription);
    familyDescription = new SunFontManager.FamilyDescription();
    familyDescription.familyName = "Tahoma";
    familyDescription.plainFullName = "Tahoma";
    familyDescription.plainFileName = "tahoma.ttf";
    familyDescription.boldFullName = "Tahoma Bold";
    familyDescription.boldFileName = "tahomabd.ttf";
    hashMap.put("tahoma", familyDescription);
    familyDescription = new SunFontManager.FamilyDescription();
    familyDescription.familyName = "Verdana";
    familyDescription.plainFullName = "Verdana";
    familyDescription.plainFileName = "verdana.TTF";
    familyDescription.boldFullName = "Verdana Bold";
    familyDescription.boldFileName = "verdanab.TTF";
    familyDescription.italicFullName = "Verdana Italic";
    familyDescription.italicFileName = "verdanai.TTF";
    familyDescription.boldItalicFullName = "Verdana Bold Italic";
    familyDescription.boldItalicFileName = "verdanaz.TTF";
    hashMap.put("verdana", familyDescription);
    familyDescription = new SunFontManager.FamilyDescription();
    familyDescription.familyName = "Arial";
    familyDescription.plainFullName = "Arial";
    familyDescription.plainFileName = "ARIAL.TTF";
    familyDescription.boldFullName = "Arial Bold";
    familyDescription.boldFileName = "ARIALBD.TTF";
    familyDescription.italicFullName = "Arial Italic";
    familyDescription.italicFileName = "ARIALI.TTF";
    familyDescription.boldItalicFullName = "Arial Bold Italic";
    familyDescription.boldItalicFileName = "ARIALBI.TTF";
    hashMap.put("arial", familyDescription);
    familyDescription = new SunFontManager.FamilyDescription();
    familyDescription.familyName = "Symbol";
    familyDescription.plainFullName = "Symbol";
    familyDescription.plainFileName = "Symbol.TTF";
    hashMap.put("symbol", familyDescription);
    familyDescription = new SunFontManager.FamilyDescription();
    familyDescription.familyName = "WingDings";
    familyDescription.plainFullName = "WingDings";
    familyDescription.plainFileName = "WINGDING.TTF";
    hashMap.put("wingdings", familyDescription);
    return (HashMap)hashMap;
  }
  
  private static native String getEUDCFontFile();
  
  private static native void populateFontFileNameMap0(HashMap<String, String> paramHashMap1, HashMap<String, String> paramHashMap2, HashMap<String, ArrayList<String>> paramHashMap, Locale paramLocale);
  
  protected synchronized native String getFontPath(boolean paramBoolean);
  
  protected static native void registerFontWithPlatform(String paramString);
  
  protected static native void deRegisterFontWithPlatform(String paramString);
}
