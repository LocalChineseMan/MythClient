package javax.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

final class CryptoPermissions extends PermissionCollection implements Serializable {
  private static final long serialVersionUID = 4946547168093391015L;
  
  private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("perms", Hashtable.class) };
  
  private transient ConcurrentHashMap<String, PermissionCollection> perms = new ConcurrentHashMap<>(7);
  
  void load(InputStream paramInputStream) throws IOException, CryptoPolicyParser.ParsingException {
    CryptoPolicyParser cryptoPolicyParser = new CryptoPolicyParser();
    cryptoPolicyParser.read(new BufferedReader(new InputStreamReader(paramInputStream, "UTF-8")));
    CryptoPermission[] arrayOfCryptoPermission = cryptoPolicyParser.getPermissions();
    for (byte b = 0; b < arrayOfCryptoPermission.length; b++)
      add(arrayOfCryptoPermission[b]); 
  }
  
  boolean isEmpty() {
    return this.perms.isEmpty();
  }
  
  public void add(Permission paramPermission) {
    if (isReadOnly())
      throw new SecurityException("Attempt to add a Permission to a readonly CryptoPermissions object"); 
    if (!(paramPermission instanceof CryptoPermission))
      return; 
    CryptoPermission cryptoPermission = (CryptoPermission)paramPermission;
    PermissionCollection permissionCollection = getPermissionCollection(cryptoPermission);
    permissionCollection.add(cryptoPermission);
    String str = cryptoPermission.getAlgorithm();
    this.perms.putIfAbsent(str, permissionCollection);
  }
  
  public boolean implies(Permission paramPermission) {
    if (!(paramPermission instanceof CryptoPermission))
      return false; 
    CryptoPermission cryptoPermission = (CryptoPermission)paramPermission;
    PermissionCollection permissionCollection = getPermissionCollection(cryptoPermission.getAlgorithm());
    return permissionCollection.implies(cryptoPermission);
  }
  
  public Enumeration<Permission> elements() {
    return new PermissionsEnumerator(this.perms.elements());
  }
  
  CryptoPermissions getMinimum(CryptoPermissions paramCryptoPermissions) {
    if (paramCryptoPermissions == null)
      return null; 
    if (this.perms.containsKey("CryptoAllPermission"))
      return paramCryptoPermissions; 
    if (paramCryptoPermissions.perms.containsKey("CryptoAllPermission"))
      return this; 
    CryptoPermissions cryptoPermissions = new CryptoPermissions();
    PermissionCollection permissionCollection1 = paramCryptoPermissions.perms.get("*");
    int i = 0;
    if (permissionCollection1 != null)
      i = ((CryptoPermission)permissionCollection1.elements().nextElement()).getMaxKeySize(); 
    Enumeration<String> enumeration1 = this.perms.keys();
    while (enumeration1.hasMoreElements()) {
      CryptoPermission[] arrayOfCryptoPermission;
      String str = enumeration1.nextElement();
      PermissionCollection permissionCollection3 = this.perms.get(str);
      PermissionCollection permissionCollection4 = paramCryptoPermissions.perms.get(str);
      if (permissionCollection4 == null) {
        if (permissionCollection1 == null)
          continue; 
        arrayOfCryptoPermission = getMinimum(i, permissionCollection3);
      } else {
        arrayOfCryptoPermission = getMinimum(permissionCollection3, permissionCollection4);
      } 
      for (byte b = 0; b < arrayOfCryptoPermission.length; b++)
        cryptoPermissions.add(arrayOfCryptoPermission[b]); 
    } 
    PermissionCollection permissionCollection2 = this.perms.get("*");
    if (permissionCollection2 == null)
      return cryptoPermissions; 
    i = ((CryptoPermission)permissionCollection2.elements().nextElement()).getMaxKeySize();
    Enumeration<String> enumeration2 = paramCryptoPermissions.perms.keys();
    while (enumeration2.hasMoreElements()) {
      String str = enumeration2.nextElement();
      if (this.perms.containsKey(str))
        continue; 
      PermissionCollection permissionCollection = paramCryptoPermissions.perms.get(str);
      CryptoPermission[] arrayOfCryptoPermission = getMinimum(i, permissionCollection);
      for (byte b = 0; b < arrayOfCryptoPermission.length; b++)
        cryptoPermissions.add(arrayOfCryptoPermission[b]); 
    } 
    return cryptoPermissions;
  }
  
  private CryptoPermission[] getMinimum(PermissionCollection paramPermissionCollection1, PermissionCollection paramPermissionCollection2) {
    Vector<CryptoPermission> vector = new Vector(2);
    Enumeration<Permission> enumeration = paramPermissionCollection1.elements();
    while (enumeration.hasMoreElements()) {
      CryptoPermission cryptoPermission = (CryptoPermission)enumeration.nextElement();
      Enumeration<Permission> enumeration1 = paramPermissionCollection2.elements();
      while (enumeration1.hasMoreElements()) {
        CryptoPermission cryptoPermission1 = (CryptoPermission)enumeration1.nextElement();
        if (cryptoPermission1.implies(cryptoPermission)) {
          vector.addElement(cryptoPermission);
          break;
        } 
        if (cryptoPermission.implies(cryptoPermission1))
          vector.addElement(cryptoPermission1); 
      } 
    } 
    CryptoPermission[] arrayOfCryptoPermission = new CryptoPermission[vector.size()];
    vector.copyInto((Object[])arrayOfCryptoPermission);
    return arrayOfCryptoPermission;
  }
  
  private CryptoPermission[] getMinimum(int paramInt, PermissionCollection paramPermissionCollection) {
    Vector<CryptoPermission> vector = new Vector(1);
    Enumeration<Permission> enumeration = paramPermissionCollection.elements();
    while (enumeration.hasMoreElements()) {
      CryptoPermission cryptoPermission = (CryptoPermission)enumeration.nextElement();
      if (cryptoPermission.getMaxKeySize() <= paramInt) {
        vector.addElement(cryptoPermission);
        continue;
      } 
      if (cryptoPermission.getCheckParam()) {
        vector.addElement(new CryptoPermission(cryptoPermission
              .getAlgorithm(), paramInt, cryptoPermission
              
              .getAlgorithmParameterSpec(), cryptoPermission
              .getExemptionMechanism()));
        continue;
      } 
      vector.addElement(new CryptoPermission(cryptoPermission
            .getAlgorithm(), paramInt, cryptoPermission
            
            .getExemptionMechanism()));
    } 
    CryptoPermission[] arrayOfCryptoPermission = new CryptoPermission[vector.size()];
    vector.copyInto((Object[])arrayOfCryptoPermission);
    return arrayOfCryptoPermission;
  }
  
  PermissionCollection getPermissionCollection(String paramString) {
    PermissionCollection permissionCollection = this.perms.get("CryptoAllPermission");
    if (permissionCollection == null) {
      permissionCollection = this.perms.get(paramString);
      if (permissionCollection == null)
        permissionCollection = this.perms.get("*"); 
    } 
    return permissionCollection;
  }
  
  private PermissionCollection getPermissionCollection(CryptoPermission paramCryptoPermission) {
    String str = paramCryptoPermission.getAlgorithm();
    PermissionCollection permissionCollection = this.perms.get(str);
    if (permissionCollection == null)
      permissionCollection = paramCryptoPermission.newPermissionCollection(); 
    return permissionCollection;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    Hashtable<? extends String, ? extends PermissionCollection> hashtable = (Hashtable)getField.get("perms", (Object)null);
    if (hashtable != null) {
      this.perms = new ConcurrentHashMap<>(hashtable);
    } else {
      this.perms = new ConcurrentHashMap<>();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Hashtable<String, PermissionCollection> hashtable = new Hashtable<>(this.perms);
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("perms", hashtable);
    paramObjectOutputStream.writeFields();
  }
}
