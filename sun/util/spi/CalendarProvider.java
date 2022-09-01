package sun.util.spi;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.spi.LocaleServiceProvider;

public abstract class CalendarProvider extends LocaleServiceProvider {
  public abstract Calendar getInstance(TimeZone paramTimeZone, Locale paramLocale);
}
