package com.sun.org.apache.xerces.internal.xni;

import java.util.Enumeration;

public interface Augmentations {
  Object putItem(String paramString, Object paramObject);
  
  Object getItem(String paramString);
  
  Object removeItem(String paramString);
  
  Enumeration keys();
  
  void removeAllItems();
}
