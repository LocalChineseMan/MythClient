package java.util.logging;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;

public abstract class Handler {
  private static final int offValue = Level.OFF.intValue();
  
  private final LogManager manager = LogManager.getLogManager();
  
  private volatile Filter filter;
  
  private volatile Formatter formatter;
  
  private volatile Level logLevel = Level.ALL;
  
  private volatile ErrorManager errorManager = new ErrorManager();
  
  private volatile String encoding;
  
  boolean sealed = true;
  
  public abstract void publish(LogRecord paramLogRecord);
  
  public abstract void flush();
  
  public abstract void close() throws SecurityException;
  
  public synchronized void setFormatter(Formatter paramFormatter) throws SecurityException {
    checkPermission();
    paramFormatter.getClass();
    this.formatter = paramFormatter;
  }
  
  public Formatter getFormatter() {
    return this.formatter;
  }
  
  public synchronized void setEncoding(String paramString) throws SecurityException, UnsupportedEncodingException {
    checkPermission();
    if (paramString != null)
      try {
        if (!Charset.isSupported(paramString))
          throw new UnsupportedEncodingException(paramString); 
      } catch (IllegalCharsetNameException illegalCharsetNameException) {
        throw new UnsupportedEncodingException(paramString);
      }  
    this.encoding = paramString;
  }
  
  public String getEncoding() {
    return this.encoding;
  }
  
  public synchronized void setFilter(Filter paramFilter) throws SecurityException {
    checkPermission();
    this.filter = paramFilter;
  }
  
  public Filter getFilter() {
    return this.filter;
  }
  
  public synchronized void setErrorManager(ErrorManager paramErrorManager) {
    checkPermission();
    if (paramErrorManager == null)
      throw new NullPointerException(); 
    this.errorManager = paramErrorManager;
  }
  
  public ErrorManager getErrorManager() {
    checkPermission();
    return this.errorManager;
  }
  
  protected void reportError(String paramString, Exception paramException, int paramInt) {
    try {
      this.errorManager.error(paramString, paramException, paramInt);
    } catch (Exception exception) {
      System.err.println("Handler.reportError caught:");
      exception.printStackTrace();
    } 
  }
  
  public synchronized void setLevel(Level paramLevel) throws SecurityException {
    if (paramLevel == null)
      throw new NullPointerException(); 
    checkPermission();
    this.logLevel = paramLevel;
  }
  
  public Level getLevel() {
    return this.logLevel;
  }
  
  public boolean isLoggable(LogRecord paramLogRecord) {
    int i = getLevel().intValue();
    if (paramLogRecord.getLevel().intValue() < i || i == offValue)
      return false; 
    Filter filter = getFilter();
    if (filter == null)
      return true; 
    return filter.isLoggable(paramLogRecord);
  }
  
  void checkPermission() throws SecurityException {
    if (this.sealed)
      this.manager.checkPermission(); 
  }
}
