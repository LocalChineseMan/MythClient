package org.apache.logging.log4j.core.layout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.zip.DeflaterOutputStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.layout.internal.ListChecker;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.net.Severity;
import org.apache.logging.log4j.core.util.JsonUtils;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StringBuilderFormattable;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.TriConsumer;

@Plugin(name = "GelfLayout", category = "Core", elementType = "layout", printObject = true)
public final class GelfLayout extends AbstractStringLayout {
  private static final char C = ',';
  
  private static final int COMPRESSION_THRESHOLD = 1024;
  
  private static final char Q = '"';
  
  private static final String QC = "\",";
  
  private static final String QU = "\"_";
  
  private final KeyValuePair[] additionalFields;
  
  private final int compressionThreshold;
  
  private final CompressionType compressionType;
  
  private final String host;
  
  private final boolean includeStacktrace;
  
  private final boolean includeThreadContext;
  
  private final boolean includeMapMessage;
  
  private final boolean includeNullDelimiter;
  
  private final boolean includeNewLineDelimiter;
  
  private final boolean omitEmptyFields;
  
  private final PatternLayout layout;
  
  private final FieldWriter mdcWriter;
  
  private final FieldWriter mapWriter;
  
  @Deprecated
  public GelfLayout(String host, KeyValuePair[] additionalFields, CompressionType compressionType, int compressionThreshold, boolean includeStacktrace) {
    this((Configuration)null, host, additionalFields, compressionType, compressionThreshold, includeStacktrace, true, true, false, false, false, (ListChecker)null, (ListChecker)null, (PatternLayout)null, "", "");
  }
  
  private GelfLayout(Configuration config, String host, KeyValuePair[] additionalFields, CompressionType compressionType, int compressionThreshold, boolean includeStacktrace, boolean includeThreadContext, boolean includeMapMessage, boolean includeNullDelimiter, boolean includeNewLineDelimiter, boolean omitEmptyFields, ListChecker mdcChecker, ListChecker mapChecker, PatternLayout patternLayout, String mdcPrefix, String mapPrefix) {
    super(config, StandardCharsets.UTF_8, (AbstractStringLayout.Serializer)null, (AbstractStringLayout.Serializer)null);
    this.host = (host != null) ? host : NetUtils.getLocalHostname();
    this.additionalFields = (additionalFields != null) ? additionalFields : KeyValuePair.EMPTY_ARRAY;
    if (config == null)
      for (KeyValuePair additionalField : this.additionalFields) {
        if (valueNeedsLookup(additionalField.getValue()))
          throw new IllegalArgumentException("configuration needs to be set when there are additional fields with variables"); 
      }  
    this.compressionType = compressionType;
    this.compressionThreshold = compressionThreshold;
    this.includeStacktrace = includeStacktrace;
    this.includeThreadContext = includeThreadContext;
    this.includeMapMessage = includeMapMessage;
    this.includeNullDelimiter = includeNullDelimiter;
    this.includeNewLineDelimiter = includeNewLineDelimiter;
    this.omitEmptyFields = omitEmptyFields;
    if (includeNullDelimiter && compressionType != CompressionType.OFF)
      throw new IllegalArgumentException("null delimiter cannot be used with compression"); 
    this.mdcWriter = new FieldWriter(this, mdcChecker, mdcPrefix);
    this.mapWriter = new FieldWriter(this, mapChecker, mapPrefix);
    this.layout = patternLayout;
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("host=").append(this.host);
    sb.append(", compressionType=").append(this.compressionType.toString());
    sb.append(", compressionThreshold=").append(this.compressionThreshold);
    sb.append(", includeStackTrace=").append(this.includeStacktrace);
    sb.append(", includeThreadContext=").append(this.includeThreadContext);
    sb.append(", includeNullDelimiter=").append(this.includeNullDelimiter);
    sb.append(", includeNewLineDelimiter=").append(this.includeNewLineDelimiter);
    String threadVars = this.mdcWriter.getChecker().toString();
    if (threadVars.length() > 0)
      sb.append(", ").append(threadVars); 
    String mapVars = this.mapWriter.getChecker().toString();
    if (mapVars.length() > 0)
      sb.append(", ").append(mapVars); 
    if (this.layout != null)
      sb.append(", PatternLayout{").append(this.layout.toString()).append("}"); 
    return sb.toString();
  }
  
