package sun.reflect;

import java.lang.reflect.InvocationTargetException;

public class GeneratedMethodAccessor1 extends MethodAccessorImpl {
  public Object invoke(Object paramObject, Object[] paramArrayOfObject) throws InvocationTargetException {
    try {
      if (paramArrayOfObject.length != 1)
        throw new IllegalArgumentException(); 
      Object object = paramArrayOfObject[0];
      if (object instanceof Integer) {
      
      } else {
        throw new IllegalArgumentException();
      } 
      try {
        return Reflection.getCallerClass((object instanceof Byte) ? ((Byte)object).byteValue() : ((object instanceof Character) ? ((Character)object).charValue() : ((object instanceof Short) ? ((Short)object).shortValue() : "JD-Core does not support Kotlin")));
      } catch (Throwable throwable) {
        throw new InvocationTargetException(null);
      } 
    } catch (ClassCastException|NullPointerException classCastException) {
      throw new IllegalArgumentException(null.toString());
    } 
  }
}
