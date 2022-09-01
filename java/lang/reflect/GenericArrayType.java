package java.lang.reflect;

import java.lang.reflect.Type;

public interface GenericArrayType extends Type {
  Type getGenericComponentType();
}
