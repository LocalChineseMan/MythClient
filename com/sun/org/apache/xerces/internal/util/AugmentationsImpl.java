package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.Augmentations;
import java.util.Enumeration;

public class AugmentationsImpl implements Augmentations {
  private AugmentationsItemsContainer fAugmentationsContainer = (AugmentationsItemsContainer)new SmallContainer();
  
  public Object putItem(String key, Object item) {
    Object oldValue = this.fAugmentationsContainer.putItem(key, item);
    if (oldValue == null && this.fAugmentationsContainer.isFull())
      this.fAugmentationsContainer = this.fAugmentationsContainer.expand(); 
    return oldValue;
  }
  
  public Object getItem(String key) {
    return this.fAugmentationsContainer.getItem(key);
  }
  
  public Object removeItem(String key) {
    return this.fAugmentationsContainer.removeItem(key);
  }
  
  public Enumeration keys() {
    return this.fAugmentationsContainer.keys();
  }
  
  public void removeAllItems() {
    this.fAugmentationsContainer.clear();
  }
  
  public String toString() {
    return this.fAugmentationsContainer.toString();
  }
  
  abstract class AugmentationsItemsContainer {
    public abstract Object putItem(Object param1Object1, Object param1Object2);
    
    public abstract Object getItem(Object param1Object);
    
    public abstract Object removeItem(Object param1Object);
    
    public abstract Enumeration keys();
    
    public abstract void clear();
    
    public abstract boolean isFull();
    
    public abstract AugmentationsItemsContainer expand();
  }
  
  class SmallContainer extends AugmentationsItemsContainer {
    static final int SIZE_LIMIT = 10;
    
    final Object[] fAugmentations;
    
    int fNumEntries;
    
    SmallContainer() {
      this.fAugmentations = new Object[20];
      this.fNumEntries = 0;
    }
    
    public Enumeration keys() {
      return new SmallContainerKeyEnumeration(this);
    }
    
    public Object getItem(Object key) {
      for (int i = 0; i < this.fNumEntries * 2; i += 2) {
        if (this.fAugmentations[i].equals(key))
          return this.fAugmentations[i + 1]; 
      } 
      return null;
    }
    
    public Object putItem(Object key, Object item) {
      for (int i = 0; i < this.fNumEntries * 2; i += 2) {
        if (this.fAugmentations[i].equals(key)) {
          Object oldValue = this.fAugmentations[i + 1];
          this.fAugmentations[i + 1] = item;
          return oldValue;
        } 
      } 
      this.fAugmentations[this.fNumEntries * 2] = key;
      this.fAugmentations[this.fNumEntries * 2 + 1] = item;
      this.fNumEntries++;
      return null;
    }
    
    public Object removeItem(Object key) {
      for (int i = 0; i < this.fNumEntries * 2; i += 2) {
        if (this.fAugmentations[i].equals(key)) {
          Object oldValue = this.fAugmentations[i + 1];
          int j;
          for (j = i; j < this.fNumEntries * 2 - 2; j += 2) {
            this.fAugmentations[j] = this.fAugmentations[j + 2];
            this.fAugmentations[j + 1] = this.fAugmentations[j + 3];
          } 
          this.fAugmentations[this.fNumEntries * 2 - 2] = null;
          this.fAugmentations[this.fNumEntries * 2 - 1] = null;
          this.fNumEntries--;
          return oldValue;
        } 
      } 
      return null;
    }
    
    public void clear() {
      for (int i = 0; i < this.fNumEntries * 2; i += 2) {
        this.fAugmentations[i] = null;
        this.fAugmentations[i + 1] = null;
      } 
      this.fNumEntries = 0;
    }
    
    public boolean isFull() {
      return (this.fNumEntries == 10);
    }
    
    public AugmentationsImpl.AugmentationsItemsContainer expand() {
      AugmentationsImpl.LargeContainer expandedContainer = new AugmentationsImpl.LargeContainer(AugmentationsImpl.this);
      for (int i = 0; i < this.fNumEntries * 2; i += 2)
        expandedContainer.putItem(this.fAugmentations[i], this.fAugmentations[i + 1]); 
      return expandedContainer;
    }
    
    public String toString() {
      StringBuffer buff = new StringBuffer();
      buff.append("SmallContainer - fNumEntries == " + this.fNumEntries);
      for (int i = 0; i < 20; i += 2) {
        buff.append("\nfAugmentations[");
        buff.append(i);
        buff.append("] == ");
        buff.append(this.fAugmentations[i]);
        buff.append("; fAugmentations[");
        buff.append(i + 1);
        buff.append("] == ");
        buff.append(this.fAugmentations[i + 1]);
      } 
      return buff.toString();
    }
    
    class SmallContainer {}
  }
  
  class AugmentationsImpl {}
}
