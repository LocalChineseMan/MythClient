package java.net;

public interface CookiePolicy {
  public static final CookiePolicy ACCEPT_ALL = new CookiePolicy() {
      public boolean shouldAccept(URI param1URI, HttpCookie param1HttpCookie) {
        return true;
      }
    };
  
  public static final CookiePolicy ACCEPT_NONE = new CookiePolicy() {
      public boolean shouldAccept(URI param1URI, HttpCookie param1HttpCookie) {
        return false;
      }
    };
  
  public static final CookiePolicy ACCEPT_ORIGINAL_SERVER = new CookiePolicy() {
      public boolean shouldAccept(URI param1URI, HttpCookie param1HttpCookie) {
        if (param1URI == null || param1HttpCookie == null)
          return false; 
        return HttpCookie.domainMatches(param1HttpCookie.getDomain(), param1URI.getHost());
      }
    };
  
  boolean shouldAccept(URI paramURI, HttpCookie paramHttpCookie);
}
