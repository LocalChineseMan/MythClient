package sun.util.locale.provider;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.spi.LocaleServiceProvider;
import java.util.spi.TimeZoneNameProvider;
import sun.util.calendar.ZoneInfo;

public final class TimeZoneNameUtility {
  private static ConcurrentHashMap<Locale, SoftReference<String[][]>> cachedZoneData = new ConcurrentHashMap<>();
  
  private static final Map<String, SoftReference<Map<Locale, String[]>>> cachedDisplayNames = new ConcurrentHashMap<>();
  
  public static String[][] getZoneStrings(Locale paramLocale) {
    SoftReference<String[][]> softReference = cachedZoneData.get(paramLocale);
    String[][] arrayOfString;
    if (softReference == null || (arrayOfString = softReference.get()) == null) {
      arrayOfString = loadZoneStrings(paramLocale);
      softReference = (SoftReference)new SoftReference<>(arrayOfString);
      cachedZoneData.put(paramLocale, softReference);
    } 
    return arrayOfString;
  }
  
  private static String[][] loadZoneStrings(Locale paramLocale) {
    LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter((Class)TimeZoneNameProvider.class, paramLocale);
    TimeZoneNameProvider timeZoneNameProvider = localeProviderAdapter.getTimeZoneNameProvider();
    if (timeZoneNameProvider instanceof TimeZoneNameProviderImpl)
      return ((TimeZoneNameProviderImpl)timeZoneNameProvider).getZoneStrings(paramLocale); 
    Set<String> set = LocaleProviderAdapter.forJRE().getLocaleResources(paramLocale).getZoneIDs();
    LinkedList<String[]> linkedList = new LinkedList();
    for (String str : set) {
      String[] arrayOfString1 = retrieveDisplayNamesImpl(str, paramLocale);
      if (arrayOfString1 != null)
        linkedList.add(arrayOfString1); 
    } 
    String[][] arrayOfString = new String[linkedList.size()][];
    return linkedList.<String[]>toArray(arrayOfString);
  }
  
  public static String[] retrieveDisplayNames(String paramString, Locale paramLocale) {
    if (paramString == null || paramLocale == null)
      throw new NullPointerException(); 
    return retrieveDisplayNamesImpl(paramString, paramLocale);
  }
  
  public static String retrieveGenericDisplayName(String paramString, int paramInt, Locale paramLocale) {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool((Class)TimeZoneNameProvider.class);
    return localeServiceProviderPool.<LocaleServiceProvider, String>getLocalizedObject(TimeZoneNameGetter.INSTANCE, paramLocale, "generic", new Object[] { Integer.valueOf(paramInt), paramString });
  }
  
  public static String retrieveDisplayName(String paramString, boolean paramBoolean, int paramInt, Locale paramLocale) {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool((Class)TimeZoneNameProvider.class);
    return localeServiceProviderPool.<LocaleServiceProvider, String>getLocalizedObject(TimeZoneNameGetter.INSTANCE, paramLocale, paramBoolean ? "dst" : "std", new Object[] { Integer.valueOf(paramInt), paramString });
  }
  
  private static String[] retrieveDisplayNamesImpl(String paramString, Locale paramLocale) {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool((Class)TimeZoneNameProvider.class);
    SoftReference<Map> softReference = (SoftReference)cachedDisplayNames.get(paramString);
    if (softReference != null) {
      Map<Locale, String[]> map = softReference.get();
      if (map != null) {
        String[] arrayOfString1 = (String[])map.get(paramLocale);
        if (arrayOfString1 != null)
          return arrayOfString1; 
        arrayOfString1 = localeServiceProviderPool.<LocaleServiceProvider, String[]>getLocalizedObject((LocaleServiceProviderPool.LocalizedObjectGetter<LocaleServiceProvider, String>)TimeZoneNameArrayGetter.access$100(), paramLocale, paramString, new Object[0]);
        if (arrayOfString1 != null)
          map.put(paramLocale, arrayOfString1); 
        return arrayOfString1;
      } 
    } 
    String[] arrayOfString = localeServiceProviderPool.<LocaleServiceProvider, String[]>getLocalizedObject((LocaleServiceProviderPool.LocalizedObjectGetter<LocaleServiceProvider, String>)TimeZoneNameArrayGetter.access$100(), paramLocale, paramString, new Object[0]);
    if (arrayOfString != null) {
      ConcurrentHashMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();
      concurrentHashMap.put(paramLocale, arrayOfString);
      softReference = new SoftReference<>(concurrentHashMap);
      cachedDisplayNames.put(paramString, softReference);
    } 
    return arrayOfString;
  }
  
  private static class TimeZoneNameGetter implements LocaleServiceProviderPool.LocalizedObjectGetter<TimeZoneNameProvider, String> {
    private static final TimeZoneNameGetter INSTANCE = new TimeZoneNameGetter();
    
    public String getObject(TimeZoneNameProvider param1TimeZoneNameProvider, Locale param1Locale, String param1String, Object... param1VarArgs) {
      assert param1VarArgs.length == 2;
      int i = ((Integer)param1VarArgs[0]).intValue();
      String str1 = (String)param1VarArgs[1];
      String str2 = getName(param1TimeZoneNameProvider, param1Locale, param1String, i, str1);
      if (str2 == null) {
        Map<String, String> map = ZoneInfo.getAliasTable();
        if (map != null) {
          String str = map.get(str1);
          if (str != null)
            str2 = getName(param1TimeZoneNameProvider, param1Locale, param1String, i, str); 
          if (str2 == null)
            str2 = examineAliases(param1TimeZoneNameProvider, param1Locale, param1String, (str != null) ? str : str1, i, map); 
        } 
      } 
      return str2;
    }
    
    private static String examineAliases(TimeZoneNameProvider param1TimeZoneNameProvider, Locale param1Locale, String param1String1, String param1String2, int param1Int, Map<String, String> param1Map) {
      if (param1Map.containsValue(param1String2))
        for (Map.Entry<String, String> entry : param1Map.entrySet()) {
          if (((String)entry.getValue()).equals(param1String2)) {
            String str1 = (String)entry.getKey();
            String str2 = getName(param1TimeZoneNameProvider, param1Locale, param1String1, param1Int, str1);
            if (str2 != null)
              return str2; 
            str2 = examineAliases(param1TimeZoneNameProvider, param1Locale, param1String1, str1, param1Int, param1Map);
            if (str2 != null)
              return str2; 
          } 
        }  
      return null;
    }
    
    private static String getName(TimeZoneNameProvider param1TimeZoneNameProvider, Locale param1Locale, String param1String1, int param1Int, String param1String2) {
      String str = null;
      switch (param1String1) {
        case "std":
          str = param1TimeZoneNameProvider.getDisplayName(param1String2, false, param1Int, param1Locale);
          break;
        case "dst":
          str = param1TimeZoneNameProvider.getDisplayName(param1String2, true, param1Int, param1Locale);
          break;
        case "generic":
          str = param1TimeZoneNameProvider.getGenericDisplayName(param1String2, param1Int, param1Locale);
          break;
      } 
      return str;
    }
  }
  
  private static class TimeZoneNameUtility {}
}
