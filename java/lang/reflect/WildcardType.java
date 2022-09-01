package java.lang.reflect;

import java.lang.reflect.Type;

public interface WildcardType extends Type {
  Type[] getUpperBounds();
  
  Type[] getLowerBounds();
}
