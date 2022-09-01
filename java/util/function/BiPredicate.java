package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiPredicate<T, U> {
  default BiPredicate<T, U> and(BiPredicate<? super T, ? super U> paramBiPredicate) {
    Objects.requireNonNull(paramBiPredicate);
    return (paramObject1, paramObject2) -> (test((T)paramObject1, (U)paramObject2) && paramBiPredicate.test(paramObject1, paramObject2));
  }
  
  default BiPredicate<T, U> negate() {
    return (paramObject1, paramObject2) -> !test((T)paramObject1, (U)paramObject2);
  }
  
  default BiPredicate<T, U> or(BiPredicate<? super T, ? super U> paramBiPredicate) {
    Objects.requireNonNull(paramBiPredicate);
    return (paramObject1, paramObject2) -> (test((T)paramObject1, (U)paramObject2) || paramBiPredicate.test(paramObject1, paramObject2));
  }
  
  boolean test(T paramT, U paramU);
}
