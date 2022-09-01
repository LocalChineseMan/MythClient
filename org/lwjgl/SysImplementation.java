package org.lwjgl;

interface SysImplementation {
  int getRequiredJNIVersion();
  
  int getJNIVersion();
  
  int getPointerSize();
  
  void setDebug(boolean paramBoolean);
  
  long getTimerResolution();
  
  long getTime();
  
  void alert(String paramString1, String paramString2);
  
  boolean openURL(String paramString);
  
  String getClipboard();
  
  boolean has64Bit();
}
