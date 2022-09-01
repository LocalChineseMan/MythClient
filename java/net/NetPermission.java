package java.net;

import java.security.BasicPermission;

public final class NetPermission extends BasicPermission {
  private static final long serialVersionUID = -8343910153355041693L;
  
  public NetPermission(String paramString) {
    super(paramString);
  }
  
  public NetPermission(String paramString1, String paramString2) {
    super(paramString1, paramString2);
  }
}
