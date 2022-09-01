package sun.net.www.protocol.http;

public interface AuthCache {
  void put(String paramString, AuthCacheValue paramAuthCacheValue);
  
  AuthCacheValue get(String paramString1, String paramString2);
  
  void remove(String paramString, AuthCacheValue paramAuthCacheValue);
}
