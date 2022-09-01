package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

@GwtCompatible(emulated = true)
public final class Predicates {
  @GwtCompatible(serializable = true)
  public static <T> Predicate<T> alwaysTrue() {
    return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
  }
  
  @GwtCompatible(serializable = true)
  public static <T> Predicate<T> alwaysFalse() {
    return ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
  }
  
  @GwtCompatible(serializable = true)
  public static <T> Predicate<T> isNull() {
    return ObjectPredicate.IS_NULL.withNarrowedType();
  }
  
  @GwtCompatible(serializable = true)
  public static <T> Predicate<T> notNull() {
    return ObjectPredicate.NOT_NULL.withNarrowedType();
  }
  
  public static <T> Predicate<T> not(Predicate<T> predicate) {
    return (Predicate<T>)new NotPredicate(predicate);
  }
  
  public static <T> Predicate<T> and(Iterable<? extends Predicate<? super T>> components) {
    return new AndPredicate<T>(defensiveCopy(components));
  }
  
  public static <T> Predicate<T> and(Predicate<? super T>... components) {
    return new AndPredicate<T>(defensiveCopy(components));
  }
  
  public static <T> Predicate<T> and(Predicate<? super T> first, Predicate<? super T> second) {
    return new AndPredicate<T>(asList(Preconditions.<Predicate>checkNotNull(first), Preconditions.<Predicate>checkNotNull(second)));
  }
  
  public static <T> Predicate<T> or(Iterable<? extends Predicate<? super T>> components) {
    return (Predicate<T>)new OrPredicate(defensiveCopy(components), null);
  }
  
  public static <T> Predicate<T> or(Predicate<? super T>... components) {
    return (Predicate<T>)new OrPredicate(defensiveCopy(components), null);
  }
  
  public static <T> Predicate<T> or(Predicate<? super T> first, Predicate<? super T> second) {
    return (Predicate<T>)new OrPredicate(asList(Preconditions.<Predicate>checkNotNull(first), Preconditions.<Predicate>checkNotNull(second)), null);
  }
  
  public static <T> Predicate<T> equalTo(@Nullable T target) {
    return (target == null) ? isNull() : new IsEqualToPredicate<T>(target);
  }
  
  @GwtIncompatible("Class.isInstance")
  public static Predicate<Object> instanceOf(Class<?> clazz) {
    return new InstanceOfPredicate(clazz);
  }
  
  @GwtIncompatible("Class.isAssignableFrom")
  @Beta
  public static Predicate<Class<?>> assignableFrom(Class<?> clazz) {
    return (Predicate<Class<?>>)new AssignableFromPredicate(clazz, null);
  }
  
  public static <T> Predicate<T> in(Collection<? extends T> target) {
    return (Predicate<T>)new InPredicate(target, null);
  }
  
  public static <A, B> Predicate<A> compose(Predicate<B> predicate, Function<A, ? extends B> function) {
    return (Predicate<A>)new CompositionPredicate(predicate, function, null);
  }
  
  @GwtIncompatible("java.util.regex.Pattern")
  public static Predicate<CharSequence> containsPattern(String pattern) {
    return (Predicate<CharSequence>)new ContainsPatternFromStringPredicate(pattern);
  }
  
  @GwtIncompatible("java.util.regex.Pattern")
  public static Predicate<CharSequence> contains(Pattern pattern) {
    return (Predicate<CharSequence>)new ContainsPatternPredicate(pattern);
  }
  
  enum ObjectPredicate implements Predicate<Object> {
    ALWAYS_TRUE {
      public boolean apply(@Nullable Object o) {
        return true;
      }
      
      public String toString() {
        return "Predicates.alwaysTrue()";
      }
    },
    ALWAYS_FALSE {
      public boolean apply(@Nullable Object o) {
        return false;
      }
      
      public String toString() {
        return "Predicates.alwaysFalse()";
      }
    },
    IS_NULL {
      public boolean apply(@Nullable Object o) {
        return (o == null);
      }
      
      public String toString() {
        return "Predicates.isNull()";
      }
    },
    NOT_NULL {
      public boolean apply(@Nullable Object o) {
        return (o != null);
      }
      
      public String toString() {
        return "Predicates.notNull()";
      }
    };
    
    <T> Predicate<T> withNarrowedType() {
      return this;
    }
  }
  
  private static final Joiner COMMA_JOINER = Joiner.on(',');
  
  private static class Predicates {}
  
  private static class AndPredicate<T> implements Predicate<T>, Serializable {
    private final List<? extends Predicate<? super T>> components;
    
    private static final long serialVersionUID = 0L;
    
    private AndPredicate(List<? extends Predicate<? super T>> components) {
      this.components = components;
    }
    
    public boolean apply(@Nullable T t) {
      for (int i = 0; i < this.components.size(); i++) {
        if (!((Predicate<T>)this.components.get(i)).apply(t))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      return this.components.hashCode() + 306654252;
    }
    
    public boolean equals(@Nullable Object obj) {
      if (obj instanceof AndPredicate) {
        AndPredicate<?> that = (AndPredicate)obj;
        return this.components.equals(that.components);
      } 
      return false;
    }
    
    public String toString() {
      return "Predicates.and(" + Predicates.COMMA_JOINER.join(this.components) + ")";
    }
  }
  
  private static class Predicates {}
  
  private static class IsEqualToPredicate<T> implements Predicate<T>, Serializable {
    private final T target;
    
    private static final long serialVersionUID = 0L;
    
    private IsEqualToPredicate(T target) {
      this.target = target;
    }
    
    public boolean apply(T t) {
      return this.target.equals(t);
    }
    
    public int hashCode() {
      return this.target.hashCode();
    }
    
    public boolean equals(@Nullable Object obj) {
      if (obj instanceof IsEqualToPredicate) {
        IsEqualToPredicate<?> that = (IsEqualToPredicate)obj;
        return this.target.equals(that.target);
      } 
      return false;
    }
    
    public String toString() {
      return "Predicates.equalTo(" + this.target + ")";
    }
  }
  
  @GwtIncompatible("Class.isInstance")
  private static class InstanceOfPredicate implements Predicate<Object>, Serializable {
    private final Class<?> clazz;
    
    private static final long serialVersionUID = 0L;
    
    private InstanceOfPredicate(Class<?> clazz) {
      this.clazz = Preconditions.<Class<?>>checkNotNull(clazz);
    }
    
    public boolean apply(@Nullable Object o) {
      return this.clazz.isInstance(o);
    }
    
    public int hashCode() {
      return this.clazz.hashCode();
    }
    
    public boolean equals(@Nullable Object obj) {
      if (obj instanceof InstanceOfPredicate) {
        InstanceOfPredicate that = (InstanceOfPredicate)obj;
        return (this.clazz == that.clazz);
      } 
      return false;
    }
    
    public String toString() {
      return "Predicates.instanceOf(" + this.clazz.getName() + ")";
    }
  }
  
  private static <T> List<Predicate<? super T>> asList(Predicate<? super T> first, Predicate<? super T> second) {
    return Arrays.asList((Predicate<? super T>[])new Predicate[] { first, second });
  }
  
  private static <T> List<T> defensiveCopy(T... array) {
    return defensiveCopy(Arrays.asList(array));
  }
  
  static <T> List<T> defensiveCopy(Iterable<T> iterable) {
    ArrayList<T> list = new ArrayList<T>();
    for (T element : iterable)
      list.add(Preconditions.checkNotNull(element)); 
    return list;
  }
  
  private static class Predicates {}
  
  private static class Predicates {}
  
  private static class Predicates {}
  
  private static class Predicates {}
  
  private static class Predicates {}
}
