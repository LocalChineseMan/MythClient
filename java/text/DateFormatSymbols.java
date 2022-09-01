package java.text;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.text.spi.DateFormatSymbolsProvider;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;
import sun.util.locale.provider.ResourceBundleBasedAdapter;
import sun.util.locale.provider.TimeZoneNameUtility;

public class DateFormatSymbols implements Serializable, Cloneable {
  String[] eras;
  
  String[] months;
  
  String[] shortMonths;
  
  String[] weekdays;
  
  String[] shortWeekdays;
  
  String[] ampms;
  
  String[][] zoneStrings;
  
  transient boolean isZoneStringsSet;
  
  static final String patternChars = "GyMdkHmsSEDFwWahKzZYuXL";
  
  static final int PATTERN_ERA = 0;
  
  static final int PATTERN_YEAR = 1;
  
  static final int PATTERN_MONTH = 2;
  
  static final int PATTERN_DAY_OF_MONTH = 3;
  
  static final int PATTERN_HOUR_OF_DAY1 = 4;
  
  static final int PATTERN_HOUR_OF_DAY0 = 5;
  
  static final int PATTERN_MINUTE = 6;
  
  static final int PATTERN_SECOND = 7;
  
  static final int PATTERN_MILLISECOND = 8;
  
  static final int PATTERN_DAY_OF_WEEK = 9;
  
  static final int PATTERN_DAY_OF_YEAR = 10;
  
  static final int PATTERN_DAY_OF_WEEK_IN_MONTH = 11;
  
  static final int PATTERN_WEEK_OF_YEAR = 12;
  
  static final int PATTERN_WEEK_OF_MONTH = 13;
  
  static final int PATTERN_AM_PM = 14;
  
  static final int PATTERN_HOUR1 = 15;
  
  static final int PATTERN_HOUR0 = 16;
  
  static final int PATTERN_ZONE_NAME = 17;
  
  static final int PATTERN_ZONE_VALUE = 18;
  
  static final int PATTERN_WEEK_YEAR = 19;
  
  static final int PATTERN_ISO_DAY_OF_WEEK = 20;
  
  static final int PATTERN_ISO_ZONE = 21;
  
  static final int PATTERN_MONTH_STANDALONE = 22;
  
  String localPatternChars;
  
  Locale locale;
  
  static final long serialVersionUID = -5987973545549424702L;
  
  static final int millisPerHour = 3600000;
  
