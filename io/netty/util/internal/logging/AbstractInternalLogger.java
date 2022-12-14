package io.netty.util.internal.logging;

import io.netty.util.internal.StringUtil;
import java.io.ObjectStreamException;
import java.io.Serializable;

public abstract class AbstractInternalLogger implements InternalLogger, Serializable {
  private static final long serialVersionUID = -6382972526573193470L;
  
  private final String name;
  
  protected AbstractInternalLogger(String name) {
    if (name == null)
      throw new NullPointerException("name"); 
    this.name = name;
  }
  
  public String name() {
    return this.name;
  }
  
  public boolean isEnabled(InternalLogLevel level) {
    switch (null.$SwitchMap$io$netty$util$internal$logging$InternalLogLevel[level.ordinal()]) {
      case 1:
        return isTraceEnabled();
      case 2:
        return isDebugEnabled();
      case 3:
        return isInfoEnabled();
      case 4:
        return isWarnEnabled();
      case 5:
        return isErrorEnabled();
    } 
    throw new Error();
  }
  
  public void log(InternalLogLevel level, String msg, Throwable cause) {
    switch (null.$SwitchMap$io$netty$util$internal$logging$InternalLogLevel[level.ordinal()]) {
      case 1:
        trace(msg, cause);
        return;
      case 2:
        debug(msg, cause);
        return;
      case 3:
        info(msg, cause);
        return;
      case 4:
        warn(msg, cause);
        return;
      case 5:
        error(msg, cause);
        return;
    } 
    throw new Error();
  }
  
  public void log(InternalLogLevel level, String msg) {
    switch (null.$SwitchMap$io$netty$util$internal$logging$InternalLogLevel[level.ordinal()]) {
      case 1:
        trace(msg);
        return;
      case 2:
        debug(msg);
        return;
      case 3:
        info(msg);
        return;
      case 4:
        warn(msg);
        return;
      case 5:
        error(msg);
        return;
    } 
    throw new Error();
  }
  
  public void log(InternalLogLevel level, String format, Object arg) {
    switch (null.$SwitchMap$io$netty$util$internal$logging$InternalLogLevel[level.ordinal()]) {
      case 1:
        trace(format, arg);
        return;
      case 2:
        debug(format, arg);
        return;
      case 3:
        info(format, arg);
        return;
      case 4:
        warn(format, arg);
        return;
      case 5:
        error(format, arg);
        return;
    } 
    throw new Error();
  }
  
  public void log(InternalLogLevel level, String format, Object argA, Object argB) {
    switch (null.$SwitchMap$io$netty$util$internal$logging$InternalLogLevel[level.ordinal()]) {
      case 1:
        trace(format, argA, argB);
        return;
      case 2:
        debug(format, argA, argB);
        return;
      case 3:
        info(format, argA, argB);
        return;
      case 4:
        warn(format, argA, argB);
        return;
      case 5:
        error(format, argA, argB);
        return;
    } 
    throw new Error();
  }
  
  public void log(InternalLogLevel level, String format, Object... arguments) {
    switch (null.$SwitchMap$io$netty$util$internal$logging$InternalLogLevel[level.ordinal()]) {
      case 1:
        trace(format, arguments);
        return;
      case 2:
        debug(format, arguments);
        return;
      case 3:
        info(format, arguments);
        return;
      case 4:
        warn(format, arguments);
        return;
      case 5:
        error(format, arguments);
        return;
    } 
    throw new Error();
  }
  
  protected Object readResolve() throws ObjectStreamException {
    return InternalLoggerFactory.getInstance(name());
  }
  
  public String toString() {
    return StringUtil.simpleClassName(this) + '(' + name() + ')';
  }
}
