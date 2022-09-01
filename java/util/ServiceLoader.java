package java.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class ServiceLoader<S> implements Iterable<S> {
  private static final String PREFIX = "META-INF/services/";
  
  private final Class<S> service;
  
  private final ClassLoader loader;
  
  private final AccessControlContext acc;
  
  private LinkedHashMap<String, S> providers = new LinkedHashMap<>();
  
  private LazyIterator lookupIterator;
  
  public void reload() {
    this.providers.clear();
    this.lookupIterator = new LazyIterator(this.service, this.loader);
  }
  
  private ServiceLoader(Class<S> paramClass, ClassLoader paramClassLoader) {
    this.service = Objects.<Class<S>>requireNonNull(paramClass, "Service interface cannot be null");
    this.loader = (paramClassLoader == null) ? ClassLoader.getSystemClassLoader() : paramClassLoader;
    this.acc = (System.getSecurityManager() != null) ? AccessController.getContext() : null;
    reload();
  }
  
  private static void fail(Class<?> paramClass, String paramString, Throwable paramThrowable) throws ServiceConfigurationError {
    throw new ServiceConfigurationError(paramClass.getName() + ": " + paramString, paramThrowable);
  }
  
  private static void fail(Class<?> paramClass, String paramString) throws ServiceConfigurationError {
    throw new ServiceConfigurationError(paramClass.getName() + ": " + paramString);
  }
  
  private static void fail(Class<?> paramClass, URL paramURL, int paramInt, String paramString) throws ServiceConfigurationError {
    fail(paramClass, paramURL + ":" + paramInt + ": " + paramString);
  }
  
  private int parseLine(Class<?> paramClass, URL paramURL, BufferedReader paramBufferedReader, int paramInt, List<String> paramList) throws IOException, ServiceConfigurationError {
    String str = paramBufferedReader.readLine();
    if (str == null)
      return -1; 
    int i = str.indexOf('#');
    if (i >= 0)
      str = str.substring(0, i); 
    str = str.trim();
    int j = str.length();
    if (j != 0) {
      if (str.indexOf(' ') >= 0 || str.indexOf('\t') >= 0)
        fail(paramClass, paramURL, paramInt, "Illegal configuration-file syntax"); 
      int k = str.codePointAt(0);
      if (!Character.isJavaIdentifierStart(k))
        fail(paramClass, paramURL, paramInt, "Illegal provider-class name: " + str); 
      int m;
      for (m = Character.charCount(k); m < j; m += Character.charCount(k)) {
        k = str.codePointAt(m);
        if (!Character.isJavaIdentifierPart(k) && k != 46)
          fail(paramClass, paramURL, paramInt, "Illegal provider-class name: " + str); 
      } 
      if (!this.providers.containsKey(str) && !paramList.contains(str))
        paramList.add(str); 
    } 
    return paramInt + 1;
  }
  
  private Iterator<String> parse(Class<?> paramClass, URL paramURL) throws ServiceConfigurationError {
    InputStream inputStream = null;
    BufferedReader bufferedReader = null;
    ArrayList<String> arrayList = new ArrayList();
    try {
      inputStream = paramURL.openStream();
      bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
      int i = 1;
      while ((i = parseLine(paramClass, paramURL, bufferedReader, i, arrayList)) >= 0);
    } catch (IOException iOException) {
      fail(paramClass, "Error reading configuration file", iOException);
    } finally {
      try {
        if (bufferedReader != null)
          bufferedReader.close(); 
        if (inputStream != null)
          inputStream.close(); 
      } catch (IOException iOException) {
        fail(paramClass, "Error closing configuration file", iOException);
      } 
    } 
    return arrayList.iterator();
  }
  
  private class LazyIterator implements Iterator<S> {
    Class<S> service;
    
    ClassLoader loader;
    
    Enumeration<URL> configs = null;
    
    Iterator<String> pending = null;
    
    String nextName = null;
    
    private LazyIterator(Class<S> param1Class, ClassLoader param1ClassLoader) {
      this.service = param1Class;
      this.loader = param1ClassLoader;
    }
    
    private boolean hasNextService() {
      if (this.nextName != null)
        return true; 
      if (this.configs == null)
        try {
          String str = "META-INF/services/" + this.service.getName();
          if (this.loader == null) {
            this.configs = ClassLoader.getSystemResources(str);
          } else {
            this.configs = this.loader.getResources(str);
          } 
        } catch (IOException iOException) {
          ServiceLoader.fail(this.service, "Error locating configuration files", iOException);
        }  
      while (this.pending == null || !this.pending.hasNext()) {
        if (!this.configs.hasMoreElements())
          return false; 
        this.pending = ServiceLoader.this.parse(this.service, this.configs.nextElement());
      } 
      this.nextName = this.pending.next();
      return true;
    }
    
    private S nextService() {
      if (!hasNextService())
        throw new NoSuchElementException(); 
      String str = this.nextName;
      this.nextName = null;
      Class<?> clazz = null;
      try {
        clazz = Class.forName(str, false, this.loader);
      } catch (ClassNotFoundException classNotFoundException) {
        ServiceLoader.fail(this.service, "Provider " + str + " not found");
      } 
      if (!this.service.isAssignableFrom(clazz))
        ServiceLoader.fail(this.service, "Provider " + str + " not a subtype"); 
      try {
        S s = this.service.cast(clazz.newInstance());
        ServiceLoader.this.providers.put(str, s);
        return s;
      } catch (Throwable throwable) {
        ServiceLoader.fail(this.service, "Provider " + str + " could not be instantiated", throwable);
        throw new Error();
      } 
    }
    
    public boolean hasNext() {
      if (ServiceLoader.this.acc == null)
        return hasNextService(); 
      Object object = new Object(this);
      return ((Boolean)AccessController.<Boolean>doPrivileged((PrivilegedAction<Boolean>)object, ServiceLoader.this.acc)).booleanValue();
    }
    
    public S next() {
      if (ServiceLoader.this.acc == null)
        return nextService(); 
      Object object = new Object(this);
      return AccessController.doPrivileged((PrivilegedAction<S>)object, ServiceLoader.this.acc);
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  public Iterator<S> iterator() {
    return new Iterator<S>() {
        Iterator<Map.Entry<String, S>> knownProviders = ServiceLoader.this
          .providers.entrySet().iterator();
        
        public boolean hasNext() {
          if (this.knownProviders.hasNext())
            return true; 
          return ServiceLoader.this.lookupIterator.hasNext();
        }
        
        public S next() {
          if (this.knownProviders.hasNext())
            return (S)((Map.Entry)this.knownProviders.next()).getValue(); 
          return ServiceLoader.this.lookupIterator.next();
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  public static <S> ServiceLoader<S> load(Class<S> paramClass, ClassLoader paramClassLoader) {
    return new ServiceLoader<>(paramClass, paramClassLoader);
  }
  
  public static <S> ServiceLoader<S> load(Class<S> paramClass) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    return load(paramClass, classLoader);
  }
  
  public static <S> ServiceLoader<S> loadInstalled(Class<S> paramClass) {
    ClassLoader classLoader1 = ClassLoader.getSystemClassLoader();
    ClassLoader classLoader2 = null;
    while (classLoader1 != null) {
      classLoader2 = classLoader1;
      classLoader1 = classLoader1.getParent();
    } 
    return load(paramClass, classLoader2);
  }
  
  public String toString() {
    return "java.util.ServiceLoader[" + this.service.getName() + "]";
  }
}
