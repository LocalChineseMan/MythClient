package java.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class Optional<T> {
  private static final Optional<?> EMPTY = new Optional();
  
  private final T value;
  
  private Optional() {
    this.value = null;
  }
  
  public static <T> Optional<T> empty() {
    return (Optional)EMPTY;
  }
  
  private Optional(T paramT) {
    this.value = Objects.requireNonNull(paramT);
  }
  
  public static <T> Optional<T> of(T paramT) {
    return new Optional<>(paramT);
  }
  
  public static <T> Optional<T> ofNullable(T paramT) {
    return (paramT == null) ? empty() : of(paramT);
  }
  
  public T get() {
    if (this.value == null)
      throw new NoSuchElementException("No value present"); 
    return this.value;
  }
  
  public boolean isPresent() {
    return (this.value != null);
  }
  
  public void ifPresent(Consumer<? super T> paramConsumer) {
    if (this.value != null)
      paramConsumer.accept(this.value); 
  }
  
  public Optional<T> filter(Predicate<? super T> paramPredicate) {
    Objects.requireNonNull(paramPredicate);
    if (!isPresent())
      return this; 
    return paramPredicate.test(this.value) ? this : empty();
  }
  
  public <U> Optional<U> map(Function<? super T, ? extends U> paramFunction) {
    Objects.requireNonNull(paramFunction);
    if (!isPresent())
      return empty(); 
    return ofNullable(paramFunction.apply(this.value));
  }
  
  public <U> Optional<U> flatMap(Function<? super T, Optional<U>> paramFunction) {
    Objects.requireNonNull(paramFunction);
    if (!isPresent())
      return empty(); 
    return Objects.<Optional<U>>requireNonNull(paramFunction.apply(this.value));
  }
  
  public T orElse(T paramT) {
    return (this.value != null) ? this.value : paramT;
  }
  
  public T orElseGet(Supplier<? extends T> paramSupplier) {
    return (this.value != null) ? this.value : paramSupplier.get();
  }
  
  public <X extends Throwable> T orElseThrow(Supplier<? extends X> paramSupplier) throws X {
    if (this.value != null)
      return this.value; 
    throw (X)paramSupplier.get();
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (!(paramObject instanceof Optional))
      return false; 
    Optional optional = (Optional)paramObject;
    return Objects.equals(this.value, optional.value);
  }
  
  public int hashCode() {
    return Objects.hashCode(this.value);
  }
  
  public String toString() {
    return (this.value != null) ? 
      String.format("Optional[%s]", new Object[] { this.value }) : "Optional.empty";
  }
}
