package sun.util.locale.provider;

import java.util.Locale;
import java.util.Set;
import java.util.spi.TimeZoneNameProvider;

public class TimeZoneNameProviderImpl extends TimeZoneNameProvider {
  private final LocaleProviderAdapter.Type type;
  
  private final Set<String> langtags;
  
  TimeZoneNameProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet) {
    this.type = paramType;
    this.langtags = paramSet;
  }
  
  public Locale[] getAvailableLocales() {
    return LocaleProviderAdapter.toLocaleArray(this.langtags);
  }
  
  public boolean isSupportedLocale(Locale paramLocale) {
    return LocaleProviderAdapter.isSupportedLocale(paramLocale, this.type, this.langtags);
  }
  
  public String getDisplayName(String paramString, boolean paramBoolean, int paramInt, Locale paramLocale) {
    String[] arrayOfString = getDisplayNameArray(paramString, 5, paramLocale);
    if (arrayOfString != null) {
      byte b = paramBoolean ? 3 : 1;
      if (paramInt == 0)
        b++; 
      return arrayOfString[b];
    } 
    return null;
  }
  
  public String getGenericDisplayName(String paramString, int paramInt, Locale paramLocale) {
    String[] arrayOfString = getDisplayNameArray(paramString, 7, paramLocale);
    if (arrayOfString != null && arrayOfString.length >= 7)
      return arrayOfString[(paramInt == 1) ? 5 : 6]; 
    return null;
  }
  
  private String[] getDisplayNameArray(String paramString, int paramInt, Locale paramLocale) {
    if (paramString == null || paramLocale == null)
      throw new NullPointerException(); 
    return LocaleProviderAdapter.forType(this.type).getLocaleResources(paramLocale).getTimeZoneNames(paramString, paramInt);
  }
  
  String[][] getZoneStrings(Locale paramLocale) {
    return LocaleProviderAdapter.forType(this.type).getLocaleResources(paramLocale).getZoneStrings();
  }
}
