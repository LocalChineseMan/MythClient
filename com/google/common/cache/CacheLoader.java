package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Map;
import java.util.concurrent.Executor;

@GwtCompatible(emulated = true)
public abstract class CacheLoader<K, V> {
  public abstract V load(K paramK) throws Exception;
  
  @GwtIncompatible("Futures")
  public ListenableFuture<V> reload(K key, V oldValue) throws Exception {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(oldValue);
    return Futures.immediateFuture(load(key));
  }
  
  public Map<K, V> loadAll(Iterable<? extends K> keys) throws Exception {
    throw new UnsupportedLoadingOperationException();
  }
  
  @Beta
  public static <K, V> CacheLoader<K, V> from(Function<K, V> function) {
    return (CacheLoader<K, V>)new FunctionToCacheLoader(function);
  }
  
  @Beta
  public static <V> CacheLoader<Object, V> from(Supplier<V> supplier) {
    return (CacheLoader<Object, V>)new SupplierToCacheLoader(supplier);
  }
  
  @Beta
  @GwtIncompatible("Executor + Futures")
  public static <K, V> CacheLoader<K, V> asyncReloading(CacheLoader<K, V> loader, Executor executor) {
    Preconditions.checkNotNull(loader);
    Preconditions.checkNotNull(executor);
    return (CacheLoader<K, V>)new Object(loader, executor);
  }
  
  public static final class CacheLoader {}
  
  static final class CacheLoader {}
  
  private static final class CacheLoader {}
  
  private static final class CacheLoader {}
}
