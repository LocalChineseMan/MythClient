package com.sun.jmx.mbeanserver;

import com.sun.jmx.defaults.JmxProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.management.MBeanPermission;
import javax.management.ObjectName;
import sun.reflect.misc.ReflectUtil;

final class ClassLoaderRepositorySupport implements ModifiableClassLoaderRepository {
  private static class LoaderEntry {
    ObjectName name;
    
    ClassLoader loader;
    
    LoaderEntry(ObjectName param1ObjectName, ClassLoader param1ClassLoader) {
      this.name = param1ObjectName;
      this.loader = param1ClassLoader;
    }
  }
  
  private static final LoaderEntry[] EMPTY_LOADER_ARRAY = new LoaderEntry[0];
  
  private LoaderEntry[] loaders = EMPTY_LOADER_ARRAY;
  
  private synchronized boolean add(ObjectName paramObjectName, ClassLoader paramClassLoader) {
    ArrayList<LoaderEntry> arrayList = new ArrayList(Arrays.asList((Object[])this.loaders));
    arrayList.add(new LoaderEntry(paramObjectName, paramClassLoader));
    this.loaders = arrayList.<LoaderEntry>toArray(EMPTY_LOADER_ARRAY);
    return true;
  }
  
  private synchronized boolean remove(ObjectName paramObjectName, ClassLoader paramClassLoader) {
    int i = this.loaders.length;
    for (byte b = 0; b < i; b++) {
      LoaderEntry loaderEntry = this.loaders[b];
      boolean bool = (paramObjectName == null) ? ((paramClassLoader == loaderEntry.loader) ? true : false) : paramObjectName.equals(loaderEntry.name);
      if (bool) {
        LoaderEntry[] arrayOfLoaderEntry = new LoaderEntry[i - 1];
        System.arraycopy(this.loaders, 0, arrayOfLoaderEntry, 0, b);
        System.arraycopy(this.loaders, b + 1, arrayOfLoaderEntry, b, i - 1 - b);
        this.loaders = arrayOfLoaderEntry;
        return true;
      } 
    } 
    return false;
  }
  
  private final Map<String, List<ClassLoader>> search = new Hashtable<>(10);
  
  private final Map<ObjectName, ClassLoader> loadersWithNames = new Hashtable<>(10);
  
  public final Class<?> loadClass(String paramString) throws ClassNotFoundException {
    return loadClass(this.loaders, paramString, null, null);
  }
  
  public final Class<?> loadClassWithout(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class
          .getName(), "loadClassWithout", paramString + " without " + paramClassLoader); 
    if (paramClassLoader == null)
      return loadClass(this.loaders, paramString, null, null); 
    startValidSearch(paramClassLoader, paramString);
    try {
      return loadClass(this.loaders, paramString, paramClassLoader, null);
    } finally {
      stopValidSearch(paramClassLoader, paramString);
    } 
  }
  
  public final Class<?> loadClassBefore(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException {
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class
          .getName(), "loadClassBefore", paramString + " before " + paramClassLoader); 
    if (paramClassLoader == null)
      return loadClass(this.loaders, paramString, null, null); 
    startValidSearch(paramClassLoader, paramString);
    try {
      return loadClass(this.loaders, paramString, null, paramClassLoader);
    } finally {
      stopValidSearch(paramClassLoader, paramString);
    } 
  }
  
  private Class<?> loadClass(LoaderEntry[] paramArrayOfLoaderEntry, String paramString, ClassLoader paramClassLoader1, ClassLoader paramClassLoader2) throws ClassNotFoundException {
    ReflectUtil.checkPackageAccess(paramString);
    int i = paramArrayOfLoaderEntry.length;
    for (byte b = 0; b < i; b++) {
      try {
        ClassLoader classLoader = (paramArrayOfLoaderEntry[b]).loader;
        if (classLoader == null)
          return Class.forName(paramString, false, (ClassLoader)null); 
        if (classLoader != paramClassLoader1) {
          if (classLoader == paramClassLoader2)
            break; 
          if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
            JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class
                .getName(), "loadClass", "Trying loader = " + classLoader); 
          return Class.forName(paramString, false, classLoader);
        } 
      } catch (ClassNotFoundException classNotFoundException) {}
    } 
    throw new ClassNotFoundException(paramString);
  }
  
  private synchronized void startValidSearch(ClassLoader paramClassLoader, String paramString) throws ClassNotFoundException {
    List<ClassLoader> list = this.search.get(paramString);
    if (list != null && list.contains(paramClassLoader)) {
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class
            .getName(), "startValidSearch", "Already requested loader = " + paramClassLoader + " class = " + paramString); 
      throw new ClassNotFoundException(paramString);
    } 
    if (list == null) {
      list = new ArrayList(1);
      this.search.put(paramString, list);
    } 
    list.add(paramClassLoader);
    if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
      JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class
          .getName(), "startValidSearch", "loader = " + paramClassLoader + " class = " + paramString); 
  }
  
  private synchronized void stopValidSearch(ClassLoader paramClassLoader, String paramString) {
    List list = this.search.get(paramString);
    if (list != null) {
      list.remove(paramClassLoader);
      if (JmxProperties.MBEANSERVER_LOGGER.isLoggable(Level.FINER))
        JmxProperties.MBEANSERVER_LOGGER.logp(Level.FINER, ClassLoaderRepositorySupport.class
            .getName(), "stopValidSearch", "loader = " + paramClassLoader + " class = " + paramString); 
    } 
  }
  
  public final void addClassLoader(ClassLoader paramClassLoader) {
    add(null, paramClassLoader);
  }
  
  public final void removeClassLoader(ClassLoader paramClassLoader) {
    remove(null, paramClassLoader);
  }
  
  public final synchronized void addClassLoader(ObjectName paramObjectName, ClassLoader paramClassLoader) {
    this.loadersWithNames.put(paramObjectName, paramClassLoader);
    if (!(paramClassLoader instanceof javax.management.loading.PrivateClassLoader))
      add(paramObjectName, paramClassLoader); 
  }
  
  public final synchronized void removeClassLoader(ObjectName paramObjectName) {
    ClassLoader classLoader = this.loadersWithNames.remove(paramObjectName);
    if (!(classLoader instanceof javax.management.loading.PrivateClassLoader))
      remove(paramObjectName, classLoader); 
  }
  
  public final ClassLoader getClassLoader(ObjectName paramObjectName) {
    ClassLoader classLoader = this.loadersWithNames.get(paramObjectName);
    if (classLoader != null) {
      SecurityManager securityManager = System.getSecurityManager();
      if (securityManager != null) {
        MBeanPermission mBeanPermission = new MBeanPermission(classLoader.getClass().getName(), null, paramObjectName, "getClassLoader");
        securityManager.checkPermission(mBeanPermission);
      } 
    } 
    return classLoader;
  }
}
