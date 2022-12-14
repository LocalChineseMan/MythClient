package com.sun.org.apache.xerces.internal.util;

public enum Status {
  SET((short)-3, false),
  UNKNOWN((short)-2, false),
  RECOGNIZED((short)-1, false),
  NOT_SUPPORTED((short)0, true),
  NOT_RECOGNIZED((short)1, true),
  NOT_ALLOWED((short)2, true);
  
  private final short type;
  
  private boolean isExceptional;
  
  Status(short type, boolean isExceptional) {
    this.type = type;
    this.isExceptional = isExceptional;
  }
  
  public short getType() {
    return this.type;
  }
  
  public boolean isExceptional() {
    return this.isExceptional;
  }
}
