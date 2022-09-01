package java.lang.reflect;

import java.lang.reflect.Method;

public interface InvocationHandler {
  Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject) throws Throwable;
}
