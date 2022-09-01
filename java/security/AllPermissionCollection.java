package java.security;

import java.io.Serializable;
import java.util.Enumeration;

final class AllPermissionCollection extends PermissionCollection implements Serializable {
  private static final long serialVersionUID = -4023755556366636806L;
  
  private boolean all_allowed = false;
  
  public void add(Permission paramPermission) {
    if (!(paramPermission instanceof AllPermission))
      throw new IllegalArgumentException("invalid permission: " + paramPermission); 
    if (isReadOnly())
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection"); 
    this.all_allowed = true;
  }
  
  public boolean implies(Permission paramPermission) {
    return this.all_allowed;
  }
  
  public Enumeration<Permission> elements() {
    return (Enumeration<Permission>)new Object(this);
  }
}
