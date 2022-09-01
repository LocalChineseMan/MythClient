package notthatuwu.xyz.mythrecode.api.module.file;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Container<T> {
  private final List<T> items = new CopyOnWriteArrayList<>();
  
  public void add(T item) {
    this.items.add(item);
  }
  
  @SafeVarargs
  public final void add(T... items) {
    Arrays.<T>stream(items).forEach(this::add);
  }
  
  public void remove(T item) {
    this.items.remove(item);
  }
  
  public T get(int index) {
    try {
      return this.items.get(index);
    } catch (Exception e) {
      return this.items.get(0);
    } 
  }
  
  public int indexOf(T item) {
    return this.items.indexOf(item);
  }
  
  public boolean isEmpty() {
    return this.items.isEmpty();
  }
  
  public void clear() {
    this.items.clear();
  }
  
  public boolean contains(T item) {
    return this.items.contains(item);
  }
  
  public void forEach(Consumer<? super T> action) {
    this.items.forEach(action);
  }
  
  public Stream<T> reverseStream() {
    List<T> items = new CopyOnWriteArrayList<>(this.items);
    Collections.reverse(items);
    return items.stream();
  }
  
  public Stream<T> stream() {
    return this.items.stream();
  }
  
  public Stream<T> filter(Predicate<? super T> predicate) {
    return stream().filter(predicate);
  }
  
  public T find(Predicate<? super T> predicate) {
    return filter(predicate).findFirst().orElse(null);
  }
  
  public <T> T findByClass(Class<? extends T> aClass) {
    return stream().filter(item -> item.getClass().equals(aClass)).findFirst().orElse(null);
  }
  
  public int size() {
    return this.items.size();
  }
}
