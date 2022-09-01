package java.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;
import sun.net.RegisteredDomain;
import sun.net.util.IPAddressUtil;
import sun.net.www.URLConnection;
import sun.security.action.GetBooleanAction;
import sun.security.util.Debug;

public final class SocketPermission extends Permission implements Serializable {
  private static final long serialVersionUID = -7204263841984476862L;
  
  private static final int CONNECT = 1;
  
  private static final int LISTEN = 2;
  
  private static final int ACCEPT = 4;
  
  private static final int RESOLVE = 8;
  
  private static final int NONE = 0;
  
  private static final int ALL = 15;
  
  private static final int PORT_MIN = 0;
  
  private static final int PORT_MAX = 65535;
  
  private static final int PRIV_PORT_MAX = 1023;
  
  private static final int DEF_EPH_LOW = 49152;
  
  private transient int mask;
  
  private String actions;
  
  private transient String hostname;
  
  private transient String cname;
  
  private transient InetAddress[] addresses;
  
  private transient boolean wildcard;
  
  private transient boolean init_with_ip;
  
  private transient boolean invalid;
  
  private transient int[] portrange;
  
  private transient boolean defaultDeny = false;
  
  private transient boolean untrusted;
  
  private transient boolean trusted;
  
  private static boolean trustNameService;
  
  private static Debug debug = null;
  
  private static boolean debugInit = false;
  
  private transient String cdomain;
  
  private transient String hdomain;
  
  static {
    Boolean bool = AccessController.<Boolean>doPrivileged(new GetBooleanAction("sun.net.trustNameService"));
    trustNameService = bool.booleanValue();
  }
  
  private static synchronized Debug getDebug() {
    if (!debugInit) {
      debug = Debug.getInstance("access");
      debugInit = true;
    } 
    return debug;
  }
  
  public SocketPermission(String paramString1, String paramString2) {
    super(getHost(paramString1));
    init(getName(), getMask(paramString2));
  }
  
  SocketPermission(String paramString, int paramInt) {
    super(getHost(paramString));
    init(getName(), paramInt);
  }
  
  private void setDeny() {
    this.defaultDeny = true;
  }
  
  private static String getHost(String paramString) {
    if (paramString.equals(""))
      return "localhost"; 
    int i;
    if (paramString.charAt(0) != '[' && (
      i = paramString.indexOf(':')) != paramString.lastIndexOf(':')) {
      StringTokenizer stringTokenizer = new StringTokenizer(paramString, ":");
      int j = stringTokenizer.countTokens();
      if (j == 9) {
        i = paramString.lastIndexOf(':');
        paramString = "[" + paramString.substring(0, i) + "]" + paramString.substring(i);
      } else if (j == 8 && paramString.indexOf("::") == -1) {
        paramString = "[" + paramString + "]";
      } else {
        throw new IllegalArgumentException("Ambiguous hostport part");
      } 
    } 
    return paramString;
  }
  
  private int[] parsePort(String paramString) throws Exception {
    int j, k;
    if (paramString == null || paramString.equals("") || paramString.equals("*"))
      return new int[] { 0, 65535 }; 
    int i = paramString.indexOf('-');
    if (i == -1) {
      int m = Integer.parseInt(paramString);
      return new int[] { m, m };
    } 
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(i + 1);
    if (str1.equals("")) {
      j = 0;
    } else {
      j = Integer.parseInt(str1);
    } 
    if (str2.equals("")) {
      k = 65535;
    } else {
      k = Integer.parseInt(str2);
    } 
    if (j < 0 || k < 0 || k < j)
      throw new IllegalArgumentException("invalid port range"); 
    return new int[] { j, k };
  }
  
  private boolean includesEphemerals() {
    return (this.portrange[0] == 0);
  }
  
