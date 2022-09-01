package sun.net;

import java.net.Proxy;

public final class ApplicationProxy extends Proxy {
  private ApplicationProxy(Proxy paramProxy) {
    super(paramProxy.type(), paramProxy.address());
  }
  
  public static ApplicationProxy create(Proxy paramProxy) {
    return new ApplicationProxy(paramProxy);
  }
}
