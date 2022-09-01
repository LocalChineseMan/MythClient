package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import net.minecraft.network.play.server.S21PacketChunkData;

public class GeneratedConstructorAccessor56 extends ConstructorAccessorImpl {
  public Object newInstance(Object[] paramArrayOfObject) throws InvocationTargetException {
    try {
      if (paramArrayOfObject != null && paramArrayOfObject.length != 0)
        throw new IllegalArgumentException(); 
      try {
        return new S21PacketChunkData();
      } catch (Throwable throwable) {
        throw new InvocationTargetException(null);
      } 
    } catch (ClassCastException|NullPointerException classCastException) {
      throw new IllegalArgumentException(null.toString());
    } 
  }
}
