package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import net.minecraft.world.gen.structure.StructureMineshaftPieces;

public class GeneratedConstructorAccessor27 extends ConstructorAccessorImpl {
  public Object newInstance(Object[] paramArrayOfObject) throws InvocationTargetException {
    try {
      if (paramArrayOfObject != null && paramArrayOfObject.length != 0)
        throw new IllegalArgumentException(); 
      try {
        return new StructureMineshaftPieces.Cross();
      } catch (Throwable throwable) {
        throw new InvocationTargetException(null);
      } 
    } catch (ClassCastException|NullPointerException classCastException) {
      throw new IllegalArgumentException(null.toString());
    } 
  }
}
