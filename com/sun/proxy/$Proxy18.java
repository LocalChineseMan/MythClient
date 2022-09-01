package com.sun.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;
import notthatuwu.xyz.mythrecode.api.utils.shader.ShaderAnnoation;
import notthatuwu.xyz.mythrecode.api.utils.shader.ShaderRenderType;

public final class $Proxy18 extends Proxy implements ShaderAnnoation {
  private static Method m1;
  
  private static Method m3;
  
  private static Method m2;
  
  private static Method m6;
  
  private static Method m4;
  
  private static Method m5;
  
  private static Method m0;
  
  public $Proxy18(InvocationHandler paramInvocationHandler) {
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
  
  public final ShaderRenderType renderType() {
    try {
      return (ShaderRenderType)this.h.invoke(this, m3, null);
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
      return (Class)this.h.invoke(this, m6, null);
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final String fragName() {
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
      m3 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.shader.ShaderAnnoation").getMethod("renderType", new Class[0]);
      m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);
      m6 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.shader.ShaderAnnoation").getMethod("annotationType", new Class[0]);
      m4 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.shader.ShaderAnnoation").getMethod("fragName", new Class[0]);
      m5 = Class.forName("notthatuwu.xyz.mythrecode.api.utils.shader.ShaderAnnoation").getMethod("adolf6757", new Class[0]);
      m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);
      return;
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError(noSuchMethodException.getMessage());
    } catch (ClassNotFoundException classNotFoundException) {
      throw new NoClassDefFoundError(classNotFoundException.getMessage());
    } 
  }
}