  @Deprecated
  public static GelfLayout createLayout(@PluginAttribute("host") String host, @PluginElement("AdditionalField") KeyValuePair[] additionalFields, @PluginAttribute(value = "compressionType", defaultString = "GZIP") CompressionType compressionType, @PluginAttribute(value = "compressionThreshold", defaultInt = 1024) int compressionThreshold, @PluginAttribute(value = "includeStacktrace", defaultBoolean = true) boolean includeStacktrace) {
    return new GelfLayout(null, host, additionalFields, compressionType, compressionThreshold, includeStacktrace, true, true, false, false, false, null, null, null, "", "");
  }
  
  @PluginBuilderFactory
  public static <B extends Builder<B>> B newBuilder() {
    return (B)(new Builder()).asBuilder();
  }
  
  public Map<String, String> getContentFormat() {
    return Collections.emptyMap();
  }
  
  public String getContentType() {
    return "application/json; charset=" + getCharset();
  }
  
  public byte[] toByteArray(LogEvent event) {
    StringBuilder text = toText(event, getStringBuilder(), false);
    byte[] bytes = getBytes(text.toString());
    return (this.compressionType != CompressionType.OFF && bytes.length > this.compressionThreshold) ? compress(bytes) : bytes;
  }
  
  public void encode(LogEvent event, ByteBufferDestination destination) {
    if (this.compressionType != CompressionType.OFF) {
      super.encode(event, destination);
      return;
    } 
    StringBuilder text = toText(event, getStringBuilder(), true);
    Encoder<StringBuilder> helper = getStringBuilderEncoder();
    helper.encode(text, destination);
  }
  
  public boolean requiresLocation() {
    return (Objects.nonNull(this.layout) && this.layout.requiresLocation());
  }
  
