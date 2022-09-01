package java.lang.management;

import java.security.BasicPermission;

public final class ManagementPermission extends BasicPermission {
  private static final long serialVersionUID = 1897496590799378737L;
  
  public ManagementPermission(String paramString) {
    super(paramString);
    if (!paramString.equals("control") && !paramString.equals("monitor"))
      throw new IllegalArgumentException("name: " + paramString); 
  }
  
  public ManagementPermission(String paramString1, String paramString2) throws IllegalArgumentException {
    super(paramString1);
    if (!paramString1.equals("control") && !paramString1.equals("monitor"))
      throw new IllegalArgumentException("name: " + paramString1); 
    if (paramString2 != null && paramString2.length() > 0)
      throw new IllegalArgumentException("actions: " + paramString2); 
  }
}
