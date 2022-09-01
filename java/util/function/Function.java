package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Function<T, R> {
  default <V> Function<V, R> compose(Function<? super V, ? extends T> paramFunction) {
    Objects.requireNonNull(paramFunction);
    return paramObject -> apply(paramFunction.apply(paramObject));
  }
  
  default <V> Function<T, V> andThen(Function<? super R, ? extends V> paramFunction) {
    Objects.requireNonNull(paramFunction);
    return paramObject -> paramFunction.apply(apply((T)paramObject));
  }
  
  static <T> Function<T, T> identity() {
    return paramObject -> paramObject;
  }
  
  R apply(T paramT);
}
