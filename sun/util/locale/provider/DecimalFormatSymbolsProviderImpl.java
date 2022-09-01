package sun.util.locale.provider;

import java.text.DecimalFormatSymbols;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.util.Locale;
import java.util.Set;

public class DecimalFormatSymbolsProviderImpl extends DecimalFormatSymbolsProvider implements AvailableLanguageTags {
  private final LocaleProviderAdapter.Type type;
  
  private final Set<String> langtags;
  
  public DecimalFormatSymbolsProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet) {
    this.type = paramType;
    this.langtags = paramSet;
  }
  
  public Locale[] getAvailableLocales() {
    return LocaleProviderAdapter.toLocaleArray(this.langtags);
  }
  
  public boolean isSupportedLocale(Locale paramLocale) {
    return LocaleProviderAdapter.isSupportedLocale(paramLocale, this.type, this.langtags);
  }
  
  public DecimalFormatSymbols getInstance(Locale paramLocale) {
    if (paramLocale == null)
      throw new NullPointerException(); 
    return new DecimalFormatSymbols(paramLocale);
  }
  
  public Set<String> getAvailableLanguageTags() {
    return this.langtags;
  }
}
