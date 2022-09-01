package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@GwtCompatible(emulated = true)
public final class MapMaker extends GenericMapMaker<Object, Object> {
  private static final int DEFAULT_INITIAL_CAPACITY = 16;
  
  private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
  
  private static final int DEFAULT_EXPIRATION_NANOS = 0;
  
  static final int UNSET_INT = -1;
  
  boolean useCustomMap;
  
  int initialCapacity = -1;
  
  int concurrencyLevel = -1;
  
  int maximumSize = -1;
  
  MapMakerInternalMap.Strength keyStrength;
  
  MapMakerInternalMap.Strength valueStrength;
  
  long expireAfterWriteNanos = -1L;
  
  long expireAfterAccessNanos = -1L;
  
  RemovalCause nullRemovalCause;
  
  Equivalence<Object> keyEquivalence;
  
  Ticker ticker;
  
  @GwtIncompatible("To be supported")
  MapMaker keyEquivalence(Equivalence<Object> equivalence) {
    Preconditions.checkState((this.keyEquivalence == null), "key equivalence was already set to %s", new Object[] { this.keyEquivalence });
    this.keyEquivalence = (Equivalence<Object>)Preconditions.checkNotNull(equivalence);
    this.useCustomMap = true;
    return this;
  }
  
  Equivalence<Object> getKeyEquivalence() {
    return (Equivalence<Object>)Objects.firstNonNull(this.keyEquivalence, getKeyStrength().defaultEquivalence());
  }
  
  public MapMaker initialCapacity(int initialCapacity) {
    Preconditions.checkState((this.initialCapacity == -1), "initial capacity was already set to %s", new Object[] { Integer.valueOf(this.initialCapacity) });
    Preconditions.checkArgument((initialCapacity >= 0));
    this.initialCapacity = initialCapacity;
    return this;
  }
  
  int getInitialCapacity() {
    return (this.initialCapacity == -1) ? 16 : this.initialCapacity;
  }
  
  @Deprecated
  MapMaker maximumSize(int size) {
    Preconditions.checkState((this.maximumSize == -1), "maximum size was already set to %s", new Object[] { Integer.valueOf(this.maximumSize) });
    Preconditions.checkArgument((size >= 0), "maximum size must not be negative");
    this.maximumSize = size;
    this.useCustomMap = true;
    if (this.maximumSize == 0)
      this.nullRemovalCause = RemovalCause.SIZE; 
    return this;
  }
  
  public MapMaker concurrencyLevel(int concurrencyLevel) {
    Preconditions.checkState((this.concurrencyLevel == -1), "concurrency level was already set to %s", new Object[] { Integer.valueOf(this.concurrencyLevel) });
    Preconditions.checkArgument((concurrencyLevel > 0));
    this.concurrencyLevel = concurrencyLevel;
    return this;
  }
  
  int getConcurrencyLevel() {
    return (this.concurrencyLevel == -1) ? 4 : this.concurrencyLevel;
  }
  
  @GwtIncompatible("java.lang.ref.WeakReference")
  public MapMaker weakKeys() {
    return setKeyStrength(MapMakerInternalMap.Strength.WEAK);
  }
  
  MapMaker setKeyStrength(MapMakerInternalMap.Strength strength) {
    Preconditions.checkState((this.keyStrength == null), "Key strength was already set to %s", new Object[] { this.keyStrength });
    this.keyStrength = (MapMakerInternalMap.Strength)Preconditions.checkNotNull(strength);
    Preconditions.checkArgument((this.keyStrength != MapMakerInternalMap.Strength.SOFT), "Soft keys are not supported");
    if (strength != MapMakerInternalMap.Strength.STRONG)
      this.useCustomMap = true; 
    return this;
  }
  
  MapMakerInternalMap.Strength getKeyStrength() {
    return (MapMakerInternalMap.Strength)Objects.firstNonNull(this.keyStrength, MapMakerInternalMap.Strength.STRONG);
  }
  
  @GwtIncompatible("java.lang.ref.WeakReference")
  public MapMaker weakValues() {
    return setValueStrength(MapMakerInternalMap.Strength.WEAK);
  }
  
  @Deprecated
  @GwtIncompatible("java.lang.ref.SoftReference")
  public MapMaker softValues() {
    return setValueStrength(MapMakerInternalMap.Strength.SOFT);
  }
  
  MapMaker setValueStrength(MapMakerInternalMap.Strength strength) {
    Preconditions.checkState((this.valueStrength == null), "Value strength was already set to %s", new Object[] { this.valueStrength });
    this.valueStrength = (MapMakerInternalMap.Strength)Preconditions.checkNotNull(strength);
    if (strength != MapMakerInternalMap.Strength.STRONG)
      this.useCustomMap = true; 
    return this;
  }
  
