package sun.util.locale.provider;

import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.spi.CalendarNameProvider;

public class CalendarNameProviderImpl extends CalendarNameProvider implements AvailableLanguageTags {
  private final LocaleProviderAdapter.Type type;
  
  private final Set<String> langtags;
  
  public CalendarNameProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet) {
    this.type = paramType;
    this.langtags = paramSet;
  }
  
  public String getDisplayName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale) {
    return getDisplayNameImpl(paramString, paramInt1, paramInt2, paramInt3, paramLocale, false);
  }
  
  public String getJavaTimeDisplayName(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale) {
    return getDisplayNameImpl(paramString, paramInt1, paramInt2, paramInt3, paramLocale, true);
  }
  
  public String getDisplayNameImpl(String paramString, int paramInt1, int paramInt2, int paramInt3, Locale paramLocale, boolean paramBoolean) {
    String str1 = null;
    String str2 = getResourceKey(paramString, paramInt1, paramInt3, paramBoolean);
    if (str2 != null) {
      LocaleResources localeResources = LocaleProviderAdapter.forType(this.type).getLocaleResources(paramLocale);
      String[] arrayOfString = paramBoolean ? localeResources.getJavaTimeNames(str2) : localeResources.getCalendarNames(str2);
      if (arrayOfString != null && arrayOfString.length > 0) {
        if (paramInt1 == 7 || paramInt1 == 1)
          paramInt2--; 
        if (paramInt2 < 0 || paramInt2 >= arrayOfString.length)
          return null; 
        str1 = arrayOfString[paramInt2];
        if (str1.length() == 0 && (paramInt3 == 32769 || paramInt3 == 32770 || paramInt3 == 32772))
          str1 = getDisplayName(paramString, paramInt1, paramInt2, 
              getBaseStyle(paramInt3), paramLocale); 
      } 
    } 
    return str1;
  }
  
  private static int[] REST_OF_STYLES = new int[] { 32769, 2, 32770, 4, 32772 };
  
  public Map<String, Integer> getDisplayNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale) {
    Map<String, Integer> map;
    if (paramInt2 == 0) {
      map = getDisplayNamesImpl(paramString, paramInt1, 1, paramLocale, false);
      for (int i : REST_OF_STYLES)
        map.putAll(getDisplayNamesImpl(paramString, paramInt1, i, paramLocale, false)); 
    } else {
      map = getDisplayNamesImpl(paramString, paramInt1, paramInt2, paramLocale, false);
    } 
    return map.isEmpty() ? null : map;
  }
  
  public Map<String, Integer> getJavaTimeDisplayNames(String paramString, int paramInt1, int paramInt2, Locale paramLocale) {
    Map<String, Integer> map = getDisplayNamesImpl(paramString, paramInt1, paramInt2, paramLocale, true);
    return map.isEmpty() ? null : map;
  }
  
  private Map<String, Integer> getDisplayNamesImpl(String paramString, int paramInt1, int paramInt2, Locale paramLocale, boolean paramBoolean) {
    String str = getResourceKey(paramString, paramInt1, paramInt2, paramBoolean);
    TreeMap<Object, Object> treeMap = new TreeMap<>(LengthBasedComparator.INSTANCE);
    if (str != null) {
      LocaleResources localeResources = LocaleProviderAdapter.forType(this.type).getLocaleResources(paramLocale);
      String[] arrayOfString = paramBoolean ? localeResources.getJavaTimeNames(str) : localeResources.getCalendarNames(str);
      if (arrayOfString != null && 
        !hasDuplicates(arrayOfString))
        if (paramInt1 == 1) {
          if (arrayOfString.length > 0)
            treeMap.put(arrayOfString[0], Integer.valueOf(1)); 
        } else {
          byte b1 = (paramInt1 == 7) ? 1 : 0;
          for (byte b2 = 0; b2 < arrayOfString.length; b2++) {
            String str1 = arrayOfString[b2];
            if (str1.length() != 0)
              treeMap.put(str1, Integer.valueOf(b1 + b2)); 
          } 
        }  
    } 
    return (Map)treeMap;
  }
  
  private int getBaseStyle(int paramInt) {
    return paramInt & 0xFFFF7FFF;
  }
  
  private static class LengthBasedComparator implements Comparator<String> {
    private static final LengthBasedComparator INSTANCE = new LengthBasedComparator();
    
    public int compare(String param1String1, String param1String2) {
      int i = param1String2.length() - param1String1.length();
      return (i == 0) ? param1String1.compareTo(param1String2) : i;
    }
  }
  
  public Locale[] getAvailableLocales() {
    return LocaleProviderAdapter.toLocaleArray(this.langtags);
  }
  
  public boolean isSupportedLocale(Locale paramLocale) {
    if (Locale.ROOT.equals(paramLocale))
      return true; 
    String str = null;
    if (paramLocale.hasExtensions()) {
      str = paramLocale.getUnicodeLocaleType("ca");
      paramLocale = paramLocale.stripExtensions();
    } 
    if (str != null)
      switch (str) {
        case "buddhist":
        case "japanese":
        case "gregory":
        case "islamic":
        case "roc":
          break;
        default:
          return false;
      }  
    if (this.langtags.contains(paramLocale.toLanguageTag()))
      return true; 
    if (this.type == LocaleProviderAdapter.Type.JRE) {
      String str1 = paramLocale.toString().replace('_', '-');
      return this.langtags.contains(str1);
    } 
    return false;
  }
  
  public Set<String> getAvailableLanguageTags() {
    return this.langtags;
  }
  
  private boolean hasDuplicates(String[] paramArrayOfString) {
    int i = paramArrayOfString.length;
    for (byte b = 0; b < i - 1; b++) {
      String str = paramArrayOfString[b];
      if (str != null)
        for (int j = b + 1; j < i; j++) {
          if (str.equals(paramArrayOfString[j]))
            return true; 
        }  
    } 
    return false;
  }
  
  private String getResourceKey(String paramString, int paramInt1, int paramInt2, boolean paramBoolean) {
    int i = getBaseStyle(paramInt2);
    boolean bool1 = (paramInt2 != i) ? true : false;
    if ("gregory".equals(paramString))
      paramString = null; 
    boolean bool2 = (i == 4) ? true : false;
    StringBuilder stringBuilder = new StringBuilder();
    if (paramBoolean)
      stringBuilder.append("java.time."); 
    switch (paramInt1) {
      case 0:
        if (paramString != null)
          stringBuilder.append(paramString).append('.'); 
        if (bool2) {
          stringBuilder.append("narrow.");
        } else if (this.type == LocaleProviderAdapter.Type.JRE) {
          if (paramBoolean && 
            i == 2)
            stringBuilder.append("long."); 
          if (i == 1)
            stringBuilder.append("short."); 
        } else if (i == 2) {
          stringBuilder.append("long.");
        } 
        stringBuilder.append("Eras");
        break;
      case 1:
        if (!bool2)
          stringBuilder.append(paramString).append(".FirstYear"); 
        break;
      case 2:
        if ("islamic".equals(paramString))
          stringBuilder.append(paramString).append('.'); 
        if (bool1)
          stringBuilder.append("standalone."); 
        stringBuilder.append("Month").append(toStyleName(i));
        break;
      case 7:
        if (bool1 && bool2)
          stringBuilder.append("standalone."); 
        stringBuilder.append("Day").append(toStyleName(i));
        break;
      case 9:
        if (bool2)
          stringBuilder.append("narrow."); 
        stringBuilder.append("AmPmMarkers");
        break;
    } 
    return (stringBuilder.length() > 0) ? stringBuilder.toString() : null;
  }
  
  private String toStyleName(int paramInt) {
    switch (paramInt) {
      case 1:
        return "Abbreviations";
      case 4:
        return "Narrows";
    } 
    return "Names";
  }
}