  private void init(String paramString, int paramInt) {
    if ((paramInt & 0xF) != paramInt)
      throw new IllegalArgumentException("invalid actions mask"); 
    this.mask = paramInt | 0x8;
    int i = 0;
    boolean bool = false;
    int j = 0;
    int k = -1;
    String str = paramString;
    if (paramString.charAt(0) == '[') {
      bool = true;
      i = paramString.indexOf(']');
      if (i != -1) {
        paramString = paramString.substring(bool, i);
      } else {
        throw new IllegalArgumentException("invalid host/port: " + paramString);
      } 
      k = str.indexOf(':', i + 1);
    } else {
      bool = false;
      k = paramString.indexOf(':', i);
      j = k;
      if (k != -1)
        paramString = paramString.substring(bool, j); 
    } 
    if (k != -1) {
      String str1 = str.substring(k + 1);
      try {
        this.portrange = parsePort(str1);
      } catch (Exception exception) {
        throw new IllegalArgumentException("invalid port range: " + str1);
      } 
    } else {
      this.portrange = new int[] { 0, 65535 };
    } 
    this.hostname = paramString;
    if (paramString.lastIndexOf('*') > 0)
      throw new IllegalArgumentException("invalid host wildcard specification"); 
    if (paramString.startsWith("*")) {
      this.wildcard = true;
      if (paramString.equals("*")) {
        this.cname = "";
      } else if (paramString.startsWith("*.")) {
        this.cname = paramString.substring(1).toLowerCase();
      } else {
        throw new IllegalArgumentException("invalid host wildcard specification");
      } 
      return;
    } 
    if (paramString.length() > 0) {
      char c = paramString.charAt(0);
      if (c == ':' || Character.digit(c, 16) != -1) {
        byte[] arrayOfByte = IPAddressUtil.textToNumericFormatV4(paramString);
        if (arrayOfByte == null)
          arrayOfByte = IPAddressUtil.textToNumericFormatV6(paramString); 
        if (arrayOfByte != null)
          try {
            this
              
              .addresses = new InetAddress[] { InetAddress.getByAddress(arrayOfByte) };
            this.init_with_ip = true;
          } catch (UnknownHostException unknownHostException) {
            this.invalid = true;
          }  
      } 
    } 
  }
  
  private static int getMask(String paramString) {
    if (paramString == null)
      throw new NullPointerException("action can't be null"); 
    if (paramString.equals(""))
      throw new IllegalArgumentException("action can't be empty"); 
    int i = 0;
    if (paramString == "resolve")
      return 8; 
    if (paramString == "connect")
      return 1; 
    if (paramString == "listen")
      return 2; 
    if (paramString == "accept")
      return 4; 
    if (paramString == "connect,accept")
      return 5; 
    char[] arrayOfChar = paramString.toCharArray();
    int j = arrayOfChar.length - 1;
    if (j < 0)
      return i; 
    while (j != -1) {
      byte b;
      char c;
      while (j != -1 && ((c = arrayOfChar[j]) == ' ' || c == '\r' || c == '\n' || c == '\f' || c == '\t'))
        j--; 
      if (j >= 6 && (arrayOfChar[j - 6] == 'c' || arrayOfChar[j - 6] == 'C') && (arrayOfChar[j - 5] == 'o' || arrayOfChar[j - 5] == 'O') && (arrayOfChar[j - 4] == 'n' || arrayOfChar[j - 4] == 'N') && (arrayOfChar[j - 3] == 'n' || arrayOfChar[j - 3] == 'N') && (arrayOfChar[j - 2] == 'e' || arrayOfChar[j - 2] == 'E') && (arrayOfChar[j - 1] == 'c' || arrayOfChar[j - 1] == 'C') && (arrayOfChar[j] == 't' || arrayOfChar[j] == 'T')) {
        b = 7;
        i |= 0x1;
      } else if (j >= 6 && (arrayOfChar[j - 6] == 'r' || arrayOfChar[j - 6] == 'R') && (arrayOfChar[j - 5] == 'e' || arrayOfChar[j - 5] == 'E') && (arrayOfChar[j - 4] == 's' || arrayOfChar[j - 4] == 'S') && (arrayOfChar[j - 3] == 'o' || arrayOfChar[j - 3] == 'O') && (arrayOfChar[j - 2] == 'l' || arrayOfChar[j - 2] == 'L') && (arrayOfChar[j - 1] == 'v' || arrayOfChar[j - 1] == 'V') && (arrayOfChar[j] == 'e' || arrayOfChar[j] == 'E')) {
        b = 7;
        i |= 0x8;
      } else if (j >= 5 && (arrayOfChar[j - 5] == 'l' || arrayOfChar[j - 5] == 'L') && (arrayOfChar[j - 4] == 'i' || arrayOfChar[j - 4] == 'I') && (arrayOfChar[j - 3] == 's' || arrayOfChar[j - 3] == 'S') && (arrayOfChar[j - 2] == 't' || arrayOfChar[j - 2] == 'T') && (arrayOfChar[j - 1] == 'e' || arrayOfChar[j - 1] == 'E') && (arrayOfChar[j] == 'n' || arrayOfChar[j] == 'N')) {
        b = 6;
        i |= 0x2;
      } else if (j >= 5 && (arrayOfChar[j - 5] == 'a' || arrayOfChar[j - 5] == 'A') && (arrayOfChar[j - 4] == 'c' || arrayOfChar[j - 4] == 'C') && (arrayOfChar[j - 3] == 'c' || arrayOfChar[j - 3] == 'C') && (arrayOfChar[j - 2] == 'e' || arrayOfChar[j - 2] == 'E') && (arrayOfChar[j - 1] == 'p' || arrayOfChar[j - 1] == 'P') && (arrayOfChar[j] == 't' || arrayOfChar[j] == 'T')) {
        b = 6;
        i |= 0x4;
      } else {
        throw new IllegalArgumentException("invalid permission: " + paramString);
      } 
      boolean bool = false;
      while (j >= b && !bool) {
        switch (arrayOfChar[j - b]) {
          case ',':
            bool = true;
            break;
          case '\t':
          case '\n':
          case '\f':
          case '\r':
          case ' ':
            break;
          default:
            throw new IllegalArgumentException("invalid permission: " + paramString);
        } 
        j--;
      } 
      j -= b;
    } 
    return i;
  }
  
