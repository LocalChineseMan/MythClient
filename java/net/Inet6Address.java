package java.net;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.util.Arrays;
import java.util.Enumeration;
import sun.misc.Unsafe;

public final class Inet6Address extends InetAddress {
  static final int INADDRSZ = 16;
  
  private transient int cached_scope_id;
  
  private final transient Inet6AddressHolder holder6;
  
  private static final long serialVersionUID = 6880410070516793377L;
  
  private class Inet6AddressHolder {
    byte[] ipaddress;
    
    int scope_id;
    
    boolean scope_id_set;
    
    NetworkInterface scope_ifname;
    
    boolean scope_ifname_set;
    
    private Inet6AddressHolder() {
      this.ipaddress = new byte[16];
    }
    
    private Inet6AddressHolder(byte[] param1ArrayOfbyte, int param1Int, boolean param1Boolean1, NetworkInterface param1NetworkInterface, boolean param1Boolean2) {
      this.ipaddress = param1ArrayOfbyte;
      this.scope_id = param1Int;
      this.scope_id_set = param1Boolean1;
      this.scope_ifname_set = param1Boolean2;
      this.scope_ifname = param1NetworkInterface;
    }
    
    void setAddr(byte[] param1ArrayOfbyte) {
      if (param1ArrayOfbyte.length == 16)
        System.arraycopy(param1ArrayOfbyte, 0, this.ipaddress, 0, 16); 
    }
    
    void init(byte[] param1ArrayOfbyte, int param1Int) {
      setAddr(param1ArrayOfbyte);
      if (param1Int >= 0) {
        this.scope_id = param1Int;
        this.scope_id_set = true;
      } 
    }
    
    void init(byte[] param1ArrayOfbyte, NetworkInterface param1NetworkInterface) throws UnknownHostException {
      setAddr(param1ArrayOfbyte);
      if (param1NetworkInterface != null) {
        this.scope_id = Inet6Address.deriveNumericScope(this.ipaddress, param1NetworkInterface);
        this.scope_id_set = true;
        this.scope_ifname = param1NetworkInterface;
        this.scope_ifname_set = true;
      } 
    }
    
