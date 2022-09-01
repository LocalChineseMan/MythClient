package java.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.CalendarDate;
import sun.util.calendar.CalendarSystem;
import sun.util.calendar.CalendarUtils;
import sun.util.calendar.Era;
import sun.util.calendar.Gregorian;
import sun.util.calendar.JulianCalendar;
import sun.util.calendar.ZoneInfo;

public class GregorianCalendar extends Calendar {
  public static final int BC = 0;
  
  static final int BCE = 0;
  
  public static final int AD = 1;
  
  static final int CE = 1;
  
  private static final int EPOCH_OFFSET = 719163;
  
  private static final int EPOCH_YEAR = 1970;
  
  static final int[] MONTH_LENGTH = new int[] { 
      31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 
      30, 31 };
  
  static final int[] LEAP_MONTH_LENGTH = new int[] { 
      31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 
      30, 31 };
  
  private static final int ONE_SECOND = 1000;
  
  private static final int ONE_MINUTE = 60000;
  
  private static final int ONE_HOUR = 3600000;
  
  private static final long ONE_DAY = 86400000L;
  
  private static final long ONE_WEEK = 604800000L;
  
  static final int[] MIN_VALUES = new int[] { 
      0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 
      0, 0, 0, 0, 0, -46800000, 0 };
  
  static final int[] LEAST_MAX_VALUES = new int[] { 
      1, 292269054, 11, 52, 4, 28, 365, 7, 4, 1, 
      11, 23, 59, 59, 999, 50400000, 1200000 };
  
  static final int[] MAX_VALUES = new int[] { 
      1, 292278994, 11, 53, 6, 31, 366, 7, 6, 1, 
      11, 23, 59, 59, 999, 50400000, 7200000 };
  
  static final long serialVersionUID = -8125100834729963327L;
  
  private static final Gregorian gcal = CalendarSystem.getGregorianCalendar();
  
  private static JulianCalendar jcal;
  
  private static Era[] jeras;
  
  static final long DEFAULT_GREGORIAN_CUTOVER = -12219292800000L;
  
  private long gregorianCutover = -12219292800000L;
  
  private transient long gregorianCutoverDate = 577736L;
  
  private transient int gregorianCutoverYear = 1582;
  
  private transient int gregorianCutoverYearJulian = 1582;
  
  private transient BaseCalendar.Date gdate;
  
  private transient BaseCalendar.Date cdate;
  
  private transient BaseCalendar calsys;
  
  private transient int[] zoneOffsets;
  
  private transient int[] originalFields;
  
  private transient long cachedFixedDate;
  
  public GregorianCalendar() {
    this(TimeZone.getDefaultRef(), Locale.getDefault(Locale.Category.FORMAT));
    setZoneShared(true);
  }
  
  public GregorianCalendar(TimeZone paramTimeZone) {
    this(paramTimeZone, Locale.getDefault(Locale.Category.FORMAT));
  }
  
  public GregorianCalendar(Locale paramLocale) {
    this(TimeZone.getDefaultRef(), paramLocale);
    setZoneShared(true);
  }
  
  public GregorianCalendar(TimeZone paramTimeZone, Locale paramLocale) {
    super(paramTimeZone, paramLocale);
    this.cachedFixedDate = Long.MIN_VALUE;
    this.gdate = gcal.newCalendarDate(paramTimeZone);
    setTimeInMillis(System.currentTimeMillis());
  }
  
  public GregorianCalendar(int paramInt1, int paramInt2, int paramInt3) {
    this(paramInt1, paramInt2, paramInt3, 0, 0, 0, 0);
  }
  
