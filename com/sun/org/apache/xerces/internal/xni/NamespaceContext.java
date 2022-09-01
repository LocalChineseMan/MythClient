package com.sun.org.apache.xerces.internal.xni;

import java.util.Enumeration;

public interface NamespaceContext {
  public static final String XML_URI = "http://www.w3.org/XML/1998/namespace".intern();
  
  public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/".intern();
  
  void pushContext();
  
  void popContext();
  
  boolean declarePrefix(String paramString1, String paramString2);
  
  String getURI(String paramString);
  
  String getPrefix(String paramString);
  
  int getDeclaredPrefixCount();
  
  String getDeclaredPrefixAt(int paramInt);
  
  Enumeration getAllPrefixes();
  
  void reset();
}
