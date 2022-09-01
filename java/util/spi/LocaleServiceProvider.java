package java.util.spi;

import java.util.Locale;

public abstract class LocaleServiceProvider {
  public abstract Locale[] getAvailableLocales();
  
  public boolean isSupportedLocale(Locale paramLocale) {
    paramLocale = paramLocale.stripExtensions();
    for (Locale locale : getAvailableLocales()) {
      if (paramLocale.equals(locale.stripExtensions()))
        return true; 
    } 
    return false;
  }
}
