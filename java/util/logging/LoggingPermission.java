package java.util.logging;

import java.security.BasicPermission;

public final class LoggingPermission extends BasicPermission {
  private static final long serialVersionUID = 63564341580231582L;
  
  public LoggingPermission(String paramString1, String paramString2) throws IllegalArgumentException {
    super(paramString1);
    if (!paramString1.equals("control"))
      throw new IllegalArgumentException("name: " + paramString1); 
    if (paramString2 != null && paramString2.length() > 0)
      throw new IllegalArgumentException("actions: " + paramString2); 
  }
}
