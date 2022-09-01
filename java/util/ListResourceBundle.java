package java.util;

import sun.util.ResourceBundleEnumeration;

public abstract class ListResourceBundle extends ResourceBundle {
  public final Object handleGetObject(String paramString) {
    if (this.lookup == null)
      loadLookup(); 
    if (paramString == null)
      throw new NullPointerException(); 
    return this.lookup.get(paramString);
  }
  
  public Enumeration<String> getKeys() {
    if (this.lookup == null)
      loadLookup(); 
    ResourceBundle resourceBundle = this.parent;
    return new ResourceBundleEnumeration(this.lookup.keySet(), (resourceBundle != null) ? resourceBundle
        .getKeys() : null);
  }
  
  protected Set<String> handleKeySet() {
    if (this.lookup == null)
      loadLookup(); 
    return this.lookup.keySet();
  }
  
  protected abstract Object[][] getContents();
  
  private synchronized void loadLookup() {
    if (this.lookup != null)
      return; 
    Object[][] arrayOfObject = getContents();
    HashMap<Object, Object> hashMap = new HashMap<>(arrayOfObject.length);
    for (byte b = 0; b < arrayOfObject.length; b++) {
      String str = (String)arrayOfObject[b][0];
      Object object = arrayOfObject[b][1];
      if (str == null || object == null)
        throw new NullPointerException(); 
      hashMap.put(str, object);
    } 
    this.lookup = (Map)hashMap;
  }
  
  private Map<String, Object> lookup = null;
}
