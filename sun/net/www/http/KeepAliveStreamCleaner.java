package sun.net.www.http;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import sun.net.NetProperties;

class KeepAliveStreamCleaner extends LinkedList<KeepAliveCleanerEntry> implements Runnable {
  protected static int MAX_DATA_REMAINING = 512;
  
  protected static int MAX_CAPACITY = 10;
  
  protected static final int TIMEOUT = 5000;
  
  private static final int MAX_RETRIES = 5;
  
  static {
    int i = ((Integer)AccessController.<Integer>doPrivileged(new PrivilegedAction<Integer>() {
          public Integer run() {
            return NetProperties.getInteger("http.KeepAlive.remainingData", KeepAliveStreamCleaner.MAX_DATA_REMAINING);
          }
        })).intValue() * 1024;
    MAX_DATA_REMAINING = i;
    int j = ((Integer)AccessController.<Integer>doPrivileged(new PrivilegedAction<Integer>() {
          public Integer run() {
            return NetProperties.getInteger("http.KeepAlive.queuedConnections", KeepAliveStreamCleaner.MAX_CAPACITY);
          }
        })).intValue();
    MAX_CAPACITY = j;
  }
  
  public boolean offer(KeepAliveCleanerEntry paramKeepAliveCleanerEntry) {
    if (size() >= MAX_CAPACITY)
      return false; 
    return super.offer(paramKeepAliveCleanerEntry);
  }
  
  public void run() {
    KeepAliveCleanerEntry keepAliveCleanerEntry = null;
    do {
      try {
        synchronized (this) {
          long l1 = System.currentTimeMillis();
          long l2 = 5000L;
          while ((keepAliveCleanerEntry = poll()) == null) {
            wait(l2);
            long l3 = System.currentTimeMillis();
            long l4 = l3 - l1;
            if (l4 > l2) {
              keepAliveCleanerEntry = poll();
              break;
            } 
            l1 = l3;
            l2 -= l4;
          } 
        } 
        if (keepAliveCleanerEntry == null)
          break; 
        KeepAliveStream keepAliveStream = keepAliveCleanerEntry.getKeepAliveStream();
        if (keepAliveStream != null)
          synchronized (keepAliveStream) {
            HttpClient httpClient = keepAliveCleanerEntry.getHttpClient();
            try {
              if (httpClient != null && !httpClient.isInKeepAliveCache()) {
                int i = httpClient.getReadTimeout();
                httpClient.setReadTimeout(5000);
                long l = keepAliveStream.remainingToRead();
                if (l > 0L) {
                  long l1 = 0L;
                  byte b = 0;
                  while (l1 < l && b < 5) {
                    l -= l1;
                    l1 = keepAliveStream.skip(l);
                    if (l1 == 0L)
                      b++; 
                  } 
                  l -= l1;
                } 
                if (l == 0L) {
                  httpClient.setReadTimeout(i);
                  httpClient.finished();
                } else {
                  httpClient.closeServer();
                } 
              } 
            } catch (IOException iOException) {
              httpClient.closeServer();
            } finally {
              keepAliveStream.setClosed();
            } 
          }  
      } catch (InterruptedException interruptedException) {}
    } while (keepAliveCleanerEntry != null);
  }
}
