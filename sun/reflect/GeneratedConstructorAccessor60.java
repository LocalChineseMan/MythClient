package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;

public class GeneratedConstructorAccessor60 extends ConstructorAccessorImpl {
  public Object newInstance(Object[] paramArrayOfObject) throws InvocationTargetException {
    try {
      if (paramArrayOfObject != null && paramArrayOfObject.length != 0)
        throw new IllegalArgumentException(); 
      try {
        return new S26PacketMapChunkBulk();
      } catch (Throwable throwable) {
        throw new InvocationTargetException(null);
      } 
    } catch (ClassCastException|NullPointerException classCastException) {
      throw new IllegalArgumentException(null.toString());
    } 
  }
}
