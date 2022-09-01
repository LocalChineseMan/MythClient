package com.sun.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;

public final class $Proxy15 extends Proxy implements PluginAttribute {
  private static Method m1;
  
  private static Method m5;
  
  private static Method m14;
  
  private static Method m2;
  
  private static Method m15;
  
  private static Method m8;
  
  private static Method m4;
  
  private static Method m10;
  
  private static Method m7;
  
  private static Method m0;
  
  private static Method m6;
  
  private static Method m11;
  
  private static Method m9;
  
  private static Method m13;
  
  private static Method m12;
  
  private static Method m3;
  
  public $Proxy15(InvocationHandler paramInvocationHandler) {
    super(paramInvocationHandler);
  }
  
  public final boolean equals(Object paramObject) {
    try {
      return ((Boolean)this.h.invoke(this, m1, new Object[] { paramObject })).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final String defaultString() {
    try {
      return (String)this.h.invoke(this, m5, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final byte defaultByte() {
    try {
      return ((Byte)this.h.invoke(this, m14, null)).byteValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final String toString() {
    try {
      return (String)this.h.invoke(this, m2, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final Class annotationType() {
    try {
      return (Class)this.h.invoke(this, m15, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int defaultInt() {
    try {
      return ((Integer)this.h.invoke(this, m8, null)).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean defaultBoolean() {
    try {
      return ((Boolean)this.h.invoke(this, m4, null)).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final Class defaultClass() {
    try {
      return (Class)this.h.invoke(this, m10, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final long defaultLong() {
    try {
      return ((Long)this.h.invoke(this, m7, null)).longValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final int hashCode() {
    try {
      return ((Integer)this.h.invoke(this, m0, null)).intValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean sensitive() {
    try {
      return ((Boolean)this.h.invoke(this, m6, null)).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final double defaultDouble() {
    try {
      return ((Double)this.h.invoke(this, m11, null)).doubleValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final char defaultChar() {
    try {
      return ((Character)this.h.invoke(this, m9, null)).charValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final short defaultShort() {
    try {
      return ((Short)this.h.invoke(this, m13, null)).shortValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final float defaultFloat() {
    try {
      return ((Float)this.h.invoke(this, m12, null)).floatValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final String value() {
    try {
      return (String)this.h.invoke(this, m3, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  static {
    try {
      m1 = Class.forName("java.lang.Object").getMethod("equals", new Class[] { Class.forName("java.lang.Object") });
      m5 = Class.forName("org.apache.logging.log4j.core.config.plugins.PluginAttribute").getMethod("defaultString", new Class[0]);
      m14 = Class.forName("org.apache.logging.log4j.core.config.plugins.PluginAttribute").getMethod("defaultByte", new Class[0]);
      m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);
      m15 = Class.forName("org.apache.logging.log4j.core.config.plugins.PluginAttribute").getMethod("annotationType", new Class[0]);
      m8 = Class.forName("org.apache.logging.log4j.core.config.plugins.PluginAttribute").getMethod("defaultInt", new Class[0]);
      m4 = Class.forName("org.apache.logging.log4j.core.config.plugins.PluginAttribute").getMethod("defaultBoolean", new Class[0]);
      m10 = Class.forName("org.apache.logging.log4j.core.config.plugins.PluginAttribute").getMethod("defaultClass", new Class[0]);
      m7 = Class.forName("org.apache.logging.log4j.core.config.plugins.PluginAttribute").getMethod("defaultLong", new Class[0]);
      m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);
      m6 = Class.forName("org.apache.logging.log4j.core.config.plugins.PluginAttribute").getMethod("sensitive", new Class[0]);
      m11 = Class.forName("org.apache.logging.log4j.core.config.plugins.PluginAttribute").getMethod("defaultDouble", new Class[0]);
      m9 = Class.forName("org.apache.logging.log4j.core.config.plugins.PluginAttribute").getMethod("defaultChar", new Class[0]);
      m13 = Class.forName("org.apache.logging.log4j.core.config.plugins.PluginAttribute").getMethod("defaultShort", new Class[0]);
      m12 = Class.forName("org.apache.logging.log4j.core.config.plugins.PluginAttribute").getMethod("defaultFloat", new Class[0]);
      m3 = Class.forName("org.apache.logging.log4j.core.config.plugins.PluginAttribute").getMethod("value", new Class[0]);
      return;
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError(noSuchMethodException.getMessage());
    } catch (ClassNotFoundException classNotFoundException) {
      throw new NoClassDefFoundError(classNotFoundException.getMessage());
    } 
  }
}