  public GregorianCalendar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, 0, 0);
  }
  
  public GregorianCalendar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
    this(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, 0);
  }
  
  GregorianCalendar(TimeZone paramTimeZone, Locale paramLocale, boolean paramBoolean) {
    super(paramTimeZone, paramLocale);
    this.cachedFixedDate = Long.MIN_VALUE;
    this.gdate = gcal.newCalendarDate(getZone());
  }
  
  public void setGregorianChange(Date paramDate) {
    long l = paramDate.getTime();
    if (l == this.gregorianCutover)
      return; 
    complete();
    setGregorianChange(l);
  }
  
  private void setGregorianChange(long paramLong) {
    this.gregorianCutover = paramLong;
    this.gregorianCutoverDate = CalendarUtils.floorDivide(paramLong, 86400000L) + 719163L;
    if (paramLong == Long.MAX_VALUE)
      this.gregorianCutoverDate++; 
    BaseCalendar.Date date = getGregorianCutoverDate();
    this.gregorianCutoverYear = date.getYear();
    BaseCalendar baseCalendar = getJulianCalendarSystem();
    date = (BaseCalendar.Date)baseCalendar.newCalendarDate(TimeZone.NO_TIMEZONE);
    baseCalendar.getCalendarDateFromFixedDate(date, this.gregorianCutoverDate - 1L);
    this.gregorianCutoverYearJulian = date.getNormalizedYear();
    if (this.time < this.gregorianCutover)
      setUnnormalized(); 
  }
  
  public final Date getGregorianChange() {
    return new Date(this.gregorianCutover);
  }
  
  public boolean isLeapYear(int paramInt) {
    boolean bool;
    if ((paramInt & 0x3) != 0)
      return false; 
    if (paramInt > this.gregorianCutoverYear)
      return (paramInt % 100 != 0 || paramInt % 400 == 0); 
    if (paramInt < this.gregorianCutoverYearJulian)
      return true; 
    if (this.gregorianCutoverYear == this.gregorianCutoverYearJulian) {
      BaseCalendar.Date date = getCalendarDate(this.gregorianCutoverDate);
      bool = (date.getMonth() < 3) ? true : false;
    } else {
      bool = (paramInt == this.gregorianCutoverYear) ? true : false;
    } 
    return bool ? ((paramInt % 100 != 0 || paramInt % 400 == 0)) : true;
  }
  
  public String getCalendarType() {
    return "gregory";
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject instanceof GregorianCalendar && super.equals(paramObject) && this.gregorianCutover == ((GregorianCalendar)paramObject).gregorianCutover);
  }
  
  public int hashCode() {
    return super.hashCode() ^ (int)this.gregorianCutoverDate;
  }
  
  public void add(int paramInt1, int paramInt2) {
    if (paramInt2 == 0)
      return; 
    if (paramInt1 < 0 || paramInt1 >= 15)
      throw new IllegalArgumentException(); 
    complete();
    if (paramInt1 == 1) {
      int i = internalGet(1);
      if (internalGetEra() == 1) {
        i += paramInt2;
        if (i > 0) {
          set(1, i);
        } else {
          set(1, 1 - i);
          set(0, 0);
        } 
      } else {
        i -= paramInt2;
        if (i > 0) {
          set(1, i);
        } else {
          set(1, 1 - i);
          set(0, 1);
        } 
      } 
      pinDayOfMonth();
    } else if (paramInt1 == 2) {
      int k, i = internalGet(2) + paramInt2;
      int j = internalGet(1);
      if (i >= 0) {
        k = i / 12;
      } else {
        k = (i + 1) / 12 - 1;
      } 
      if (k != 0)
        if (internalGetEra() == 1) {
          j += k;
          if (j > 0) {
            set(1, j);
          } else {
            set(1, 1 - j);
            set(0, 0);
          } 
        } else {
          j -= k;
          if (j > 0) {
            set(1, j);
          } else {
            set(1, 1 - j);
            set(0, 1);
          } 
        }  
      if (i >= 0) {
        set(2, i % 12);
      } else {
        i %= 12;
        if (i < 0)
          i += 12; 
        set(2, 0 + i);
      } 
      pinDayOfMonth();
    } else if (paramInt1 == 0) {
      int i = internalGet(0) + paramInt2;
      if (i < 0)
        i = 0; 
      if (i > 1)
        i = 1; 
      set(0, i);
    } else {
      long l1 = paramInt2;
      long l2 = 0L;
      switch (paramInt1) {
        case 10:
        case 11:
          l1 *= 3600000L;
          break;
        case 12:
          l1 *= 60000L;
          break;
        case 13:
          l1 *= 1000L;
          break;
        case 3:
        case 4:
        case 8:
          l1 *= 7L;
          break;
        case 9:
          l1 = (paramInt2 / 2);
          l2 = (12 * paramInt2 % 2);
          break;
      } 
      if (paramInt1 >= 10) {
        setTimeInMillis(this.time + l1);
        return;
      } 
      long l3 = getCurrentFixedDate();
      l2 += internalGet(11);
      l2 *= 60L;
      l2 += internalGet(12);
      l2 *= 60L;
      l2 += internalGet(13);
      l2 *= 1000L;
      l2 += internalGet(14);
      if (l2 >= 86400000L) {
        l3++;
        l2 -= 86400000L;
      } else if (l2 < 0L) {
        l3--;
        l2 += 86400000L;
      } 
      l3 += l1;
      int i = internalGet(15) + internalGet(16);
      setTimeInMillis((l3 - 719163L) * 86400000L + l2 - i);
      i -= internalGet(15) + internalGet(16);
      if (i != 0) {
        setTimeInMillis(this.time + i);
        long l = getCurrentFixedDate();
        if (l != l3)
          setTimeInMillis(this.time - i); 
      } 
    } 
  }
  
  public void roll(int paramInt, boolean paramBoolean) {
    roll(paramInt, paramBoolean ? 1 : -1);
  }
  
  public void roll(int paramInt1, int paramInt2) {
    int k;
    boolean bool;
    long l1;
    int m, n;
    long l2;
    CalendarDate calendarDate;
    long l3;
    int i3;
    long l4;
    int i2;
    BaseCalendar.Date date1;
    int i1, i5;
    BaseCalendar baseCalendar1;
    BaseCalendar.Date date2;
    int i4, i7;
    long l5;
    int i6;
    long l6;
    int i8;
    BaseCalendar.Date date3;
    BaseCalendar baseCalendar2;
    int i9;
    BaseCalendar.Date date4;
    long l7;
    int i10;
    if (paramInt2 == 0)
      return; 
    if (paramInt1 < 0 || paramInt1 >= 15)
      throw new IllegalArgumentException(); 
    complete();
    int i = getMinimum(paramInt1);
    int j = getMaximum(paramInt1);
    switch (paramInt1) {
      case 10:
      case 11:
        k = j + 1;
        m = internalGet(paramInt1);
        n = (m + paramInt2) % k;
        if (n < 0)
          n += k; 
        this.time += (3600000 * (n - m));
        calendarDate = this.calsys.getCalendarDate(this.time, getZone());
        if (internalGet(5) != calendarDate.getDayOfMonth()) {
          calendarDate.setDate(internalGet(1), internalGet(2) + 1, internalGet(5));
          if (paramInt1 == 10) {
            assert internalGet(9) == 1;
            calendarDate.addHours(12);
          } 
          this.time = this.calsys.getTime(calendarDate);
        } 
        i3 = calendarDate.getHours();
        internalSet(paramInt1, i3 % k);
        if (paramInt1 == 10) {
          internalSet(11, i3);
        } else {
          internalSet(9, i3 / 12);
          internalSet(10, i3 % 12);
        } 
        i5 = calendarDate.getZoneOffset();
        i7 = calendarDate.getDaylightSaving();
        internalSet(15, i5 - i7);
        internalSet(16, i7);
        return;
      case 2:
        if (!isCutoverYear(this.cdate.getNormalizedYear())) {
          k = (internalGet(2) + paramInt2) % 12;
          if (k < 0)
            k += 12; 
          set(2, k);
          m = monthLength(k);
          if (internalGet(5) > m)
            set(5, m); 
        } else {
          k = getActualMaximum(2) + 1;
          m = (internalGet(2) + paramInt2) % k;
          if (m < 0)
            m += k; 
          set(2, m);
          n = getActualMaximum(5);
          if (internalGet(5) > n)
            set(5, n); 
        } 
        return;
      case 3:
        k = this.cdate.getNormalizedYear();
        j = getActualMaximum(3);
        set(7, internalGet(7));
        m = internalGet(3);
        n = m + paramInt2;
        if (!isCutoverYear(k)) {
          int i11 = getWeekYear();
          if (i11 == k) {
            if (n > i && n < j) {
              set(3, n);
              return;
            } 
            l4 = getCurrentFixedDate();
            long l = l4 - (7 * (m - i));
            if (this.calsys.getYearFromFixedDate(l) != k)
              i++; 
            l4 += (7 * (j - internalGet(3)));
            if (this.calsys.getYearFromFixedDate(l4) != k)
              j--; 
          } else if (i11 > k) {
            if (paramInt2 < 0)
              paramInt2++; 
            m = j;
          } else {
            if (paramInt2 > 0)
              paramInt2 -= m - j; 
            m = i;
          } 
          set(paramInt1, getRolledValue(m, paramInt2, i, j));
          return;
        } 
        l3 = getCurrentFixedDate();
        if (this.gregorianCutoverYear == this.gregorianCutoverYearJulian) {
          baseCalendar1 = getCutoverCalendarSystem();
        } else if (k == this.gregorianCutoverYear) {
          baseCalendar1 = gcal;
        } else {
          baseCalendar1 = getJulianCalendarSystem();
        } 
        l5 = l3 - (7 * (m - i));
        if (baseCalendar1.getYearFromFixedDate(l5) != k)
          i++; 
        l3 += (7 * (j - m));
        baseCalendar1 = (l3 >= this.gregorianCutoverDate) ? gcal : getJulianCalendarSystem();
        if (baseCalendar1.getYearFromFixedDate(l3) != k)
          j--; 
        n = getRolledValue(m, paramInt2, i, j) - 1;
        date3 = getCalendarDate(l5 + (n * 7));
        set(2, date3.getMonth() - 1);
        set(5, date3.getDayOfMonth());
        return;
      case 4:
        bool = isCutoverYear(this.cdate.getNormalizedYear());
        m = internalGet(7) - getFirstDayOfWeek();
        if (m < 0)
          m += 7; 
        l2 = getCurrentFixedDate();
        if (bool) {
          l4 = getFixedDateMonth1(this.cdate, l2);
          i6 = actualMonthLength();
        } else {
          l4 = l2 - internalGet(5) + 1L;
          i6 = this.calsys.getMonthLength(this.cdate);
        } 
        l6 = BaseCalendar.getDayOfWeekDateOnOrBefore(l4 + 6L, getFirstDayOfWeek());
        if ((int)(l6 - l4) >= getMinimalDaysInFirstWeek())
          l6 -= 7L; 
        j = getActualMaximum(paramInt1);
        i9 = getRolledValue(internalGet(paramInt1), paramInt2, 1, j) - 1;
        l7 = l6 + (i9 * 7) + m;
        if (l7 < l4) {
          l7 = l4;
        } else if (l7 >= l4 + i6) {
          l7 = l4 + i6 - 1L;
        } 
        if (bool) {
          BaseCalendar.Date date = getCalendarDate(l7);
          i10 = date.getDayOfMonth();
        } else {
          i10 = (int)(l7 - l4) + 1;
        } 
        set(5, i10);
        return;
      case 5:
        if (!isCutoverYear(this.cdate.getNormalizedYear())) {
          j = this.calsys.getMonthLength(this.cdate);
          break;
        } 
        l1 = getCurrentFixedDate();
        l2 = getFixedDateMonth1(this.cdate, l1);
        i2 = getRolledValue((int)(l1 - l2), paramInt2, 0, actualMonthLength() - 1);
        date2 = getCalendarDate(l2 + i2);
        assert date2.getMonth() - 1 == internalGet(2);
        set(5, date2.getDayOfMonth());
        return;
      case 6:
        j = getActualMaximum(paramInt1);
        if (!isCutoverYear(this.cdate.getNormalizedYear()))
          break; 
        l1 = getCurrentFixedDate();
        l2 = l1 - internalGet(6) + 1L;
        i2 = getRolledValue((int)(l1 - l2) + 1, paramInt2, i, j);
        date2 = getCalendarDate(l2 + i2 - 1L);
        set(2, date2.getMonth() - 1);
        set(5, date2.getDayOfMonth());
        return;
      case 7:
        if (!isCutoverYear(this.cdate.getNormalizedYear())) {
          int i11 = internalGet(3);
          if (i11 > 1 && i11 < 52) {
            set(3, i11);
            j = 7;
            break;
          } 
        } 
        paramInt2 %= 7;
        if (paramInt2 == 0)
          return; 
        l1 = getCurrentFixedDate();
        l2 = BaseCalendar.getDayOfWeekDateOnOrBefore(l1, getFirstDayOfWeek());
        l1 += paramInt2;
        if (l1 < l2) {
          l1 += 7L;
        } else if (l1 >= l2 + 7L) {
          l1 -= 7L;
        } 
        date1 = getCalendarDate(l1);
        set(0, (date1.getNormalizedYear() <= 0) ? 0 : 1);
        set(date1.getYear(), date1.getMonth() - 1, date1.getDayOfMonth());
        return;
      case 8:
        i = 1;
        if (!isCutoverYear(this.cdate.getNormalizedYear())) {
          int i11 = internalGet(5);
          m = this.calsys.getMonthLength(this.cdate);
          int i12 = m % 7;
          j = m / 7;
          int i13 = (i11 - 1) % 7;
          if (i13 < i12)
            j++; 
          set(7, internalGet(7));
          break;
        } 
        l1 = getCurrentFixedDate();
        l2 = getFixedDateMonth1(this.cdate, l1);
        i1 = actualMonthLength();
        i4 = i1 % 7;
        j = i1 / 7;
        i6 = (int)(l1 - l2) % 7;
        if (i6 < i4)
          j++; 
        i8 = getRolledValue(internalGet(paramInt1), paramInt2, i, j) - 1;
        l1 = l2 + (i8 * 7) + i6;
        baseCalendar2 = (l1 >= this.gregorianCutoverDate) ? gcal : getJulianCalendarSystem();
        date4 = (BaseCalendar.Date)baseCalendar2.newCalendarDate(TimeZone.NO_TIMEZONE);
        baseCalendar2.getCalendarDateFromFixedDate(date4, l1);
        set(5, date4.getDayOfMonth());
        return;
    } 
    set(paramInt1, getRolledValue(internalGet(paramInt1), paramInt2, i, j));
  }
  
  public int getMinimum(int paramInt) {
    return MIN_VALUES[paramInt];
  }
  
  public int getMaximum(int paramInt) {
    GregorianCalendar gregorianCalendar;
    int i;
    int j;
    switch (paramInt) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 8:
        if (this.gregorianCutoverYear > 200)
          break; 
        gregorianCalendar = (GregorianCalendar)clone();
        gregorianCalendar.setLenient(true);
        gregorianCalendar.setTimeInMillis(this.gregorianCutover);
        i = gregorianCalendar.getActualMaximum(paramInt);
        gregorianCalendar.setTimeInMillis(this.gregorianCutover - 1L);
        j = gregorianCalendar.getActualMaximum(paramInt);
        return Math.max(MAX_VALUES[paramInt], Math.max(i, j));
    } 
    return MAX_VALUES[paramInt];
  }
  
  public int getGreatestMinimum(int paramInt) {
    if (paramInt == 5) {
      BaseCalendar.Date date = getGregorianCutoverDate();
      long l = getFixedDateMonth1(date, this.gregorianCutoverDate);
      date = getCalendarDate(l);
      return Math.max(MIN_VALUES[paramInt], date.getDayOfMonth());
    } 
    return MIN_VALUES[paramInt];
  }
  
  public int getLeastMaximum(int paramInt) {
    GregorianCalendar gregorianCalendar;
    int i;
    int j;
    switch (paramInt) {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 8:
        gregorianCalendar = (GregorianCalendar)clone();
        gregorianCalendar.setLenient(true);
        gregorianCalendar.setTimeInMillis(this.gregorianCutover);
        i = gregorianCalendar.getActualMaximum(paramInt);
        gregorianCalendar.setTimeInMillis(this.gregorianCutover - 1L);
        j = gregorianCalendar.getActualMaximum(paramInt);
        return Math.min(LEAST_MAX_VALUES[paramInt], Math.min(i, j));
    } 
    return LEAST_MAX_VALUES[paramInt];
  }
  
  public int getActualMinimum(int paramInt) {
    if (paramInt == 5) {
      GregorianCalendar gregorianCalendar = getNormalizedCalendar();
      int i = gregorianCalendar.cdate.getNormalizedYear();
      if (i == this.gregorianCutoverYear || i == this.gregorianCutoverYearJulian) {
        long l = getFixedDateMonth1(gregorianCalendar.cdate, gregorianCalendar.calsys.getFixedDate(gregorianCalendar.cdate));
        BaseCalendar.Date date = getCalendarDate(l);
        return date.getDayOfMonth();
      } 
    } 
    return getMinimum(paramInt);
  }
  
  public int getActualMaximum(int paramInt) {
    int k;
    long l;
    int m, n, i1;
    if ((0x1FE81 & 1 << paramInt) != 0)
      return getMaximum(paramInt); 
    GregorianCalendar gregorianCalendar = getNormalizedCalendar();
    BaseCalendar.Date date = gregorianCalendar.cdate;
    BaseCalendar baseCalendar = gregorianCalendar.calsys;
    int i = date.getNormalizedYear();
    int j = -1;
    switch (paramInt) {
      case 2:
        if (!gregorianCalendar.isCutoverYear(i)) {
          j = 11;
        } else {
          while (true) {
            long l1 = gcal.getFixedDate(++i, 1, 1, (BaseCalendar.Date)null);
            if (l1 >= this.gregorianCutoverDate) {
              BaseCalendar.Date date1 = (BaseCalendar.Date)date.clone();
              baseCalendar.getCalendarDateFromFixedDate(date1, l1 - 1L);
              j = date1.getMonth() - 1;
              return j;
            } 
          } 
        } 
        return j;
      case 5:
        j = baseCalendar.getMonthLength(date);
        if (gregorianCalendar.isCutoverYear(i) && date.getDayOfMonth() != j) {
          long l1 = gregorianCalendar.getCurrentFixedDate();
          if (l1 < this.gregorianCutoverDate) {
            int i2 = gregorianCalendar.actualMonthLength();
            long l2 = gregorianCalendar.getFixedDateMonth1(gregorianCalendar.cdate, l1) + i2 - 1L;
            BaseCalendar.Date date1 = gregorianCalendar.getCalendarDate(l2);
            j = date1.getDayOfMonth();
          } 
        } 
        return j;
      case 6:
        if (!gregorianCalendar.isCutoverYear(i)) {
          j = baseCalendar.getYearLength(date);
        } else {
          long l1;
          if (this.gregorianCutoverYear == this.gregorianCutoverYearJulian) {
            BaseCalendar baseCalendar1 = gregorianCalendar.getCutoverCalendarSystem();
            l1 = baseCalendar1.getFixedDate(i, 1, 1, (BaseCalendar.Date)null);
          } else if (i == this.gregorianCutoverYearJulian) {
            l1 = baseCalendar.getFixedDate(i, 1, 1, (BaseCalendar.Date)null);
          } else {
            l1 = this.gregorianCutoverDate;
          } 
          long l2 = gcal.getFixedDate(++i, 1, 1, (BaseCalendar.Date)null);
          if (l2 < this.gregorianCutoverDate)
            l2 = this.gregorianCutoverDate; 
          assert l1 <= baseCalendar.getFixedDate(date.getNormalizedYear(), date.getMonth(), date.getDayOfMonth(), date);
          assert l2 >= baseCalendar.getFixedDate(date.getNormalizedYear(), date.getMonth(), date.getDayOfMonth(), date);
          j = (int)(l2 - l1);
        } 
        return j;
      case 3:
        if (!gregorianCalendar.isCutoverYear(i)) {
          CalendarDate calendarDate = baseCalendar.newCalendarDate(TimeZone.NO_TIMEZONE);
          calendarDate.setDate(date.getYear(), 1, 1);
          m = baseCalendar.getDayOfWeek(calendarDate);
          m -= getFirstDayOfWeek();
          if (m < 0)
            m += 7; 
          j = 52;
          int i2 = m + getMinimalDaysInFirstWeek() - 1;
          if (i2 == 6 || (date.isLeapYear() && (i2 == 5 || i2 == 12)))
            j++; 
        } else {
          if (gregorianCalendar == this)
            gregorianCalendar = (GregorianCalendar)gregorianCalendar.clone(); 
          k = getActualMaximum(6);
          gregorianCalendar.set(6, k);
          j = gregorianCalendar.get(3);
          if (internalGet(1) != gregorianCalendar.getWeekYear()) {
            gregorianCalendar.set(6, k - 7);
            j = gregorianCalendar.get(3);
          } 
        } 
        return j;
      case 4:
        if (!gregorianCalendar.isCutoverYear(i)) {
          CalendarDate calendarDate = baseCalendar.newCalendarDate(null);
          calendarDate.setDate(date.getYear(), date.getMonth(), 1);
          m = baseCalendar.getDayOfWeek(calendarDate);
          int i2 = baseCalendar.getMonthLength(calendarDate);
          m -= getFirstDayOfWeek();
          if (m < 0)
            m += 7; 
          int i3 = 7 - m;
          j = 3;
          if (i3 >= getMinimalDaysInFirstWeek())
            j++; 
          i2 -= i3 + 21;
          if (i2 > 0) {
            j++;
            if (i2 > 7)
              j++; 
          } 
        } else {
          if (gregorianCalendar == this)
            gregorianCalendar = (GregorianCalendar)gregorianCalendar.clone(); 
          k = gregorianCalendar.internalGet(1);
          m = gregorianCalendar.internalGet(2);
          do {
            j = gregorianCalendar.get(4);
            gregorianCalendar.add(4, 1);
          } while (gregorianCalendar.get(1) == k && gregorianCalendar.get(2) == m);
        } 
        return j;
      case 8:
        n = date.getDayOfWeek();
        if (!gregorianCalendar.isCutoverYear(i)) {
          BaseCalendar.Date date1 = (BaseCalendar.Date)date.clone();
          k = baseCalendar.getMonthLength(date1);
          date1.setDayOfMonth(1);
          baseCalendar.normalize(date1);
          m = date1.getDayOfWeek();
        } else {
          if (gregorianCalendar == this)
            gregorianCalendar = (GregorianCalendar)clone(); 
          k = gregorianCalendar.actualMonthLength();
          gregorianCalendar.set(5, gregorianCalendar.getActualMinimum(5));
          m = gregorianCalendar.get(7);
        } 
        i1 = n - m;
        if (i1 < 0)
          i1 += 7; 
        k -= i1;
        j = (k + 6) / 7;
        return j;
      case 1:
        if (gregorianCalendar == this)
          gregorianCalendar = (GregorianCalendar)clone(); 
        l = gregorianCalendar.getYearOffsetInMillis();
        if (gregorianCalendar.internalGetEra() == 1) {
          gregorianCalendar.setTimeInMillis(Long.MAX_VALUE);
          j = gregorianCalendar.get(1);
          long l1 = gregorianCalendar.getYearOffsetInMillis();
          if (l > l1)
            j--; 
        } else {
          BaseCalendar baseCalendar1 = (gregorianCalendar.getTimeInMillis() >= this.gregorianCutover) ? gcal : getJulianCalendarSystem();
          CalendarDate calendarDate = baseCalendar1.getCalendarDate(Long.MIN_VALUE, getZone());
          long l1 = (baseCalendar.getDayOfYear(calendarDate) - 1L) * 24L + calendarDate.getHours();
          l1 *= 60L;
          l1 += calendarDate.getMinutes();
          l1 *= 60L;
          l1 += calendarDate.getSeconds();
          l1 *= 1000L;
          l1 += calendarDate.getMillis();
          j = calendarDate.getYear();
          if (j <= 0) {
            assert baseCalendar1 == gcal;
            j = 1 - j;
          } 
          if (l < l1)
            j--; 
        } 
        return j;
    } 
    throw new ArrayIndexOutOfBoundsException(paramInt);
  }
  
  private long getYearOffsetInMillis() {
    long l = ((internalGet(6) - 1) * 24);
    l += internalGet(11);
    l *= 60L;
    l += internalGet(12);
    l *= 60L;
    l += internalGet(13);
    l *= 1000L;
    return l + internalGet(14) - (internalGet(15) + internalGet(16));
  }
  
  public Object clone() {
    GregorianCalendar gregorianCalendar = (GregorianCalendar)super.clone();
    gregorianCalendar.gdate = (BaseCalendar.Date)this.gdate.clone();
    if (this.cdate != null)
      if (this.cdate != this.gdate) {
        gregorianCalendar.cdate = (BaseCalendar.Date)this.cdate.clone();
      } else {
        gregorianCalendar.cdate = gregorianCalendar.gdate;
      }  
    gregorianCalendar.originalFields = null;
    gregorianCalendar.zoneOffsets = null;
    return gregorianCalendar;
  }
  
  public TimeZone getTimeZone() {
    TimeZone timeZone = super.getTimeZone();
    this.gdate.setZone(timeZone);
    if (this.cdate != null && this.cdate != this.gdate)
      this.cdate.setZone(timeZone); 
    return timeZone;
  }
  
  public void setTimeZone(TimeZone paramTimeZone) {
    super.setTimeZone(paramTimeZone);
    this.gdate.setZone(paramTimeZone);
    if (this.cdate != null && this.cdate != this.gdate)
      this.cdate.setZone(paramTimeZone); 
  }
  
  public final boolean isWeekDateSupported() {
    return true;
  }
  
  public int getWeekYear() {
    int i = get(1);
    if (internalGetEra() == 0)
      i = 1 - i; 
    if (i > this.gregorianCutoverYear + 1) {
      int i2 = internalGet(3);
      if (internalGet(2) == 0) {
        if (i2 >= 52)
          i--; 
      } else if (i2 == 1) {
        i++;
      } 
      return i;
    } 
    int j = internalGet(6);
    int k = getActualMaximum(6);
    int m = getMinimalDaysInFirstWeek();
    if (j > m && j < k - 6)
      return i; 
    GregorianCalendar gregorianCalendar = (GregorianCalendar)clone();
    gregorianCalendar.setLenient(true);
    gregorianCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
    gregorianCalendar.set(6, 1);
    gregorianCalendar.complete();
    int n = getFirstDayOfWeek() - gregorianCalendar.get(7);
    if (n != 0) {
      if (n < 0)
        n += 7; 
      gregorianCalendar.add(6, n);
    } 
    int i1 = gregorianCalendar.get(6);
    if (j < i1) {
      if (i1 <= m)
        i--; 
    } else {
      gregorianCalendar.set(1, i + 1);
      gregorianCalendar.set(6, 1);
      gregorianCalendar.complete();
      int i2 = getFirstDayOfWeek() - gregorianCalendar.get(7);
      if (i2 != 0) {
        if (i2 < 0)
          i2 += 7; 
        gregorianCalendar.add(6, i2);
      } 
      i1 = gregorianCalendar.get(6) - 1;
      if (i1 == 0)
        i1 = 7; 
      if (i1 >= m) {
        int i3 = k - j + 1;
        if (i3 <= 7 - i1)
          i++; 
      } 
    } 
    return i;
  }
  
  public void setWeekDate(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt3 < 1 || paramInt3 > 7)
      throw new IllegalArgumentException("invalid dayOfWeek: " + paramInt3); 
    GregorianCalendar gregorianCalendar = (GregorianCalendar)clone();
    gregorianCalendar.setLenient(true);
    int i = gregorianCalendar.get(0);
    gregorianCalendar.clear();
    gregorianCalendar.setTimeZone(TimeZone.getTimeZone("GMT"));
    gregorianCalendar.set(0, i);
    gregorianCalendar.set(1, paramInt1);
    gregorianCalendar.set(3, 1);
    gregorianCalendar.set(7, getFirstDayOfWeek());
    int j = paramInt3 - getFirstDayOfWeek();
    if (j < 0)
      j += 7; 
    j += 7 * (paramInt2 - 1);
    if (j != 0) {
      gregorianCalendar.add(6, j);
    } else {
      gregorianCalendar.complete();
    } 
    if (!isLenient() && (gregorianCalendar.getWeekYear() != paramInt1 || gregorianCalendar.internalGet(3) != paramInt2 || gregorianCalendar.internalGet(7) != paramInt3))
      throw new IllegalArgumentException(); 
    set(0, gregorianCalendar.internalGet(0));
    set(1, gregorianCalendar.internalGet(1));
    set(2, gregorianCalendar.internalGet(2));
    set(5, gregorianCalendar.internalGet(5));
    internalSet(3, paramInt2);
    complete();
  }
  
  public int getWeeksInWeekYear() {
    GregorianCalendar gregorianCalendar = getNormalizedCalendar();
    int i = gregorianCalendar.getWeekYear();
    if (i == gregorianCalendar.internalGet(1))
      return gregorianCalendar.getActualMaximum(3); 
    if (gregorianCalendar == this)
      gregorianCalendar = (GregorianCalendar)gregorianCalendar.clone(); 
    gregorianCalendar.setWeekDate(i, 2, internalGet(7));
    return gregorianCalendar.getActualMaximum(3);
  }
  
  GregorianCalendar(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7) {
    this.cachedFixedDate = Long.MIN_VALUE;
    this.gdate = gcal.newCalendarDate(getZone());
    set(1, paramInt1);
    set(2, paramInt2);
    set(5, paramInt3);
    if (paramInt4 >= 12 && paramInt4 <= 23) {
      internalSet(9, 1);
      internalSet(10, paramInt4 - 12);
    } else {
      internalSet(10, paramInt4);
    } 
    setFieldsComputed(1536);
    set(11, paramInt4);
    set(12, paramInt5);
    set(13, paramInt6);
    internalSet(14, paramInt7);
  }
  
  protected void computeFields() {
    int i;
    if (isPartiallyNormalized()) {
      i = getSetStateFields();
      int j = (i ^ 0xFFFFFFFF) & 0x1FFFF;
      if (j != 0 || this.calsys == null) {
        i |= computeFields(j, i & 0x18000);
        assert i == 131071;
      } 
    } else {
      i = 131071;
      computeFields(i, 0);
    } 
    setFieldsComputed(i);
  }
  
  private int computeFields(int paramInt1, int paramInt2) {
    int k, i = 0;
    TimeZone timeZone = getZone();
    if (this.zoneOffsets == null)
      this.zoneOffsets = new int[2]; 
    if (paramInt2 != 98304)
      if (timeZone instanceof ZoneInfo) {
        i = ((ZoneInfo)timeZone).getOffsets(this.time, this.zoneOffsets);
      } else {
        i = timeZone.getOffset(this.time);
        this.zoneOffsets[0] = timeZone.getRawOffset();
        this.zoneOffsets[1] = i - this.zoneOffsets[0];
      }  
    if (paramInt2 != 0) {
      if (isFieldSet(paramInt2, 15))
        this.zoneOffsets[0] = internalGet(15); 
      if (isFieldSet(paramInt2, 16))
        this.zoneOffsets[1] = internalGet(16); 
      i = this.zoneOffsets[0] + this.zoneOffsets[1];
    } 
    long l = i / 86400000L;
    int j = i % 86400000;
    l += this.time / 86400000L;
    j += (int)(this.time % 86400000L);
    if (j >= 86400000L) {
      j = (int)(j - 86400000L);
      l++;
    } else {
      while (j < 0) {
        j = (int)(j + 86400000L);
        l--;
      } 
    } 
    l += 719163L;
    boolean bool = true;
    if (l >= this.gregorianCutoverDate) {
      assert this.cachedFixedDate == Long.MIN_VALUE || this.gdate.isNormalized() : "cache control: not normalized";
      assert this.cachedFixedDate == Long.MIN_VALUE || gcal
        .getFixedDate(this.gdate.getNormalizedYear(), this.gdate
          .getMonth(), this.gdate
          .getDayOfMonth(), this.gdate) == this.cachedFixedDate : "cache control: inconsictency, cachedFixedDate=" + this.cachedFixedDate + ", computed=" + gcal
        
        .getFixedDate(this.gdate.getNormalizedYear(), this.gdate
          .getMonth(), this.gdate
          .getDayOfMonth(), this.gdate) + ", date=" + this.gdate;
      if (l != this.cachedFixedDate) {
        gcal.getCalendarDateFromFixedDate(this.gdate, l);
        this.cachedFixedDate = l;
      } 
      k = this.gdate.getYear();
      if (k <= 0) {
        k = 1 - k;
        bool = false;
      } 
      this.calsys = gcal;
      this.cdate = this.gdate;
      assert this.cdate.getDayOfWeek() > 0 : "dow=" + this.cdate.getDayOfWeek() + ", date=" + this.cdate;
    } else {
      this.calsys = getJulianCalendarSystem();
      this.cdate = jcal.newCalendarDate(getZone());
      jcal.getCalendarDateFromFixedDate(this.cdate, l);
      Era era = this.cdate.getEra();
      if (era == jeras[0])
        bool = false; 
      k = this.cdate.getYear();
    } 
    internalSet(0, bool);
    internalSet(1, k);
    int m = paramInt1 | 0x3;
    int n = this.cdate.getMonth() - 1;
    int i1 = this.cdate.getDayOfMonth();
    if ((paramInt1 & 0xA4) != 0) {
      internalSet(2, n);
      internalSet(5, i1);
      internalSet(7, this.cdate.getDayOfWeek());
      m |= 0xA4;
    } 
    if ((paramInt1 & 0x7E00) != 0) {
      if (j != 0) {
        int i2 = j / 3600000;
        internalSet(11, i2);
        internalSet(9, i2 / 12);
        internalSet(10, i2 % 12);
        int i3 = j % 3600000;
        internalSet(12, i3 / 60000);
        i3 %= 60000;
        internalSet(13, i3 / 1000);
        internalSet(14, i3 % 1000);
      } else {
        internalSet(11, 0);
        internalSet(9, 0);
        internalSet(10, 0);
        internalSet(12, 0);
        internalSet(13, 0);
        internalSet(14, 0);
      } 
      m |= 0x7E00;
    } 
    if ((paramInt1 & 0x18000) != 0) {
      internalSet(15, this.zoneOffsets[0]);
      internalSet(16, this.zoneOffsets[1]);
      m |= 0x18000;
    } 
    if ((paramInt1 & 0x158) != 0) {
      int i2 = this.cdate.getNormalizedYear();
      long l1 = this.calsys.getFixedDate(i2, 1, 1, this.cdate);
      int i3 = (int)(l - l1) + 1;
      long l2 = l - i1 + 1L;
      int i4 = 0;
      int i5 = (this.calsys == gcal) ? this.gregorianCutoverYear : this.gregorianCutoverYearJulian;
      int i6 = i1 - 1;
      if (i2 == i5) {
        if (this.gregorianCutoverYearJulian <= this.gregorianCutoverYear) {
          l1 = getFixedDateJan1(this.cdate, l);
          if (l >= this.gregorianCutoverDate)
            l2 = getFixedDateMonth1(this.cdate, l); 
        } 
        int i8 = (int)(l - l1) + 1;
        i4 = i3 - i8;
        i3 = i8;
        i6 = (int)(l - l2);
      } 
      internalSet(6, i3);
      internalSet(8, i6 / 7 + 1);
      int i7 = getWeekNumber(l1, l);
      if (i7 == 0) {
        long l3 = l1 - 1L;
        long l4 = l1 - 365L;
        if (i2 > i5 + 1) {
          if (CalendarUtils.isGregorianLeapYear(i2 - 1))
            l4--; 
        } else if (i2 <= this.gregorianCutoverYearJulian) {
          if (CalendarUtils.isJulianLeapYear(i2 - 1))
            l4--; 
        } else {
          BaseCalendar baseCalendar = this.calsys;
          int i8 = getCalendarDate(l3).getNormalizedYear();
          if (i8 == this.gregorianCutoverYear) {
            baseCalendar = getCutoverCalendarSystem();
            if (baseCalendar == jcal) {
              l4 = baseCalendar.getFixedDate(i8, 1, 1, (BaseCalendar.Date)null);
            } else {
              l4 = this.gregorianCutoverDate;
              baseCalendar = gcal;
            } 
          } else if (i8 <= this.gregorianCutoverYearJulian) {
            baseCalendar = getJulianCalendarSystem();
            l4 = baseCalendar.getFixedDate(i8, 1, 1, (BaseCalendar.Date)null);
          } 
        } 
        i7 = getWeekNumber(l4, l3);
      } else if (i2 > this.gregorianCutoverYear || i2 < this.gregorianCutoverYearJulian - 1) {
        if (i7 >= 52) {
          long l3 = l1 + 365L;
          if (this.cdate.isLeapYear())
            l3++; 
          long l4 = BaseCalendar.getDayOfWeekDateOnOrBefore(l3 + 6L, 
              getFirstDayOfWeek());
          int i8 = (int)(l4 - l3);
          if (i8 >= getMinimalDaysInFirstWeek() && l >= l4 - 7L)
            i7 = 1; 
        } 
      } else {
        long l3;
        BaseCalendar baseCalendar = this.calsys;
        int i8 = i2 + 1;
        if (i8 == this.gregorianCutoverYearJulian + 1 && i8 < this.gregorianCutoverYear)
          i8 = this.gregorianCutoverYear; 
        if (i8 == this.gregorianCutoverYear)
          baseCalendar = getCutoverCalendarSystem(); 
        if (i8 > this.gregorianCutoverYear || this.gregorianCutoverYearJulian == this.gregorianCutoverYear || i8 == this.gregorianCutoverYearJulian) {
          l3 = baseCalendar.getFixedDate(i8, 1, 1, (BaseCalendar.Date)null);
        } else {
          l3 = this.gregorianCutoverDate;
          baseCalendar = gcal;
        } 
        long l4 = BaseCalendar.getDayOfWeekDateOnOrBefore(l3 + 6L, 
            getFirstDayOfWeek());
        int i9 = (int)(l4 - l3);
        if (i9 >= getMinimalDaysInFirstWeek() && l >= l4 - 7L)
          i7 = 1; 
      } 
      internalSet(3, i7);
      internalSet(4, getWeekNumber(l2, l));
      m |= 0x158;
    } 
    return m;
  }
  
  private int getWeekNumber(long paramLong1, long paramLong2) {
    long l = Gregorian.getDayOfWeekDateOnOrBefore(paramLong1 + 6L, 
        getFirstDayOfWeek());
    int i = (int)(l - paramLong1);
    assert i <= 7;
    if (i >= getMinimalDaysInFirstWeek())
      l -= 7L; 
    int j = (int)(paramLong2 - l);
    if (j >= 0)
      return j / 7 + 1; 
    return CalendarUtils.floorDivide(j, 7) + 1;
  }
  
  protected void computeTime() {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual isLenient : ()Z
    //   4: ifne -> 87
    //   7: aload_0
    //   8: getfield originalFields : [I
    //   11: ifnonnull -> 22
    //   14: aload_0
    //   15: bipush #17
    //   17: newarray int
    //   19: putfield originalFields : [I
    //   22: iconst_0
    //   23: istore_1
    //   24: iload_1
    //   25: bipush #17
    //   27: if_icmpge -> 87
    //   30: aload_0
    //   31: iload_1
    //   32: invokevirtual internalGet : (I)I
    //   35: istore_2
    //   36: aload_0
    //   37: iload_1
    //   38: invokevirtual isExternallySet : (I)Z
    //   41: ifeq -> 74
    //   44: iload_2
    //   45: aload_0
    //   46: iload_1
    //   47: invokevirtual getMinimum : (I)I
    //   50: if_icmplt -> 62
    //   53: iload_2
    //   54: aload_0
    //   55: iload_1
    //   56: invokevirtual getMaximum : (I)I
    //   59: if_icmple -> 74
    //   62: new java/lang/IllegalArgumentException
    //   65: dup
    //   66: iload_1
    //   67: invokestatic getFieldName : (I)Ljava/lang/String;
    //   70: invokespecial <init> : (Ljava/lang/String;)V
    //   73: athrow
    //   74: aload_0
    //   75: getfield originalFields : [I
    //   78: iload_1
    //   79: iload_2
    //   80: iastore
    //   81: iinc #1, 1
    //   84: goto -> 24
    //   87: aload_0
    //   88: invokevirtual selectFields : ()I
    //   91: istore_1
    //   92: aload_0
    //   93: iconst_1
    //   94: invokevirtual isSet : (I)Z
    //   97: ifeq -> 108
    //   100: aload_0
    //   101: iconst_1
    //   102: invokevirtual internalGet : (I)I
    //   105: goto -> 111
    //   108: sipush #1970
    //   111: istore_2
    //   112: aload_0
    //   113: invokespecial internalGetEra : ()I
    //   116: istore_3
    //   117: iload_3
    //   118: ifne -> 128
    //   121: iconst_1
    //   122: iload_2
    //   123: isub
    //   124: istore_2
    //   125: goto -> 144
    //   128: iload_3
    //   129: iconst_1
    //   130: if_icmpeq -> 144
    //   133: new java/lang/IllegalArgumentException
    //   136: dup
    //   137: ldc_w 'Invalid era'
    //   140: invokespecial <init> : (Ljava/lang/String;)V
    //   143: athrow
    //   144: iload_2
    //   145: ifgt -> 165
    //   148: aload_0
    //   149: iconst_0
    //   150: invokevirtual isSet : (I)Z
    //   153: ifne -> 165
    //   156: iload_1
    //   157: iconst_1
    //   158: ior
    //   159: istore_1
    //   160: aload_0
    //   161: iconst_1
    //   162: invokevirtual setFieldsComputed : (I)V
    //   165: lconst_0
    //   166: lstore #4
    //   168: iload_1
    //   169: bipush #11
    //   171: invokestatic isFieldSet : (II)Z
    //   174: ifeq -> 192
    //   177: lload #4
    //   179: aload_0
    //   180: bipush #11
    //   182: invokevirtual internalGet : (I)I
    //   185: i2l
    //   186: ladd
    //   187: lstore #4
    //   189: goto -> 228
    //   192: lload #4
    //   194: aload_0
    //   195: bipush #10
    //   197: invokevirtual internalGet : (I)I
    //   200: i2l
    //   201: ladd
    //   202: lstore #4
    //   204: iload_1
    //   205: bipush #9
    //   207: invokestatic isFieldSet : (II)Z
    //   210: ifeq -> 228
    //   213: lload #4
    //   215: bipush #12
    //   217: aload_0
    //   218: bipush #9
    //   220: invokevirtual internalGet : (I)I
    //   223: imul
    //   224: i2l
    //   225: ladd
    //   226: lstore #4
    //   228: lload #4
    //   230: ldc2_w 60
    //   233: lmul
    //   234: lstore #4
    //   236: lload #4
    //   238: aload_0
    //   239: bipush #12
    //   241: invokevirtual internalGet : (I)I
    //   244: i2l
    //   245: ladd
    //   246: lstore #4
    //   248: lload #4
    //   250: ldc2_w 60
    //   253: lmul
    //   254: lstore #4
    //   256: lload #4
    //   258: aload_0
    //   259: bipush #13
    //   261: invokevirtual internalGet : (I)I
    //   264: i2l
    //   265: ladd
    //   266: lstore #4
    //   268: lload #4
    //   270: ldc2_w 1000
    //   273: lmul
    //   274: lstore #4
    //   276: lload #4
    //   278: aload_0
    //   279: bipush #14
    //   281: invokevirtual internalGet : (I)I
    //   284: i2l
    //   285: ladd
    //   286: lstore #4
    //   288: lload #4
    //   290: ldc2_w 86400000
    //   293: ldiv
    //   294: lstore #6
    //   296: lload #4
    //   298: ldc2_w 86400000
    //   301: lrem
    //   302: lstore #4
    //   304: lload #4
    //   306: lconst_0
    //   307: lcmp
    //   308: ifge -> 328
    //   311: lload #4
    //   313: ldc2_w 86400000
    //   316: ladd
    //   317: lstore #4
    //   319: lload #6
    //   321: lconst_1
    //   322: lsub
    //   323: lstore #6
    //   325: goto -> 304
    //   328: iload_2
    //   329: aload_0
    //   330: getfield gregorianCutoverYear : I
    //   333: if_icmple -> 392
    //   336: iload_2
    //   337: aload_0
    //   338: getfield gregorianCutoverYearJulian : I
    //   341: if_icmple -> 392
    //   344: lload #6
    //   346: aload_0
    //   347: getstatic java/util/GregorianCalendar.gcal : Lsun/util/calendar/Gregorian;
    //   350: iload_2
    //   351: iload_1
    //   352: invokespecial getFixedDate : (Lsun/util/calendar/BaseCalendar;II)J
    //   355: ladd
    //   356: lstore #8
    //   358: lload #8
    //   360: aload_0
    //   361: getfield gregorianCutoverDate : J
    //   364: lcmp
    //   365: iflt -> 375
    //   368: lload #8
    //   370: lstore #6
    //   372: goto -> 621
    //   375: lload #6
    //   377: aload_0
    //   378: invokestatic getJulianCalendarSystem : ()Lsun/util/calendar/BaseCalendar;
    //   381: iload_2
    //   382: iload_1
    //   383: invokespecial getFixedDate : (Lsun/util/calendar/BaseCalendar;II)J
    //   386: ladd
    //   387: lstore #10
    //   389: goto -> 474
    //   392: iload_2
    //   393: aload_0
    //   394: getfield gregorianCutoverYear : I
    //   397: if_icmpge -> 446
    //   400: iload_2
    //   401: aload_0
    //   402: getfield gregorianCutoverYearJulian : I
    //   405: if_icmpge -> 446
    //   408: lload #6
    //   410: aload_0
    //   411: invokestatic getJulianCalendarSystem : ()Lsun/util/calendar/BaseCalendar;
    //   414: iload_2
    //   415: iload_1
    //   416: invokespecial getFixedDate : (Lsun/util/calendar/BaseCalendar;II)J
    //   419: ladd
    //   420: lstore #10
    //   422: lload #10
    //   424: aload_0
    //   425: getfield gregorianCutoverDate : J
    //   428: lcmp
    //   429: ifge -> 439
    //   432: lload #10
    //   434: lstore #6
    //   436: goto -> 621
    //   439: lload #10
    //   441: lstore #8
    //   443: goto -> 474
    //   446: lload #6
    //   448: aload_0
    //   449: invokestatic getJulianCalendarSystem : ()Lsun/util/calendar/BaseCalendar;
    //   452: iload_2
    //   453: iload_1
    //   454: invokespecial getFixedDate : (Lsun/util/calendar/BaseCalendar;II)J
    //   457: ladd
    //   458: lstore #10
    //   460: lload #6
    //   462: aload_0
    //   463: getstatic java/util/GregorianCalendar.gcal : Lsun/util/calendar/Gregorian;
    //   466: iload_2
    //   467: iload_1
    //   468: invokespecial getFixedDate : (Lsun/util/calendar/BaseCalendar;II)J
    //   471: ladd
    //   472: lstore #8
    //   474: iload_1
    //   475: bipush #6
    //   477: invokestatic isFieldSet : (II)Z
    //   480: ifne -> 491
    //   483: iload_1
    //   484: iconst_3
    //   485: invokestatic isFieldSet : (II)Z
    //   488: ifeq -> 524
    //   491: aload_0
    //   492: getfield gregorianCutoverYear : I
    //   495: aload_0
    //   496: getfield gregorianCutoverYearJulian : I
    //   499: if_icmpne -> 509
    //   502: lload #10
    //   504: lstore #6
    //   506: goto -> 621
    //   509: iload_2
    //   510: aload_0
    //   511: getfield gregorianCutoverYear : I
    //   514: if_icmpne -> 524
    //   517: lload #8
    //   519: lstore #6
    //   521: goto -> 621
    //   524: lload #8
    //   526: aload_0
    //   527: getfield gregorianCutoverDate : J
    //   530: lcmp
    //   531: iflt -> 582
    //   534: lload #10
    //   536: aload_0
    //   537: getfield gregorianCutoverDate : J
    //   540: lcmp
    //   541: iflt -> 551
    //   544: lload #8
    //   546: lstore #6
    //   548: goto -> 621
    //   551: aload_0
    //   552: getfield calsys : Lsun/util/calendar/BaseCalendar;
    //   555: getstatic java/util/GregorianCalendar.gcal : Lsun/util/calendar/Gregorian;
    //   558: if_acmpeq -> 568
    //   561: aload_0
    //   562: getfield calsys : Lsun/util/calendar/BaseCalendar;
    //   565: ifnonnull -> 575
    //   568: lload #8
    //   570: lstore #6
    //   572: goto -> 621
    //   575: lload #10
    //   577: lstore #6
    //   579: goto -> 621
    //   582: lload #10
    //   584: aload_0
    //   585: getfield gregorianCutoverDate : J
    //   588: lcmp
    //   589: ifge -> 599
    //   592: lload #10
    //   594: lstore #6
    //   596: goto -> 621
    //   599: aload_0
    //   600: invokevirtual isLenient : ()Z
    //   603: ifne -> 617
    //   606: new java/lang/IllegalArgumentException
    //   609: dup
    //   610: ldc_w 'the specified date doesn't exist'
    //   613: invokespecial <init> : (Ljava/lang/String;)V
    //   616: athrow
    //   617: lload #10
    //   619: lstore #6
    //   621: lload #6
    //   623: ldc2_w 719163
    //   626: lsub
    //   627: ldc2_w 86400000
    //   630: lmul
    //   631: lload #4
    //   633: ladd
    //   634: lstore #8
    //   636: aload_0
    //   637: invokevirtual getZone : ()Ljava/util/TimeZone;
    //   640: astore #10
    //   642: aload_0
    //   643: getfield zoneOffsets : [I
    //   646: ifnonnull -> 656
    //   649: aload_0
    //   650: iconst_2
    //   651: newarray int
    //   653: putfield zoneOffsets : [I
    //   656: iload_1
    //   657: ldc_w 98304
    //   660: iand
    //   661: istore #11
    //   663: iload #11
    //   665: ldc_w 98304
    //   668: if_icmpeq -> 738
    //   671: aload #10
    //   673: instanceof sun/util/calendar/ZoneInfo
    //   676: ifeq -> 697
    //   679: aload #10
    //   681: checkcast sun/util/calendar/ZoneInfo
    //   684: lload #8
    //   686: aload_0
    //   687: getfield zoneOffsets : [I
    //   690: invokevirtual getOffsetsByWall : (J[I)I
    //   693: pop
    //   694: goto -> 738
    //   697: iload_1
    //   698: bipush #15
    //   700: invokestatic isFieldSet : (II)Z
    //   703: ifeq -> 715
    //   706: aload_0
    //   707: bipush #15
    //   709: invokevirtual internalGet : (I)I
    //   712: goto -> 720
    //   715: aload #10
    //   717: invokevirtual getRawOffset : ()I
    //   720: istore #12
    //   722: aload #10
    //   724: lload #8
    //   726: iload #12
    //   728: i2l
    //   729: lsub
    //   730: aload_0
    //   731: getfield zoneOffsets : [I
    //   734: invokevirtual getOffsets : (J[I)I
    //   737: pop
    //   738: iload #11
    //   740: ifeq -> 787
    //   743: iload #11
    //   745: bipush #15
    //   747: invokestatic isFieldSet : (II)Z
    //   750: ifeq -> 765
    //   753: aload_0
    //   754: getfield zoneOffsets : [I
    //   757: iconst_0
    //   758: aload_0
    //   759: bipush #15
    //   761: invokevirtual internalGet : (I)I
    //   764: iastore
    //   765: iload #11
    //   767: bipush #16
    //   769: invokestatic isFieldSet : (II)Z
    //   772: ifeq -> 787
    //   775: aload_0
    //   776: getfield zoneOffsets : [I
    //   779: iconst_1
    //   780: aload_0
    //   781: bipush #16
    //   783: invokevirtual internalGet : (I)I
    //   786: iastore
    //   787: lload #8
    //   789: aload_0
    //   790: getfield zoneOffsets : [I
    //   793: iconst_0
    //   794: iaload
    //   795: aload_0
    //   796: getfield zoneOffsets : [I
    //   799: iconst_1
    //   800: iaload
    //   801: iadd
    //   802: i2l
    //   803: lsub
    //   804: lstore #8
    //   806: aload_0
    //   807: lload #8
    //   809: putfield time : J
    //   812: aload_0
    //   813: iload_1
    //   814: aload_0
    //   815: invokevirtual getSetStateFields : ()I
    //   818: ior
    //   819: iload #11
    //   821: invokespecial computeFields : (II)I
    //   824: istore #12
    //   826: aload_0
    //   827: invokevirtual isLenient : ()Z
    //   830: ifne -> 969
    //   833: iconst_0
    //   834: istore #13
    //   836: iload #13
    //   838: bipush #17
    //   840: if_icmpge -> 969
    //   843: aload_0
    //   844: iload #13
    //   846: invokevirtual isExternallySet : (I)Z
    //   849: ifne -> 855
    //   852: goto -> 963
    //   855: aload_0
    //   856: getfield originalFields : [I
    //   859: iload #13
    //   861: iaload
    //   862: aload_0
    //   863: iload #13
    //   865: invokevirtual internalGet : (I)I
    //   868: if_icmpeq -> 963
    //   871: new java/lang/StringBuilder
    //   874: dup
    //   875: invokespecial <init> : ()V
    //   878: aload_0
    //   879: getfield originalFields : [I
    //   882: iload #13
    //   884: iaload
    //   885: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   888: ldc_w ' -> '
    //   891: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   894: aload_0
    //   895: iload #13
    //   897: invokevirtual internalGet : (I)I
    //   900: invokevirtual append : (I)Ljava/lang/StringBuilder;
    //   903: invokevirtual toString : ()Ljava/lang/String;
    //   906: astore #14
    //   908: aload_0
    //   909: getfield originalFields : [I
    //   912: iconst_0
    //   913: aload_0
    //   914: getfield fields : [I
    //   917: iconst_0
    //   918: aload_0
    //   919: getfield fields : [I
    //   922: arraylength
    //   923: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
    //   926: new java/lang/IllegalArgumentException
    //   929: dup
    //   930: new java/lang/StringBuilder
    //   933: dup
    //   934: invokespecial <init> : ()V
    //   937: iload #13
    //   939: invokestatic getFieldName : (I)Ljava/lang/String;
    //   942: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   945: ldc_w ': '
    //   948: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   951: aload #14
    //   953: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   956: invokevirtual toString : ()Ljava/lang/String;
    //   959: invokespecial <init> : (Ljava/lang/String;)V
    //   962: athrow
    //   963: iinc #13, 1
    //   966: goto -> 836
    //   969: aload_0
    //   970: iload #12
    //   972: invokevirtual setFieldsNormalized : (I)V
    //   975: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #2639	-> 0
    //   #2640	-> 7
    //   #2641	-> 14
    //   #2643	-> 22
    //   #2644	-> 30
    //   #2645	-> 36
    //   #2647	-> 44
    //   #2648	-> 62
    //   #2651	-> 74
    //   #2643	-> 81
    //   #2657	-> 87
    //   #2662	-> 92
    //   #2664	-> 112
    //   #2665	-> 117
    //   #2666	-> 121
    //   #2667	-> 128
    //   #2672	-> 133
    //   #2676	-> 144
    //   #2677	-> 156
    //   #2678	-> 160
    //   #2683	-> 165
    //   #2684	-> 168
    //   #2685	-> 177
    //   #2687	-> 192
    //   #2689	-> 204
    //   #2690	-> 213
    //   #2693	-> 228
    //   #2694	-> 236
    //   #2695	-> 248
    //   #2696	-> 256
    //   #2697	-> 268
    //   #2698	-> 276
    //   #2702	-> 288
    //   #2703	-> 296
    //   #2704	-> 304
    //   #2705	-> 311
    //   #2706	-> 319
    //   #2712	-> 328
    //   #2713	-> 344
    //   #2714	-> 358
    //   #2715	-> 368
    //   #2716	-> 372
    //   #2718	-> 375
    //   #2719	-> 392
    //   #2720	-> 408
    //   #2721	-> 422
    //   #2722	-> 432
    //   #2723	-> 436
    //   #2725	-> 439
    //   #2727	-> 446
    //   #2728	-> 460
    //   #2735	-> 474
    //   #2736	-> 491
    //   #2737	-> 502
    //   #2738	-> 506
    //   #2739	-> 509
    //   #2740	-> 517
    //   #2741	-> 521
    //   #2745	-> 524
    //   #2746	-> 534
    //   #2747	-> 544
    //   #2752	-> 551
    //   #2753	-> 568
    //   #2755	-> 575
    //   #2759	-> 582
    //   #2760	-> 592
    //   #2763	-> 599
    //   #2764	-> 606
    //   #2768	-> 617
    //   #2774	-> 621
    //   #2789	-> 636
    //   #2790	-> 642
    //   #2791	-> 649
    //   #2793	-> 656
    //   #2794	-> 663
    //   #2795	-> 671
    //   #2796	-> 679
    //   #2798	-> 697
    //   #2799	-> 709
    //   #2800	-> 722
    //   #2803	-> 738
    //   #2804	-> 743
    //   #2805	-> 753
    //   #2807	-> 765
    //   #2808	-> 775
    //   #2813	-> 787
    //   #2816	-> 806
    //   #2818	-> 812
    //   #2820	-> 826
    //   #2821	-> 833
    //   #2822	-> 843
    //   #2823	-> 852
    //   #2825	-> 855
    //   #2826	-> 871
    //   #2828	-> 908
    //   #2829	-> 926
    //   #2821	-> 963
    //   #2833	-> 969
    //   #2834	-> 975
  }
  
  private long getFixedDate(BaseCalendar paramBaseCalendar, int paramInt1, int paramInt2) {
    int i = 0;
    if (isFieldSet(paramInt2, 2)) {
      i = internalGet(2);
      if (i > 11) {
        paramInt1 += i / 12;
        i %= 12;
      } else if (i < 0) {
        int[] arrayOfInt = new int[1];
        paramInt1 += CalendarUtils.floorDivide(i, 12, arrayOfInt);
        i = arrayOfInt[0];
      } 
    } 
    long l = paramBaseCalendar.getFixedDate(paramInt1, i + 1, 1, (paramBaseCalendar == gcal) ? this.gdate : null);
    if (isFieldSet(paramInt2, 2)) {
      if (isFieldSet(paramInt2, 5)) {
        if (isSet(5)) {
          l += internalGet(5);
          l--;
        } 
      } else if (isFieldSet(paramInt2, 4)) {
        long l1 = BaseCalendar.getDayOfWeekDateOnOrBefore(l + 6L, 
            getFirstDayOfWeek());
        if (l1 - l >= getMinimalDaysInFirstWeek())
          l1 -= 7L; 
        if (isFieldSet(paramInt2, 7))
          l1 = BaseCalendar.getDayOfWeekDateOnOrBefore(l1 + 6L, 
              internalGet(7)); 
        l = l1 + (7 * (internalGet(4) - 1));
      } else {
        int j;
        byte b;
        if (isFieldSet(paramInt2, 7)) {
          j = internalGet(7);
        } else {
          j = getFirstDayOfWeek();
        } 
        if (isFieldSet(paramInt2, 8)) {
          b = internalGet(8);
        } else {
          b = 1;
        } 
        if (b) {
          l = BaseCalendar.getDayOfWeekDateOnOrBefore(l + (7 * b) - 1L, j);
        } else {
          int k = monthLength(i, paramInt1) + 7 * (b + 1);
          l = BaseCalendar.getDayOfWeekDateOnOrBefore(l + k - 1L, j);
        } 
      } 
    } else {
      if (paramInt1 == this.gregorianCutoverYear && paramBaseCalendar == gcal && l < this.gregorianCutoverDate && this.gregorianCutoverYear != this.gregorianCutoverYearJulian)
        l = this.gregorianCutoverDate; 
      if (isFieldSet(paramInt2, 6)) {
        l += internalGet(6);
        l--;
      } else {
        long l1 = BaseCalendar.getDayOfWeekDateOnOrBefore(l + 6L, 
            getFirstDayOfWeek());
        if (l1 - l >= getMinimalDaysInFirstWeek())
          l1 -= 7L; 
        if (isFieldSet(paramInt2, 7)) {
          int j = internalGet(7);
          if (j != getFirstDayOfWeek())
            l1 = BaseCalendar.getDayOfWeekDateOnOrBefore(l1 + 6L, j); 
        } 
        l = l1 + 7L * (internalGet(3) - 1L);
      } 
    } 
    return l;
  }
  
  private GregorianCalendar getNormalizedCalendar() {
    GregorianCalendar gregorianCalendar;
    if (isFullyNormalized()) {
      gregorianCalendar = this;
    } else {
      gregorianCalendar = (GregorianCalendar)clone();
      gregorianCalendar.setLenient(true);
      gregorianCalendar.complete();
    } 
    return gregorianCalendar;
  }
  
  private static synchronized BaseCalendar getJulianCalendarSystem() {
    if (jcal == null) {
      jcal = (JulianCalendar)CalendarSystem.forName("julian");
      jeras = jcal.getEras();
    } 
    return jcal;
  }
  
  private BaseCalendar getCutoverCalendarSystem() {
    if (this.gregorianCutoverYearJulian < this.gregorianCutoverYear)
      return gcal; 
    return getJulianCalendarSystem();
  }
  
  private boolean isCutoverYear(int paramInt) {
    int i = (this.calsys == gcal) ? this.gregorianCutoverYear : this.gregorianCutoverYearJulian;
    return (paramInt == i);
  }
  
  private long getFixedDateJan1(BaseCalendar.Date paramDate, long paramLong) {
    assert paramDate.getNormalizedYear() == this.gregorianCutoverYear || paramDate
      .getNormalizedYear() == this.gregorianCutoverYearJulian;
    if (this.gregorianCutoverYear != this.gregorianCutoverYearJulian && 
      paramLong >= this.gregorianCutoverDate)
      return this.gregorianCutoverDate; 
    BaseCalendar baseCalendar = getJulianCalendarSystem();
    return baseCalendar.getFixedDate(paramDate.getNormalizedYear(), 1, 1, (BaseCalendar.Date)null);
  }
  
  private long getFixedDateMonth1(BaseCalendar.Date paramDate, long paramLong) {
    long l;
    assert paramDate.getNormalizedYear() == this.gregorianCutoverYear || paramDate
      .getNormalizedYear() == this.gregorianCutoverYearJulian;
    BaseCalendar.Date date = getGregorianCutoverDate();
    if (date.getMonth() == 1 && date
      .getDayOfMonth() == 1)
      return paramLong - paramDate.getDayOfMonth() + 1L; 
    if (paramDate.getMonth() == date.getMonth()) {
      BaseCalendar.Date date1 = getLastJulianDate();
      if (this.gregorianCutoverYear == this.gregorianCutoverYearJulian && date
        .getMonth() == date1.getMonth()) {
        l = jcal.getFixedDate(paramDate.getNormalizedYear(), paramDate
            .getMonth(), 1, (BaseCalendar.Date)null);
      } else {
        l = this.gregorianCutoverDate;
      } 
    } else {
      l = paramLong - paramDate.getDayOfMonth() + 1L;
    } 
    return l;
  }
  
  private BaseCalendar.Date getCalendarDate(long paramLong) {
    BaseCalendar baseCalendar = (paramLong >= this.gregorianCutoverDate) ? gcal : getJulianCalendarSystem();
    BaseCalendar.Date date = (BaseCalendar.Date)baseCalendar.newCalendarDate(TimeZone.NO_TIMEZONE);
    baseCalendar.getCalendarDateFromFixedDate(date, paramLong);
    return date;
  }
  
  private BaseCalendar.Date getGregorianCutoverDate() {
    return getCalendarDate(this.gregorianCutoverDate);
  }
  
  private BaseCalendar.Date getLastJulianDate() {
    return getCalendarDate(this.gregorianCutoverDate - 1L);
  }
  
  private int monthLength(int paramInt1, int paramInt2) {
    return isLeapYear(paramInt2) ? LEAP_MONTH_LENGTH[paramInt1] : MONTH_LENGTH[paramInt1];
  }
  
  private int monthLength(int paramInt) {
    int i = internalGet(1);
    if (internalGetEra() == 0)
      i = 1 - i; 
    return monthLength(paramInt, i);
  }
  
  private int actualMonthLength() {
    int i = this.cdate.getNormalizedYear();
    if (i != this.gregorianCutoverYear && i != this.gregorianCutoverYearJulian)
      return this.calsys.getMonthLength(this.cdate); 
    BaseCalendar.Date date = (BaseCalendar.Date)this.cdate.clone();
    long l1 = this.calsys.getFixedDate(date);
    long l2 = getFixedDateMonth1(date, l1);
    long l3 = l2 + this.calsys.getMonthLength(date);
    if (l3 < this.gregorianCutoverDate)
      return (int)(l3 - l2); 
    if (this.cdate != this.gdate)
      date = gcal.newCalendarDate(TimeZone.NO_TIMEZONE); 
    gcal.getCalendarDateFromFixedDate(date, l3);
    l3 = getFixedDateMonth1(date, l3);
    return (int)(l3 - l2);
  }
  
  private int yearLength(int paramInt) {
    return isLeapYear(paramInt) ? 366 : 365;
  }
  
  private int yearLength() {
    int i = internalGet(1);
    if (internalGetEra() == 0)
      i = 1 - i; 
    return yearLength(i);
  }
  
  private void pinDayOfMonth() {
    int j, i = internalGet(1);
    if (i > this.gregorianCutoverYear || i < this.gregorianCutoverYearJulian) {
      j = monthLength(internalGet(2));
    } else {
      GregorianCalendar gregorianCalendar = getNormalizedCalendar();
      j = gregorianCalendar.getActualMaximum(5);
    } 
    int k = internalGet(5);
    if (k > j)
      set(5, j); 
  }
  
  private long getCurrentFixedDate() {
    return (this.calsys == gcal) ? this.cachedFixedDate : this.calsys.getFixedDate(this.cdate);
  }
  
  private static int getRolledValue(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    assert paramInt1 >= paramInt3 && paramInt1 <= paramInt4;
    int i = paramInt4 - paramInt3 + 1;
    paramInt2 %= i;
    int j = paramInt1 + paramInt2;
    if (j > paramInt4) {
      j -= i;
    } else if (j < paramInt3) {
      j += i;
    } 
    assert j >= paramInt3 && j <= paramInt4;
    return j;
  }
  
  private int internalGetEra() {
    return isSet(0) ? internalGet(0) : 1;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.gdate == null) {
      this.gdate = gcal.newCalendarDate(getZone());
      this.cachedFixedDate = Long.MIN_VALUE;
    } 
    setGregorianChange(this.gregorianCutover);
  }
  
  public ZonedDateTime toZonedDateTime() {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(getTimeInMillis()), 
        getTimeZone().toZoneId());
  }
  
  public static GregorianCalendar from(ZonedDateTime paramZonedDateTime) {
    GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone(paramZonedDateTime.getZone()));
    gregorianCalendar.setGregorianChange(new Date(Long.MIN_VALUE));
    gregorianCalendar.setFirstDayOfWeek(2);
    gregorianCalendar.setMinimalDaysInFirstWeek(4);
    try {
      gregorianCalendar.setTimeInMillis(Math.addExact(Math.multiplyExact(paramZonedDateTime.toEpochSecond(), 1000L), paramZonedDateTime
            .get(ChronoField.MILLI_OF_SECOND)));
    } catch (ArithmeticException arithmeticException) {
      throw new IllegalArgumentException(arithmeticException);
    } 
    return gregorianCalendar;
  }
}
