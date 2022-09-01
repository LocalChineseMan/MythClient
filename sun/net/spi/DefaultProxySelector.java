package sun.net.spi;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import sun.misc.REException;
import sun.misc.RegexpPool;
import sun.net.NetProperties;
import sun.net.SocksProxy;

public class DefaultProxySelector extends ProxySelector {
  static final String[][] props = new String[][] { { "http", "http.proxy", "proxy", "socksProxy" }, { "https", "https.proxy", "proxy", "socksProxy" }, { "ftp", "ftp.proxy", "ftpProxy", "proxy", "socksProxy" }, { "gopher", "gopherProxy", "socksProxy" }, { "socket", "socksProxy" } };
  
  private static final String SOCKS_PROXY_VERSION = "socksProxyVersion";
  
  private static boolean hasSystemProxies = false;
  
  static {
    Boolean bool = AccessController.<Boolean>doPrivileged(new PrivilegedAction<Boolean>() {
          public Boolean run() {
            return NetProperties.getBoolean("java.net.useSystemProxies");
          }
        });
    if (bool != null && bool.booleanValue()) {
      AccessController.doPrivileged((PrivilegedAction<?>)new Object());
      hasSystemProxies = init();
    } 
  }
  
  static class NonProxyInfo {
    static final String defStringVal = "localhost|127.*|[::1]|0.0.0.0|[::0]";
    
    String hostsSource;
    
    RegexpPool hostsPool;
    
    final String property;
    
    final String defaultVal;
    
