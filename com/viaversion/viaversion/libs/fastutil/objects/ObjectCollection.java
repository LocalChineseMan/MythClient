package com.viaversion.viaversion.libs.fastutil.objects;

import java.util.Collection;
import java.util.Iterator;

public interface ObjectCollection<K> extends Collection<K>, ObjectIterable<K> {
  ObjectIterator<K> iterator();
}
