package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Consumer<T> {
  void accept(T paramT);
  
  default Consumer<T> andThen(Consumer<? super T> paramConsumer) {
    Objects.requireNonNull(paramConsumer);
    return paramObject -> {
        accept((T)paramObject);
        paramConsumer.accept(paramObject);
      };
  }
}
