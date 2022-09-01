package sun.net;

import java.net.URL;

class DefaultProgressMeteringPolicy implements ProgressMeteringPolicy {
  public boolean shouldMeterInput(URL paramURL, String paramString) {
    return false;
  }
  
  public int getProgressUpdateThreshold() {
    return 8192;
  }
}
