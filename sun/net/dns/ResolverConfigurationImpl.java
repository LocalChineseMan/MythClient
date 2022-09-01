package sun.net.dns;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class ResolverConfigurationImpl extends ResolverConfiguration {
  private static Object lock = new Object();
  
  private final ResolverConfiguration.Options opts;
  
  private static boolean changed = false;
  
  private static long lastRefresh = -1L;
  
  private static final int TIMEOUT = 120000;
  
  private static String os_searchlist;
  
  private static String os_nameservers;
  
  private static LinkedList<String> searchlist;
  
  private static LinkedList<String> nameservers;
  
  private LinkedList<String> stringToList(String paramString) {
    LinkedList<String> linkedList = new LinkedList();
    StringTokenizer stringTokenizer = new StringTokenizer(paramString, ", ");
    while (stringTokenizer.hasMoreTokens()) {
      String str = stringTokenizer.nextToken();
      if (!linkedList.contains(str))
        linkedList.add(str); 
    } 
    return linkedList;
  }
  
  private void loadConfig() {
    assert Thread.holdsLock(lock);
    if (changed) {
      changed = false;
    } else if (lastRefresh >= 0L) {
      long l = System.currentTimeMillis();
      if (l - lastRefresh < 120000L)
        return; 
    } 
    loadDNSconfig0();
    lastRefresh = System.currentTimeMillis();
    searchlist = stringToList(os_searchlist);
    nameservers = stringToList(os_nameservers);
    os_searchlist = null;
    os_nameservers = null;
  }
  
  ResolverConfigurationImpl() {
    this.opts = new OptionsImpl();
  }
  
  public List<String> searchlist() {
    synchronized (lock) {
      loadConfig();
      return (List<String>)searchlist.clone();
    } 
  }
  
  public List<String> nameservers() {
    synchronized (lock) {
      loadConfig();
      return (List<String>)nameservers.clone();
    } 
  }
  
  public ResolverConfiguration.Options options() {
    return this.opts;
  }
  
  static class AddressChangeListener extends Thread {
    public void run() {
      while (true) {
        if (ResolverConfigurationImpl.notifyAddrChange0() != 0)
          return; 
        synchronized (ResolverConfigurationImpl.lock) {
          ResolverConfigurationImpl.changed = true;
        } 
      } 
    }
  }
  
  static {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
          public Void run() {
            System.loadLibrary("net");
            return null;
          }
        });
    init0();
    AddressChangeListener addressChangeListener = new AddressChangeListener();
    addressChangeListener.setDaemon(true);
    addressChangeListener.start();
  }
  
  static native void init0();
  
  static native void loadDNSconfig0();
  
  static native int notifyAddrChange0();
}
