package sun.java2d.cmm;

import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetPropertyAction;

public class CMSManager {
  public static ColorSpace GRAYspace;
  
  public static ColorSpace LINEAR_RGBspace;
  
  private static PCMM cmmImpl = null;
  
  public static synchronized PCMM getModule() {
    if (cmmImpl != null)
      return cmmImpl; 
    CMMServiceProvider cMMServiceProvider = AccessController.<CMMServiceProvider>doPrivileged((PrivilegedAction<CMMServiceProvider>)new Object());
    cmmImpl = cMMServiceProvider.getColorManagementModule();
    if (cmmImpl == null)
      throw new CMMException("Cannot initialize Color Management System.No CM module found"); 
    GetPropertyAction getPropertyAction = new GetPropertyAction("sun.java2d.cmm.trace");
    String str = AccessController.<String>doPrivileged(getPropertyAction);
    if (str != null)
      cmmImpl = new CMMTracer(cmmImpl); 
    return cmmImpl;
  }
  
  static synchronized boolean canCreateModule() {
    return (cmmImpl == null);
  }
  
  public static class CMSManager {}
}
