package sun.java2d.pipe.hw;

import java.awt.image.VolatileImage;

public interface AccelGraphicsConfig extends BufferedContextProvider {
  VolatileImage createCompatibleVolatileImage(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  ContextCapabilities getContextCapabilities();
  
  void addDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener);
  
  void removeDeviceEventListener(AccelDeviceEventListener paramAccelDeviceEventListener);
}
