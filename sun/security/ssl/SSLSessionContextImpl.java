package sun.security.ssl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Locale;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import sun.security.util.Cache;

final class SSLSessionContextImpl implements SSLSessionContext {
  private int cacheLimit = getDefaultCacheLimit();
  
  private int timeout = 86400;
  
  private Cache<SessionId, SSLSessionImpl> sessionCache = Cache.newSoftMemoryCache(this.cacheLimit, this.timeout);
  
  private Cache<String, SSLSessionImpl> sessionHostPortCache = Cache.newSoftMemoryCache(this.cacheLimit, this.timeout);
  
  public SSLSession getSession(byte[] paramArrayOfbyte) {
    if (paramArrayOfbyte == null)
      throw new NullPointerException("session id cannot be null"); 
    SSLSessionImpl sSLSessionImpl = this.sessionCache.get(new SessionId(paramArrayOfbyte));
    if (!isTimedout(sSLSessionImpl))
      return sSLSessionImpl; 
    return null;
  }
  
  public Enumeration<byte[]> getIds() {
    SessionCacheVisitor sessionCacheVisitor = new SessionCacheVisitor(this);
    this.sessionCache.accept(sessionCacheVisitor);
    return sessionCacheVisitor.getSessionIds();
  }
  
  public void setSessionTimeout(int paramInt) throws IllegalArgumentException {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    if (this.timeout != paramInt) {
      this.sessionCache.setTimeout(paramInt);
      this.sessionHostPortCache.setTimeout(paramInt);
      this.timeout = paramInt;
    } 
  }
  
  public int getSessionTimeout() {
    return this.timeout;
  }
  
  public void setSessionCacheSize(int paramInt) throws IllegalArgumentException {
    if (paramInt < 0)
      throw new IllegalArgumentException(); 
    if (this.cacheLimit != paramInt) {
      this.sessionCache.setCapacity(paramInt);
      this.sessionHostPortCache.setCapacity(paramInt);
      this.cacheLimit = paramInt;
    } 
  }
  
  public int getSessionCacheSize() {
    return this.cacheLimit;
  }
  
  SSLSessionImpl get(byte[] paramArrayOfbyte) {
    return (SSLSessionImpl)getSession(paramArrayOfbyte);
  }
  
  SSLSessionImpl get(String paramString, int paramInt) {
    if (paramString == null && paramInt == -1)
      return null; 
    SSLSessionImpl sSLSessionImpl = this.sessionHostPortCache.get(getKey(paramString, paramInt));
    if (!isTimedout(sSLSessionImpl))
      return sSLSessionImpl; 
    return null;
  }
  
  private String getKey(String paramString, int paramInt) {
    return (paramString + ":" + String.valueOf(paramInt)).toLowerCase(Locale.ENGLISH);
  }
  
  void put(SSLSessionImpl paramSSLSessionImpl) {
    this.sessionCache.put(paramSSLSessionImpl.getSessionId(), paramSSLSessionImpl);
    if (paramSSLSessionImpl.getPeerHost() != null && paramSSLSessionImpl.getPeerPort() != -1)
      this.sessionHostPortCache.put(
          getKey(paramSSLSessionImpl.getPeerHost(), paramSSLSessionImpl.getPeerPort()), paramSSLSessionImpl); 
    paramSSLSessionImpl.setContext(this);
  }
  
  void remove(SessionId paramSessionId) {
    SSLSessionImpl sSLSessionImpl = this.sessionCache.get(paramSessionId);
    if (sSLSessionImpl != null) {
      this.sessionCache.remove(paramSessionId);
      this.sessionHostPortCache.remove(
          getKey(sSLSessionImpl.getPeerHost(), sSLSessionImpl.getPeerPort()));
    } 
  }
  
  private int getDefaultCacheLimit() {
    boolean bool = false;
    try {
      String str = AccessController.<String>doPrivileged(new PrivilegedAction<String>() {
            public String run() {
              return System.getProperty("javax.net.ssl.sessionCacheSize");
            }
          });
      bool = (str != null) ? Integer.valueOf(str).intValue() : false;
    } catch (Exception exception) {}
    return bool ? bool : 0;
  }
  
  final class SSLSessionContextImpl {}
  
  boolean isTimedout(SSLSession paramSSLSession) {
    if (this.timeout == 0)
      return false; 
    if (paramSSLSession != null && paramSSLSession.getCreationTime() + this.timeout * 1000L <= 
      System.currentTimeMillis()) {
      paramSSLSession.invalidate();
      return true;
    } 
    return false;
  }
}
