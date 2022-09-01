package sun.net;

import java.net.SocketException;
import java.security.AccessController;
import java.util.concurrent.atomic.AtomicInteger;
import sun.security.action.GetPropertyAction;

public class ResourceManager {
  private static final int DEFAULT_MAX_SOCKETS = 25;
  
  private static final int maxSockets;
  
  static {
    String str = AccessController.<String>doPrivileged(new GetPropertyAction("sun.net.maxDatagramSockets"));
    int i = 25;
    try {
      if (str != null)
        i = Integer.parseInt(str); 
    } catch (NumberFormatException numberFormatException) {}
    maxSockets = i;
  }
  
  private static final AtomicInteger numSockets = new AtomicInteger(0);
  
  public static void beforeUdpCreate() throws SocketException {
    if (System.getSecurityManager() != null && 
      numSockets.incrementAndGet() > maxSockets) {
      numSockets.decrementAndGet();
      throw new SocketException("maximum number of DatagramSockets reached");
    } 
  }
  
  public static void afterUdpClose() {
    if (System.getSecurityManager() != null)
      numSockets.decrementAndGet(); 
  }
}
