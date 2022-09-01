package sun.util.locale.provider;

import java.text.spi.BreakIteratorProvider;
import java.text.spi.CollatorProvider;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CalendarNameProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.util.spi.CalendarProvider;

public abstract class AuxLocaleProviderAdapter extends LocaleProviderAdapter {
  private ConcurrentMap<Class<? extends LocaleServiceProvider>, LocaleServiceProvider> providersMap = new ConcurrentHashMap<>();
  
  public <P extends LocaleServiceProvider> P getLocaleServiceProvider(Class<P> paramClass) {
    LocaleServiceProvider localeServiceProvider = this.providersMap.get(paramClass);
    if (localeServiceProvider == null) {
      localeServiceProvider = findInstalledProvider(paramClass);
      this.providersMap.putIfAbsent(paramClass, (localeServiceProvider == null) ? NULL_PROVIDER : localeServiceProvider);
    } 
    return (P)localeServiceProvider;
  }
  
  protected abstract <P extends LocaleServiceProvider> P findInstalledProvider(Class<P> paramClass);
  
  public BreakIteratorProvider getBreakIteratorProvider() {
    return getLocaleServiceProvider(BreakIteratorProvider.class);
  }
  
  public CollatorProvider getCollatorProvider() {
    return getLocaleServiceProvider(CollatorProvider.class);
  }
  
  public DateFormatProvider getDateFormatProvider() {
    return getLocaleServiceProvider(DateFormatProvider.class);
  }
  
  public DateFormatSymbolsProvider getDateFormatSymbolsProvider() {
    return getLocaleServiceProvider(DateFormatSymbolsProvider.class);
  }
  
  public DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider() {
    return getLocaleServiceProvider(DecimalFormatSymbolsProvider.class);
  }
  
  public NumberFormatProvider getNumberFormatProvider() {
    return getLocaleServiceProvider(NumberFormatProvider.class);
  }
  
  public CurrencyNameProvider getCurrencyNameProvider() {
    return getLocaleServiceProvider(CurrencyNameProvider.class);
  }
  
  public LocaleNameProvider getLocaleNameProvider() {
    return getLocaleServiceProvider(LocaleNameProvider.class);
  }
  
  public TimeZoneNameProvider getTimeZoneNameProvider() {
    return getLocaleServiceProvider(TimeZoneNameProvider.class);
  }
  
  public CalendarDataProvider getCalendarDataProvider() {
    return getLocaleServiceProvider(CalendarDataProvider.class);
  }
  
  public CalendarNameProvider getCalendarNameProvider() {
    return getLocaleServiceProvider(CalendarNameProvider.class);
  }
  
  public CalendarProvider getCalendarProvider() {
    return getLocaleServiceProvider(CalendarProvider.class);
  }
  
  public LocaleResources getLocaleResources(Locale paramLocale) {
    return null;
  }
  
  private static Locale[] availableLocales = null;
  
  public Locale[] getAvailableLocales() {
    if (availableLocales == null) {
      HashSet hashSet = new HashSet();
      for (Class<LocaleServiceProvider> clazz : LocaleServiceProviderPool.spiClasses) {
        LocaleServiceProvider localeServiceProvider = (LocaleServiceProvider)getLocaleServiceProvider((Class)clazz);
        if (localeServiceProvider != null)
          hashSet.addAll(Arrays.asList(localeServiceProvider.getAvailableLocales())); 
      } 
      availableLocales = (Locale[])hashSet.toArray((Object[])new Locale[0]);
    } 
    return availableLocales;
  }
  
  private static NullProvider NULL_PROVIDER = new NullProvider();
  
  private static class NullProvider extends LocaleServiceProvider {
    private NullProvider() {}
    
    public Locale[] getAvailableLocales() {
      return new Locale[0];
    }
  }
}
