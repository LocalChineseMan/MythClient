package com.viaversion.viaversion.libs.fastutil.ints;

import java.io.Serializable;

public abstract class AbstractInt2IntFunction implements Int2IntFunction, Serializable {
  private static final long serialVersionUID = -4940583368468432370L;
  
  protected int defRetValue;
  
  public void defaultReturnValue(int rv) {
    this.defRetValue = rv;
  }
  
  public int defaultReturnValue() {
    return this.defRetValue;
  }
}
