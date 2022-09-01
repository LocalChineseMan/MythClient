package org.apache.logging.log4j.core.util.datetime;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.core.util.Throwables;

public class FastDatePrinter implements DatePrinter, Serializable {
  private static final long serialVersionUID = 1L;
  
  public static final int FULL = 0;
  
  public static final int LONG = 1;
  
  public static final int MEDIUM = 2;
  
  public static final int SHORT = 3;
  
  private final String mPattern;
  
  private final TimeZone mTimeZone;
  
  private final Locale mLocale;
  
  private transient Rule[] mRules;
  
  private transient int mMaxLengthEstimate;
  
  private static final int MAX_DIGITS = 10;
  
  protected FastDatePrinter(String pattern, TimeZone timeZone, Locale locale) {
    this.mPattern = pattern;
    this.mTimeZone = timeZone;
    this.mLocale = locale;
    init();
  }
  
  private void init() {
    List<Rule> rulesList = parsePattern();
    this.mRules = rulesList.<Rule>toArray(new Rule[rulesList.size()]);
    int len = 0;
    for (int i = this.mRules.length; --i >= 0;)
      len += this.mRules[i].estimateLength(); 
    this.mMaxLengthEstimate = len;
  }
  
  protected List<Rule> parsePattern() {
    DateFormatSymbols symbols = new DateFormatSymbols(this.mLocale);
    List<Rule> rules = new ArrayList<>();
    String[] ERAs = symbols.getEras();
    String[] months = symbols.getMonths();
    String[] shortMonths = symbols.getShortMonths();
    String[] weekdays = symbols.getWeekdays();
    String[] shortWeekdays = symbols.getShortWeekdays();
    String[] AmPmStrings = symbols.getAmPmStrings();
    int length = this.mPattern.length();
    int[] indexRef = new int[1];
    for (int i = 0; i < length; i++) {
      TextField textField3;
      Rule rule4;
      UnpaddedMonthField unpaddedMonthField;
      Rule rule3;
      TwelveHourField twelveHourField;
      Rule rule2;
      TextField textField2;
      DayInWeekField dayInWeekField;
      Rule rule1;
      TextField textField1;
      TwentyFourHourField twentyFourHourField;
      Rule rule;
      Iso8601_Rule iso8601_Rule;
      TimeZoneNameRule timeZoneNameRule;
      TimeZoneNumberRule timeZoneNumberRule;
      StringLiteral stringLiteral;
      String sub;
      indexRef[0] = i;
      String token = parseToken(this.mPattern, indexRef);
      i = indexRef[0];
      int tokenLen = token.length();
      if (tokenLen == 0)
        break; 
      char c = token.charAt(0);
      switch (c) {
        case 'G':
          textField3 = new TextField(0, ERAs);
          break;
        case 'Y':
        case 'y':
          if (tokenLen == 2) {
            TwoDigitYearField twoDigitYearField = TwoDigitYearField.INSTANCE;
          } else {
            rule4 = selectNumberRule(1, (tokenLen < 4) ? 4 : tokenLen);
          } 
          if (c == 'Y')
            WeekYear weekYear = new WeekYear((NumberRule)rule4); 
          break;
        case 'M':
          if (tokenLen >= 4) {
            TextField textField = new TextField(2, months);
            break;
          } 
          if (tokenLen == 3) {
            TextField textField = new TextField(2, shortMonths);
            break;
          } 
          if (tokenLen == 2) {
            rule4 = TwoDigitMonthField.INSTANCE;
            break;
          } 
          unpaddedMonthField = UnpaddedMonthField.INSTANCE;
          break;
        case 'd':
          rule3 = selectNumberRule(5, tokenLen);
          break;
        case 'h':
          twelveHourField = new TwelveHourField(selectNumberRule(10, tokenLen));
          break;
        case 'H':
          rule2 = selectNumberRule(11, tokenLen);
          break;
        case 'm':
          rule2 = selectNumberRule(12, tokenLen);
          break;
        case 's':
          rule2 = selectNumberRule(13, tokenLen);
          break;
        case 'S':
          rule2 = selectNumberRule(14, tokenLen);
          break;
        case 'E':
          textField2 = new TextField(7, (tokenLen < 4) ? shortWeekdays : weekdays);
          break;
        case 'u':
          dayInWeekField = new DayInWeekField(selectNumberRule(7, tokenLen));
          break;
        case 'D':
          rule1 = selectNumberRule(6, tokenLen);
          break;
        case 'F':
          rule1 = selectNumberRule(8, tokenLen);
          break;
        case 'w':
          rule1 = selectNumberRule(3, tokenLen);
          break;
        case 'W':
          rule1 = selectNumberRule(4, tokenLen);
          break;
        case 'a':
          textField1 = new TextField(9, AmPmStrings);
          break;
        case 'k':
          twentyFourHourField = new TwentyFourHourField(selectNumberRule(11, tokenLen));
          break;
        case 'K':
          rule = selectNumberRule(10, tokenLen);
          break;
        case 'X':
          iso8601_Rule = Iso8601_Rule.getRule(tokenLen);
          break;
        case 'z':
          if (tokenLen >= 4) {
            TimeZoneNameRule timeZoneNameRule1 = new TimeZoneNameRule(this.mTimeZone, this.mLocale, 1);
            break;
          } 
          timeZoneNameRule = new TimeZoneNameRule(this.mTimeZone, this.mLocale, 0);
          break;
        case 'Z':
          if (tokenLen == 1) {
            TimeZoneNumberRule timeZoneNumberRule1 = TimeZoneNumberRule.INSTANCE_NO_COLON;
            break;
          } 
          if (tokenLen == 2) {
            Iso8601_Rule iso8601_Rule1 = Iso8601_Rule.ISO8601_HOURS_COLON_MINUTES;
            break;
          } 
          timeZoneNumberRule = TimeZoneNumberRule.INSTANCE_COLON;
          break;
        case '\'':
          sub = token.substring(1);
          if (sub.length() == 1) {
            Rule rule5 = new CharacterLiteral(sub.charAt(0));
            break;
          } 
          stringLiteral = new StringLiteral(sub);
          break;
        default:
          throw new IllegalArgumentException("Illegal pattern component: " + token);
      } 
      rules.add(stringLiteral);
    } 
    return rules;
  }
  
