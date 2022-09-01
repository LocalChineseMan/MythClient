package com.sun.org.apache.xerces.internal.impl.dv;

import java.util.Locale;

public interface ValidationContext {
  boolean needFacetChecking();
  
  boolean needExtraChecking();
  
  boolean needToNormalize();
  
  boolean useNamespaces();
  
  boolean isEntityDeclared(String paramString);
  
  boolean isEntityUnparsed(String paramString);
  
  boolean isIdDeclared(String paramString);
  
  void addId(String paramString);
  
  void addIdRef(String paramString);
  
  String getSymbol(String paramString);
  
  String getURI(String paramString);
  
  Locale getLocale();
}
