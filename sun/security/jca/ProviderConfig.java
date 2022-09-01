package sun.security.jca;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.ProviderException;
import sun.security.util.Debug;

final class ProviderConfig {
  private static final Debug debug = Debug.getInstance("jca", "ProviderConfig");
  
  private static final String P11_SOL_NAME = "sun.security.pkcs11.SunPKCS11";
  
  private static final String P11_SOL_ARG = "${java.home}/lib/security/sunpkcs11-solaris.cfg";
  
  private static final int MAX_LOAD_TRIES = 30;
  
  private static final Class[] CL_STRING = new Class[] { String.class };
  
  private final String className;
  
  private final String argument;
  
  private int tries;
  
  private volatile Provider provider;
  
  private boolean isLoading;
  
  ProviderConfig(String paramString1, String paramString2) {
    if (paramString1.equals("sun.security.pkcs11.SunPKCS11") && paramString2.equals("${java.home}/lib/security/sunpkcs11-solaris.cfg"))
      checkSunPKCS11Solaris(); 
    this.className = paramString1;
    this.argument = expand(paramString2);
  }
  
  ProviderConfig(String paramString) {
    this(paramString, "");
  }
  
  ProviderConfig(Provider paramProvider) {
    this.className = paramProvider.getClass().getName();
    this.argument = "";
    this.provider = paramProvider;
  }
  
  private void checkSunPKCS11Solaris() {
    Boolean bool = AccessController.<Boolean>doPrivileged((PrivilegedAction<Boolean>)new Object(this));
    if (bool == Boolean.FALSE)
      this.tries = 30; 
  }
  
  private boolean hasArgument() {
    return (this.argument.length() != 0);
  }
  
  private boolean shouldLoad() {
    return (this.tries < 30);
  }
  
  private void disableLoad() {
    this.tries = 30;
  }
  
  boolean isLoaded() {
    return (this.provider != null);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof ProviderConfig))
      return false; 
    ProviderConfig providerConfig = (ProviderConfig)paramObject;
    return (this.className.equals(providerConfig.className) && this.argument
      .equals(providerConfig.argument));
  }
  
  public int hashCode() {
    return this.className.hashCode() + this.argument.hashCode();
  }
  
  public String toString() {
    if (hasArgument())
      return this.className + "('" + this.argument + "')"; 
    return this.className;
  }
  
  synchronized Provider getProvider() {
    Provider provider = this.provider;
    if (provider != null)
      return provider; 
    if (!shouldLoad())
      return null; 
    if (this.isLoading) {
      if (debug != null) {
        debug.println("Recursion loading provider: " + this);
        (new Exception("Call trace")).printStackTrace();
      } 
      return null;
    } 
    try {
      this.isLoading = true;
      this.tries++;
      provider = doLoadProvider();
    } finally {
      this.isLoading = false;
    } 
    this.provider = provider;
    return provider;
  }
  
  private Provider doLoadProvider() {
    return AccessController.<Provider>doPrivileged(new PrivilegedAction<Provider>() {
          public Provider run() {
            if (ProviderConfig.debug != null)
              ProviderConfig.debug.println("Loading provider: " + ProviderConfig.this); 
            try {
              Class<?> clazz;
              Object object;
              ClassLoader classLoader = ClassLoader.getSystemClassLoader();
              if (classLoader != null) {
                clazz = classLoader.loadClass(ProviderConfig.this.className);
              } else {
                clazz = Class.forName(ProviderConfig.this.className);
              } 
              if (!ProviderConfig.this.hasArgument()) {
                object = clazz.newInstance();
              } else {
                Constructor<?> constructor = clazz.getConstructor(ProviderConfig.CL_STRING);
                object = constructor.newInstance(new Object[] { ProviderConfig.access$400(this.this$0) });
              } 
              if (object instanceof Provider) {
                if (ProviderConfig.debug != null)
                  ProviderConfig.debug.println("Loaded provider " + object); 
                return (Provider)object;
              } 
              if (ProviderConfig.debug != null)
                ProviderConfig.debug.println(ProviderConfig.this.className + " is not a provider"); 
              ProviderConfig.this.disableLoad();
              return null;
            } catch (Exception exception) {
              Throwable throwable;
              if (exception instanceof InvocationTargetException) {
                throwable = ((InvocationTargetException)exception).getCause();
              } else {
                throwable = exception;
              } 
              if (ProviderConfig.debug != null) {
                ProviderConfig.debug.println("Error loading provider " + ProviderConfig.this);
                throwable.printStackTrace();
              } 
              if (throwable instanceof ProviderException)
                throw (ProviderException)throwable; 
              if (throwable instanceof UnsupportedOperationException)
                ProviderConfig.this.disableLoad(); 
              return null;
            } 
          }
        });
  }
  
  private static String expand(String paramString) {
    if (!paramString.contains("${"))
      return paramString; 
    return AccessController.<String>doPrivileged((PrivilegedAction<String>)new Object(paramString));
  }
}