  private boolean isUntrusted() throws UnknownHostException {
    if (this.trusted)
      return false; 
    if (this.invalid || this.untrusted)
      return true; 
    try {
      if (!trustNameService && (this.defaultDeny || 
        URLConnection.isProxiedHost(this.hostname))) {
        if (this.cname == null)
          getCanonName(); 
        if (!match(this.cname, this.hostname))
          if (!authorized(this.hostname, this.addresses[0].getAddress())) {
            this.untrusted = true;
            Debug debug = getDebug();
            if (debug != null && Debug.isOn("failure"))
              debug.println("socket access restriction: proxied host (" + this.addresses[0] + ")" + " does not match " + this.cname + " from reverse lookup"); 
            return true;
          }  
        this.trusted = true;
      } 
    } catch (UnknownHostException unknownHostException) {
      this.invalid = true;
      throw unknownHostException;
    } 
    return false;
  }
  
  void getCanonName() throws UnknownHostException {
    if (this.cname != null || this.invalid || this.untrusted)
      return; 
    try {
      if (this.addresses == null)
        getIP(); 
      if (this.init_with_ip) {
        this.cname = this.addresses[0].getHostName(false).toLowerCase();
      } else {
        this
          .cname = InetAddress.getByName(this.addresses[0].getHostAddress()).getHostName(false).toLowerCase();
      } 
    } catch (UnknownHostException unknownHostException) {
      this.invalid = true;
      throw unknownHostException;
    } 
  }
  
  private boolean match(String paramString1, String paramString2) {
    String str1 = paramString1.toLowerCase();
    String str2 = paramString2.toLowerCase();
    if (str1.startsWith(str2) && (str1
      .length() == str2.length() || str1.charAt(str2.length()) == '.'))
      return true; 
    if (this.cdomain == null)
      this.cdomain = RegisteredDomain.getRegisteredDomain(str1); 
    if (this.hdomain == null)
      this.hdomain = RegisteredDomain.getRegisteredDomain(str2); 
    return (this.cdomain.length() != 0 && this.hdomain.length() != 0 && this.cdomain
      .equals(this.hdomain));
  }
  
