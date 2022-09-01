package javax.crypto;

import java.security.Permission;
import java.security.PermissionCollection;

final class CryptoAllPermission extends CryptoPermission {
  private static final long serialVersionUID = -5066513634293192112L;
  
  static final String ALG_NAME = "CryptoAllPermission";
  
  static final CryptoAllPermission INSTANCE = new CryptoAllPermission();
  
  private CryptoAllPermission() {
    super("CryptoAllPermission");
  }
  
  public boolean implies(Permission paramPermission) {
    return paramPermission instanceof CryptoPermission;
  }
  
  public boolean equals(Object paramObject) {
    return (paramObject == INSTANCE);
  }
  
  public int hashCode() {
    return 1;
  }
  
  public PermissionCollection newPermissionCollection() {
    return new CryptoAllPermissionCollection();
  }
}
