package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import org.apache.logging.log4j.core.config.plugins.visitors.PluginElementVisitor;

public class GeneratedConstructorAccessor6 extends ConstructorAccessorImpl {
  public Object newInstance(Object[] paramArrayOfObject) throws InvocationTargetException {
    try {
      if (paramArrayOfObject != null && paramArrayOfObject.length != 0)
        throw new IllegalArgumentException(); 
      try {
        return new PluginElementVisitor();
      } catch (Throwable throwable) {
        throw new InvocationTargetException(null);
      } 
    } catch (ClassCastException|NullPointerException classCastException) {
      throw new IllegalArgumentException(null.toString());
    } 
  }
}
