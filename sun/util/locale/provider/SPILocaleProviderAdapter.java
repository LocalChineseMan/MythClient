package sun.util.locale.provider;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.spi.LocaleServiceProvider;

public class SPILocaleProviderAdapter extends AuxLocaleProviderAdapter {
  public LocaleProviderAdapter.Type getAdapterType() {
    return LocaleProviderAdapter.Type.SPI;
  }
  
  protected <P extends LocaleServiceProvider> P findInstalledProvider(final Class<P> c) {
    try {
      return (P)AccessController.<LocaleServiceProvider>doPrivileged(new PrivilegedExceptionAction<P>() {
            public P run() {
              LocaleServiceProvider localeServiceProvider = null;
              for (LocaleServiceProvider localeServiceProvider1 : ServiceLoader.loadInstalled(c)) {
                if (localeServiceProvider == null)
                  try {
                    localeServiceProvider = (LocaleServiceProvider)Class.forName(SPILocaleProviderAdapter.class.getCanonicalName() + "$" + c.getSimpleName() + "Delegate").newInstance();
                  } catch (ClassNotFoundException|InstantiationException|IllegalAccessException classNotFoundException) {
                    LocaleServiceProviderPool.config((Class)SPILocaleProviderAdapter.class, classNotFoundException.toString());
                    return null;
                  }  
                ((SPILocaleProviderAdapter.Delegate<LocaleServiceProvider>)localeServiceProvider).addImpl(localeServiceProvider1);
              } 
              return (P)localeServiceProvider;
            }
          });
    } catch (PrivilegedActionException privilegedActionException) {
      LocaleServiceProviderPool.config((Class)SPILocaleProviderAdapter.class, privilegedActionException.toString());
      return null;
    } 
  }
  
  static class SPILocaleProviderAdapter {}
  
  static class SPILocaleProviderAdapter {}
  
  static class SPILocaleProviderAdapter {}
  
  static class SPILocaleProviderAdapter {}
  
  static class SPILocaleProviderAdapter {}
  
  static class SPILocaleProviderAdapter {}
  
  static class SPILocaleProviderAdapter {}
  
  static class SPILocaleProviderAdapter {}
  
  static class SPILocaleProviderAdapter {}
  
  static class SPILocaleProviderAdapter {}
  
  static class SPILocaleProviderAdapter {}
  
  static interface SPILocaleProviderAdapter {}
  
  private static <P extends LocaleServiceProvider> P getImpl(Map<Locale, P> paramMap, Locale paramLocale) {
    for (Locale locale : LocaleServiceProviderPool.getLookupLocales(paramLocale)) {
      LocaleServiceProvider localeServiceProvider = (LocaleServiceProvider)paramMap.get(locale);
      if (localeServiceProvider != null)
        return (P)localeServiceProvider; 
    } 
    return null;
  }
}
