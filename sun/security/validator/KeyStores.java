package sun.security.validator;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class KeyStores {
  public static Set<X509Certificate> getTrustedCerts(KeyStore paramKeyStore) {
    HashSet<X509Certificate> hashSet = new HashSet();
    try {
      for (Enumeration<String> enumeration = paramKeyStore.aliases(); enumeration.hasMoreElements(); ) {
        String str = enumeration.nextElement();
        if (paramKeyStore.isCertificateEntry(str)) {
          Certificate certificate = paramKeyStore.getCertificate(str);
          if (certificate instanceof X509Certificate)
            hashSet.add((X509Certificate)certificate); 
          continue;
        } 
        if (paramKeyStore.isKeyEntry(str)) {
          Certificate[] arrayOfCertificate = paramKeyStore.getCertificateChain(str);
          if (arrayOfCertificate != null && arrayOfCertificate.length > 0 && arrayOfCertificate[0] instanceof X509Certificate)
            hashSet.add((X509Certificate)arrayOfCertificate[0]); 
        } 
      } 
    } catch (KeyStoreException keyStoreException) {}
    return hashSet;
  }
}
