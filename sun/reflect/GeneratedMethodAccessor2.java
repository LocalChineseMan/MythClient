package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import javax.imageio.spi.ImageReaderWriterSpi;

public class GeneratedMethodAccessor2 extends MethodAccessorImpl {
  public Object invoke(Object paramObject, Object[] paramArrayOfObject) throws InvocationTargetException {
    if (paramObject == null)
      throw new NullPointerException(); 
    try {
      if (paramArrayOfObject != null && paramArrayOfObject.length != 0)
        throw new IllegalArgumentException(); 
      try {
        return ((ImageReaderWriterSpi)paramObject).getFileSuffixes();
      } catch (Throwable throwable) {
        throw new InvocationTargetException(null);
      } 
    } catch (ClassCastException|NullPointerException classCastException) {
      throw new IllegalArgumentException(null.toString());
    } 
  }
}
