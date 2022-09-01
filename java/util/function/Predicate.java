package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Predicate<T> {
  default Predicate<T> and(Predicate<? super T> paramPredicate) {
    Objects.requireNonNull(paramPredicate);
    return paramObject -> (test((T)paramObject) && paramPredicate.test(paramObject));
  }
  
  default Predicate<T> negate() {
    return paramObject -> !test((T)paramObject);
  }
  
  default Predicate<T> or(Predicate<? super T> paramPredicate) {
    Objects.requireNonNull(paramPredicate);
    return paramObject -> (test((T)paramObject) || paramPredicate.test(paramObject));
  }
  
  static <T> Predicate<T> isEqual(Object paramObject) {
    return (null == paramObject) ? Objects::isNull : (paramObject2 -> paramObject1.equals(paramObject2));
  }
  
  boolean test(T paramT);
}
