package sun.net;

import java.net.URL;

public interface ProgressMeteringPolicy {
  boolean shouldMeterInput(URL paramURL, String paramString);
  
  int getProgressUpdateThreshold();
}
