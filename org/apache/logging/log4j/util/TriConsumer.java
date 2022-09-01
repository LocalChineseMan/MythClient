package org.apache.logging.log4j.util;

public interface TriConsumer<K, V, S> {
  void accept(K paramK, V paramV, S paramS);
}
