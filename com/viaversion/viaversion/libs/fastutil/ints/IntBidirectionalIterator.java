package com.viaversion.viaversion.libs.fastutil.ints;

import com.viaversion.viaversion.libs.fastutil.objects.ObjectBidirectionalIterator;

public interface IntBidirectionalIterator extends IntIterator, ObjectBidirectionalIterator<Integer> {
  @Deprecated
  default Integer previous() {
    return Integer.valueOf(previousInt());
  }
  
  default int back(int n) {
    int i = n;
    while (i-- != 0 && hasPrevious())
      previousInt(); 
    return n - i - 1;
  }
  
  default int skip(int n) {
    return super.skip(n);
  }
  
  int previousInt();
}
