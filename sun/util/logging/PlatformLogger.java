package sun.util.logging;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import sun.misc.JavaLangAccess;
import sun.misc.SharedSecrets;

public class PlatformLogger {
  private static final int OFF = 2147483647;
  
  private static final int SEVERE = 1000;
  
  private static final int WARNING = 900;
  
  private static final int INFO = 800;
  
  private static final int CONFIG = 700;
  
  private static final int FINE = 500;
  
  private static final int FINER = 400;
  
  private static final int FINEST = 300;
  
  private static final int ALL = -2147483648;
  
  public enum Level {
    ALL, FINEST, FINER, FINE, CONFIG, INFO, WARNING, SEVERE, OFF;
    
    Object javaLevel;
    
    private static final int[] LEVEL_VALUES = new int[] { Integer.MIN_VALUE, 300, 400, 500, 700, 800, 900, 1000, Integer.MAX_VALUE };
    
    static {
    
    }
    
    public int intValue() {
      return LEVEL_VALUES[ordinal()];
    }
  }
  
  private static final Level DEFAULT_LEVEL = Level.INFO;
  
  private static boolean loggingEnabled = ((Boolean)AccessController.<Boolean>doPrivileged(new PrivilegedAction<Boolean>() {
        public Boolean run() {
          String str1 = System.getProperty("java.util.logging.config.class");
          String str2 = System.getProperty("java.util.logging.config.file");
          return Boolean.valueOf((str1 != null || str2 != null));
        }
      })).booleanValue();
  
  static {
    try {
      Class.forName("sun.util.logging.PlatformLogger$DefaultLoggerProxy", false, PlatformLogger.class
          
          .getClassLoader());
      Class.forName("sun.util.logging.PlatformLogger$JavaLoggerProxy", false, PlatformLogger.class
          
          .getClassLoader());
    } catch (ClassNotFoundException classNotFoundException) {
      throw new InternalError(classNotFoundException);
    } 
  }
  
  private static Map<String, WeakReference<PlatformLogger>> loggers = new HashMap<>();
  
  private volatile LoggerProxy loggerProxy;
  
  private volatile JavaLoggerProxy javaLoggerProxy;
  
  public static synchronized PlatformLogger getLogger(String paramString) {
    PlatformLogger platformLogger = null;
    WeakReference<PlatformLogger> weakReference = loggers.get(paramString);
    if (weakReference != null)
      platformLogger = weakReference.get(); 
    if (platformLogger == null) {
      platformLogger = new PlatformLogger(paramString);
      loggers.put(paramString, new WeakReference<>(platformLogger));
    } 
    return platformLogger;
  }
  
  public static synchronized void redirectPlatformLoggers() {
    if (loggingEnabled || !LoggingSupport.isAvailable())
      return; 
    loggingEnabled = true;
    for (Map.Entry<String, WeakReference<PlatformLogger>> entry : loggers.entrySet()) {
      WeakReference<PlatformLogger> weakReference = (WeakReference)entry.getValue();
      PlatformLogger platformLogger = weakReference.get();
      if (platformLogger != null)
        platformLogger.redirectToJavaLoggerProxy(); 
    } 
  }
  
  private void redirectToJavaLoggerProxy() {
    DefaultLoggerProxy defaultLoggerProxy = DefaultLoggerProxy.class.cast(this.loggerProxy);
    JavaLoggerProxy javaLoggerProxy = new JavaLoggerProxy(defaultLoggerProxy.name, defaultLoggerProxy.level);
    this.javaLoggerProxy = javaLoggerProxy;
    this.loggerProxy = javaLoggerProxy;
  }
  
  private PlatformLogger(String paramString) {
    if (loggingEnabled) {
      this.loggerProxy = this.javaLoggerProxy = new JavaLoggerProxy(paramString);
    } else {
      this.loggerProxy = new DefaultLoggerProxy(paramString);
    } 
  }
  
  public boolean isEnabled() {
    return this.loggerProxy.isEnabled();
  }
  
  public String getName() {
    return this.loggerProxy.name;
  }
  
  public boolean isLoggable(Level paramLevel) {
    if (paramLevel == null)
      throw new NullPointerException(); 
    JavaLoggerProxy javaLoggerProxy = this.javaLoggerProxy;
    return (javaLoggerProxy != null) ? javaLoggerProxy.isLoggable(paramLevel) : this.loggerProxy.isLoggable(paramLevel);
  }
  
  public Level level() {
    return this.loggerProxy.getLevel();
  }
  
  public void setLevel(Level paramLevel) {
    this.loggerProxy.setLevel(paramLevel);
  }
  
  public void severe(String paramString) {
    this.loggerProxy.doLog(Level.SEVERE, paramString);
  }
  
  public void severe(String paramString, Throwable paramThrowable) {
    this.loggerProxy.doLog(Level.SEVERE, paramString, paramThrowable);
  }
  
