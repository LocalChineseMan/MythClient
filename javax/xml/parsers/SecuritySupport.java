package javax.xml.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

class SecuritySupport {
  ClassLoader getContextClassLoader() throws SecurityException {
    return AccessController.<ClassLoader>doPrivileged(new PrivilegedAction<ClassLoader>() {
          public Object run() {
            ClassLoader cl = null;
            cl = Thread.currentThread().getContextClassLoader();
            if (cl == null)
              cl = ClassLoader.getSystemClassLoader(); 
            return cl;
          }
        });
  }
  
  String getSystemProperty(final String propName) {
    return AccessController.<String>doPrivileged(new PrivilegedAction<String>() {
          public Object run() {
            return System.getProperty(propName);
          }
        });
  }
  
  FileInputStream getFileInputStream(File file) throws FileNotFoundException {
    try {
      return AccessController.<FileInputStream>doPrivileged((PrivilegedExceptionAction<FileInputStream>)new Object(this, file));
    } catch (PrivilegedActionException e) {
      throw (FileNotFoundException)e.getException();
    } 
  }
  
  InputStream getResourceAsStream(ClassLoader cl, String name) {
    return AccessController.<InputStream>doPrivileged((PrivilegedAction<InputStream>)new Object(this, cl, name));
  }
  
  boolean doesFileExist(final File f) {
    return ((Boolean)AccessController.<Boolean>doPrivileged(new PrivilegedAction<Boolean>() {
          public Object run() {
            return new Boolean(f.exists());
          }
        })).booleanValue();
  }
}
