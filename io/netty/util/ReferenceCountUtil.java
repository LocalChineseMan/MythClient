package io.netty.util;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public final class ReferenceCountUtil {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(ReferenceCountUtil.class);
  
  public static <T> T retain(T msg) {
    if (msg instanceof ReferenceCounted)
      return (T)((ReferenceCounted)msg).retain(); 
    return msg;
  }
  
  public static <T> T retain(T msg, int increment) {
    if (msg instanceof ReferenceCounted)
      return (T)((ReferenceCounted)msg).retain(increment); 
    return msg;
  }
  
  public static boolean release(Object msg) {
    if (msg instanceof ReferenceCounted)
      return ((ReferenceCounted)msg).release(); 
    return false;
  }
  
  public static boolean release(Object msg, int decrement) {
    if (msg instanceof ReferenceCounted)
      return ((ReferenceCounted)msg).release(decrement); 
    return false;
  }
  
  public static void safeRelease(Object msg) {
    try {
      release(msg);
    } catch (Throwable t) {
      logger.warn("Failed to release a message: {}", msg, t);
    } 
  }
  
  public static void safeRelease(Object msg, int decrement) {
    try {
      release(msg, decrement);
    } catch (Throwable t) {
      if (logger.isWarnEnabled())
        logger.warn("Failed to release a message: {} (decrement: {})", new Object[] { msg, Integer.valueOf(decrement), t }); 
    } 
  }
  
  public static <T> T releaseLater(T msg) {
    return releaseLater(msg, 1);
  }
  
  public static <T> T releaseLater(T msg, int decrement) {
    if (msg instanceof ReferenceCounted)
      ThreadDeathWatcher.watch(Thread.currentThread(), (Runnable)new ReleasingTask((ReferenceCounted)msg, decrement)); 
    return msg;
  }
  
  private static final class ReferenceCountUtil {}
}
