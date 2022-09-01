package javax.imageio.spi;

import java.util.Iterator;
import java.util.NoSuchElementException;

class FilterIterator<T> implements Iterator<T> {
  private Iterator<T> iter;
  
  private ServiceRegistry.Filter filter;
  
  private T next = null;
  
  public FilterIterator(Iterator<T> paramIterator, ServiceRegistry.Filter paramFilter) {
    this.iter = paramIterator;
    this.filter = paramFilter;
    advance();
  }
  
  private void advance() {
    while (this.iter.hasNext()) {
      T t = this.iter.next();
      if (this.filter.filter(t)) {
        this.next = t;
        return;
      } 
    } 
    this.next = null;
  }
  
  public boolean hasNext() {
    return (this.next != null);
  }
  
  public T next() {
    if (this.next == null)
      throw new NoSuchElementException(); 
    T t = this.next;
    advance();
    return t;
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
