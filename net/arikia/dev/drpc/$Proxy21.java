package net.arikia.dev.drpc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

final class $Proxy21 extends Proxy implements DiscordRPC.DLL {
  private static Method m1;
  
  private static Method m5;
  
  private static Method m6;
  
  private static Method m10;
  
  private static Method m9;
  
  private static Method m2;
  
  private static Method m3;
  
  private static Method m7;
  
  private static Method m8;
  
  private static Method m4;
  
  private static Method m0;
  
  private static Method m11;
  
  public $Proxy21(InvocationHandler paramInvocationHandler) {
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
  
  public final void Discord_Respond(String paramString, int paramInt) {
    try {
      this.h.invoke(this, m5, new Object[] { paramString, Integer.valueOf(paramInt) });
      return;
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final void Discord_UpdatePresence(DiscordRichPresence paramDiscordRichPresence) {
    try {
      this.h.invoke(this, m6, new Object[] { paramDiscordRichPresence });
      return;
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final void Discord_ClearPresence() {
    try {
      this.h.invoke(this, m10, null);
      return;
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final void Discord_RunCallbacks() {
    try {
      this.h.invoke(this, m9, null);
      return;
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
  
  public final void Discord_Register(String paramString1, String paramString2) {
    try {
      this.h.invoke(this, m3, new Object[] { paramString1, paramString2 });
      return;
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final void Discord_Initialize(String paramString1, DiscordEventHandlers paramDiscordEventHandlers, int paramInt, String paramString2) {
    try {
      this.h.invoke(this, m7, new Object[] { paramString1, paramDiscordEventHandlers, Integer.valueOf(paramInt), paramString2 });
      return;
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final void Discord_RegisterSteamGame(String paramString1, String paramString2) {
    try {
      this.h.invoke(this, m8, new Object[] { paramString1, paramString2 });
      return;
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  public final void Discord_Shutdown() {
    try {
      this.h.invoke(this, m4, null);
      return;
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
  
  public final void Discord_UpdateHandlers(DiscordEventHandlers paramDiscordEventHandlers) {
    try {
      this.h.invoke(this, m11, new Object[] { paramDiscordEventHandlers });
      return;
    } catch (Error|RuntimeException error) {
      throw null;
    } catch (Throwable throwable) {
      throw new UndeclaredThrowableException(throwable);
    } 
  }
  
  static {
    try {
      m1 = Class.forName("java.lang.Object").getMethod("equals", new Class[] { Class.forName("java.lang.Object") });
      m5 = Class.forName("net.arikia.dev.drpc.DiscordRPC$DLL").getMethod("Discord_Respond", new Class[] { Class.forName("java.lang.String"), int.class });
      m6 = Class.forName("net.arikia.dev.drpc.DiscordRPC$DLL").getMethod("Discord_UpdatePresence", new Class[] { Class.forName("net.arikia.dev.drpc.DiscordRichPresence") });
      m10 = Class.forName("net.arikia.dev.drpc.DiscordRPC$DLL").getMethod("Discord_ClearPresence", new Class[0]);
      m9 = Class.forName("net.arikia.dev.drpc.DiscordRPC$DLL").getMethod("Discord_RunCallbacks", new Class[0]);
      m2 = Class.forName("java.lang.Object").getMethod("toString", new Class[0]);
      m3 = Class.forName("net.arikia.dev.drpc.DiscordRPC$DLL").getMethod("Discord_Register", new Class[] { Class.forName("java.lang.String"), Class.forName("java.lang.String") });
      m7 = Class.forName("net.arikia.dev.drpc.DiscordRPC$DLL").getMethod("Discord_Initialize", new Class[] { Class.forName("java.lang.String"), Class.forName("net.arikia.dev.drpc.DiscordEventHandlers"), int.class, Class.forName("java.lang.String") });
      m8 = Class.forName("net.arikia.dev.drpc.DiscordRPC$DLL").getMethod("Discord_RegisterSteamGame", new Class[] { Class.forName("java.lang.String"), Class.forName("java.lang.String") });
      m4 = Class.forName("net.arikia.dev.drpc.DiscordRPC$DLL").getMethod("Discord_Shutdown", new Class[0]);
      m0 = Class.forName("java.lang.Object").getMethod("hashCode", new Class[0]);
      m11 = Class.forName("net.arikia.dev.drpc.DiscordRPC$DLL").getMethod("Discord_UpdateHandlers", new Class[] { Class.forName("net.arikia.dev.drpc.DiscordEventHandlers") });
      return;
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new NoSuchMethodError(noSuchMethodException.getMessage());
    } catch (ClassNotFoundException classNotFoundException) {
      throw new NoClassDefFoundError(classNotFoundException.getMessage());
    } 
  }
}