  public void severe(String paramString, Object... paramVarArgs) {
    this.loggerProxy.doLog(Level.SEVERE, paramString, paramVarArgs);
  }
  
  public void warning(String paramString) {
    this.loggerProxy.doLog(Level.WARNING, paramString);
  }
  
  public void warning(String paramString, Throwable paramThrowable) {
    this.loggerProxy.doLog(Level.WARNING, paramString, paramThrowable);
  }
  
  public void warning(String paramString, Object... paramVarArgs) {
    this.loggerProxy.doLog(Level.WARNING, paramString, paramVarArgs);
  }
  
  public void info(String paramString) {
    this.loggerProxy.doLog(Level.INFO, paramString);
  }
  
  public void info(String paramString, Throwable paramThrowable) {
    this.loggerProxy.doLog(Level.INFO, paramString, paramThrowable);
  }
  
  public void info(String paramString, Object... paramVarArgs) {
    this.loggerProxy.doLog(Level.INFO, paramString, paramVarArgs);
  }
  
  public void config(String paramString) {
    this.loggerProxy.doLog(Level.CONFIG, paramString);
  }
  
  public void config(String paramString, Throwable paramThrowable) {
    this.loggerProxy.doLog(Level.CONFIG, paramString, paramThrowable);
  }
  
  public void config(String paramString, Object... paramVarArgs) {
    this.loggerProxy.doLog(Level.CONFIG, paramString, paramVarArgs);
  }
  
  public void fine(String paramString) {
    this.loggerProxy.doLog(Level.FINE, paramString);
  }
  
  public void fine(String paramString, Throwable paramThrowable) {
    this.loggerProxy.doLog(Level.FINE, paramString, paramThrowable);
  }
  
  public void fine(String paramString, Object... paramVarArgs) {
    this.loggerProxy.doLog(Level.FINE, paramString, paramVarArgs);
  }
  
  public void finer(String paramString) {
    this.loggerProxy.doLog(Level.FINER, paramString);
  }
  
  public void finer(String paramString, Throwable paramThrowable) {
    this.loggerProxy.doLog(Level.FINER, paramString, paramThrowable);
  }
  
  public void finer(String paramString, Object... paramVarArgs) {
    this.loggerProxy.doLog(Level.FINER, paramString, paramVarArgs);
  }
  
  public void finest(String paramString) {
    this.loggerProxy.doLog(Level.FINEST, paramString);
  }
  
  public void finest(String paramString, Throwable paramThrowable) {
    this.loggerProxy.doLog(Level.FINEST, paramString, paramThrowable);
  }
  
  public void finest(String paramString, Object... paramVarArgs) {
    this.loggerProxy.doLog(Level.FINEST, paramString, paramVarArgs);
  }
  
  private static abstract class LoggerProxy {
    final String name;
    
    protected LoggerProxy(String param1String) {
      this.name = param1String;
    }
    
    abstract boolean isEnabled();
    
    abstract PlatformLogger.Level getLevel();
    
    abstract void setLevel(PlatformLogger.Level param1Level);
    
    abstract void doLog(PlatformLogger.Level param1Level, String param1String);
    
    abstract void doLog(PlatformLogger.Level param1Level, String param1String, Throwable param1Throwable);
    
    abstract void doLog(PlatformLogger.Level param1Level, String param1String, Object... param1VarArgs);
    
    abstract boolean isLoggable(PlatformLogger.Level param1Level);
  }
  
  private static final class DefaultLoggerProxy extends LoggerProxy {
    volatile PlatformLogger.Level effectiveLevel;
    
    volatile PlatformLogger.Level level;
    
    private static PrintStream outputStream() {
      return System.err;
    }
    
    DefaultLoggerProxy(String param1String) {
      super(param1String);
      this.date = new Date();
      this.effectiveLevel = deriveEffectiveLevel(null);
      this.level = null;
    }
    
    boolean isEnabled() {
      return (this.effectiveLevel != PlatformLogger.Level.OFF);
    }
    
    PlatformLogger.Level getLevel() {
      return this.level;
    }
    
    void setLevel(PlatformLogger.Level param1Level) {
      PlatformLogger.Level level = this.level;
      if (level != param1Level) {
        this.level = param1Level;
        this.effectiveLevel = deriveEffectiveLevel(param1Level);
      } 
    }
    
    void doLog(PlatformLogger.Level param1Level, String param1String) {
      if (isLoggable(param1Level))
        outputStream().print(format(param1Level, param1String, null)); 
    }
    
    void doLog(PlatformLogger.Level param1Level, String param1String, Throwable param1Throwable) {
      if (isLoggable(param1Level))
        outputStream().print(format(param1Level, param1String, param1Throwable)); 
    }
    
    void doLog(PlatformLogger.Level param1Level, String param1String, Object... param1VarArgs) {
      if (isLoggable(param1Level)) {
        String str = formatMessage(param1String, param1VarArgs);
        outputStream().print(format(param1Level, str, null));
      } 
    }
    
