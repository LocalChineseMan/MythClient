package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntPredicate {
  default IntPredicate and(IntPredicate paramIntPredicate) {
    Objects.requireNonNull(paramIntPredicate);
    return paramInt -> (test(paramInt) && paramIntPredicate.test(paramInt));
  }
  
  default IntPredicate negate() {
    return paramInt -> !test(paramInt);
  }
  
  default IntPredicate or(IntPredicate paramIntPredicate) {
    Objects.requireNonNull(paramIntPredicate);
    return paramInt -> (test(paramInt) || paramIntPredicate.test(paramInt));
  }
  
  boolean test(int paramInt);
}