    static NonProxyInfo ftpNonProxyInfo = new NonProxyInfo("ftp.nonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
    
    static NonProxyInfo httpNonProxyInfo = new NonProxyInfo("http.nonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
    
    static NonProxyInfo socksNonProxyInfo = new NonProxyInfo("socksNonProxyHosts", null, null, "localhost|127.*|[::1]|0.0.0.0|[::0]");
    
    NonProxyInfo(String param1String1, String param1String2, RegexpPool param1RegexpPool, String param1String3) {
      this.property = param1String1;
      this.hostsSource = param1String2;
      this.hostsPool = param1RegexpPool;
      this.defaultVal = param1String3;
    }
  }
  
  public List<Proxy> select(URI paramURI) {
    if (paramURI == null)
      throw new IllegalArgumentException("URI can't be null."); 
    String str1 = paramURI.getScheme();
    String str2 = paramURI.getHost();
    if (str2 == null) {
      String str = paramURI.getAuthority();
      if (str != null) {
        int i = str.indexOf('@');
        if (i >= 0)
          str = str.substring(i + 1); 
        i = str.lastIndexOf(':');
        if (i >= 0)
          str = str.substring(0, i); 
        str2 = str;
      } 
    } 
    if (str1 == null || str2 == null)
      throw new IllegalArgumentException("protocol = " + str1 + " host = " + str2); 
    ArrayList<Proxy> arrayList = new ArrayList(1);
    NonProxyInfo nonProxyInfo1 = null;
    if ("http".equalsIgnoreCase(str1)) {
      nonProxyInfo1 = NonProxyInfo.httpNonProxyInfo;
    } else if ("https".equalsIgnoreCase(str1)) {
      nonProxyInfo1 = NonProxyInfo.httpNonProxyInfo;
    } else if ("ftp".equalsIgnoreCase(str1)) {
      nonProxyInfo1 = NonProxyInfo.ftpNonProxyInfo;
    } else if ("socket".equalsIgnoreCase(str1)) {
      nonProxyInfo1 = NonProxyInfo.socksNonProxyInfo;
    } 
    final String proto = str1;
    final NonProxyInfo nprop = nonProxyInfo1;
    final String urlhost = str2.toLowerCase();
    Proxy proxy = AccessController.<Proxy>doPrivileged(new PrivilegedAction<Proxy>() {
          public Proxy run() {
            String str1 = null;
            int i = 0;
            String str2 = null;
            InetSocketAddress inetSocketAddress = null;
            for (byte b = 0; b < DefaultProxySelector.props.length; b++) {
              if (DefaultProxySelector.props[b][0].equalsIgnoreCase(proto)) {
                byte b1;
                for (b1 = 1; b1 < (DefaultProxySelector.props[b]).length; b1++) {
                  str1 = NetProperties.get(DefaultProxySelector.props[b][b1] + "Host");
                  if (str1 != null && str1.length() != 0)
                    break; 
                } 
                if (str1 == null || str1.length() == 0) {
                  if (DefaultProxySelector.hasSystemProxies) {
                    String str;
                    if (proto.equalsIgnoreCase("socket")) {
                      str = "socks";
                    } else {
                      str = proto;
                    } 
                    Proxy proxy = DefaultProxySelector.this.getSystemProxy(str, urlhost);
                    if (proxy != null)
                      return proxy; 
                  } 
                  return Proxy.NO_PROXY;
                } 
                if (nprop != null) {
                  str2 = NetProperties.get(nprop.property);
                  synchronized (nprop) {
                    if (str2 == null) {
                      if (nprop.defaultVal != null) {
                        str2 = nprop.defaultVal;
                      } else {
                        nprop.hostsSource = null;
                        nprop.hostsPool = null;
                      } 
                    } else if (str2.length() != 0) {
                      str2 = str2 + "|localhost|127.*|[::1]|0.0.0.0|[::0]";
                    } 
                    if (str2 != null && 
                      !str2.equals(nprop.hostsSource)) {
                      RegexpPool regexpPool = new RegexpPool();
                      StringTokenizer stringTokenizer = new StringTokenizer(str2, "|", false);
                      try {
                        while (stringTokenizer.hasMoreTokens())
                          regexpPool.add(stringTokenizer.nextToken().toLowerCase(), Boolean.TRUE); 
                      } catch (REException rEException) {}
                      nprop.hostsPool = regexpPool;
                      nprop.hostsSource = str2;
                    } 
                    if (nprop.hostsPool != null && nprop.hostsPool
                      .match(urlhost) != null)
                      return Proxy.NO_PROXY; 
                  } 
                } 
                i = NetProperties.getInteger(DefaultProxySelector.props[b][b1] + "Port", 0).intValue();
                if (i == 0 && b1 < (DefaultProxySelector.props[b]).length - 1)
                  for (byte b2 = 1; b2 < (DefaultProxySelector.props[b]).length - 1; b2++) {
                    if (b2 != b1 && i == 0)
                      i = NetProperties.getInteger(DefaultProxySelector.props[b][b2] + "Port", 0).intValue(); 
                  }  
                if (i == 0)
                  if (b1 == (DefaultProxySelector.props[b]).length - 1) {
                    i = DefaultProxySelector.this.defaultPort("socket");
                  } else {
                    i = DefaultProxySelector.this.defaultPort(proto);
                  }  
                inetSocketAddress = InetSocketAddress.createUnresolved(str1, i);
                if (b1 == (DefaultProxySelector.props[b]).length - 1) {
                  int j = NetProperties.getInteger("socksProxyVersion", 5).intValue();
                  return SocksProxy.create(inetSocketAddress, j);
                } 
                return new Proxy(Proxy.Type.HTTP, inetSocketAddress);
              } 
            } 
            return Proxy.NO_PROXY;
          }
        });
    arrayList.add(proxy);
    return arrayList;
  }
  
  public void connectFailed(URI paramURI, SocketAddress paramSocketAddress, IOException paramIOException) {
    if (paramURI == null || paramSocketAddress == null || paramIOException == null)
      throw new IllegalArgumentException("Arguments can't be null."); 
  }
  
  private int defaultPort(String paramString) {
    if ("http".equalsIgnoreCase(paramString))
      return 80; 
    if ("https".equalsIgnoreCase(paramString))
      return 443; 
    if ("ftp".equalsIgnoreCase(paramString))
      return 80; 
    if ("socket".equalsIgnoreCase(paramString))
      return 1080; 
    if ("gopher".equalsIgnoreCase(paramString))
      return 80; 
    return -1;
  }
  
  private static native boolean init();
  
  private synchronized native Proxy getSystemProxy(String paramString1, String paramString2);
}
