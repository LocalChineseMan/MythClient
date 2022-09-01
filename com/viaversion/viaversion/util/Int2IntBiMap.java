package com.viaversion.viaversion.util;

import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import java.util.Map;

public interface Int2IntBiMap extends Int2IntMap {
  Int2IntBiMap inverse();
  
  int put(int paramInt1, int paramInt2);
  
  @Deprecated
  default void putAll(Map<? extends Integer, ? extends Integer> m) {
    throw new UnsupportedOperationException();
  }
}
