package org.apache.logging.log4j.core.pattern;

import java.util.List;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.EnglishEnums;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(name = "encode", category = "Converter")
@ConverterKeys({"enc", "encode"})
@PerformanceSensitive({"allocation"})
public final class EncodingPatternConverter extends LogEventPatternConverter {
  private final List<PatternFormatter> formatters;
  
  private final EscapeFormat escapeFormat;
  
  private EncodingPatternConverter(List<PatternFormatter> formatters, EscapeFormat escapeFormat) {
    super("encode", "encode");
    this.formatters = formatters;
    this.escapeFormat = escapeFormat;
  }
  
  public boolean handlesThrowable() {
    return (this.formatters != null && this.formatters.stream()
      .map(PatternFormatter::getConverter)
      .anyMatch(LogEventPatternConverter::handlesThrowable));
  }
  
  public static EncodingPatternConverter newInstance(Configuration config, String[] options) {
    if (options.length > 2 || options.length == 0) {
      LOGGER.error("Incorrect number of options on escape. Expected 1 or 2, but received {}", 
          Integer.valueOf(options.length));
      return null;
    } 
    if (options[0] == null) {
      LOGGER.error("No pattern supplied on escape");
      return null;
    } 
    EscapeFormat escapeFormat = (options.length < 2) ? EscapeFormat.HTML : (EscapeFormat)EnglishEnums.valueOf(EscapeFormat.class, options[1], (Enum)EscapeFormat.HTML);
    PatternParser parser = PatternLayout.createPatternParser(config);
    List<PatternFormatter> formatters = parser.parse(options[0]);
    return new EncodingPatternConverter(formatters, escapeFormat);
  }
  
  public void format(LogEvent event, StringBuilder toAppendTo) {
    int start = toAppendTo.length();
    for (int i = 0; i < this.formatters.size(); i++)
      ((PatternFormatter)this.formatters.get(i)).format(event, toAppendTo); 
    this.escapeFormat.escape(toAppendTo, start);
  }
  
  private enum EncodingPatternConverter {
  
  }
}
