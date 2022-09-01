package com.viaversion.viaversion.libs.fastutil.objects;

import java.util.Iterator;

public abstract class AbstractObjectSortedSet<K> extends AbstractObjectSet<K> implements ObjectSortedSet<K> {
  public abstract ObjectBidirectionalIterator<K> iterator();
}
