package com.viaversion.viaversion.libs.fastutil.objects;

import java.util.Iterator;
import java.util.Set;

public interface ObjectSet<K> extends ObjectCollection<K>, Set<K> {
  ObjectIterator<K> iterator();
}
