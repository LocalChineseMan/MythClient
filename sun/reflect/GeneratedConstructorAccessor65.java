package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.world.World;

public class GeneratedConstructorAccessor65 extends ConstructorAccessorImpl {
  public Object newInstance(Object[] paramArrayOfObject) throws InvocationTargetException {
    try {
      if (paramArrayOfObject.length != 1)
        throw new IllegalArgumentException(); 
      try {
        return new EntityArmorStand((World)paramArrayOfObject[0]);
      } catch (Throwable throwable) {
        throw new InvocationTargetException(null);
      } 
    } catch (ClassCastException|NullPointerException classCastException) {
      throw new IllegalArgumentException(null.toString());
    } 
  }
}
