package io.netty.util.internal.logging;

public abstract class InternalLoggerFactory {
  private static volatile InternalLoggerFactory defaultFactory = newDefaultFactory(InternalLoggerFactory.class.getName());
  
  private static InternalLoggerFactory newDefaultFactory(String name) {
    JdkLoggerFactory jdkLoggerFactory;
    try {
      InternalLoggerFactory f = new Slf4JLoggerFactory(true);
      f.newInstance(name).debug("Using SLF4J as the default logging framework");
    } catch (Throwable t1) {
      try {
        Log4JLoggerFactory log4JLoggerFactory = new Log4JLoggerFactory();
        log4JLoggerFactory.newInstance(name).debug("Using Log4J as the default logging framework");
      } catch (Throwable t2) {
        jdkLoggerFactory = new JdkLoggerFactory();
        jdkLoggerFactory.newInstance(name).debug("Using java.util.logging as the default logging framework");
      } 
    } 
    return (InternalLoggerFactory)jdkLoggerFactory;
  }
  
  public static InternalLoggerFactory getDefaultFactory() {
    return defaultFactory;
  }
  
  public static void setDefaultFactory(InternalLoggerFactory defaultFactory) {
    if (defaultFactory == null)
      throw new NullPointerException("defaultFactory"); 
    InternalLoggerFactory.defaultFactory = defaultFactory;
  }
  
  public static InternalLogger getInstance(Class<?> clazz) {
    return getInstance(clazz.getName());
  }
  
  public static InternalLogger getInstance(String name) {
    return getDefaultFactory().newInstance(name);
  }
  
  protected abstract InternalLogger newInstance(String paramString);
}
