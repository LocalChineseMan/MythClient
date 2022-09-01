package sun.reflect;

import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import java.lang.reflect.InvocationTargetException;

public class GeneratedConstructorAccessor68 extends ConstructorAccessorImpl {
  public Object newInstance(Object[] paramArrayOfObject) throws InvocationTargetException {
    try {
      if (paramArrayOfObject != null && paramArrayOfObject.length != 0)
        throw new IllegalArgumentException(); 
      try {
        return new MinecraftTexturesPayload();
      } catch (Throwable throwable) {
        throw new InvocationTargetException(null);
      } 
    } catch (ClassCastException|NullPointerException classCastException) {
      throw new IllegalArgumentException(null.toString());
    } 
  }
}