  private boolean authorized(String paramString, byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte.length == 4)
      return authorizedIPv4(paramString, paramArrayOfbyte); 
    if (paramArrayOfbyte.length == 16)
      return authorizedIPv6(paramString, paramArrayOfbyte); 
    return false;
  }
  
  private boolean authorizedIPv4(String paramString, byte[] paramArrayOfbyte) {
    String str = "";
    try {
      str = "auth." + (paramArrayOfbyte[3] & 0xFF) + "." + (paramArrayOfbyte[2] & 0xFF) + "." + (paramArrayOfbyte[1] & 0xFF) + "." + (paramArrayOfbyte[0] & 0xFF) + ".in-addr.arpa";
      str = this.hostname + '.' + str;
      InetAddress inetAddress = InetAddress.getAllByName0(str, false)[0];
      if (inetAddress.equals(InetAddress.getByAddress(paramArrayOfbyte)))
        return true; 
      Debug debug = getDebug();
      if (debug != null && Debug.isOn("failure"))
        debug.println("socket access restriction: IP address of " + inetAddress + " != " + InetAddress.getByAddress(paramArrayOfbyte)); 
    } catch (UnknownHostException unknownHostException) {
      Debug debug = getDebug();
      if (debug != null && Debug.isOn("failure"))
        debug.println("socket access restriction: forward lookup failed for " + str); 
    } 
    return false;
  }
  
  private boolean authorizedIPv6(String paramString, byte[] paramArrayOfbyte) {
    String str = "";
    try {
      StringBuffer stringBuffer = new StringBuffer(39);
      for (byte b = 15; b >= 0; b--) {
        stringBuffer.append(Integer.toHexString(paramArrayOfbyte[b] & 0xF));
        stringBuffer.append('.');
        stringBuffer.append(Integer.toHexString(paramArrayOfbyte[b] >> 4 & 0xF));
        stringBuffer.append('.');
      } 
      str = "auth." + stringBuffer.toString() + "IP6.ARPA";
      str = this.hostname + '.' + str;
      InetAddress inetAddress = InetAddress.getAllByName0(str, false)[0];
      if (inetAddress.equals(InetAddress.getByAddress(paramArrayOfbyte)))
        return true; 
      Debug debug = getDebug();
      if (debug != null && Debug.isOn("failure"))
        debug.println("socket access restriction: IP address of " + inetAddress + " != " + InetAddress.getByAddress(paramArrayOfbyte)); 
    } catch (UnknownHostException unknownHostException) {
      Debug debug = getDebug();
      if (debug != null && Debug.isOn("failure"))
        debug.println("socket access restriction: forward lookup failed for " + str); 
    } 
    return false;
  }
  
  void getIP() throws UnknownHostException {
    if (this.addresses != null || this.wildcard || this.invalid)
      return; 
    try {
      String str;
      if (getName().charAt(0) == '[') {
        str = getName().substring(1, getName().indexOf(']'));
      } else {
        int i = getName().indexOf(":");
        if (i == -1) {
          str = getName();
        } else {
          str = getName().substring(0, i);
        } 
      } 
      this
        .addresses = new InetAddress[] { InetAddress.getAllByName0(str, false)[0] };
    } catch (UnknownHostException unknownHostException) {
      this.invalid = true;
      throw unknownHostException;
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      this.invalid = true;
      throw new UnknownHostException(getName());
    } 
  }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof SocketPermission))
      return false; 
    if (paramPermission == this)
      return true; 
    SocketPermission socketPermission = (SocketPermission)paramPermission;
    return ((this.mask & socketPermission.mask) == socketPermission.mask && 
      impliesIgnoreMask(socketPermission));
  }
  
  boolean impliesIgnoreMask(SocketPermission paramSocketPermission) {
    if ((paramSocketPermission.mask & 0x8) != paramSocketPermission.mask)
      if (paramSocketPermission.portrange[0] < this.portrange[0] || paramSocketPermission.portrange[1] > this.portrange[1])
        if (includesEphemerals() || paramSocketPermission.includesEphemerals()) {
          if (!inRange(this.portrange[0], this.portrange[1], paramSocketPermission.portrange[0], paramSocketPermission.portrange[1]))
            return false; 
        } else {
          return false;
        }   
    if (this.wildcard && "".equals(this.cname))
      return true; 
    if (this.invalid || paramSocketPermission.invalid)
      return compareHostnames(paramSocketPermission); 
    try {
      if (this.init_with_ip) {
        if (paramSocketPermission.wildcard)
          return false; 
        if (paramSocketPermission.init_with_ip)
          return this.addresses[0].equals(paramSocketPermission.addresses[0]); 
        if (paramSocketPermission.addresses == null)
          paramSocketPermission.getIP(); 
        for (byte b = 0; b < paramSocketPermission.addresses.length; b++) {
          if (this.addresses[0].equals(paramSocketPermission.addresses[b]))
            return true; 
        } 
        return false;
      } 
      if (this.wildcard || paramSocketPermission.wildcard) {
        if (this.wildcard && paramSocketPermission.wildcard)
          return paramSocketPermission.cname.endsWith(this.cname); 
        if (paramSocketPermission.wildcard)
          return false; 
        if (paramSocketPermission.cname == null)
          paramSocketPermission.getCanonName(); 
        return paramSocketPermission.cname.endsWith(this.cname);
      } 
      if (this.addresses == null)
        getIP(); 
      if (paramSocketPermission.addresses == null)
        paramSocketPermission.getIP(); 
      if (!paramSocketPermission.init_with_ip || !isUntrusted()) {
        for (byte b = 0; b < this.addresses.length; b++) {
          for (byte b1 = 0; b1 < paramSocketPermission.addresses.length; b1++) {
            if (this.addresses[b].equals(paramSocketPermission.addresses[b1]))
              return true; 
          } 
        } 
        if (this.cname == null)
          getCanonName(); 
        if (paramSocketPermission.cname == null)
          paramSocketPermission.getCanonName(); 
        return this.cname.equalsIgnoreCase(paramSocketPermission.cname);
      } 
    } catch (UnknownHostException unknownHostException) {
      return compareHostnames(paramSocketPermission);
    } 
    return false;
  }
  
  private boolean compareHostnames(SocketPermission paramSocketPermission) {
    String str1 = this.hostname;
    String str2 = paramSocketPermission.hostname;
    if (str1 == null)
      return false; 
    if (this.wildcard) {
      int i = this.cname.length();
      return str2.regionMatches(true, str2
          .length() - i, this.cname, 0, i);
    } 
    return str1.equalsIgnoreCase(str2);
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof SocketPermission))
      return false; 
    SocketPermission socketPermission = (SocketPermission)paramObject;
    if (this.mask != socketPermission.mask)
      return false; 
    if ((socketPermission.mask & 0x8) != socketPermission.mask)
      if (this.portrange[0] != socketPermission.portrange[0] || this.portrange[1] != socketPermission.portrange[1])
        return false;  
    if (getName().equalsIgnoreCase(socketPermission.getName()))
      return true; 
    try {
      getCanonName();
      socketPermission.getCanonName();
    } catch (UnknownHostException unknownHostException) {
      return false;
    } 
    if (this.invalid || socketPermission.invalid)
      return false; 
    if (this.cname != null)
      return this.cname.equalsIgnoreCase(socketPermission.cname); 
    return false;
  }
  
  public int hashCode() {
    if (this.init_with_ip || this.wildcard)
      return getName().hashCode(); 
    try {
      getCanonName();
    } catch (UnknownHostException unknownHostException) {}
    if (this.invalid || this.cname == null)
      return getName().hashCode(); 
    return this.cname.hashCode();
  }
  
  int getMask() {
    return this.mask;
  }
  
  private static String getActions(int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    boolean bool = false;
    if ((paramInt & 0x1) == 1) {
      bool = true;
      stringBuilder.append("connect");
    } 
    if ((paramInt & 0x2) == 2) {
      if (bool) {
        stringBuilder.append(',');
      } else {
        bool = true;
      } 
      stringBuilder.append("listen");
    } 
    if ((paramInt & 0x4) == 4) {
      if (bool) {
        stringBuilder.append(',');
      } else {
        bool = true;
      } 
      stringBuilder.append("accept");
    } 
    if ((paramInt & 0x8) == 8) {
      if (bool) {
        stringBuilder.append(',');
      } else {
        bool = true;
      } 
      stringBuilder.append("resolve");
    } 
    return stringBuilder.toString();
  }
  
  public String getActions() {
    if (this.actions == null)
      this.actions = getActions(this.mask); 
    return this.actions;
  }
  
  public PermissionCollection newPermissionCollection() {
    return new SocketPermissionCollection();
  }
  
  private synchronized void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    if (this.actions == null)
      getActions(); 
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private synchronized void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    init(getName(), getMask(this.actions));
  }
  
  private static int initEphemeralPorts(String paramString, int paramInt) {
    return ((Integer)AccessController.<Integer>doPrivileged((PrivilegedAction<Integer>)new Object(paramString))).intValue();
  }
  
  private static boolean inRange(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    int i = EphemeralRange.low;
    int j = EphemeralRange.high;
    if (paramInt3 == 0) {
      if (!inRange(paramInt1, paramInt2, i, j))
        return false; 
      if (paramInt4 == 0)
        return true; 
      paramInt3 = 1;
    } 
    if (paramInt1 == 0 && paramInt2 == 0)
      return (paramInt3 >= i && paramInt4 <= j); 
    if (paramInt1 != 0)
      return (paramInt3 >= paramInt1 && paramInt4 <= paramInt2); 
    if (paramInt2 >= i - 1)
      return (paramInt4 <= j); 
    return ((paramInt3 <= paramInt2 && paramInt4 <= paramInt2) || (paramInt3 >= i && paramInt4 <= j));
  }
  
  private static class SocketPermission {}
}
