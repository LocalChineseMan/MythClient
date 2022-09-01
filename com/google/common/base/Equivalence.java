package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible
public abstract class Equivalence<T> {
  public final boolean equivalent(@Nullable T a, @Nullable T b) {
    if (a == b)
      return true; 
    if (a == null || b == null)
      return false; 
    return doEquivalent(a, b);
  }
  
  protected abstract boolean doEquivalent(T paramT1, T paramT2);
  
  public final int hash(@Nullable T t) {
    if (t == null)
      return 0; 
    return doHash(t);
  }
  
  protected abstract int doHash(T paramT);
  
  public final <F> Equivalence<F> onResultOf(Function<F, ? extends T> function) {
    return (Equivalence<F>)new FunctionalEquivalence(function, this);
  }
  
  public final <S extends T> Wrapper<S> wrap(@Nullable S reference) {
    return new Wrapper(this, reference, null);
  }
  
  @GwtCompatible(serializable = true)
  public final <S extends T> Equivalence<Iterable<S>> pairwise() {
    return (Equivalence<Iterable<S>>)new PairwiseEquivalence(this);
  }
  
  @Beta
  public final Predicate<T> equivalentTo(@Nullable T target) {
    return (Predicate<T>)new EquivalentToPredicate(this, target);
  }
  
  public static Equivalence<Object> equals() {
    return Equals.INSTANCE;
  }
  
  public static Equivalence<Object> identity() {
    return (Equivalence<Object>)Identity.INSTANCE;
  }
  
  static final class Equivalence {}
  
  static final class Equals extends Equivalence<Object> implements Serializable {
    static final Equals INSTANCE = new Equals();
    
    private static final long serialVersionUID = 1L;
    
    protected boolean doEquivalent(Object a, Object b) {
      return a.equals(b);
    }
    
    public int doHash(Object o) {
      return o.hashCode();
    }
    
    private Object readResolve() {
      return INSTANCE;
    }
  }
  
  private static final class Equivalence {}
  
  public static final class Equivalence {}
}
