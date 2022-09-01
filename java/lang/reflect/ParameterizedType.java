package java.lang.reflect;

import java.lang.reflect.Type;

public interface ParameterizedType extends Type {
  Type[] getActualTypeArguments();
  
  Type getRawType();
  
  Type getOwnerType();
}
