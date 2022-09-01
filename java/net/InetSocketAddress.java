package java.net;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.ObjectStreamField;
import sun.misc.Unsafe;

public class InetSocketAddress extends SocketAddress {
  private final transient InetSocketAddressHolder holder;
  
  private static final long serialVersionUID = 5076001401234631237L;
  
  private static class InetSocketAddressHolder {
    private String hostname;
    
    private InetAddress addr;
    
    private int port;
    
    private InetSocketAddressHolder(String param1String, InetAddress param1InetAddress, int param1Int) {
      this.hostname = param1String;
      this.addr = param1InetAddress;
      this.port = param1Int;
    }
    
    private int getPort() {
      return this.port;
    }
    
    private InetAddress getAddress() {
      return this.addr;
    }
    
    private String getHostName() {
      if (this.hostname != null)
        return this.hostname; 
      if (this.addr != null)
        return this.addr.getHostName(); 
      return null;
    }
    
    private String getHostString() {
      if (this.hostname != null)
        return this.hostname; 
      if (this.addr != null) {
        if (this.addr.holder().getHostName() != null)
          return this.addr.holder().getHostName(); 
        return this.addr.getHostAddress();
      } 
      return null;
    }
    
    private boolean isUnresolved() {
      return (this.addr == null);
    }
    
    public String toString() {
      if (isUnresolved())
        return this.hostname + ":" + this.port; 
      return this.addr.toString() + ":" + this.port;
    }
    
    public final boolean equals(Object param1Object) {
      boolean bool;
      if (param1Object == null || !(param1Object instanceof InetSocketAddressHolder))
        return false; 
      InetSocketAddressHolder inetSocketAddressHolder = (InetSocketAddressHolder)param1Object;
      if (this.addr != null) {
        bool = this.addr.equals(inetSocketAddressHolder.addr);
      } else if (this.hostname != null) {
        bool = (inetSocketAddressHolder.addr == null && this.hostname.equalsIgnoreCase(inetSocketAddressHolder.hostname)) ? true : false;
      } else {
        bool = (inetSocketAddressHolder.addr == null && inetSocketAddressHolder.hostname == null) ? true : false;
      } 
      return (bool && this.port == inetSocketAddressHolder.port);
    }
    
    public final int hashCode() {
      if (this.addr != null)
        return this.addr.hashCode() + this.port; 
      if (this.hostname != null)
        return this.hostname.toLowerCase().hashCode() + this.port; 
      return this.port;
    }
  }
  
  private static int checkPort(int paramInt) {
    if (paramInt < 0 || paramInt > 65535)
      throw new IllegalArgumentException("port out of range:" + paramInt); 
    return paramInt;
  }
  
  private static String checkHost(String paramString) {
    if (paramString == null)
      throw new IllegalArgumentException("hostname can't be null"); 
    return paramString;
  }
  
  public InetSocketAddress(int paramInt) {
    this(InetAddress.anyLocalAddress(), paramInt);
  }
  
  public InetSocketAddress(InetAddress paramInetAddress, int paramInt) {
    this
      
      .holder = new InetSocketAddressHolder(null, (paramInetAddress == null) ? InetAddress.anyLocalAddress() : paramInetAddress, checkPort(paramInt));
  }
  
  public InetSocketAddress(String paramString, int paramInt) {
    checkHost(paramString);
    InetAddress inetAddress = null;
    String str = null;
    try {
      inetAddress = InetAddress.getByName(paramString);
    } catch (UnknownHostException unknownHostException) {
      str = paramString;
    } 
    this.holder = new InetSocketAddressHolder(str, inetAddress, checkPort(paramInt));
  }
  
  private InetSocketAddress(int paramInt, String paramString) {
    this.holder = new InetSocketAddressHolder(paramString, null, paramInt);
  }
  
  public static InetSocketAddress createUnresolved(String paramString, int paramInt) {
    return new InetSocketAddress(checkPort(paramInt), checkHost(paramString));
  }
  
  private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("hostname", String.class), new ObjectStreamField("addr", InetAddress.class), new ObjectStreamField("port", int.class) };
  
  private static final long FIELDS_OFFSET;
  
  private static final Unsafe UNSAFE;
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("hostname", this.holder.hostname);
    putField.put("addr", this.holder.addr);
    putField.put("port", this.holder.port);
    paramObjectOutputStream.writeFields();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    String str = (String)getField.get("hostname", (Object)null);
    InetAddress inetAddress = (InetAddress)getField.get("addr", (Object)null);
    int i = getField.get("port", -1);
    checkPort(i);
    if (str == null && inetAddress == null)
      throw new InvalidObjectException("hostname and addr can't both be null"); 
    InetSocketAddressHolder inetSocketAddressHolder = new InetSocketAddressHolder(str, inetAddress, i);
    UNSAFE.putObject(this, FIELDS_OFFSET, inetSocketAddressHolder);
  }
  
  private void readObjectNoData() throws ObjectStreamException {
    throw new InvalidObjectException("Stream data required");
  }
  
  static {
    try {
      Unsafe unsafe = Unsafe.getUnsafe();
      FIELDS_OFFSET = unsafe.objectFieldOffset(InetSocketAddress.class
          .getDeclaredField("holder"));
      UNSAFE = unsafe;
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw new Error(reflectiveOperationException);
    } 
  }
  
  public final int getPort() {
    return this.holder.getPort();
  }
  
  public final InetAddress getAddress() {
    return this.holder.getAddress();
  }
  
  public final String getHostName() {
    return this.holder.getHostName();
  }
  
  public final String getHostString() {
    return this.holder.getHostString();
  }
  
  public final boolean isUnresolved() {
    return this.holder.isUnresolved();
  }
  
  public String toString() {
    return this.holder.toString();
  }
  
  public final boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof InetSocketAddress))
      return false; 
    return this.holder.equals(((InetSocketAddress)paramObject).holder);
  }
  
  public final int hashCode() {
    return this.holder.hashCode();
  }
}
