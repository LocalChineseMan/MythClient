package com.viaversion.viaversion.libs.fastutil.objects;

import java.util.Iterator;

public interface ObjectIterator<K> extends Iterator<K> {
  default int skip(int n) {
    if (n < 0)
      throw new IllegalArgumentException("Argument must be nonnegative: " + n); 
    int i = n;
    while (i-- != 0 && hasNext())
      next(); 
    return n - i - 1;
  }
}
