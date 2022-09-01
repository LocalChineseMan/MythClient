package sun.util.resources;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import sun.util.locale.provider.LocaleDataMetaInfo;
import sun.util.locale.provider.LocaleProviderAdapter;

public class LocaleData {
  private final LocaleProviderAdapter.Type type;
  
  public LocaleData(LocaleProviderAdapter.Type paramType) {
    this.type = paramType;
  }
  
  public ResourceBundle getCalendarData(Locale paramLocale) {
    return getBundle(this.type.getUtilResourcesPackage() + ".CalendarData", paramLocale);
  }
  
  public OpenListResourceBundle getCurrencyNames(Locale paramLocale) {
    return (OpenListResourceBundle)getBundle(this.type.getUtilResourcesPackage() + ".CurrencyNames", paramLocale);
  }
  
  public OpenListResourceBundle getLocaleNames(Locale paramLocale) {
    return (OpenListResourceBundle)getBundle(this.type.getUtilResourcesPackage() + ".LocaleNames", paramLocale);
  }
  
  public TimeZoneNamesBundle getTimeZoneNames(Locale paramLocale) {
    return (TimeZoneNamesBundle)getBundle(this.type.getUtilResourcesPackage() + ".TimeZoneNames", paramLocale);
  }
  
  public ResourceBundle getBreakIteratorInfo(Locale paramLocale) {
    return getBundle(this.type.getTextResourcesPackage() + ".BreakIteratorInfo", paramLocale);
  }
  
  public ResourceBundle getCollationData(Locale paramLocale) {
    return getBundle(this.type.getTextResourcesPackage() + ".CollationData", paramLocale);
  }
  
  public ResourceBundle getDateFormatData(Locale paramLocale) {
    return getBundle(this.type.getTextResourcesPackage() + ".FormatData", paramLocale);
  }
  
  public void setSupplementary(ParallelListResourceBundle paramParallelListResourceBundle) {
    if (!paramParallelListResourceBundle.areParallelContentsComplete()) {
      String str = this.type.getTextResourcesPackage() + ".JavaTimeSupplementary";
      setSupplementary(str, paramParallelListResourceBundle);
    } 
  }
  
  private boolean setSupplementary(String paramString, ParallelListResourceBundle paramParallelListResourceBundle) {
    ParallelListResourceBundle parallelListResourceBundle = (ParallelListResourceBundle)paramParallelListResourceBundle.getParent();
    boolean bool = false;
    if (parallelListResourceBundle != null)
      bool = setSupplementary(paramString, parallelListResourceBundle); 
    OpenListResourceBundle openListResourceBundle = getSupplementary(paramString, paramParallelListResourceBundle.getLocale());
    paramParallelListResourceBundle.setParallelContents(openListResourceBundle);
    int i = bool | ((openListResourceBundle != null) ? 1 : 0);
    if (i != 0)
      paramParallelListResourceBundle.resetKeySet(); 
    return i;
  }
  
  public ResourceBundle getNumberFormatData(Locale paramLocale) {
    return getBundle(this.type.getTextResourcesPackage() + ".FormatData", paramLocale);
  }
  
  public static ResourceBundle getBundle(final String baseName, final Locale locale) {
    return AccessController.<ResourceBundle>doPrivileged(new PrivilegedAction<ResourceBundle>() {
          public ResourceBundle run() {
            return ResourceBundle.getBundle(baseName, locale, LocaleData.LocaleDataResourceBundleControl.INSTANCE);
          }
        });
  }
  
  private static OpenListResourceBundle getSupplementary(String paramString, Locale paramLocale) {
    return AccessController.<OpenListResourceBundle>doPrivileged((PrivilegedAction<OpenListResourceBundle>)new Object(paramString, paramLocale));
  }
  
  private static class LocaleDataResourceBundleControl extends ResourceBundle.Control {
    private static final LocaleDataResourceBundleControl INSTANCE = new LocaleDataResourceBundleControl();
    
    private static final String CLDR = ".cldr";
    
    private LocaleDataResourceBundleControl() {}
    
    public List<Locale> getCandidateLocales(String param1String, Locale param1Locale) {
      List<Locale> list = super.getCandidateLocales(param1String, param1Locale);
      String str = LocaleDataMetaInfo.getSupportedLocaleString(param1String);
      if (str != null && str.length() != 0)
        for (Iterator<Locale> iterator = list.iterator(); iterator.hasNext(); ) {
          String str1;
          Locale locale = iterator.next();
          if (locale.getScript().length() > 0) {
            str1 = locale.toLanguageTag().replace('-', '_');
          } else {
            str1 = locale.toString();
            int i = str1.indexOf("_#");
            if (i >= 0)
              str1 = str1.substring(0, i); 
          } 
          if (str1.length() != 0 && str.indexOf(" " + str1 + " ") == -1)
            iterator.remove(); 
        }  
      if (param1Locale.getLanguage() != "en" && param1String
        .contains(".cldr") && param1String.endsWith("TimeZoneNames"))
        list.add(list.size() - 1, Locale.ENGLISH); 
      return list;
    }
    
    public Locale getFallbackLocale(String param1String, Locale param1Locale) {
      if (param1String == null || param1Locale == null)
        throw new NullPointerException(); 
      return null;
    }
    
    public String toBundleName(String param1String, Locale param1Locale) {
      String str1 = param1String;
      String str2 = param1Locale.getLanguage();
      if (str2.length() > 0 && (
        param1String.startsWith(LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage()) || param1String
        .startsWith(LocaleProviderAdapter.Type.JRE.getTextResourcesPackage()))) {
        assert LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage().length() == LocaleProviderAdapter.Type.JRE
          .getTextResourcesPackage().length();
        int i = LocaleProviderAdapter.Type.JRE.getUtilResourcesPackage().length();
        if (param1String.indexOf(".cldr", i) > 0)
          i += ".cldr".length(); 
        str1 = param1String.substring(0, i + 1) + str2 + param1String.substring(i);
      } 
      return super.toBundleName(str1, param1Locale);
    }
  }
  
  private static class LocaleData {}
}
