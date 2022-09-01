package sun.security.ssl;

import java.net.Socket;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.security.auth.x500.X500Principal;

final class SunX509KeyManagerImpl extends X509ExtendedKeyManager {
  private static final Debug debug = Debug.getInstance("ssl");
  
  private static final String[] STRING0 = new String[0];
  
  private Map<String, X509Credentials> credentialsMap = new HashMap<>();
  
  private final Map<String, String[]> serverAliasCache = (Map)Collections.synchronizedMap(new HashMap<>());
  
  SunX509KeyManagerImpl(KeyStore paramKeyStore, char[] paramArrayOfchar) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
    if (paramKeyStore == null)
      return; 
    Enumeration<String> enumeration = paramKeyStore.aliases();
    while (enumeration.hasMoreElements()) {
      X509Certificate[] arrayOfX509Certificate;
      String str = enumeration.nextElement();
      if (!paramKeyStore.isKeyEntry(str))
        continue; 
      Key key = paramKeyStore.getKey(str, paramArrayOfchar);
      if (!(key instanceof PrivateKey))
        continue; 
      Certificate[] arrayOfCertificate = paramKeyStore.getCertificateChain(str);
      if (arrayOfCertificate == null || arrayOfCertificate.length == 0 || !(arrayOfCertificate[0] instanceof X509Certificate))
        continue; 
      if (!(arrayOfCertificate instanceof X509Certificate[])) {
        X509Certificate[] arrayOfX509Certificate1 = new X509Certificate[arrayOfCertificate.length];
        System.arraycopy(arrayOfCertificate, 0, arrayOfX509Certificate1, 0, arrayOfCertificate.length);
        arrayOfX509Certificate = arrayOfX509Certificate1;
      } 
      X509Credentials x509Credentials = new X509Credentials((PrivateKey)key, arrayOfX509Certificate);
      this.credentialsMap.put(str, x509Credentials);
      if (debug != null && Debug.isOn("keymanager")) {
        System.out.println("***");
        System.out.println("found key for : " + str);
        for (byte b = 0; b < arrayOfX509Certificate.length; b++)
          System.out.println("chain [" + b + "] = " + arrayOfX509Certificate[b]); 
        System.out.println("***");
      } 
    } 
  }
  
  public X509Certificate[] getCertificateChain(String paramString) {
    if (paramString == null)
      return null; 
    X509Credentials x509Credentials = this.credentialsMap.get(paramString);
    if (x509Credentials == null)
      return null; 
    return (X509Certificate[])x509Credentials.certificates.clone();
  }
  
  public PrivateKey getPrivateKey(String paramString) {
    if (paramString == null)
      return null; 
    X509Credentials x509Credentials = this.credentialsMap.get(paramString);
    if (x509Credentials == null)
      return null; 
    return x509Credentials.privateKey;
  }
  
  public String chooseClientAlias(String[] paramArrayOfString, Principal[] paramArrayOfPrincipal, Socket paramSocket) {
    if (paramArrayOfString == null)
      return null; 
    for (byte b = 0; b < paramArrayOfString.length; b++) {
      String[] arrayOfString = getClientAliases(paramArrayOfString[b], paramArrayOfPrincipal);
      if (arrayOfString != null && arrayOfString.length > 0)
        return arrayOfString[0]; 
    } 
    return null;
  }
  
  public String chooseEngineClientAlias(String[] paramArrayOfString, Principal[] paramArrayOfPrincipal, SSLEngine paramSSLEngine) {
    return chooseClientAlias(paramArrayOfString, paramArrayOfPrincipal, null);
  }
  
  public String chooseServerAlias(String paramString, Principal[] paramArrayOfPrincipal, Socket paramSocket) {
    String[] arrayOfString;
    if (paramString == null)
      return null; 
    if (paramArrayOfPrincipal == null || paramArrayOfPrincipal.length == 0) {
      arrayOfString = this.serverAliasCache.get(paramString);
      if (arrayOfString == null) {
        arrayOfString = getServerAliases(paramString, paramArrayOfPrincipal);
        if (arrayOfString == null)
          arrayOfString = STRING0; 
        this.serverAliasCache.put(paramString, arrayOfString);
      } 
    } else {
      arrayOfString = getServerAliases(paramString, paramArrayOfPrincipal);
    } 
    if (arrayOfString != null && arrayOfString.length > 0)
      return arrayOfString[0]; 
    return null;
  }
  
  public String chooseEngineServerAlias(String paramString, Principal[] paramArrayOfPrincipal, SSLEngine paramSSLEngine) {
    return chooseServerAlias(paramString, paramArrayOfPrincipal, null);
  }
  
  public String[] getClientAliases(String paramString, Principal[] paramArrayOfPrincipal) {
    return getAliases(paramString, paramArrayOfPrincipal);
  }
  
  public String[] getServerAliases(String paramString, Principal[] paramArrayOfPrincipal) {
    return getAliases(paramString, paramArrayOfPrincipal);
  }
  
  private String[] getAliases(String paramString, Principal[] paramArrayOfPrincipal) {
    X500Principal[] arrayOfX500Principal1;
    String str;
    if (paramString == null)
      return null; 
    if (paramArrayOfPrincipal == null)
      arrayOfX500Principal1 = new X500Principal[0]; 
    if (!(arrayOfX500Principal1 instanceof X500Principal[]))
      arrayOfX500Principal1 = convertPrincipals((Principal[])arrayOfX500Principal1); 
    if (paramString.contains("_")) {
      int i = paramString.indexOf("_");
      str = paramString.substring(i + 1);
      paramString = paramString.substring(0, i);
    } else {
      str = null;
    } 
    X500Principal[] arrayOfX500Principal2 = arrayOfX500Principal1;
    ArrayList<String> arrayList = new ArrayList();
    for (Map.Entry<String, X509Credentials> entry : this.credentialsMap.entrySet()) {
      String str1 = (String)entry.getKey();
      X509Credentials x509Credentials = (X509Credentials)entry.getValue();
      X509Certificate[] arrayOfX509Certificate = x509Credentials.certificates;
      if (!paramString.equals(arrayOfX509Certificate[0].getPublicKey().getAlgorithm()))
        continue; 
      if (str != null)
        if (arrayOfX509Certificate.length > 1) {
          if (!str.equals(arrayOfX509Certificate[1]
              .getPublicKey().getAlgorithm()))
            continue; 
        } else {
          String str2 = arrayOfX509Certificate[0].getSigAlgName().toUpperCase(Locale.ENGLISH);
          String str3 = "WITH" + str.toUpperCase(Locale.ENGLISH);
          if (!str2.contains(str3))
            continue; 
        }  
      if (arrayOfX500Principal1.length == 0) {
        arrayList.add(str1);
        if (debug != null && Debug.isOn("keymanager"))
          System.out.println("matching alias: " + str1); 
        continue;
      } 
      Set<X500Principal> set = x509Credentials.getIssuerX500Principals();
      for (byte b = 0; b < arrayOfX500Principal2.length; b++) {
        if (set.contains(arrayOfX500Principal1[b])) {
          arrayList.add(str1);
          if (debug != null && Debug.isOn("keymanager"))
            System.out.println("matching alias: " + str1); 
          break;
        } 
      } 
    } 
    String[] arrayOfString = arrayList.<String>toArray(STRING0);
    return (arrayOfString.length == 0) ? null : arrayOfString;
  }
  
  private static X500Principal[] convertPrincipals(Principal[] paramArrayOfPrincipal) {
    ArrayList<X500Principal> arrayList = new ArrayList(paramArrayOfPrincipal.length);
    for (byte b = 0; b < paramArrayOfPrincipal.length; b++) {
      Principal principal = paramArrayOfPrincipal[b];
      if (principal instanceof X500Principal) {
        arrayList.add((X500Principal)principal);
      } else {
        try {
          arrayList.add(new X500Principal(principal.getName()));
        } catch (IllegalArgumentException illegalArgumentException) {}
      } 
    } 
    return arrayList.<X500Principal>toArray(new X500Principal[arrayList.size()]);
  }
  
  private static class SunX509KeyManagerImpl {}
}
