package javax.crypto;

import java.net.URL;
import java.security.AccessController;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class JceSecurityManager extends SecurityManager {
  private static final CryptoPermissions defaultPolicy;
  
  private static final CryptoPermissions exemptPolicy;
  
  private static final CryptoAllPermission allPerm;
  
  private static final Vector<Class<?>> TrustedCallersCache = new Vector<>(2);
  
  private static final ConcurrentMap<URL, CryptoPermissions> exemptCache = new ConcurrentHashMap<>();
  
  private static final CryptoPermissions CACHE_NULL_MARK = new CryptoPermissions();
  
  static final JceSecurityManager INSTANCE;
  
  static {
    defaultPolicy = JceSecurity.getDefaultPolicy();
    exemptPolicy = JceSecurity.getExemptPolicy();
    allPerm = CryptoAllPermission.INSTANCE;
    INSTANCE = AccessController.<JceSecurityManager>doPrivileged(new PrivilegedAction<JceSecurityManager>() {
          public JceSecurityManager run() {
            return new JceSecurityManager();
          }
        });
  }
  
  private JceSecurityManager() {}
  
  CryptoPermission getCryptoPermission(String paramString) {
    paramString = paramString.toUpperCase(Locale.ENGLISH);
    CryptoPermission cryptoPermission = getDefaultPermission(paramString);
    if (cryptoPermission == CryptoAllPermission.INSTANCE)
      return cryptoPermission; 
    Class[] arrayOfClass = getClassContext();
    URL uRL = null;
    byte b;
    for (b = 0; b < arrayOfClass.length; ) {
      Class<?> clazz = arrayOfClass[b];
      uRL = JceSecurity.getCodeBase(clazz);
      if (uRL != null)
        break; 
      if (clazz.getName().startsWith("javax.crypto.")) {
        b++;
        continue;
      } 
      return cryptoPermission;
    } 
    if (b == arrayOfClass.length)
      return cryptoPermission; 
    CryptoPermissions cryptoPermissions = exemptCache.get(uRL);
    if (cryptoPermissions == null)
      synchronized (getClass()) {
        cryptoPermissions = exemptCache.get(uRL);
        if (cryptoPermissions == null) {
          cryptoPermissions = getAppPermissions(uRL);
          exemptCache.putIfAbsent(uRL, (cryptoPermissions == null) ? CACHE_NULL_MARK : cryptoPermissions);
        } 
      }  
    if (cryptoPermissions == null || cryptoPermissions == CACHE_NULL_MARK)
      return cryptoPermission; 
    if (cryptoPermissions.implies(allPerm))
      return allPerm; 
    PermissionCollection permissionCollection1 = cryptoPermissions.getPermissionCollection(paramString);
    if (permissionCollection1 == null)
      return cryptoPermission; 
    Enumeration<Permission> enumeration = permissionCollection1.elements();
    while (enumeration.hasMoreElements()) {
      CryptoPermission cryptoPermission1 = (CryptoPermission)enumeration.nextElement();
      if (cryptoPermission1.getExemptionMechanism() == null)
        return cryptoPermission1; 
    } 
    PermissionCollection permissionCollection2 = exemptPolicy.getPermissionCollection(paramString);
    if (permissionCollection2 == null)
      return cryptoPermission; 
    enumeration = permissionCollection2.elements();
    while (enumeration.hasMoreElements()) {
      CryptoPermission cryptoPermission1 = (CryptoPermission)enumeration.nextElement();
      try {
        ExemptionMechanism.getInstance(cryptoPermission1.getExemptionMechanism());
        if (cryptoPermission1.getAlgorithm().equals("*")) {
          CryptoPermission cryptoPermission2;
          if (cryptoPermission1.getCheckParam()) {
            cryptoPermission2 = new CryptoPermission(paramString, cryptoPermission1.getMaxKeySize(), cryptoPermission1.getAlgorithmParameterSpec(), cryptoPermission1.getExemptionMechanism());
          } else {
            cryptoPermission2 = new CryptoPermission(paramString, cryptoPermission1.getMaxKeySize(), cryptoPermission1.getExemptionMechanism());
          } 
          if (cryptoPermissions.implies(cryptoPermission2))
            return cryptoPermission2; 
        } 
        if (cryptoPermissions.implies(cryptoPermission1))
          return cryptoPermission1; 
      } catch (Exception exception) {}
    } 
    return cryptoPermission;
  }
  
  private static CryptoPermissions getAppPermissions(URL paramURL) {
    try {
      return JceSecurity.verifyExemptJar(paramURL);
    } catch (Exception exception) {
      return null;
    } 
  }
  
  private CryptoPermission getDefaultPermission(String paramString) {
    Enumeration<Permission> enumeration = defaultPolicy.getPermissionCollection(paramString).elements();
    return (CryptoPermission)enumeration.nextElement();
  }
  
  boolean isCallerTrusted() {
    Class[] arrayOfClass = getClassContext();
    URL uRL = null;
    byte b;
    for (b = 0; b < arrayOfClass.length; b++) {
      uRL = JceSecurity.getCodeBase(arrayOfClass[b]);
      if (uRL != null)
        break; 
    } 
    if (b == arrayOfClass.length)
      return true; 
    if (TrustedCallersCache.contains(arrayOfClass[b]))
      return true; 
    try {
      JceSecurity.verifyProviderJar(uRL);
    } catch (Exception exception) {
      return false;
    } 
    TrustedCallersCache.addElement(arrayOfClass[b]);
    return true;
  }
}
