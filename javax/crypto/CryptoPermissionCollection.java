package javax.crypto;

import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Vector;

final class CryptoPermissionCollection extends PermissionCollection implements Serializable {
  private static final long serialVersionUID = -511215555898802763L;
  
  private Vector<Permission> permissions = new Vector<>(3);
  
  public void add(Permission paramPermission) {
    if (isReadOnly())
      throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection"); 
    if (!(paramPermission instanceof CryptoPermission))
      return; 
    this.permissions.addElement(paramPermission);
  }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof CryptoPermission))
      return false; 
    CryptoPermission cryptoPermission = (CryptoPermission)paramPermission;
    Enumeration<Permission> enumeration = this.permissions.elements();
    while (enumeration.hasMoreElements()) {
      CryptoPermission cryptoPermission1 = (CryptoPermission)enumeration.nextElement();
      if (cryptoPermission1.implies(cryptoPermission))
        return true; 
    } 
    return false;
  }
  
  public Enumeration<Permission> elements() {
    return this.permissions.elements();
  }
}
