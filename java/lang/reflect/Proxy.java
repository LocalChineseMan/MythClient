package java.lang.reflect;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.ReflectPermission;
import java.lang.reflect.WeakCache;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import sun.misc.ProxyGenerator;
import sun.misc.VM;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;
import sun.security.util.SecurityConstants;

public class Proxy implements Serializable {
  private static final long serialVersionUID = -2222568056686623797L;
  
  private static final Class<?>[] constructorParams = new Class[] { InvocationHandler.class };
  
  private static final WeakCache<ClassLoader, Class<?>[], Class<?>> proxyClassCache = (WeakCache)new WeakCache<>(new KeyFactory(), new ProxyClassFactory());
  
  protected InvocationHandler h;
  
  private Proxy() {}
  
  protected Proxy(InvocationHandler paramInvocationHandler) {
    Objects.requireNonNull(paramInvocationHandler);
    this.h = paramInvocationHandler;
  }
  
  @CallerSensitive
  public static Class<?> getProxyClass(ClassLoader paramClassLoader, Class<?>... paramVarArgs) throws IllegalArgumentException {
    Class[] arrayOfClass = (Class[])paramVarArgs.clone();
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      checkProxyAccess(Reflection.getCallerClass(), paramClassLoader, arrayOfClass); 
    return getProxyClass0(paramClassLoader, arrayOfClass);
  }
  
