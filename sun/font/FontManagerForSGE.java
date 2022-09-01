package sun.font;

import java.awt.Font;
import java.util.Locale;
import java.util.TreeMap;

public interface FontManagerForSGE extends FontManager {
  Font[] getCreatedFonts();
  
  TreeMap<String, String> getCreatedFontFamilyNames();
  
  Font[] getAllInstalledFonts();
  
  String[] getInstalledFontFamilyNames(Locale paramLocale);
  
  void useAlternateFontforJALocales();
}
