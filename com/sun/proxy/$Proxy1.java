package com.sun.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import org.apache.logging.log4j.core.config.plugins.Plugin;

public final class $Proxy1 extends Proxy implements Plugin {
  private static Method m1;
  
  private static Method m3;
  
  private static Method m2;
  
  private static Method m5;
  
  private static Method m7;
  
  private static Method m8;
  
  private static Method m6;
  
  private static Method m4;
  
  private static Method m0;
  
  public $Proxy1(InvocationHandler paramInvocationHandler) {
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
  
  public final String name() {
    try {
      return (String)this.h.invoke(this, m3, null);
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
  
  public final boolean printObject() {
    try {
      return ((Boolean)this.h.invoke(this, m5, null)).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final boolean deferChildren() {
    try {
      return ((Boolean)this.h.invoke(this, m7, null)).booleanValue();
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final Class annotationType() {
    try {
      return (Class)this.h.invoke(this, m8, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final String category() {
    try {
      return (String)this.h.invoke(this, m6, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final String elementType() {
    try {
      return (String)this.h.invoke(this, m4, null);
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
  
  static {
    try {
      m1 = Class.forName("java.lang.Object").getMethod("equals", new Class[] { Class.forName("java.lang.Object") });
      m3 = Class.forName("org.apache.logging.log4j.core.config.plugins.Plugin").getMethod("name", new Class[0]);
      m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);
      m5 = Class.forName("org.apache.logging.log4j.core.config.plugins.Plugin").getMethod("printObject", new Class[0]);
      m7 = Class.forName("org.apache.logging.log4j.core.config.plugins.Plugin").getMethod("deferChildren", new Class[0]);
      m8 = Class.forName("org.apache.logging.log4j.core.config.plugins.Plugin").getMethod("annotationType", new Class[0]);
      m6 = Class.forName("org.apache.logging.log4j.core.config.plugins.Plugin").getMethod("category", new Class[0]);
      m4 = Class.forName("org.apache.logging.log4j.core.config.plugins.Plugin").getMethod("elementType", new Class[0]);
      m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);
      return;
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError(noSuchMethodException.getMessage());
    } catch (ClassNotFoundException classNotFoundException) {
      throw new NoClassDefFoundError(classNotFoundException.getMessage());
    } 
  }
}