    String getHostAddress() {
      String str = Inet6Address.numericToTextFormat(this.ipaddress);
      if (this.scope_ifname != null) {
        str = str + "%" + this.scope_ifname.getName();
      } else if (this.scope_id_set) {
        str = str + "%" + this.scope_id;
      } 
      return str;
    }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof Inet6AddressHolder))
        return false; 
      Inet6AddressHolder inet6AddressHolder = (Inet6AddressHolder)param1Object;
      return Arrays.equals(this.ipaddress, inet6AddressHolder.ipaddress);
    }
    
    public int hashCode() {
      if (this.ipaddress != null) {
        int i = 0;
        byte b = 0;
        while (b < 16) {
          byte b1 = 0;
          int j = 0;
          while (b1 < 4 && b < 16) {
            j = (j << 8) + this.ipaddress[b];
            b1++;
            b++;
          } 
          i += j;
        } 
        return i;
      } 
      return 0;
    }
    
    boolean isIPv4CompatibleAddress() {
      if (this.ipaddress[0] == 0 && this.ipaddress[1] == 0 && this.ipaddress[2] == 0 && this.ipaddress[3] == 0 && this.ipaddress[4] == 0 && this.ipaddress[5] == 0 && this.ipaddress[6] == 0 && this.ipaddress[7] == 0 && this.ipaddress[8] == 0 && this.ipaddress[9] == 0 && this.ipaddress[10] == 0 && this.ipaddress[11] == 0)
        return true; 
      return false;
    }
    
    boolean isMulticastAddress() {
      return ((this.ipaddress[0] & 0xFF) == 255);
    }
    
    boolean isAnyLocalAddress() {
      byte b = 0;
      for (byte b1 = 0; b1 < 16; b1++)
        b = (byte)(b | this.ipaddress[b1]); 
      return (b == 0);
    }
    
    boolean isLoopbackAddress() {
      byte b = 0;
      for (byte b1 = 0; b1 < 15; b1++)
        b = (byte)(b | this.ipaddress[b1]); 
      return (b == 0 && this.ipaddress[15] == 1);
    }
    
    boolean isLinkLocalAddress() {
      return ((this.ipaddress[0] & 0xFF) == 254 && (this.ipaddress[1] & 0xC0) == 128);
    }
    
    boolean isSiteLocalAddress() {
      return ((this.ipaddress[0] & 0xFF) == 254 && (this.ipaddress[1] & 0xC0) == 192);
    }
    
    boolean isMCGlobal() {
      return ((this.ipaddress[0] & 0xFF) == 255 && (this.ipaddress[1] & 0xF) == 14);
    }
    
    boolean isMCNodeLocal() {
      return ((this.ipaddress[0] & 0xFF) == 255 && (this.ipaddress[1] & 0xF) == 1);
    }
    
    boolean isMCLinkLocal() {
      return ((this.ipaddress[0] & 0xFF) == 255 && (this.ipaddress[1] & 0xF) == 2);
    }
    
    boolean isMCSiteLocal() {
      return ((this.ipaddress[0] & 0xFF) == 255 && (this.ipaddress[1] & 0xF) == 5);
    }
    
    boolean isMCOrgLocal() {
      return ((this.ipaddress[0] & 0xFF) == 255 && (this.ipaddress[1] & 0xF) == 8);
    }
  }
  
  static {
    init();
  }
  
  Inet6Address() {
    this.holder.init(null, 2);
    this.holder6 = new Inet6AddressHolder();
  }
  
  Inet6Address(String paramString, byte[] paramArrayOfbyte, int paramInt) {
    this.holder.init(paramString, 2);
    this.holder6 = new Inet6AddressHolder();
    this.holder6.init(paramArrayOfbyte, paramInt);
  }
  
  Inet6Address(String paramString, byte[] paramArrayOfbyte) {
    this.holder6 = new Inet6AddressHolder();
    try {
      initif(paramString, paramArrayOfbyte, null);
    } catch (UnknownHostException unknownHostException) {}
  }
  
  Inet6Address(String paramString, byte[] paramArrayOfbyte, NetworkInterface paramNetworkInterface) throws UnknownHostException {
    this.holder6 = new Inet6AddressHolder();
    initif(paramString, paramArrayOfbyte, paramNetworkInterface);
  }
  
  Inet6Address(String paramString1, byte[] paramArrayOfbyte, String paramString2) throws UnknownHostException {
    this.holder6 = new Inet6AddressHolder();
    initstr(paramString1, paramArrayOfbyte, paramString2);
  }
  
  public static Inet6Address getByAddress(String paramString, byte[] paramArrayOfbyte, NetworkInterface paramNetworkInterface) throws UnknownHostException {
    if (paramString != null && paramString.length() > 0 && paramString.charAt(0) == '[' && 
      paramString.charAt(paramString.length() - 1) == ']')
      paramString = paramString.substring(1, paramString.length() - 1); 
    if (paramArrayOfbyte != null && 
      paramArrayOfbyte.length == 16)
      return new Inet6Address(paramString, paramArrayOfbyte, paramNetworkInterface); 
    throw new UnknownHostException("addr is of illegal length");
  }
  
  public static Inet6Address getByAddress(String paramString, byte[] paramArrayOfbyte, int paramInt) throws UnknownHostException {
    if (paramString != null && paramString.length() > 0 && paramString.charAt(0) == '[' && 
      paramString.charAt(paramString.length() - 1) == ']')
      paramString = paramString.substring(1, paramString.length() - 1); 
    if (paramArrayOfbyte != null && 
      paramArrayOfbyte.length == 16)
      return new Inet6Address(paramString, paramArrayOfbyte, paramInt); 
    throw new UnknownHostException("addr is of illegal length");
  }
  
  private void initstr(String paramString1, byte[] paramArrayOfbyte, String paramString2) throws UnknownHostException {
    try {
      NetworkInterface networkInterface = NetworkInterface.getByName(paramString2);
      if (networkInterface == null)
        throw new UnknownHostException("no such interface " + paramString2); 
      initif(paramString1, paramArrayOfbyte, networkInterface);
    } catch (SocketException socketException) {
      throw new UnknownHostException("SocketException thrown" + paramString2);
    } 
  }
  
  private void initif(String paramString, byte[] paramArrayOfbyte, NetworkInterface paramNetworkInterface) throws UnknownHostException {
    byte b = -1;
    this.holder6.init(paramArrayOfbyte, paramNetworkInterface);
    if (paramArrayOfbyte.length == 16)
      b = 2; 
    this.holder.init(paramString, b);
  }
  
  private static boolean isDifferentLocalAddressType(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) {
    if (isLinkLocalAddress(paramArrayOfbyte1) && 
      !isLinkLocalAddress(paramArrayOfbyte2))
      return false; 
    if (isSiteLocalAddress(paramArrayOfbyte1) && 
      !isSiteLocalAddress(paramArrayOfbyte2))
      return false; 
    return true;
  }
  
  private static int deriveNumericScope(byte[] paramArrayOfbyte, NetworkInterface paramNetworkInterface) throws UnknownHostException {
    Enumeration<InetAddress> enumeration = paramNetworkInterface.getInetAddresses();
    while (enumeration.hasMoreElements()) {
      InetAddress inetAddress = enumeration.nextElement();
      if (!(inetAddress instanceof Inet6Address))
        continue; 
      Inet6Address inet6Address = (Inet6Address)inetAddress;
      if (!isDifferentLocalAddressType(paramArrayOfbyte, inet6Address.getAddress()))
        continue; 
      return inet6Address.getScopeId();
    } 
    throw new UnknownHostException("no scope_id found");
  }
  
  private int deriveNumericScope(String paramString) throws UnknownHostException {
    Enumeration<NetworkInterface> enumeration;
    try {
      enumeration = NetworkInterface.getNetworkInterfaces();
    } catch (SocketException socketException) {
      throw new UnknownHostException("could not enumerate local network interfaces");
    } 
    while (enumeration.hasMoreElements()) {
      NetworkInterface networkInterface = enumeration.nextElement();
      if (networkInterface.getName().equals(paramString))
        return deriveNumericScope(this.holder6.ipaddress, networkInterface); 
    } 
    throw new UnknownHostException("No matching address found for interface : " + paramString);
  }
  
  private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("ipaddress", byte[].class), new ObjectStreamField("scope_id", int.class), new ObjectStreamField("scope_id_set", boolean.class), new ObjectStreamField("scope_ifname_set", boolean.class), new ObjectStreamField("ifname", String.class) };
  
  private static final long FIELDS_OFFSET;
  
  private static final Unsafe UNSAFE;
  
  private static final int INT16SZ = 2;
  
  static {
    try {
      Unsafe unsafe = Unsafe.getUnsafe();
      FIELDS_OFFSET = unsafe.objectFieldOffset(Inet6Address.class
          .getDeclaredField("holder6"));
      UNSAFE = unsafe;
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw new Error(reflectiveOperationException);
    } 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    NetworkInterface networkInterface = null;
    if (getClass().getClassLoader() != null)
      throw new SecurityException("invalid address type"); 
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    byte[] arrayOfByte = (byte[])getField.get("ipaddress", (Object)null);
    int i = getField.get("scope_id", -1);
    boolean bool1 = getField.get("scope_id_set", false);
    boolean bool2 = getField.get("scope_ifname_set", false);
    String str = (String)getField.get("ifname", (Object)null);
    if (str != null && !"".equals(str))
      try {
        networkInterface = NetworkInterface.getByName(str);
        if (networkInterface == null) {
          bool1 = false;
          bool2 = false;
          i = 0;
        } else {
          bool2 = true;
          try {
            i = deriveNumericScope(arrayOfByte, networkInterface);
          } catch (UnknownHostException unknownHostException) {}
        } 
      } catch (SocketException socketException) {} 
    arrayOfByte = (byte[])arrayOfByte.clone();
    if (arrayOfByte.length != 16)
      throw new InvalidObjectException("invalid address length: " + arrayOfByte.length); 
    if (this.holder.getFamily() != 2)
      throw new InvalidObjectException("invalid address family type"); 
    Inet6AddressHolder inet6AddressHolder = new Inet6AddressHolder(arrayOfByte, i, bool1, networkInterface, bool2);
    UNSAFE.putObject(this, FIELDS_OFFSET, inet6AddressHolder);
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    String str = null;
    if (this.holder6.scope_ifname != null) {
      str = this.holder6.scope_ifname.getName();
      this.holder6.scope_ifname_set = true;
    } 
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("ipaddress", this.holder6.ipaddress);
    putField.put("scope_id", this.holder6.scope_id);
    putField.put("scope_id_set", this.holder6.scope_id_set);
    putField.put("scope_ifname_set", this.holder6.scope_ifname_set);
    putField.put("ifname", str);
    paramObjectOutputStream.writeFields();
  }
  
  public boolean isMulticastAddress() {
    return this.holder6.isMulticastAddress();
  }
  
  public boolean isAnyLocalAddress() {
    return this.holder6.isAnyLocalAddress();
  }
  
  public boolean isLoopbackAddress() {
    return this.holder6.isLoopbackAddress();
  }
  
  public boolean isLinkLocalAddress() {
    return this.holder6.isLinkLocalAddress();
  }
  
  static boolean isLinkLocalAddress(byte[] paramArrayOfbyte) {
    return ((paramArrayOfbyte[0] & 0xFF) == 254 && (paramArrayOfbyte[1] & 0xC0) == 128);
  }
  
  public boolean isSiteLocalAddress() {
    return this.holder6.isSiteLocalAddress();
  }
  
  static boolean isSiteLocalAddress(byte[] paramArrayOfbyte) {
    return ((paramArrayOfbyte[0] & 0xFF) == 254 && (paramArrayOfbyte[1] & 0xC0) == 192);
  }
  
  public boolean isMCGlobal() {
    return this.holder6.isMCGlobal();
  }
  
  public boolean isMCNodeLocal() {
    return this.holder6.isMCNodeLocal();
  }
  
  public boolean isMCLinkLocal() {
    return this.holder6.isMCLinkLocal();
  }
  
  public boolean isMCSiteLocal() {
    return this.holder6.isMCSiteLocal();
  }
  
  public boolean isMCOrgLocal() {
    return this.holder6.isMCOrgLocal();
  }
  
  public byte[] getAddress() {
    return (byte[])this.holder6.ipaddress.clone();
  }
  
  public int getScopeId() {
    return this.holder6.scope_id;
  }
  
  public NetworkInterface getScopedInterface() {
    return this.holder6.scope_ifname;
  }
  
  public String getHostAddress() {
    return this.holder6.getHostAddress();
  }
  
  public int hashCode() {
    return this.holder6.hashCode();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null || !(paramObject instanceof Inet6Address))
      return false; 
    Inet6Address inet6Address = (Inet6Address)paramObject;
    return this.holder6.equals(inet6Address.holder6);
  }
  
  public boolean isIPv4CompatibleAddress() {
    return this.holder6.isIPv4CompatibleAddress();
  }
  
  static String numericToTextFormat(byte[] paramArrayOfbyte) {
    StringBuilder stringBuilder = new StringBuilder(39);
    for (byte b = 0; b < 8; b++) {
      stringBuilder.append(Integer.toHexString(paramArrayOfbyte[b << 1] << 8 & 0xFF00 | paramArrayOfbyte[(b << 1) + 1] & 0xFF));
      if (b < 7)
        stringBuilder.append(":"); 
    } 
    return stringBuilder.toString();
  }
  
  private static native void init();
}
