package org.apache.logging.log4j.core.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilderFormattable;

@Plugin(name = "MessagePatternConverter", category = "Converter")
@ConverterKeys({"m", "msg", "message"})
@PerformanceSensitive({"allocation"})
public class MessagePatternConverter extends LogEventPatternConverter {
  private static final String LOOKUPS = "lookups";
  
  private static final String NOLOOKUPS = "nolookups";
  
  private MessagePatternConverter() {
    super("Message", "message");
  }
  
  private static TextRenderer loadMessageRenderer(String[] options) {
    if (options != null)
      for (String option : options) {
        switch (option.toUpperCase(Locale.ROOT)) {
          case "ANSI":
            if (Loader.isJansiAvailable())
              return (TextRenderer)new JAnsiTextRenderer(options, JAnsiTextRenderer.DefaultMessageStyleMap); 
            StatusLogger.getLogger()
              .warn("You requested ANSI message rendering but JANSI is not on the classpath.");
            return null;
          case "HTML":
            return (TextRenderer)new HtmlTextRenderer(options);
        } 
      }  
    return null;
  }
  
  public static MessagePatternConverter newInstance(Configuration config, String[] options) {
    RenderingPatternConverter renderingPatternConverter;
    String[] formats = withoutLookupOptions(options);
    TextRenderer textRenderer = loadMessageRenderer(formats);
    MessagePatternConverter result = (formats == null || formats.length == 0) ? SimpleMessagePatternConverter.INSTANCE : (MessagePatternConverter)new FormattedMessagePatternConverter(formats);
    if (textRenderer != null)
      renderingPatternConverter = new RenderingPatternConverter(result, textRenderer); 
    return (MessagePatternConverter)renderingPatternConverter;
  }
  
  private static String[] withoutLookupOptions(String[] options) {
    if (options == null || options.length == 0)
      return options; 
    List<String> results = new ArrayList<>(options.length);
    for (String option : options) {
      if ("lookups".equalsIgnoreCase(option) || "nolookups".equalsIgnoreCase(option)) {
        LOGGER.info("The {} option will be ignored. Message Lookups are no longer supported.", option);
      } else {
        results.add(option);
      } 
    } 
    return results.<String>toArray(new String[0]);
  }
  
  public void format(LogEvent event, StringBuilder toAppendTo) {
    throw new UnsupportedOperationException();
  }
  
  private static final class MessagePatternConverter {}
  
  private static final class MessagePatternConverter {}
  
  private static final class SimpleMessagePatternConverter extends MessagePatternConverter {
    private static final MessagePatternConverter INSTANCE = new SimpleMessagePatternConverter();
    
    public void format(LogEvent event, StringBuilder toAppendTo) {
      Message msg = event.getMessage();
      if (msg instanceof StringBuilderFormattable) {
        ((StringBuilderFormattable)msg).formatTo(toAppendTo);
      } else if (msg != null) {
        toAppendTo.append(msg.getFormattedMessage());
      } 
    }
  }
}
