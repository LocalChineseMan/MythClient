package java.util.spi;

import java.util.Locale;

public abstract class CalendarDataProvider extends LocaleServiceProvider {
  public abstract int getFirstDayOfWeek(Locale paramLocale);
  
  public abstract int getMinimalDaysInFirstWeek(Locale paramLocale);
}
