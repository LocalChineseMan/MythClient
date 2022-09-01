package java.net;

public class InterfaceAddress {
  private InetAddress address = null;
  
  private Inet4Address broadcast = null;
  
  private short maskLength = 0;
  
  public InetAddress getAddress() {
    return this.address;
  }
  
  public InetAddress getBroadcast() {
    return this.broadcast;
  }
  
  public short getNetworkPrefixLength() {
    return this.maskLength;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof InterfaceAddress))
      return false; 
    InterfaceAddress interfaceAddress = (InterfaceAddress)paramObject;
    if ((this.address == null) ? (interfaceAddress.address == null) : this.address.equals(interfaceAddress.address)) {
      if ((this.broadcast == null) ? (interfaceAddress.broadcast == null) : this.broadcast.equals(interfaceAddress.broadcast)) {
        if (this.maskLength != interfaceAddress.maskLength)
          return false; 
        return true;
      } 
      return false;
    } 
    return false;
  }
  
  public int hashCode() {
    return this.address.hashCode() + ((this.broadcast != null) ? this.broadcast.hashCode() : 0) + this.maskLength;
  }
  
  public String toString() {
    return this.address + "/" + this.maskLength + " [" + this.broadcast + "]";
  }
}
