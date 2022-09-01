package sun.text.normalizer;

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.MissingResourceException;

public final class ICUData {
  private static InputStream getStream(Class<ICUData> paramClass, String paramString, boolean paramBoolean) {
    InputStream inputStream = null;
    if (System.getSecurityManager() != null) {
      inputStream = AccessController.<InputStream>doPrivileged((PrivilegedAction<InputStream>)new Object(paramClass, paramString));
    } else {
      inputStream = paramClass.getResourceAsStream(paramString);
    } 
    if (inputStream == null && paramBoolean)
      throw new MissingResourceException("could not locate data", paramClass.getPackage().getName(), paramString); 
    return inputStream;
  }
  
  public static InputStream getStream(String paramString) {
    return getStream(ICUData.class, paramString, false);
  }
  
  public static InputStream getRequiredStream(String paramString) {
    return getStream(ICUData.class, paramString, true);
  }
}
