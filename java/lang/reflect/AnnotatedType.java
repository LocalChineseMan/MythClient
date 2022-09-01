package java.lang.reflect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

public interface AnnotatedType extends AnnotatedElement {
  Type getType();
}