  private byte[] compress(byte[] bytes) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream(this.compressionThreshold / 8);
      try (DeflaterOutputStream stream = this.compressionType.createDeflaterOutputStream(baos)) {
        if (stream == null)
          return bytes; 
        stream.write(bytes);
        stream.finish();
      } 
      return baos.toByteArray();
    } catch (IOException e) {
      StatusLogger.getLogger().error(e);
      return bytes;
    } 
  }
  
  public String toSerializable(LogEvent event) {
    StringBuilder text = toText(event, getStringBuilder(), false);
    return text.toString();
  }
  
  private StringBuilder toText(LogEvent event, StringBuilder builder, boolean gcFree) {
    builder.append('{');
    builder.append("\"version\":\"1.1\",");
    builder.append("\"host\":\"");
    JsonUtils.quoteAsString(toNullSafeString(this.host), builder);
    builder.append("\",");
    builder.append("\"timestamp\":").append(formatTimestamp(event.getTimeMillis())).append(',');
    builder.append("\"level\":").append(formatLevel(event.getLevel())).append(',');
    if (event.getThreadName() != null) {
      builder.append("\"_thread\":\"");
      JsonUtils.quoteAsString(event.getThreadName(), builder);
      builder.append("\",");
    } 
    if (event.getLoggerName() != null) {
      builder.append("\"_logger\":\"");
      JsonUtils.quoteAsString(event.getLoggerName(), builder);
      builder.append("\",");
    } 
    if (this.additionalFields.length > 0) {
      StrSubstitutor strSubstitutor = getConfiguration().getStrSubstitutor();
      for (KeyValuePair additionalField : this.additionalFields) {
        String value = valueNeedsLookup(additionalField.getValue()) ? strSubstitutor.replace(event, additionalField.getValue()) : additionalField.getValue();
        if (Strings.isNotEmpty(value) || !this.omitEmptyFields) {
          builder.append("\"_");
          JsonUtils.quoteAsString(additionalField.getKey(), builder);
          builder.append("\":\"");
          JsonUtils.quoteAsString(toNullSafeString(value), builder);
          builder.append("\",");
        } 
      } 
    } 
    if (this.includeThreadContext)
      event.getContextData().forEach((TriConsumer)this.mdcWriter, builder); 
    if (this.includeMapMessage && event.getMessage() instanceof MapMessage)
      ((MapMessage)event.getMessage()).forEach((key, value) -> this.mapWriter.accept(key, value, builder)); 
    if (event.getThrown() != null || this.layout != null) {
      builder.append("\"full_message\":\"");
      if (this.layout != null) {
        StringBuilder messageBuffer = getMessageStringBuilder();
        this.layout.serialize(event, messageBuffer);
        JsonUtils.quoteAsString(messageBuffer, builder);
      } else if (this.includeStacktrace) {
        JsonUtils.quoteAsString(formatThrowable(event.getThrown()), builder);
      } else {
        JsonUtils.quoteAsString(event.getThrown().toString(), builder);
      } 
      builder.append("\",");
    } 
    builder.append("\"short_message\":\"");
    Message message = event.getMessage();
    if (message instanceof CharSequence) {
      JsonUtils.quoteAsString((CharSequence)message, builder);
    } else if (gcFree && message instanceof StringBuilderFormattable) {
      StringBuilder messageBuffer = getMessageStringBuilder();
      try {
        ((StringBuilderFormattable)message).formatTo(messageBuffer);
        JsonUtils.quoteAsString(messageBuffer, builder);
      } finally {
        trimToMaxSize(messageBuffer);
      } 
    } else {
      JsonUtils.quoteAsString(toNullSafeString(message.getFormattedMessage()), builder);
    } 
    builder.append('"');
    builder.append('}');
    if (this.includeNullDelimiter)
      builder.append(false); 
    if (this.includeNewLineDelimiter)
      builder.append('\n'); 
    return builder;
  }
  
  private static boolean valueNeedsLookup(String value) {
    return (value != null && value.contains("${"));
  }
  
  private static final ThreadLocal<StringBuilder> messageStringBuilder = new ThreadLocal<>();
  
  private static StringBuilder getMessageStringBuilder() {
    StringBuilder result = messageStringBuilder.get();
    if (result == null) {
      result = new StringBuilder(1024);
      messageStringBuilder.set(result);
    } 
    result.setLength(0);
    return result;
  }
  
  private static CharSequence toNullSafeString(CharSequence s) {
    return (s == null) ? "" : s;
  }
  
  static CharSequence formatTimestamp(long timeMillis) {
    if (timeMillis < 1000L)
      return "0"; 
    StringBuilder builder = getTimestampStringBuilder();
    builder.append(timeMillis);
    builder.insert(builder.length() - 3, '.');
    return builder;
  }
  
  private static final ThreadLocal<StringBuilder> timestampStringBuilder = new ThreadLocal<>();
  
  private static StringBuilder getTimestampStringBuilder() {
    StringBuilder result = timestampStringBuilder.get();
    if (result == null) {
      result = new StringBuilder(20);
      timestampStringBuilder.set(result);
    } 
    result.setLength(0);
    return result;
  }
  
  private int formatLevel(Level level) {
    return Severity.getSeverity(level).getCode();
  }
  
  static CharSequence formatThrowable(Throwable throwable) {
    StringWriter sw = new StringWriter(2048);
    PrintWriter pw = new PrintWriter(sw);
    throwable.printStackTrace(pw);
    pw.flush();
    return sw.getBuffer();
  }
  
  private class GelfLayout {}
  
  public static class GelfLayout {}
  
  public enum GelfLayout {
  
  }
}
