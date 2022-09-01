package com.sun.jmx.mbeanserver;

import java.security.PrivilegedAction;

public class GetPropertyAction implements PrivilegedAction<String> {
  private final String key;
  
  public GetPropertyAction(String paramString) {
    this.key = paramString;
  }
  
  public String run() {
    return System.getProperty(this.key);
  }
}
