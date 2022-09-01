package sun.util.resources;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

public abstract class TimeZoneNamesBundle extends OpenListResourceBundle {
  public String[] getStringArray(String paramString, int paramInt) {
    String[] arrayOfString = handleGetObject(paramString, paramInt);
    if ((arrayOfString == null || arrayOfString.length != paramInt) && this.parent != null)
      arrayOfString = ((TimeZoneNamesBundle)this.parent).getStringArray(paramString, paramInt); 
    if (arrayOfString == null)
      throw new MissingResourceException("no time zone names", getClass().getName(), paramString); 
    return arrayOfString;
  }
  
  public Object handleGetObject(String paramString) {
    return handleGetObject(paramString, 5);
  }
  
  private String[] handleGetObject(String paramString, int paramInt) {
    String[] arrayOfString1 = (String[])super.handleGetObject(paramString);
    if (arrayOfString1 == null)
      return null; 
    int i = Math.min(paramInt - 1, arrayOfString1.length);
    String[] arrayOfString2 = new String[i + 1];
    arrayOfString2[0] = paramString;
    System.arraycopy(arrayOfString1, 0, arrayOfString2, 1, i);
    return arrayOfString2;
  }
  
  protected <K, V> Map<K, V> createMap(int paramInt) {
    return new LinkedHashMap<>(paramInt);
  }
  
  protected <E> Set<E> createSet() {
    return new LinkedHashSet<>();
  }
  
  protected abstract Object[][] getContents();
}
