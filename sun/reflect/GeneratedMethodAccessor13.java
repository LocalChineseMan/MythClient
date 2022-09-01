package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import notthatuwu.xyz.mythrecode.events.EventUpdate;
import notthatuwu.xyz.mythrecode.modules.player.InvManager;

public class GeneratedMethodAccessor13 extends MethodAccessorImpl {
  public Object invoke(Object paramObject, Object[] paramArrayOfObject) throws InvocationTargetException {
    if (paramObject == null)
      throw new NullPointerException(); 
    try {
      if (paramArrayOfObject.length != 1)
        throw new IllegalArgumentException(); 
      try {
        ((InvManager)paramObject).onUpdate((EventUpdate)paramArrayOfObject[0]);
        return null;
      } catch (Throwable throwable) {
        throw new InvocationTargetException(null);
      } 
    } catch (ClassCastException|NullPointerException classCastException) {
      throw new IllegalArgumentException(null.toString());
    } 
  }
}