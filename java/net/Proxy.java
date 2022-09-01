package java.net;

public class Proxy {
  private Type type;
  
  private SocketAddress sa;
  
  public enum Type {
    DIRECT, HTTP, SOCKS;
  }
  
  public static final Proxy NO_PROXY = new Proxy();
  
  private Proxy() {
    this.type = Type.DIRECT;
    this.sa = null;
  }
  
  public Proxy(Type paramType, SocketAddress paramSocketAddress) {
    if (paramType == Type.DIRECT || !(paramSocketAddress instanceof InetSocketAddress))
      throw new IllegalArgumentException("type " + paramType + " is not compatible with address " + paramSocketAddress); 
    this.type = paramType;
    this.sa = paramSocketAddress;
  }
  
  public Type type() {
    return this.type;
  }
  
  public SocketAddress address() {
    return this.sa;
  }
  
  public String toString() {
    if (type() == Type.DIRECT)
      return "DIRECT"; 
    return type() + " @ " + address();
  }
  
  public final boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof Proxy))
      return false; 
    Proxy proxy = (Proxy)paramObject;
    if (proxy.type() == type()) {
      if (address() == null)
        return (proxy.address() == null); 
      return address().equals(proxy.address());
    } 
    return false;
  }
  
  public final int hashCode() {
    if (address() == null)
      return type().hashCode(); 
    return type().hashCode() + address().hashCode();
  }
}
