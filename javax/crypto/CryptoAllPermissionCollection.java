package javax.crypto;

import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Vector;

final class CryptoAllPermissionCollection extends PermissionCollection implements Serializable {
  private static final long serialVersionUID = 7450076868380144072L;
  
  private boolean all_allowed = false;
  
  public void add(Permission paramPermission) {
    if (isReadOnly())
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection"); 
    if (paramPermission != CryptoAllPermission.INSTANCE)
      return; 
    this.all_allowed = true;
  }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof CryptoPermission))
      return false; 
    return this.all_allowed;
  }
  
  public Enumeration<Permission> elements() {
    Vector<CryptoAllPermission> vector = new Vector(1);
    if (this.all_allowed)
      vector.add(CryptoAllPermission.INSTANCE); 
    return vector.elements();
  }
}
