package org.apache.logging.log4j.core.pattern;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(name = "LoggerFqcnPatternConverter", category = "Converter")
@ConverterKeys({"fqcn"})
@PerformanceSensitive({"allocation"})
public final class LoggerFqcnPatternConverter extends LogEventPatternConverter {
  private static final LoggerFqcnPatternConverter INSTANCE = new LoggerFqcnPatternConverter();
  
  private LoggerFqcnPatternConverter() {
    super("LoggerFqcn", "loggerFqcn");
  }
  
  public static LoggerFqcnPatternConverter newInstance(String[] options) {
    return INSTANCE;
  }
  
  public void format(LogEvent event, StringBuilder toAppendTo) {
    toAppendTo.append(event.getLoggerFqcn());
  }
}
