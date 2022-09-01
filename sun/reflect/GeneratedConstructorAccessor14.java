package sun.reflect;

import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.RecipeData;
import java.lang.reflect.InvocationTargetException;

public class GeneratedConstructorAccessor14 extends ConstructorAccessorImpl {
  public Object newInstance(Object[] paramArrayOfObject) throws InvocationTargetException {
    try {
      if (paramArrayOfObject != null && paramArrayOfObject.length != 0)
        throw new IllegalArgumentException(); 
      try {
        return new RecipeData.Recipe();
      } catch (Throwable throwable) {
        throw new InvocationTargetException(null);
      } 
    } catch (ClassCastException|NullPointerException classCastException) {
      throw new IllegalArgumentException(null.toString());
    } 
  }
}
