package java.time.temporal;

import java.time.DateTimeException;
import java.util.Objects;

public interface TemporalAccessor {
  boolean isSupported(TemporalField paramTemporalField);
  
  default ValueRange range(TemporalField paramTemporalField) {
    if (paramTemporalField instanceof ChronoField) {
      if (isSupported(paramTemporalField))
        return paramTemporalField.range(); 
      throw new UnsupportedTemporalTypeException("Unsupported field: " + paramTemporalField);
    } 
    Objects.requireNonNull(paramTemporalField, "field");
    return paramTemporalField.rangeRefinedBy(this);
  }
  
  default int get(TemporalField paramTemporalField) {
    ValueRange valueRange = range(paramTemporalField);
    if (!valueRange.isIntValue())
      throw new UnsupportedTemporalTypeException("Invalid field " + paramTemporalField + " for get() method, use getLong() instead"); 
    long l = getLong(paramTemporalField);
    if (!valueRange.isValidValue(l))
      throw new DateTimeException("Invalid value for " + paramTemporalField + " (valid values " + valueRange + "): " + l); 
    return (int)l;
  }
  
  long getLong(TemporalField paramTemporalField);
  
  default <R> R query(TemporalQuery<R> paramTemporalQuery) {
    if (paramTemporalQuery == TemporalQueries.zoneId() || paramTemporalQuery == 
      TemporalQueries.chronology() || paramTemporalQuery == 
      TemporalQueries.precision())
      return null; 
    return paramTemporalQuery.queryFrom(this);
  }
}
