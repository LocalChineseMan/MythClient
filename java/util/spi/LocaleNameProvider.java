package java.util.spi;

import java.util.Locale;

public abstract class LocaleNameProvider extends LocaleServiceProvider {
  public abstract String getDisplayLanguage(String paramString, Locale paramLocale);
  
  public String getDisplayScript(String paramString, Locale paramLocale) {
    return null;
  }
  
  public abstract String getDisplayCountry(String paramString, Locale paramLocale);
  
  public abstract String getDisplayVariant(String paramString, Locale paramLocale);
}
