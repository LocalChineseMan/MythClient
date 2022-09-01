package com.ibm.icu.impl;

import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.MissingResourceException;

public final class ICUData {
  public static boolean exists(String resourceName) {
    URL i = null;
    if (System.getSecurityManager() != null) {
      i = AccessController.<URL>doPrivileged((PrivilegedAction<URL>)new Object(resourceName));
    } else {
      i = ICUData.class.getResource(resourceName);
    } 
    return (i != null);
  }
  
  private static InputStream getStream(Class<?> root, String resourceName, boolean required) {
    InputStream i = null;
    if (System.getSecurityManager() != null) {
      i = AccessController.<InputStream>doPrivileged((PrivilegedAction<InputStream>)new Object(root, resourceName));
    } else {
      i = root.getResourceAsStream(resourceName);
    } 
    if (i == null && required)
      throw new MissingResourceException("could not locate data " + resourceName, root.getPackage().getName(), resourceName); 
    return i;
  }
  
  private static InputStream getStream(ClassLoader loader, String resourceName, boolean required) {
    InputStream i = null;
    if (System.getSecurityManager() != null) {
      i = AccessController.<InputStream>doPrivileged((PrivilegedAction<InputStream>)new Object(loader, resourceName));
    } else {
      i = loader.getResourceAsStream(resourceName);
    } 
    if (i == null && required)
      throw new MissingResourceException("could not locate data", loader.toString(), resourceName); 
    return i;
  }
  
  public static InputStream getStream(ClassLoader loader, String resourceName) {
    return getStream(loader, resourceName, false);
  }
  
  public static InputStream getRequiredStream(ClassLoader loader, String resourceName) {
    return getStream(loader, resourceName, true);
  }
  
  public static InputStream getStream(String resourceName) {
    return getStream(ICUData.class, resourceName, false);
  }
  
  public static InputStream getRequiredStream(String resourceName) {
    return getStream(ICUData.class, resourceName, true);
  }
  
  public static InputStream getStream(Class<?> root, String resourceName) {
    return getStream(root, resourceName, false);
  }
  
  public static InputStream getRequiredStream(Class<?> root, String resourceName) {
    return getStream(root, resourceName, true);
  }
}
