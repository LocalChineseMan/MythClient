package java.lang.reflect;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;

public interface TypeVariable<D extends GenericDeclaration> extends Type, AnnotatedElement {
  Type[] getBounds();
  
  D getGenericDeclaration();
  
  String getName();
  
  AnnotatedType[] getAnnotatedBounds();
}
