package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import javax.security.auth.x500.X500Principal;
import sun.security.x509.X500Name;

public class GeneratedConstructorAccessor22 extends ConstructorAccessorImpl {
  public Object newInstance(Object[] paramArrayOfObject) throws InvocationTargetException {
    try {
      if (paramArrayOfObject.length != 1)
        throw new IllegalArgumentException(); 
      try {
        return new X500Principal((X500Name)paramArrayOfObject[0]);
      } catch (Throwable throwable) {
        throw new InvocationTargetException(null);
      } 
    } catch (ClassCastException|NullPointerException classCastException) {
      throw new IllegalArgumentException(null.toString());
    } 
  }
}