  public DateFormatSymbols() {
    this.eras = null;
    this.months = null;
    this.shortMonths = null;
    this.weekdays = null;
    this.shortWeekdays = null;
    this.ampms = null;
    this.zoneStrings = (String[][])null;
    this.isZoneStringsSet = false;
    this.localPatternChars = null;
    this.locale = null;
    this.lastZoneIndex = 0;
    this.cachedHashCode = 0;
    initializeData(Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public DateFormatSymbols(Locale paramLocale) {
    this.eras = null;
    this.months = null;
    this.shortMonths = null;
    this.weekdays = null;
    this.shortWeekdays = null;
    this.ampms = null;
    this.zoneStrings = (String[][])null;
    this.isZoneStringsSet = false;
    this.localPatternChars = null;
    this.locale = null;
    this.lastZoneIndex = 0;
    this.cachedHashCode = 0;
    initializeData(paramLocale);
  }
  
  public static Locale[] getAvailableLocales() {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool((Class)DateFormatSymbolsProvider.class);
    return localeServiceProviderPool.getAvailableLocales();
  }
  
  public static final DateFormatSymbols getInstance() {
    return getInstance(Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public static final DateFormatSymbols getInstance(Locale paramLocale) {
    DateFormatSymbols dateFormatSymbols = getProviderInstance(paramLocale);
    if (dateFormatSymbols != null)
      return dateFormatSymbols; 
    throw new RuntimeException("DateFormatSymbols instance creation failed.");
  }
  
  static final DateFormatSymbols getInstanceRef(Locale paramLocale) {
    DateFormatSymbols dateFormatSymbols = getProviderInstance(paramLocale);
    if (dateFormatSymbols != null)
      return dateFormatSymbols; 
    throw new RuntimeException("DateFormatSymbols instance creation failed.");
  }
  
  private static DateFormatSymbols getProviderInstance(Locale paramLocale) {
    LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter((Class)DateFormatSymbolsProvider.class, paramLocale);
    DateFormatSymbolsProvider dateFormatSymbolsProvider = localeProviderAdapter.getDateFormatSymbolsProvider();
    DateFormatSymbols dateFormatSymbols = dateFormatSymbolsProvider.getInstance(paramLocale);
    if (dateFormatSymbols == null) {
      dateFormatSymbolsProvider = LocaleProviderAdapter.forJRE().getDateFormatSymbolsProvider();
      dateFormatSymbols = dateFormatSymbolsProvider.getInstance(paramLocale);
    } 
    return dateFormatSymbols;
  }
  
  public String[] getEras() {
    return Arrays.<String>copyOf(this.eras, this.eras.length);
  }
  
  public void setEras(String[] paramArrayOfString) {
    this.eras = Arrays.<String>copyOf(paramArrayOfString, paramArrayOfString.length);
    this.cachedHashCode = 0;
  }
  
  public String[] getMonths() {
    return Arrays.<String>copyOf(this.months, this.months.length);
  }
  
  public void setMonths(String[] paramArrayOfString) {
    this.months = Arrays.<String>copyOf(paramArrayOfString, paramArrayOfString.length);
    this.cachedHashCode = 0;
  }
  
  public String[] getShortMonths() {
    return Arrays.<String>copyOf(this.shortMonths, this.shortMonths.length);
  }
  
  public void setShortMonths(String[] paramArrayOfString) {
    this.shortMonths = Arrays.<String>copyOf(paramArrayOfString, paramArrayOfString.length);
    this.cachedHashCode = 0;
  }
  
  public String[] getWeekdays() {
    return Arrays.<String>copyOf(this.weekdays, this.weekdays.length);
  }
  
  public void setWeekdays(String[] paramArrayOfString) {
    this.weekdays = Arrays.<String>copyOf(paramArrayOfString, paramArrayOfString.length);
    this.cachedHashCode = 0;
  }
  
  public String[] getShortWeekdays() {
    return Arrays.<String>copyOf(this.shortWeekdays, this.shortWeekdays.length);
  }
  
  public void setShortWeekdays(String[] paramArrayOfString) {
    this.shortWeekdays = Arrays.<String>copyOf(paramArrayOfString, paramArrayOfString.length);
    this.cachedHashCode = 0;
  }
  
  public String[] getAmPmStrings() {
    return Arrays.<String>copyOf(this.ampms, this.ampms.length);
  }
  
  public void setAmPmStrings(String[] paramArrayOfString) {
    this.ampms = Arrays.<String>copyOf(paramArrayOfString, paramArrayOfString.length);
    this.cachedHashCode = 0;
  }
  
  public String[][] getZoneStrings() {
    return getZoneStringsImpl(true);
  }
  
  public void setZoneStrings(String[][] paramArrayOfString) {
    String[][] arrayOfString = new String[paramArrayOfString.length][];
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      int i = (paramArrayOfString[b]).length;
      if (i < 5)
        throw new IllegalArgumentException(); 
      arrayOfString[b] = Arrays.<String>copyOf(paramArrayOfString[b], i);
    } 
    this.zoneStrings = arrayOfString;
    this.isZoneStringsSet = true;
    this.cachedHashCode = 0;
  }
  
  public String getLocalPatternChars() {
    return this.localPatternChars;
  }
  
  public void setLocalPatternChars(String paramString) {
    this.localPatternChars = paramString.toString();
    this.cachedHashCode = 0;
  }
  
  public Object clone() {
    try {
      DateFormatSymbols dateFormatSymbols = (DateFormatSymbols)super.clone();
      copyMembers(this, dateFormatSymbols);
      return dateFormatSymbols;
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public int hashCode() {
    int i = this.cachedHashCode;
    if (i == 0) {
      i = 5;
      i = 11 * i + Arrays.hashCode((Object[])this.eras);
      i = 11 * i + Arrays.hashCode((Object[])this.months);
      i = 11 * i + Arrays.hashCode((Object[])this.shortMonths);
      i = 11 * i + Arrays.hashCode((Object[])this.weekdays);
      i = 11 * i + Arrays.hashCode((Object[])this.shortWeekdays);
      i = 11 * i + Arrays.hashCode((Object[])this.ampms);
      i = 11 * i + Arrays.deepHashCode((Object[])getZoneStringsWrapper());
      i = 11 * i + Objects.hashCode(this.localPatternChars);
      this.cachedHashCode = i;
    } 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null || getClass() != paramObject.getClass())
      return false; 
    DateFormatSymbols dateFormatSymbols = (DateFormatSymbols)paramObject;
    return (Arrays.equals((Object[])this.eras, (Object[])dateFormatSymbols.eras) && Arrays.equals((Object[])this.months, (Object[])dateFormatSymbols.months) && Arrays.equals((Object[])this.shortMonths, (Object[])dateFormatSymbols.shortMonths) && Arrays.equals((Object[])this.weekdays, (Object[])dateFormatSymbols.weekdays) && Arrays.equals((Object[])this.shortWeekdays, (Object[])dateFormatSymbols.shortWeekdays) && Arrays.equals((Object[])this.ampms, (Object[])dateFormatSymbols.ampms) && Arrays.deepEquals((Object[])getZoneStringsWrapper(), (Object[])dateFormatSymbols.getZoneStringsWrapper()) && ((this.localPatternChars != null && this.localPatternChars.equals(dateFormatSymbols.localPatternChars)) || (this.localPatternChars == null && dateFormatSymbols.localPatternChars == null)));
  }
  
  private static final ConcurrentMap<Locale, SoftReference<DateFormatSymbols>> cachedInstances = new ConcurrentHashMap<>(3);
  
  private transient int lastZoneIndex;
  
  volatile transient int cachedHashCode;
  
  private void initializeData(Locale paramLocale) {
    this.locale = paramLocale;
    SoftReference<DateFormatSymbols> softReference1 = cachedInstances.get(this.locale);
    DateFormatSymbols dateFormatSymbols;
    if (softReference1 != null && (dateFormatSymbols = softReference1.get()) != null) {
      copyMembers(dateFormatSymbols, this);
      return;
    } 
    LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter((Class)DateFormatSymbolsProvider.class, this.locale);
    if (!(localeProviderAdapter instanceof ResourceBundleBasedAdapter))
      localeProviderAdapter = LocaleProviderAdapter.getResourceBundleBased(); 
    ResourceBundle resourceBundle = ((ResourceBundleBasedAdapter)localeProviderAdapter).getLocaleData().getDateFormatData(this.locale);
    if (resourceBundle.containsKey("Eras")) {
      this.eras = resourceBundle.getStringArray("Eras");
    } else if (resourceBundle.containsKey("long.Eras")) {
      this.eras = resourceBundle.getStringArray("long.Eras");
    } else if (resourceBundle.containsKey("short.Eras")) {
      this.eras = resourceBundle.getStringArray("short.Eras");
    } 
    this.months = resourceBundle.getStringArray("MonthNames");
    this.shortMonths = resourceBundle.getStringArray("MonthAbbreviations");
    this.ampms = resourceBundle.getStringArray("AmPmMarkers");
    this.localPatternChars = resourceBundle.getString("DateTimePatternChars");
    this.weekdays = toOneBasedArray(resourceBundle.getStringArray("DayNames"));
    this.shortWeekdays = toOneBasedArray(resourceBundle.getStringArray("DayAbbreviations"));
    softReference1 = new SoftReference<>((DateFormatSymbols)clone());
    SoftReference<DateFormatSymbols> softReference2 = cachedInstances.putIfAbsent(this.locale, softReference1);
    if (softReference2 != null) {
      DateFormatSymbols dateFormatSymbols1 = softReference2.get();
      if (dateFormatSymbols1 == null)
        cachedInstances.put(this.locale, softReference1); 
    } 
  }
  
  private static String[] toOneBasedArray(String[] paramArrayOfString) {
    int i = paramArrayOfString.length;
    String[] arrayOfString = new String[i + 1];
    arrayOfString[0] = "";
    for (byte b = 0; b < i; b++)
      arrayOfString[b + 1] = paramArrayOfString[b]; 
    return arrayOfString;
  }
  
  final int getZoneIndex(String paramString) {
    String[][] arrayOfString = getZoneStringsWrapper();
    if (this.lastZoneIndex < arrayOfString.length && paramString.equals(arrayOfString[this.lastZoneIndex][0]))
      return this.lastZoneIndex; 
    for (byte b = 0; b < arrayOfString.length; b++) {
      if (paramString.equals(arrayOfString[b][0])) {
        this.lastZoneIndex = b;
        return b;
      } 
    } 
    return -1;
  }
  
  final String[][] getZoneStringsWrapper() {
    if (isSubclassObject())
      return getZoneStrings(); 
    return getZoneStringsImpl(false);
  }
  
  private String[][] getZoneStringsImpl(boolean paramBoolean) {
    if (this.zoneStrings == null)
      this.zoneStrings = TimeZoneNameUtility.getZoneStrings(this.locale); 
    if (!paramBoolean)
      return this.zoneStrings; 
    int i = this.zoneStrings.length;
    String[][] arrayOfString = new String[i][];
    for (byte b = 0; b < i; b++)
      arrayOfString[b] = Arrays.<String>copyOf(this.zoneStrings[b], (this.zoneStrings[b]).length); 
    return arrayOfString;
  }
  
  private boolean isSubclassObject() {
    return !getClass().getName().equals("java.text.DateFormatSymbols");
  }
  
  private void copyMembers(DateFormatSymbols paramDateFormatSymbols1, DateFormatSymbols paramDateFormatSymbols2) {
    paramDateFormatSymbols2.eras = Arrays.<String>copyOf(paramDateFormatSymbols1.eras, paramDateFormatSymbols1.eras.length);
    paramDateFormatSymbols2.months = Arrays.<String>copyOf(paramDateFormatSymbols1.months, paramDateFormatSymbols1.months.length);
    paramDateFormatSymbols2.shortMonths = Arrays.<String>copyOf(paramDateFormatSymbols1.shortMonths, paramDateFormatSymbols1.shortMonths.length);
    paramDateFormatSymbols2.weekdays = Arrays.<String>copyOf(paramDateFormatSymbols1.weekdays, paramDateFormatSymbols1.weekdays.length);
    paramDateFormatSymbols2.shortWeekdays = Arrays.<String>copyOf(paramDateFormatSymbols1.shortWeekdays, paramDateFormatSymbols1.shortWeekdays.length);
    paramDateFormatSymbols2.ampms = Arrays.<String>copyOf(paramDateFormatSymbols1.ampms, paramDateFormatSymbols1.ampms.length);
    if (paramDateFormatSymbols1.zoneStrings != null) {
      paramDateFormatSymbols2.zoneStrings = paramDateFormatSymbols1.getZoneStringsImpl(true);
    } else {
      paramDateFormatSymbols2.zoneStrings = (String[][])null;
    } 
    paramDateFormatSymbols2.localPatternChars = paramDateFormatSymbols1.localPatternChars;
    paramDateFormatSymbols2.cachedHashCode = 0;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.zoneStrings == null)
      this.zoneStrings = TimeZoneNameUtility.getZoneStrings(this.locale); 
    paramObjectOutputStream.defaultWriteObject();
  }
}
