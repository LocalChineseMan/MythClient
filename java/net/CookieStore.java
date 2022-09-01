package java.net;

import java.util.List;

public interface CookieStore {
  void add(URI paramURI, HttpCookie paramHttpCookie);
  
  List<HttpCookie> get(URI paramURI);
  
  List<HttpCookie> getCookies();
  
  List<URI> getURIs();
  
  boolean remove(URI paramURI, HttpCookie paramHttpCookie);
  
  boolean removeAll();
}
