package com.sun.jmx.mbeanserver;

import javax.management.loading.ClassLoaderRepository;

final class SecureClassLoaderRepository implements ClassLoaderRepository {
  private final ClassLoaderRepository clr;
  
  public SecureClassLoaderRepository(ClassLoaderRepository paramClassLoaderRepository) {
    this.clr = paramClassLoaderRepository;
  }
  
  public final Class<?> loadClass(String paramString) throws ClassNotFoundException {
    return this.clr.loadClass(paramString);
  }
  
  public final Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException {
    return this.clr.loadClassWithout(paramClassLoader, paramString);
  }
  
  public final Class<?> loadClassBefore(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException {
    return this.clr.loadClassBefore(paramClassLoader, paramString);
  }
}
