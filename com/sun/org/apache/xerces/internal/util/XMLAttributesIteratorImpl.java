package com.sun.org.apache.xerces.internal.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class XMLAttributesIteratorImpl extends XMLAttributesImpl implements Iterator {
  protected int fCurrent = 0;
  
  protected XMLAttributesImpl.Attribute fLastReturnedItem;
  
  public boolean hasNext() {
    return (this.fCurrent < getLength());
  }
  
  public Object next() {
    if (hasNext())
      return this.fLastReturnedItem = this.fAttributes[this.fCurrent++]; 
    throw new NoSuchElementException();
  }
  
  public void remove() {
    if (this.fLastReturnedItem == this.fAttributes[this.fCurrent - 1]) {
      removeAttributeAt(this.fCurrent--);
    } else {
      throw new IllegalStateException();
    } 
  }
  
  public void removeAllAttributes() {
    super.removeAllAttributes();
    this.fCurrent = 0;
  }
}
