package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiFunction<T, U, R> {
  R apply(T paramT, U paramU);
  
  default <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> paramFunction) {
    Objects.requireNonNull(paramFunction);
    return (paramObject1, paramObject2) -> paramFunction.apply(apply((T)paramObject1, (U)paramObject2));
  }
}
