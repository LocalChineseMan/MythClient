package java.security;

public final class SecurityPermission extends BasicPermission {
  private static final long serialVersionUID = 5236109936224050470L;
  
  public SecurityPermission(String paramString) {
    super(paramString);
  }
  
  public SecurityPermission(String paramString1, String paramString2) {
    super(paramString1, paramString2);
  }
}
