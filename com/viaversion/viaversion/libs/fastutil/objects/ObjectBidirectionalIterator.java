package com.viaversion.viaversion.libs.fastutil.objects;

import com.viaversion.viaversion.libs.fastutil.BidirectionalIterator;

public interface ObjectBidirectionalIterator<K> extends ObjectIterator<K>, BidirectionalIterator<K> {
  default int back(int n) {
    int i = n;
    while (i-- != 0 && hasPrevious())
      previous(); 
    return n - i - 1;
  }
  
  default int skip(int n) {
    return super.skip(n);
  }
}