    boolean isLoggable(PlatformLogger.Level param1Level) {
      PlatformLogger.Level level = this.effectiveLevel;
      return (param1Level.intValue() >= level.intValue() && level != PlatformLogger.Level.OFF);
    }
    
    private PlatformLogger.Level deriveEffectiveLevel(PlatformLogger.Level param1Level) {
      return (param1Level == null) ? PlatformLogger.DEFAULT_LEVEL : param1Level;
    }
    
    private String formatMessage(String param1String, Object... param1VarArgs) {
      try {
        if (param1VarArgs == null || param1VarArgs.length == 0)
          return param1String; 
        if (param1String.indexOf("{0") >= 0 || param1String.indexOf("{1") >= 0 || param1String.indexOf("{2") >= 0 || param1String.indexOf("{3") >= 0)
          return MessageFormat.format(param1String, param1VarArgs); 
        return param1String;
      } catch (Exception exception) {
        return param1String;
      } 
    }
    
    private static final String formatString = LoggingSupport.getSimpleFormat(false);
    
    private Date date;
    
    private synchronized String format(PlatformLogger.Level param1Level, String param1String, Throwable param1Throwable) {
      this.date.setTime(System.currentTimeMillis());
      String str = "";
      if (param1Throwable != null) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        printWriter.println();
        param1Throwable.printStackTrace(printWriter);
        printWriter.close();
        str = stringWriter.toString();
      } 
      return String.format(formatString, new Object[] { this.date, 
            
            getCallerInfo(), this.name, param1Level
            
            .name(), param1String, str });
    }
    
    private String getCallerInfo() {
      String str1 = null;
      String str2 = null;
      JavaLangAccess javaLangAccess = SharedSecrets.getJavaLangAccess();
      Throwable throwable = new Throwable();
      int i = javaLangAccess.getStackTraceDepth(throwable);
      String str3 = "sun.util.logging.PlatformLogger";
      boolean bool = true;
      for (byte b = 0; b < i; b++) {
        StackTraceElement stackTraceElement = javaLangAccess.getStackTraceElement(throwable, b);
        String str = stackTraceElement.getClassName();
        if (bool) {
          if (str.equals(str3))
            bool = false; 
        } else if (!str.equals(str3)) {
          str1 = str;
          str2 = stackTraceElement.getMethodName();
          break;
        } 
      } 
      if (str1 != null)
        return str1 + " " + str2; 
      return this.name;
    }
  }
  
  private static final class JavaLoggerProxy extends LoggerProxy {
    private final Object javaLogger;
    
    static {
      for (PlatformLogger.Level level : PlatformLogger.Level.values())
        level.javaLevel = LoggingSupport.parseLevel(level.name()); 
    }
    
    JavaLoggerProxy(String param1String) {
      this(param1String, null);
    }
    
    JavaLoggerProxy(String param1String, PlatformLogger.Level param1Level) {
      super(param1String);
      this.javaLogger = LoggingSupport.getLogger(param1String);
      if (param1Level != null)
        LoggingSupport.setLevel(this.javaLogger, param1Level.javaLevel); 
    }
    
    void doLog(PlatformLogger.Level param1Level, String param1String) {
      LoggingSupport.log(this.javaLogger, param1Level.javaLevel, param1String);
    }
    
    void doLog(PlatformLogger.Level param1Level, String param1String, Throwable param1Throwable) {
      LoggingSupport.log(this.javaLogger, param1Level.javaLevel, param1String, param1Throwable);
    }
    
    void doLog(PlatformLogger.Level param1Level, String param1String, Object... param1VarArgs) {
      if (!isLoggable(param1Level))
        return; 
      byte b1 = (param1VarArgs != null) ? param1VarArgs.length : 0;
      String[] arrayOfString = new String[b1];
      for (byte b2 = 0; b2 < b1; b2++)
        arrayOfString[b2] = String.valueOf(param1VarArgs[b2]); 
      LoggingSupport.log(this.javaLogger, param1Level.javaLevel, param1String, (Object[])arrayOfString);
    }
    
    boolean isEnabled() {
      return LoggingSupport.isLoggable(this.javaLogger, PlatformLogger.Level.OFF.javaLevel);
    }
    
    PlatformLogger.Level getLevel() {
      Object object = LoggingSupport.getLevel(this.javaLogger);
      if (object == null)
        return null; 
      try {
        return PlatformLogger.Level.valueOf(LoggingSupport.getLevelName(object));
      } catch (IllegalArgumentException illegalArgumentException) {
        return PlatformLogger.Level.valueOf(LoggingSupport.getLevelValue(object));
      } 
    }
    
    void setLevel(PlatformLogger.Level param1Level) {
      LoggingSupport.setLevel(this.javaLogger, (param1Level == null) ? null : param1Level.javaLevel);
    }
    
    boolean isLoggable(PlatformLogger.Level param1Level) {
      return LoggingSupport.isLoggable(this.javaLogger, param1Level.javaLevel);
    }
  }
}
