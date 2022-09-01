package com.viaversion.viaversion.libs.fastutil.ints;

import java.util.AbstractCollection;
import java.util.Iterator;

public abstract class AbstractIntCollection extends AbstractCollection<Integer> implements IntCollection {
  public boolean add(int k) {
    throw new UnsupportedOperationException();
  }
  
  public boolean contains(int k) {
    IntIterator iterator = iterator();
    while (iterator.hasNext()) {
      if (k == iterator.nextInt())
        return true; 
    } 
    return false;
  }
  
  public boolean rem(int k) {
    IntIterator iterator = iterator();
    while (iterator.hasNext()) {
      if (k == iterator.nextInt()) {
        iterator.remove();
        return true;
      } 
    } 
    return false;
  }
  
  @Deprecated
  public boolean add(Integer key) {
    return super.add(key);
  }
  
  @Deprecated
  public boolean contains(Object key) {
    return super.contains(key);
  }
  
  @Deprecated
  public boolean remove(Object key) {
    return super.remove(key);
  }
  
  public int[] toArray(int[] a) {
    if (a == null || a.length < size())
      a = new int[size()]; 
    IntIterators.unwrap(iterator(), a);
    return a;
  }
  
  public int[] toIntArray() {
    return toArray((int[])null);
  }
  
  @Deprecated
  public int[] toIntArray(int[] a) {
    return toArray(a);
  }
  
  public boolean addAll(IntCollection c) {
    boolean retVal = false;
    for (IntIterator i = c.iterator(); i.hasNext();) {
      if (add(i.nextInt()))
        retVal = true; 
    } 
    return retVal;
  }
  
  public boolean containsAll(IntCollection c) {
    for (IntIterator i = c.iterator(); i.hasNext();) {
      if (!contains(i.nextInt()))
        return false; 
    } 
    return true;
  }
  
  public boolean removeAll(IntCollection c) {
    boolean retVal = false;
    for (IntIterator i = c.iterator(); i.hasNext();) {
      if (rem(i.nextInt()))
        retVal = true; 
    } 
    return retVal;
  }
  
  public boolean retainAll(IntCollection c) {
    boolean retVal = false;
    for (IntIterator i = iterator(); i.hasNext();) {
      if (!c.contains(i.nextInt())) {
        i.remove();
        retVal = true;
      } 
    } 
    return retVal;
  }
  
  public String toString() {
    StringBuilder s = new StringBuilder();
    IntIterator i = iterator();
    int n = size();
    boolean first = true;
    s.append("{");
    while (n-- != 0) {
      if (first) {
        first = false;
      } else {
        s.append(", ");
      } 
      int k = i.nextInt();
      s.append(String.valueOf(k));
    } 
    s.append("}");
    return s.toString();
  }
  
  public abstract IntIterator iterator();
}
