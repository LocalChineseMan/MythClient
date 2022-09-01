package sun.reflect.annotation;

import java.io.Serializable;

public abstract class ExceptionProxy implements Serializable {
  protected abstract RuntimeException generateException();
}
