package com.sun.org.apache.xerces.internal.xni;

public interface XMLResourceIdentifier {
  void setPublicId(String paramString);
  
  String getPublicId();
  
  void setExpandedSystemId(String paramString);
  
  String getExpandedSystemId();
  
  void setLiteralSystemId(String paramString);
  
  String getLiteralSystemId();
  
  void setBaseSystemId(String paramString);
  
  String getBaseSystemId();
  
  void setNamespace(String paramString);
  
  String getNamespace();
}
