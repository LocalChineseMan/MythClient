package com.sun.naming.internal;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import javax.naming.ConfigurationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public final class ResourceManager {
  private static final String PROVIDER_RESOURCE_FILE_NAME = "jndiprovider.properties";
  
  private static final String APP_RESOURCE_FILE_NAME = "jndi.properties";
  
  private static final String JRELIB_PROPERTY_FILE_NAME = "jndi.properties";
  
  private static final String DISABLE_APP_RESOURCE_FILES = "com.sun.naming.disable.app.resource.files";
  
  private static final String[] listProperties = new String[] { "java.naming.factory.object", "java.naming.factory.url.pkgs", "java.naming.factory.state", "java.naming.factory.control" };
  
  private static final VersionHelper helper = VersionHelper.getVersionHelper();
  
  private static final WeakHashMap<Object, Hashtable<? super String, Object>> propertiesCache = new WeakHashMap<>(11);
  
  private static final WeakHashMap<ClassLoader, Map<String, List<NamedWeakReference<Object>>>> factoryCache = new WeakHashMap<>(11);
  
  private static final WeakHashMap<ClassLoader, Map<String, WeakReference<Object>>> urlFactoryCache = new WeakHashMap<>(11);
  
  private static final WeakReference<Object> NO_FACTORY = new WeakReference(null);
  
  public static Hashtable<?, ?> getInitialEnvironment(Hashtable<?, ?> paramHashtable) throws NamingException {
    String[] arrayOfString1 = VersionHelper.PROPS;
    if (paramHashtable == null)
      paramHashtable = new Hashtable<>(11); 
    Object object = paramHashtable.get("java.naming.applet");
    String[] arrayOfString2 = helper.getJndiProperties();
    for (byte b = 0; b < arrayOfString1.length; b++) {
      Object object1 = paramHashtable.get(arrayOfString1[b]);
      if (object1 == null) {
        if (object != null)
          object1 = AppletParameter.get(object, arrayOfString1[b]); 
        if (object1 == null)
          object1 = (arrayOfString2 != null) ? arrayOfString2[b] : helper.getJndiProperty(b); 
        if (object1 != null)
          paramHashtable.put(arrayOfString1[b], object1); 
      } 
    } 
    String str = (String)paramHashtable.get("com.sun.naming.disable.app.resource.files");
    if (str != null && str.equalsIgnoreCase("true"))
      return paramHashtable; 
    mergeTables((Hashtable)paramHashtable, getApplicationResources());
    return paramHashtable;
  }
  
  public static String getProperty(String paramString, Hashtable<?, ?> paramHashtable, Context paramContext, boolean paramBoolean) throws NamingException {
    String str1 = (paramHashtable != null) ? (String)paramHashtable.get(paramString) : null;
    if (paramContext == null || (str1 != null && !paramBoolean))
      return str1; 
    String str2 = (String)getProviderResource(paramContext).get(paramString);
    if (str1 == null)
      return str2; 
    if (str2 == null || !paramBoolean)
      return str1; 
    return str1 + ":" + str2;
  }
  
  public static FactoryEnumeration getFactories(String paramString, Hashtable<?, ?> paramHashtable, Context paramContext) throws NamingException {
    String str = getProperty(paramString, paramHashtable, paramContext, true);
    if (str == null)
      return null; 
    ClassLoader classLoader = helper.getContextClassLoader();
    Map<Object, Object> map = null;
    synchronized (factoryCache) {
      map = (Map)factoryCache.get(classLoader);
      if (map == null) {
        map = new HashMap<>(11);
        factoryCache.put(classLoader, map);
      } 
    } 
    synchronized (map) {
      List<NamedWeakReference<Object>> list = (List)map.get(str);
      if (list != null)
        return (list.size() == 0) ? null : new FactoryEnumeration(list, classLoader); 
      StringTokenizer stringTokenizer = new StringTokenizer(str, ":");
      list = new ArrayList<>(5);
      while (stringTokenizer.hasMoreTokens()) {
        try {
          String str1 = stringTokenizer.nextToken();
          Class<?> clazz = helper.loadClass(str1, classLoader);
          list.add(new NamedWeakReference(clazz, str1));
        } catch (Exception exception) {}
      } 
      map.put(str, list);
      return new FactoryEnumeration(list, classLoader);
    } 
  }
  
  public static Object getFactory(String paramString1, Hashtable<?, ?> paramHashtable, Context paramContext, String paramString2, String paramString3) throws NamingException {
    String str1 = getProperty(paramString1, paramHashtable, paramContext, true);
    if (str1 != null) {
      str1 = str1 + ":" + paramString3;
    } else {
      str1 = paramString3;
    } 
    ClassLoader classLoader = helper.getContextClassLoader();
    String str2 = paramString2 + " " + str1;
    Map<Object, Object> map = null;
    synchronized (urlFactoryCache) {
      map = (Map)urlFactoryCache.get(classLoader);
      if (map == null) {
        map = new HashMap<>(11);
        urlFactoryCache.put(classLoader, map);
      } 
    } 
    synchronized (map) {
      Object object = null;
      WeakReference<Object> weakReference = (WeakReference)map.get(str2);
      if (weakReference == NO_FACTORY)
        return null; 
      if (weakReference != null) {
        object = weakReference.get();
        if (object != null)
          return object; 
      } 
      StringTokenizer stringTokenizer = new StringTokenizer(str1, ":");
      while (object == null && stringTokenizer.hasMoreTokens()) {
        String str = stringTokenizer.nextToken() + paramString2;
        try {
          object = helper.loadClass(str, classLoader).newInstance();
        } catch (InstantiationException instantiationException) {
          NamingException namingException = new NamingException("Cannot instantiate " + str);
          namingException.setRootCause(instantiationException);
          throw namingException;
        } catch (IllegalAccessException illegalAccessException) {
          NamingException namingException = new NamingException("Cannot access " + str);
          namingException.setRootCause(illegalAccessException);
          throw namingException;
        } catch (Exception exception) {}
      } 
      map.put(str2, (object != null) ? new WeakReference(object) : NO_FACTORY);
      return object;
    } 
  }
  
  private static Hashtable<? super String, Object> getProviderResource(Object paramObject) throws NamingException {
    if (paramObject == null)
      return new Hashtable<>(1); 
    synchronized (propertiesCache) {
      Class<?> clazz = paramObject.getClass();
      Hashtable<? super String, Object> hashtable = propertiesCache.get(clazz);
      if (hashtable != null)
        return hashtable; 
      hashtable = new Properties();
      InputStream inputStream = helper.getResourceAsStream(clazz, "jndiprovider.properties");
      if (inputStream != null)
        try {
          ((Properties)hashtable).load(inputStream);
        } catch (IOException iOException) {
          ConfigurationException configurationException = new ConfigurationException("Error reading provider resource file for " + clazz);
          configurationException.setRootCause(iOException);
          throw configurationException;
        }  
      propertiesCache.put(clazz, hashtable);
      return hashtable;
    } 
  }
  
  private static Hashtable<? super String, Object> getApplicationResources() throws NamingException {
    ClassLoader classLoader = helper.getContextClassLoader();
    synchronized (propertiesCache) {
      Hashtable<? super String, Object> hashtable = propertiesCache.get(classLoader);
      if (hashtable != null)
        return hashtable; 
      try {
        NamingEnumeration<InputStream> namingEnumeration = helper.getResources(classLoader, "jndi.properties");
        try {
          while (namingEnumeration.hasMore()) {
            Properties properties = new Properties();
            InputStream inputStream1 = namingEnumeration.next();
            try {
              properties.load(inputStream1);
            } finally {
              inputStream1.close();
            } 
            if (hashtable == null) {
              hashtable = properties;
              continue;
            } 
            mergeTables(hashtable, properties);
          } 
        } finally {
          while (namingEnumeration.hasMore())
            ((InputStream)namingEnumeration.next()).close(); 
        } 
        InputStream inputStream = helper.getJavaHomeLibStream("jndi.properties");
        if (inputStream != null)
          try {
            Properties properties = new Properties();
            properties.load(inputStream);
            if (hashtable == null) {
              hashtable = properties;
            } else {
              mergeTables(hashtable, properties);
            } 
          } finally {
            inputStream.close();
          }  
      } catch (IOException iOException) {
        ConfigurationException configurationException = new ConfigurationException("Error reading application resource file");
        configurationException.setRootCause(iOException);
        throw configurationException;
      } 
      if (hashtable == null)
        hashtable = new Hashtable<>(11); 
      propertiesCache.put(classLoader, hashtable);
      return hashtable;
    } 
  }
  
  private static void mergeTables(Hashtable<? super String, Object> paramHashtable1, Hashtable<? super String, Object> paramHashtable2) {
    for (String str1 : paramHashtable2.keySet()) {
      String str2 = str1;
      Object object = paramHashtable1.get(str2);
      if (object == null) {
        paramHashtable1.put(str2, paramHashtable2.get(str2));
        continue;
      } 
      if (isListProperty(str2)) {
        String str = (String)paramHashtable2.get(str2);
        paramHashtable1.put(str2, (String)object + ":" + str);
      } 
    } 
  }
  
  private static boolean isListProperty(String paramString) {
    paramString = paramString.intern();
    for (byte b = 0; b < listProperties.length; b++) {
      if (paramString == listProperties[b])
        return true; 
    } 
    return false;
  }
  
  private static class ResourceManager {}
}
