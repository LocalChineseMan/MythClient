package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import sun.security.provider.X509Factory;

public class GeneratedConstructorAccessor23 extends ConstructorAccessorImpl {
  public Object newInstance(Object[] paramArrayOfObject) throws InvocationTargetException {
    try {
      if (paramArrayOfObject != null && paramArrayOfObject.length != 0)
        throw new IllegalArgumentException(); 
      try {
        return new X509Factory();
      } catch (Throwable throwable) {
        throw new InvocationTargetException(null);
      } 
    } catch (ClassCastException|NullPointerException classCastException) {
      throw new IllegalArgumentException(null.toString());
    } 
  }
}
