package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

@GwtCompatible
public final class Suppliers {
  public static <F, T> Supplier<T> compose(Function<? super F, T> function, Supplier<F> supplier) {
    Preconditions.checkNotNull(function);
    Preconditions.checkNotNull(supplier);
    return (Supplier<T>)new SupplierComposition(function, supplier);
  }
  
  public static <T> Supplier<T> memoize(Supplier<T> delegate) {
    return (delegate instanceof MemoizingSupplier) ? delegate : (Supplier<T>)new MemoizingSupplier(Preconditions.<Supplier>checkNotNull(delegate));
  }
  
  public static <T> Supplier<T> memoizeWithExpiration(Supplier<T> delegate, long duration, TimeUnit unit) {
    return (Supplier<T>)new ExpiringMemoizingSupplier(delegate, duration, unit);
  }
  
  public static <T> Supplier<T> ofInstance(@Nullable T instance) {
    return new SupplierOfInstance<T>(instance);
  }
  
  private enum Suppliers {
  
  }
  
  private static interface Suppliers {}
  
  private static class Suppliers {}
  
  private static class SupplierOfInstance<T> implements Supplier<T>, Serializable {
    final T instance;
    
    private static final long serialVersionUID = 0L;
    
    SupplierOfInstance(@Nullable T instance) {
      this.instance = instance;
    }
    
    public T get() {
      return this.instance;
    }
    
    public boolean equals(@Nullable Object obj) {
      if (obj instanceof SupplierOfInstance) {
        SupplierOfInstance<?> that = (SupplierOfInstance)obj;
        return Objects.equal(this.instance, that.instance);
      } 
      return false;
    }
    
    public int hashCode() {
      return Objects.hashCode(new Object[] { this.instance });
    }
    
    public String toString() {
      return "Suppliers.ofInstance(" + this.instance + ")";
    }
  }
  
  public static <T> Supplier<T> synchronizedSupplier(Supplier<T> delegate) {
    return (Supplier<T>)new ThreadSafeSupplier(Preconditions.<Supplier>checkNotNull(delegate));
  }
  
  @Beta
  public static <T> Function<Supplier<T>, T> supplierFunction() {
    return (Function<Supplier<T>, T>)SupplierFunctionImpl.INSTANCE;
  }
  
  static class Suppliers {}
  
  static class Suppliers {}
  
  private static class Suppliers {}
}