  private static void checkProxyAccess(Class<?> paramClass, ClassLoader paramClassLoader, Class<?>... paramVarArgs) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null) {
      ClassLoader classLoader = paramClass.getClassLoader();
      if (VM.isSystemDomainLoader(paramClassLoader) && !VM.isSystemDomainLoader(classLoader))
        securityManager.checkPermission(SecurityConstants.GET_CLASSLOADER_PERMISSION); 
      ReflectUtil.checkProxyPackageAccess(classLoader, paramVarArgs);
    } 
  }
  
  private static Class<?> getProxyClass0(ClassLoader paramClassLoader, Class<?>... paramVarArgs) {
    if (paramVarArgs.length > 65535)
      throw new IllegalArgumentException("interface limit exceeded"); 
    return proxyClassCache.get(paramClassLoader, paramVarArgs);
  }
  
  private static final Object key0 = new Object();
  
  private static final class Key1 extends WeakReference<Class<?>> {
    private final int hash;
    
    Key1(Class<?> param1Class) {
      super(param1Class);
      this.hash = param1Class.hashCode();
    }
    
    public int hashCode() {
      return this.hash;
    }
    
    public boolean equals(Object param1Object) {
      Class<?> clazz;
      return (this == param1Object || (param1Object != null && param1Object
        
        .getClass() == Key1.class && (
        clazz = get()) != null && clazz == ((Key1)param1Object)
        .get()));
    }
  }
  
  private static final class KeyFactory implements BiFunction<ClassLoader, Class<?>[], Object> {
    private KeyFactory() {}
    
    public Object apply(ClassLoader param1ClassLoader, Class<?>[] param1ArrayOfClass) {
      switch (param1ArrayOfClass.length) {
        case 1:
          return new Proxy.Key1(param1ArrayOfClass[0]);
        case 2:
          return new Proxy.Key2(param1ArrayOfClass[0], param1ArrayOfClass[1]);
        case 0:
          return Proxy.key0;
      } 
      return new Proxy.KeyX(param1ArrayOfClass);
    }
  }
  
  private static final class ProxyClassFactory implements BiFunction<ClassLoader, Class<?>[], Class<?>> {
    private static final String proxyClassNamePrefix = "$Proxy";
    
    private ProxyClassFactory() {}
    
    private static final AtomicLong nextUniqueNumber = new AtomicLong();
    
    public Class<?> apply(ClassLoader param1ClassLoader, Class<?>[] param1ArrayOfClass) {
      IdentityHashMap<Object, Object> identityHashMap = new IdentityHashMap<>(param1ArrayOfClass.length);
      for (Class<?> clazz1 : param1ArrayOfClass) {
        Class<?> clazz2 = null;
        try {
          clazz2 = Class.forName(clazz1.getName(), false, param1ClassLoader);
        } catch (ClassNotFoundException classNotFoundException) {}
        if (clazz2 != clazz1)
          throw new IllegalArgumentException(clazz1 + " is not visible from class loader"); 
        if (!clazz2.isInterface())
          throw new IllegalArgumentException(clazz2
              .getName() + " is not an interface"); 
        if (identityHashMap.put(clazz2, Boolean.TRUE) != null)
          throw new IllegalArgumentException("repeated interface: " + clazz2
              .getName()); 
      } 
      String str1 = null;
      byte b = 17;
      for (Class<?> clazz : param1ArrayOfClass) {
        int i = clazz.getModifiers();
        if (!Modifier.isPublic(i)) {
          b = 16;
          String str3 = clazz.getName();
          int j = str3.lastIndexOf('.');
          String str4 = (j == -1) ? "" : str3.substring(0, j + 1);
          if (str1 == null) {
            str1 = str4;
          } else if (!str4.equals(str1)) {
            throw new IllegalArgumentException("non-public interfaces from different packages");
          } 
        } 
      } 
      if (str1 == null)
        str1 = "com.sun.proxy."; 
      long l = nextUniqueNumber.getAndIncrement();
      String str2 = str1 + "$Proxy" + l;
      byte[] arrayOfByte = ProxyGenerator.generateProxyClass(str2, param1ArrayOfClass, b);
      try {
        return Proxy.defineClass0(param1ClassLoader, str2, arrayOfByte, 0, arrayOfByte.length);
      } catch (ClassFormatError classFormatError) {
        throw new IllegalArgumentException(classFormatError.toString());
      } 
    }
  }
  
  @CallerSensitive
  public static Object newProxyInstance(ClassLoader paramClassLoader, Class<?>[] paramArrayOfClass, InvocationHandler paramInvocationHandler) throws IllegalArgumentException {
    Objects.requireNonNull(paramInvocationHandler);
    Class[] arrayOfClass = (Class[])paramArrayOfClass.clone();
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null)
      checkProxyAccess(Reflection.getCallerClass(), paramClassLoader, arrayOfClass); 
    Class<?> clazz = getProxyClass0(paramClassLoader, arrayOfClass);
    try {
      if (securityManager != null)
        checkNewProxyPermission(Reflection.getCallerClass(), clazz); 
      final Constructor<?> cons = clazz.getConstructor(constructorParams);
      InvocationHandler invocationHandler = paramInvocationHandler;
      if (!Modifier.isPublic(clazz.getModifiers()))
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                cons.setAccessible(true);
                return null;
              }
            }); 
      return constructor.newInstance(new Object[] { paramInvocationHandler });
    } catch (IllegalAccessException|InstantiationException illegalAccessException) {
      throw new InternalError(illegalAccessException.toString(), illegalAccessException);
    } catch (InvocationTargetException invocationTargetException) {
      Throwable throwable = invocationTargetException.getCause();
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      throw new InternalError(throwable.toString(), throwable);
    } catch (NoSuchMethodException noSuchMethodException) {
      throw new InternalError(noSuchMethodException.toString(), noSuchMethodException);
    } 
  }
  
  private static void checkNewProxyPermission(Class<?> paramClass1, Class<?> paramClass2) {
    SecurityManager securityManager = System.getSecurityManager();
    if (securityManager != null && 
      ReflectUtil.isNonPublicProxyClass(paramClass2)) {
      ClassLoader classLoader1 = paramClass1.getClassLoader();
      ClassLoader classLoader2 = paramClass2.getClassLoader();
      int i = paramClass2.getName().lastIndexOf('.');
      String str1 = (i == -1) ? "" : paramClass2.getName().substring(0, i);
      i = paramClass1.getName().lastIndexOf('.');
      String str2 = (i == -1) ? "" : paramClass1.getName().substring(0, i);
      if (classLoader2 != classLoader1 || !str1.equals(str2))
        securityManager.checkPermission(new ReflectPermission("newProxyInPackage." + str1)); 
    } 
  }
  
  public static boolean isProxyClass(Class<?> paramClass) {
    return (Proxy.class.isAssignableFrom(paramClass) && proxyClassCache.containsValue(paramClass));
  }
  
  @CallerSensitive
  public static InvocationHandler getInvocationHandler(Object paramObject) throws IllegalArgumentException {
    if (!isProxyClass(paramObject.getClass()))
      throw new IllegalArgumentException("not a proxy instance"); 
    Proxy proxy = (Proxy)paramObject;
    InvocationHandler invocationHandler = proxy.h;
    if (System.getSecurityManager() != null) {
      Class<?> clazz = invocationHandler.getClass();
      Class clazz1 = Reflection.getCallerClass();
      if (ReflectUtil.needsPackageAccessCheck(clazz1.getClassLoader(), clazz
          .getClassLoader()))
        ReflectUtil.checkPackageAccess(clazz); 
    } 
    return invocationHandler;
  }
  
  private static native Class<?> defineClass0(ClassLoader paramClassLoader, String paramString, byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  private static final class Proxy {}
  
  private static final class Proxy {}
}
