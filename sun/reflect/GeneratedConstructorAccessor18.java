package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import sun.security.x509.KeyUsageExtension;

public class GeneratedConstructorAccessor18 extends ConstructorAccessorImpl {
  public Object newInstance(Object[] paramArrayOfObject) throws InvocationTargetException {
    try {
      if (paramArrayOfObject.length != 2)
        throw new IllegalArgumentException(); 
      try {
        return new KeyUsageExtension((Boolean)paramArrayOfObject[0], paramArrayOfObject[1]);
      } catch (Throwable throwable) {
        throw new InvocationTargetException(null);
      } 
    } catch (ClassCastException|NullPointerException classCastException) {
      throw new IllegalArgumentException(null.toString());
    } 
  }
}
