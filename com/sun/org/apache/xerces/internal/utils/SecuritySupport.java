package com.sun.org.apache.xerces.internal.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public final class SecuritySupport {
  private static final SecuritySupport securitySupport = new SecuritySupport();
  
  public static SecuritySupport getInstance() {
    return securitySupport;
  }
  
  static ClassLoader getContextClassLoader() {
    return AccessController.<ClassLoader>doPrivileged(new PrivilegedAction<ClassLoader>() {
          public Object run() {
            ClassLoader cl = null;
            try {
              cl = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException securityException) {}
            return cl;
          }
        });
  }
  
  static ClassLoader getSystemClassLoader() {
    return AccessController.<ClassLoader>doPrivileged(new PrivilegedAction<ClassLoader>() {
          public Object run() {
            ClassLoader cl = null;
            try {
              cl = ClassLoader.getSystemClassLoader();
            } catch (SecurityException securityException) {}
            return cl;
          }
        });
  }
  
  static ClassLoader getParentClassLoader(final ClassLoader cl) {
    return AccessController.<ClassLoader>doPrivileged(new PrivilegedAction<ClassLoader>() {
          public Object run() {
            ClassLoader parent = null;
            try {
              parent = cl.getParent();
            } catch (SecurityException securityException) {}
            return (parent == cl) ? null : parent;
          }
        });
  }
  
  public static String getSystemProperty(final String propName) {
    return AccessController.<String>doPrivileged(new PrivilegedAction<String>() {
          public Object run() {
            return System.getProperty(propName);
          }
        });
  }
  
  static FileInputStream getFileInputStream(File file) throws FileNotFoundException {
    try {
      return AccessController.<FileInputStream>doPrivileged((PrivilegedExceptionAction<FileInputStream>)new Object(file));
    } catch (PrivilegedActionException e) {
      throw (FileNotFoundException)e.getException();
    } 
  }
  
  public static InputStream getResourceAsStream(String name) {
    if (System.getSecurityManager() != null)
      return getResourceAsStream(null, name); 
    return getResourceAsStream(ObjectFactory.findClassLoader(), name);
  }
  
  public static InputStream getResourceAsStream(ClassLoader cl, String name) {
    return AccessController.<InputStream>doPrivileged((PrivilegedAction<InputStream>)new Object(cl, name));
  }
  
  public static ResourceBundle getResourceBundle(String bundle) {
    return getResourceBundle(bundle, Locale.getDefault());
  }
  
  public static ResourceBundle getResourceBundle(String bundle, Locale locale) {
    return AccessController.<ResourceBundle>doPrivileged((PrivilegedAction<ResourceBundle>)new Object(bundle, locale));
  }
  
  static boolean getFileExists(final File f) {
    return ((Boolean)AccessController.<Boolean>doPrivileged(new PrivilegedAction<Boolean>() {
          public Object run() {
            return f.exists() ? Boolean.TRUE : Boolean.FALSE;
          }
        })).booleanValue();
  }
  
  static long getLastModified(File f) {
    return ((Long)AccessController.<Long>doPrivileged((PrivilegedAction<Long>)new Object(f))).longValue();
  }
  
  public static String sanitizePath(String uri) {
    if (uri == null)
      return ""; 
    int i = uri.lastIndexOf("/");
    if (i > 0)
      return uri.substring(i + 1, uri.length()); 
    return uri;
  }
  
  public static String checkAccess(String systemId, String allowedProtocols, String accessAny) throws IOException {
    String protocol;
    if (systemId == null || (allowedProtocols != null && allowedProtocols
      .equalsIgnoreCase(accessAny)))
      return null; 
    if (systemId.indexOf(":") == -1) {
      protocol = "file";
    } else {
      URL url = new URL(systemId);
      protocol = url.getProtocol();
      if (protocol.equalsIgnoreCase("jar")) {
        String path = url.getPath();
        protocol = path.substring(0, path.indexOf(":"));
      } 
    } 
    if (isProtocolAllowed(protocol, allowedProtocols))
      return null; 
    return protocol;
  }
  
  private static boolean isProtocolAllowed(String protocol, String allowedProtocols) {
    if (allowedProtocols == null)
      return false; 
    String[] temp = allowedProtocols.split(",");
    for (String t : temp) {
      t = t.trim();
      if (t.equalsIgnoreCase(protocol))
        return true; 
    } 
    return false;
  }
  
  public static String getJAXPSystemProperty(String sysPropertyId) {
    String accessExternal = getSystemProperty(sysPropertyId);
    if (accessExternal == null)
      accessExternal = readJAXPProperty(sysPropertyId); 
    return accessExternal;
  }
  
  static String readJAXPProperty(String propertyId) {
    String value = null;
    InputStream is = null;
    try {
      if (firstTime)
        synchronized (cacheProps) {
          if (firstTime) {
            String configFile = getSystemProperty("java.home") + File.separator + "lib" + File.separator + "jaxp.properties";
            File f = new File(configFile);
            if (getFileExists(f)) {
              is = getFileInputStream(f);
              cacheProps.load(is);
            } 
            firstTime = false;
          } 
        }  
      value = cacheProps.getProperty(propertyId);
    } catch (Exception exception) {
    
    } finally {
      if (is != null)
        try {
          is.close();
        } catch (IOException iOException) {} 
    } 
    return value;
  }
  
  static final Properties cacheProps = new Properties();
  
  static volatile boolean firstTime = true;
}
