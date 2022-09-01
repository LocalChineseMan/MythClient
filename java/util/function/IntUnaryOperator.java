package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntUnaryOperator {
  default IntUnaryOperator compose(IntUnaryOperator paramIntUnaryOperator) {
    Objects.requireNonNull(paramIntUnaryOperator);
    return paramInt -> applyAsInt(paramIntUnaryOperator.applyAsInt(paramInt));
  }
  
  default IntUnaryOperator andThen(IntUnaryOperator paramIntUnaryOperator) {
    Objects.requireNonNull(paramIntUnaryOperator);
    return paramInt -> paramIntUnaryOperator.applyAsInt(applyAsInt(paramInt));
  }
  
  static IntUnaryOperator identity() {
    return paramInt -> paramInt;
  }
  
  int applyAsInt(int paramInt);
}