  MapMakerInternalMap.Strength getValueStrength() {
    return (MapMakerInternalMap.Strength)Objects.firstNonNull(this.valueStrength, MapMakerInternalMap.Strength.STRONG);
  }
  
  @Deprecated
  MapMaker expireAfterWrite(long duration, TimeUnit unit) {
    checkExpiration(duration, unit);
    this.expireAfterWriteNanos = unit.toNanos(duration);
    if (duration == 0L && this.nullRemovalCause == null)
      this.nullRemovalCause = RemovalCause.EXPIRED; 
    this.useCustomMap = true;
    return this;
  }
  
  private void checkExpiration(long duration, TimeUnit unit) {
    Preconditions.checkState((this.expireAfterWriteNanos == -1L), "expireAfterWrite was already set to %s ns", new Object[] { Long.valueOf(this.expireAfterWriteNanos) });
    Preconditions.checkState((this.expireAfterAccessNanos == -1L), "expireAfterAccess was already set to %s ns", new Object[] { Long.valueOf(this.expireAfterAccessNanos) });
    Preconditions.checkArgument((duration >= 0L), "duration cannot be negative: %s %s", new Object[] { Long.valueOf(duration), unit });
  }
  
  long getExpireAfterWriteNanos() {
    return (this.expireAfterWriteNanos == -1L) ? 0L : this.expireAfterWriteNanos;
  }
  
  @Deprecated
  @GwtIncompatible("To be supported")
  MapMaker expireAfterAccess(long duration, TimeUnit unit) {
    checkExpiration(duration, unit);
    this.expireAfterAccessNanos = unit.toNanos(duration);
    if (duration == 0L && this.nullRemovalCause == null)
      this.nullRemovalCause = RemovalCause.EXPIRED; 
    this.useCustomMap = true;
    return this;
  }
  
  long getExpireAfterAccessNanos() {
    return (this.expireAfterAccessNanos == -1L) ? 0L : this.expireAfterAccessNanos;
  }
  
  Ticker getTicker() {
    return (Ticker)Objects.firstNonNull(this.ticker, Ticker.systemTicker());
  }
  
  @Deprecated
  @GwtIncompatible("To be supported")
  <K, V> GenericMapMaker<K, V> removalListener(RemovalListener<K, V> listener) {
    Preconditions.checkState((this.removalListener == null));
    GenericMapMaker<K, V> me = (GenericMapMaker<K, V>)this;
    me.removalListener = (RemovalListener<K, V>)Preconditions.checkNotNull(listener);
    this.useCustomMap = true;
    return me;
  }
  
  public <K, V> ConcurrentMap<K, V> makeMap() {
    if (!this.useCustomMap)
      return new ConcurrentHashMap<K, V>(getInitialCapacity(), 0.75F, getConcurrencyLevel()); 
    return (this.nullRemovalCause == null) ? (ConcurrentMap<K, V>)new MapMakerInternalMap(this) : (ConcurrentMap<K, V>)new NullConcurrentMap(this);
  }
  
  @GwtIncompatible("MapMakerInternalMap")
  <K, V> MapMakerInternalMap<K, V> makeCustomMap() {
    return new MapMakerInternalMap(this);
  }
  
  @Deprecated
  <K, V> ConcurrentMap<K, V> makeComputingMap(Function<? super K, ? extends V> computingFunction) {
    return (this.nullRemovalCause == null) ? (ConcurrentMap<K, V>)new ComputingMapAdapter(this, computingFunction) : (ConcurrentMap<K, V>)new NullComputingConcurrentMap(this, computingFunction);
  }
  
  public String toString() {
    Objects.ToStringHelper s = Objects.toStringHelper(this);
    if (this.initialCapacity != -1)
      s.add("initialCapacity", this.initialCapacity); 
    if (this.concurrencyLevel != -1)
      s.add("concurrencyLevel", this.concurrencyLevel); 
    if (this.maximumSize != -1)
      s.add("maximumSize", this.maximumSize); 
    if (this.expireAfterWriteNanos != -1L)
      s.add("expireAfterWrite", this.expireAfterWriteNanos + "ns"); 
    if (this.expireAfterAccessNanos != -1L)
      s.add("expireAfterAccess", this.expireAfterAccessNanos + "ns"); 
    if (this.keyStrength != null)
      s.add("keyStrength", Ascii.toLowerCase(this.keyStrength.toString())); 
    if (this.valueStrength != null)
      s.add("valueStrength", Ascii.toLowerCase(this.valueStrength.toString())); 
    if (this.keyEquivalence != null)
      s.addValue("keyEquivalence"); 
    if (this.removalListener != null)
      s.addValue("removalListener"); 
    return s.toString();
  }
  
  static final class MapMaker {}
  
  static final class MapMaker {}
  
  static class MapMaker {}
  
  enum MapMaker {
  
  }
  
  static final class MapMaker {}
  
  static interface MapMaker {}
}