  protected String parseToken(String pattern, int[] indexRef) {
    StringBuilder buf = new StringBuilder();
    int i = indexRef[0];
    int length = pattern.length();
    char c = pattern.charAt(i);
    if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
      buf.append(c);
      while (i + 1 < length) {
        char peek = pattern.charAt(i + 1);
        if (peek == c) {
          buf.append(c);
          i++;
        } 
      } 
    } else {
      buf.append('\'');
      boolean inLiteral = false;
      for (; i < length; i++) {
        c = pattern.charAt(i);
        if (c == '\'') {
          if (i + 1 < length && pattern.charAt(i + 1) == '\'') {
            i++;
            buf.append(c);
          } else {
            inLiteral = !inLiteral;
          } 
        } else {
          if (!inLiteral && ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
            i--;
            break;
          } 
          buf.append(c);
        } 
      } 
    } 
    indexRef[0] = i;
    return buf.toString();
  }
  
  protected NumberRule selectNumberRule(int field, int padding) {
    switch (padding) {
      case 1:
        return (NumberRule)new UnpaddedNumberField(field);
      case 2:
        return new TwoDigitNumberField(field);
    } 
    return new PaddedNumberField(field, padding);
  }
  
  @Deprecated
  public StringBuilder format(Object obj, StringBuilder toAppendTo, FieldPosition pos) {
    if (obj instanceof Date)
      return format((Date)obj, toAppendTo); 
    if (obj instanceof Calendar)
      return format((Calendar)obj, toAppendTo); 
    if (obj instanceof Long)
      return format(((Long)obj).longValue(), toAppendTo); 
    throw new IllegalArgumentException("Unknown class: " + ((obj == null) ? "<null>" : obj
        .getClass().getName()));
  }
  
  String format(Object obj) {
    if (obj instanceof Date)
      return format((Date)obj); 
    if (obj instanceof Calendar)
      return format((Calendar)obj); 
    if (obj instanceof Long)
      return format(((Long)obj).longValue()); 
    throw new IllegalArgumentException("Unknown class: " + ((obj == null) ? "<null>" : obj
        .getClass().getName()));
  }
  
  public String format(long millis) {
    Calendar c = newCalendar();
    c.setTimeInMillis(millis);
    return applyRulesToString(c);
  }
  
  private String applyRulesToString(Calendar c) {
    return ((StringBuilder)applyRules(c, new StringBuilder(this.mMaxLengthEstimate))).toString();
  }
  
  private Calendar newCalendar() {
    return Calendar.getInstance(this.mTimeZone, this.mLocale);
  }
  
  public String format(Date date) {
    Calendar c = newCalendar();
    c.setTime(date);
    return applyRulesToString(c);
  }
  
  public String format(Calendar calendar) {
    return ((StringBuilder)format(calendar, new StringBuilder(this.mMaxLengthEstimate))).toString();
  }
  
  public <B extends Appendable> B format(long millis, B buf) {
    Calendar c = newCalendar();
    c.setTimeInMillis(millis);
    return applyRules(c, buf);
  }
  
  public <B extends Appendable> B format(Date date, B buf) {
    Calendar c = newCalendar();
    c.setTime(date);
    return applyRules(c, buf);
  }
  
  public <B extends Appendable> B format(Calendar calendar, B buf) {
    if (!calendar.getTimeZone().equals(this.mTimeZone)) {
      calendar = (Calendar)calendar.clone();
      calendar.setTimeZone(this.mTimeZone);
    } 
    return applyRules(calendar, buf);
  }
  
  @Deprecated
  protected StringBuffer applyRules(Calendar calendar, StringBuffer buf) {
    return applyRules(calendar, buf);
  }
  
  private <B extends Appendable> B applyRules(Calendar calendar, B buf) {
    try {
      for (Rule rule : this.mRules)
        rule.appendTo((Appendable)buf, calendar); 
    } catch (IOException ioe) {
      Throwables.rethrow(ioe);
    } 
    return buf;
  }
  
  public String getPattern() {
    return this.mPattern;
  }
  
  public TimeZone getTimeZone() {
    return this.mTimeZone;
  }
  
  public Locale getLocale() {
    return this.mLocale;
  }
  
  public int getMaxLengthEstimate() {
    return this.mMaxLengthEstimate;
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof FastDatePrinter))
      return false; 
    FastDatePrinter other = (FastDatePrinter)obj;
    return (this.mPattern.equals(other.mPattern) && this.mTimeZone
      .equals(other.mTimeZone) && this.mLocale
      .equals(other.mLocale));
  }
  
  public int hashCode() {
    return this.mPattern.hashCode() + 13 * (this.mTimeZone.hashCode() + 13 * this.mLocale.hashCode());
  }
  
  public String toString() {
    return "FastDatePrinter[" + this.mPattern + "," + this.mLocale + "," + this.mTimeZone.getID() + "]";
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    init();
  }
  
  private static void appendDigits(Appendable buffer, int value) throws IOException {
    buffer.append((char)(value / 10 + 48));
    buffer.append((char)(value % 10 + 48));
  }
  
  private static void appendFullDigits(Appendable buffer, int value, int minFieldWidth) throws IOException {
    if (value < 10000) {
      int nDigits = 4;
      if (value < 1000) {
        nDigits--;
        if (value < 100) {
          nDigits--;
          if (value < 10)
            nDigits--; 
        } 
      } 
      for (int i = minFieldWidth - nDigits; i > 0; i--)
        buffer.append('0'); 
      switch (nDigits) {
        case 4:
          buffer.append((char)(value / 1000 + 48));
          value %= 1000;
        case 3:
          if (value >= 100) {
            buffer.append((char)(value / 100 + 48));
            value %= 100;
          } else {
            buffer.append('0');
          } 
        case 2:
          if (value >= 10) {
            buffer.append((char)(value / 10 + 48));
            value %= 10;
          } else {
            buffer.append('0');
          } 
        case 1:
          buffer.append((char)(value + 48));
          break;
      } 
    } else {
      char[] work = new char[10];
      int digit = 0;
      while (value != 0) {
        work[digit++] = (char)(value % 10 + 48);
        value /= 10;
      } 
      while (digit < minFieldWidth) {
        buffer.append('0');
        minFieldWidth--;
      } 
      while (--digit >= 0)
        buffer.append(work[digit]); 
    } 
  }
  
  private static interface Rule {
    int estimateLength();
    
    void appendTo(Appendable param1Appendable, Calendar param1Calendar) throws IOException;
  }
  
  private static interface NumberRule extends Rule {
    void appendTo(Appendable param1Appendable, int param1Int) throws IOException;
  }
  
  private static class CharacterLiteral implements Rule {
    private final char mValue;
    
    CharacterLiteral(char value) {
      this.mValue = value;
    }
    
    public int estimateLength() {
      return 1;
    }
    
    public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
      buffer.append(this.mValue);
    }
  }
  
  private static class FastDatePrinter {}
  
  private static class FastDatePrinter {}
  
  private static class FastDatePrinter {}
  
  private static class FastDatePrinter {}
  
  private static class PaddedNumberField implements NumberRule {
    private final int mField;
    
    private final int mSize;
    
    PaddedNumberField(int field, int size) {
      if (size < 3)
        throw new IllegalArgumentException(); 
      this.mField = field;
      this.mSize = size;
    }
    
    public int estimateLength() {
      return this.mSize;
    }
    
    public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
      appendTo(buffer, calendar.get(this.mField));
    }
    
    public final void appendTo(Appendable buffer, int value) throws IOException {
      FastDatePrinter.appendFullDigits(buffer, value, this.mSize);
    }
  }
  
  private static class TwoDigitNumberField implements NumberRule {
    private final int mField;
    
    TwoDigitNumberField(int field) {
      this.mField = field;
    }
    
    public int estimateLength() {
      return 2;
    }
    
    public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
      appendTo(buffer, calendar.get(this.mField));
    }
    
    public final void appendTo(Appendable buffer, int value) throws IOException {
      if (value < 100) {
        FastDatePrinter.appendDigits(buffer, value);
      } else {
        FastDatePrinter.appendFullDigits(buffer, value, 2);
      } 
    }
  }
  
  private static class FastDatePrinter {}
  
  private static class TwoDigitMonthField implements NumberRule {
    static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();
    
    public int estimateLength() {
      return 2;
    }
    
    public void appendTo(Appendable buffer, Calendar calendar) throws IOException {
      appendTo(buffer, calendar.get(2) + 1);
    }
    
    public final void appendTo(Appendable buffer, int value) throws IOException {
      FastDatePrinter.appendDigits(buffer, value);
    }
  }
  
  private static final ConcurrentMap<TimeZoneDisplayKey, String> cTimeZoneDisplayCache = new ConcurrentHashMap<>(7);
  
  static String getTimeZoneDisplay(TimeZone tz, boolean daylight, int style, Locale locale) {
    TimeZoneDisplayKey key = new TimeZoneDisplayKey(tz, daylight, style, locale);
    String value = cTimeZoneDisplayCache.get(key);
    if (value == null) {
      value = tz.getDisplayName(daylight, style, locale);
      String prior = cTimeZoneDisplayCache.putIfAbsent(key, value);
      if (prior != null)
        value = prior; 
    } 
    return value;
  }
  
  private static class FastDatePrinter {}
  
  private static class FastDatePrinter {}
  
  private static class FastDatePrinter {}
  
  private static class FastDatePrinter {}
  
  private static class FastDatePrinter {}
  
  private static class FastDatePrinter {}
  
  private static class FastDatePrinter {}
  
  private static class FastDatePrinter {}
}
