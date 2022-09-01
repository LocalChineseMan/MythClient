package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiConsumer<T, U> {
  void accept(T paramT, U paramU);
  
  default BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> paramBiConsumer) {
    Objects.requireNonNull(paramBiConsumer);
    return (paramObject1, paramObject2) -> {
        accept((T)paramObject1, (U)paramObject2);
        paramBiConsumer.accept(paramObject1, paramObject2);
      };
  }
}
