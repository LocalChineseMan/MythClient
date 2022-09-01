package com.viaversion.viaversion.libs.fastutil.ints;

import java.util.Iterator;
import java.util.Set;

public abstract class AbstractIntSet extends AbstractIntCollection implements Cloneable, IntSet {
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Set))
      return false; 
    Set<?> s = (Set)o;
    if (s.size() != size())
      return false; 
    return containsAll(s);
  }
  
  public int hashCode() {
    int h = 0, n = size();
    IntIterator i = iterator();
    while (n-- != 0) {
      int k = i.nextInt();
      h += k;
    } 
    return h;
  }
  
  public boolean remove(int k) {
    return super.rem(k);
  }
  
  @Deprecated
  public boolean rem(int k) {
    return remove(k);
  }
  
  public abstract IntIterator iterator();
}
