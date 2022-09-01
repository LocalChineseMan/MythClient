package java.util.spi;

import java.util.Locale;

public abstract class TimeZoneNameProvider extends LocaleServiceProvider {
  public abstract String getDisplayName(String paramString, boolean paramBoolean, int paramInt, Locale paramLocale);
  
  public String getGenericDisplayName(String paramString, int paramInt, Locale paramLocale) {
    return null;
  }
}
