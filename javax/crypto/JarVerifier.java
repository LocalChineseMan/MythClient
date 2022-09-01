package javax.crypto;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import sun.security.validator.Validator;

final class JarVerifier {
  private static final String PLUGIN_IMPL_NAME = "sun.plugin.net.protocol.jar.CachedJarURLConnection";
  
  private static X509Certificate frameworkCertificate;
  
  private static Validator providerValidator;
  
  private static Validator exemptValidator;
  
  static {
    try {
      AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws Exception {
              CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
              JarVerifier.frameworkCertificate = JarVerifier.parseCertificate("-----BEGIN CERTIFICATE-----\nMIICoTCCAl+gAwIBAgICAzkwCwYHKoZIzjgEAwUAMIGQMQswCQYDVQQGEwJVUzELMAkGA1UECBMCQ0ExEjAQBgNVBAcTCVBhbG8gQWx0bzEdMBsGA1UEChMUU3VuIE1pY3Jvc3lzdGVtcyBJbmMxIzAhBgNVBAsTGkphdmEgU29mdHdhcmUgQ29kZSBTaWduaW5nMRwwGgYDVQQDExNKQ0UgQ29kZSBTaWduaW5nIENBMB4XDTExMDQxMTA2MDA0M1oXDTE2MDQxNDA2MDA0M1owYTEdMBsGA1UEChMUU3VuIE1pY3Jvc3lzdGVtcyBJbmMxIzAhBgNVBAsTGkphdmEgU29mdHdhcmUgQ29kZSBTaWduaW5nMRswGQYDVQQDExJPcmFjbGUgQ29ycG9yYXRpb24wgZ8wDQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBALR6pnmTTdvYtjj0EH7nQTa52aHuWTsxIgX+sVzy5MyYcGZJk23QI623tCNLk1MgPf0ntUKe/HZjuvdrBIfgBcu2C+Htw0PwmQyjHQToAMUt5CfWpGLmBh0LVblnOb9mcOp/Ety4myc9V8c3LSVXpgvNgIUhu8Vv3IEM966NKtmLAgMBAAGjgY4wgYswEQYJYIZIAYb4QgEBBAQDAgQQMA4GA1UdDwEB/wQEAwIF4DAdBgNVHQ4EFgQU5YHrhAD3Wo9gQZEycFmm7NAgzUUwHwYDVR0jBBgwFoAUZeL0hsnTTvCRTliiavXYeFqawaYwJgYDVR0RBB8wHYEbYnJhZGZvcmQud2V0bW9yZUBvcmFjbGUuY29tMAsGByqGSM44BAMFAAMvADAsAhRVoQglrJDMgxGzsGFS7oHMbzLioQIUSps7E1B/RSMh6ooea/mGwKX4iVc=\n-----END CERTIFICATE-----", certificateFactory);
              X509Certificate[] arrayOfX509Certificate = { JarVerifier.parseCertificate("-----BEGIN CERTIFICATE-----\nMIIDwDCCA36gAwIBAgIBEDALBgcqhkjOOAQDBQAwgZAxCzAJBgNVBAYTAlVTMQswCQYDVQQIEwJDQTESMBAGA1UEBxMJUGFsbyBBbHRvMR0wGwYDVQQKExRTdW4gTWljcm9zeXN0ZW1zIEluYzEjMCEGA1UECxMaSmF2YSBTb2Z0d2FyZSBDb2RlIFNpZ25pbmcxHDAaBgNVBAMTE0pDRSBDb2RlIFNpZ25pbmcgQ0EwHhcNMDEwNDI1MDcwMDAwWhcNMjAwNDI1MDcwMDAwWjCBkDELMAkGA1UEBhMCVVMxCzAJBgNVBAgTAkNBMRIwEAYDVQQHEwlQYWxvIEFsdG8xHTAbBgNVBAoTFFN1biBNaWNyb3N5c3RlbXMgSW5jMSMwIQYDVQQLExpKYXZhIFNvZnR3YXJlIENvZGUgU2lnbmluZzEcMBoGA1UEAxMTSkNFIENvZGUgU2lnbmluZyBDQTCCAbcwggEsBgcqhkjOOAQBMIIBHwKBgQDrrzcEHspRHmldsPKP9rVJH8akmQXXKb90t2r1Gdge5Bv4CgGamP9wq+JKVoZsU7P84ciBjDHwxPOwi+ZwBuz3aWjbg0xyKYkpNhdcO0oHoCACKkaXUR1wyAgYC84Mbpt29wXj5/vTYXnhYJokjQaVgzxRIOEwzzhXgqYacg3O0wIVAIQlReG6ualiq3noWzC4iWsb/3t1AoGBAKvJdHt07+5CtWpTTTvdkAZyaJEPC6Qpdi5VO9WuTWVcfio6BKZnptBxqqXXt+LBcg2k0aoeklRMIAAJorAJQRkzALLDXK5C+LGLynyW2BB/N0Rbqsx4yNdydjdrQJmoVWb6qAMei0oRAmnLTLglBhygd9LJrNI96QoQ+nZwt/vcA4GEAAKBgC0JmFysuJzHmX7uIBkqNJD516urrt1rcpUNZvjvJ49Esu0oRMf+r7CmJ28AZ0WCWweoVlY70ilRYV5pOdcudHcSzxlK9S3Iy3JhxE5v+kdDPxS7+rwYZijC2WaLei0vwmCSSxT+WD4hf2hivmxISfmgS16FnRkQ+RVFURtx1PcLo2YwZDARBglghkgBhvhCAQEEBAMCAAcwDwYDVR0TAQH/BAUwAwEB/zAfBgNVHSMEGDAWgBRl4vSGydNO8JFOWKJq9dh4WprBpjAdBgNVHQ4EFgQUZeL0hsnTTvCRTliiavXYeFqawaYwCwYHKoZIzjgEAwUAAy8AMCwCFCr3zzyXXfl4tgjXQbTZDUVM5LScAhRFzXVpDiH6HdazKbLp9zMdM/38SQ==\n-----END CERTIFICATE-----", certificateFactory), JarVerifier.parseCertificate("-----BEGIN CERTIFICATE-----\nMIIDUTCCAw2gAwIBAgIEQCFoETALBgcqhkjOOAQDBQAwYDELMAkGA1UEBhMCVVMxGDAWBgNVBAoTD0lCTSBDb3Jwb3JhdGlvbjEZMBcGA1UECxMQSUJNIENvZGUgU2lnbmluZzEcMBoGA1UEAxMTSkNFIENvZGUgU2lnbmluZyBDQTAeFw0wNDAyMDQyMTQ1NTNaFw0yMDA1MjYyMDQ1NTNaMGAxCzAJBgNVBAYTAlVTMRgwFgYDVQQKEw9JQk0gQ29ycG9yYXRpb24xGTAXBgNVBAsTEElCTSBDb2RlIFNpZ25pbmcxHDAaBgNVBAMTE0pDRSBDb2RlIFNpZ25pbmcgQ0EwggG4MIIBLAYHKoZIzjgEATCCAR8CgYEA/X9TgR11EilS30qcLuzk5/YRt1I870QAwx4/gLZRJmlFXUAiUftZPY1Y+r/F9bow9subVWzXgTuAHTRv8mZgt2uZUKWkn5/oBHsQIsJPu6nX/rfGG/g7V+fGqKYVDwT7g/bTxR7DAjVUE1oWkTL2dfOuK2HXKu/yIgMZndFIAccCFQCXYFCPFSMLzLKSuYKi64QL8Fgc9QKBgQD34aCF1ps93su8q1w2uFe5eZSvu/o66oL5V0wLPQeCZ1FZV4661FlP5nEHEIGAtEkWcSPoTCgWE7fPCTKMyKbhPBZ6i1R8jSjgo64eK7OmdZFuo38L+iE1YvH7YnoBJDvMpPG+qFGQiaiD3+Fa5Z8GkotmXoB7VSVkAUw7/s9JKgOBhQACgYEA6msAx98QO7l0NafhbWaCTfdbVnHCJkUncj1REGL/s9wQyftRE9Sti6glbl3JeNJbJ9MTQUcUBnzLgjhexgthoEyDLZTMjC6EkDqPQgppUtN0JnekFH0qcUGIiXemLWKaoViYbWzPzqjqut3ooRBEjIRCwbgfK7S8s110YICNQlSjUzBRMB0GA1UdDgQWBBR+PU1NzBBZuvmuQj3lyVdaUgt+hzAfBgNVHSMEGDAWgBR+PU1NzBBZuvmuQj3lyVdaUgt+hzAPBgNVHRMBAf8EBTADAQH/MAsGByqGSM44BAMFAAMxADAuAhUAi5ncRzk0NqFYt4yWsnlcVBPt+zsCFQCM9M0mv0t9iodsOOHJhqUrW1QjAA==\n-----END CERTIFICATE-----", certificateFactory), JarVerifier.frameworkCertificate };
              JarVerifier.providerValidator = Validator.getInstance("Simple", "jce signing", 
                  
                  Arrays.asList(arrayOfX509Certificate));
              JarVerifier.exemptValidator = JarVerifier.providerValidator;
              JarVerifier.testSignatures(arrayOfX509Certificate[0], certificateFactory);
              return null;
            }
          });
    } catch (Exception exception) {
      SecurityException securityException = new SecurityException("Framework jar verification can not be initialized");
      securityException.initCause(exception);
      throw securityException;
    } 
  }
  
  private static X509Certificate parseCertificate(String paramString, CertificateFactory paramCertificateFactory) throws Exception {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramString.getBytes("UTF8"));
    return (X509Certificate)paramCertificateFactory.generateCertificate(byteArrayInputStream);
  }
  
  private static class JarHolder {
    JarFile file;
    
    boolean useCaches;
    
    JarHolder(JarFile param1JarFile, boolean param1Boolean) {
      this.file = param1JarFile;
      this.useCaches = param1Boolean;
    }
  }
  
  private Vector<X509Certificate> verifiedSignerCache = null;
  
  private URL jarURL;
  
  private Validator validator;
  
  private boolean savePerms;
  
  private CryptoPermissions appPerms = null;
  
  JarVerifier(URL paramURL, boolean paramBoolean) {
    this.jarURL = paramURL;
    this.savePerms = paramBoolean;
    this.validator = paramBoolean ? providerValidator : exemptValidator;
    this.verifiedSignerCache = new Vector<>(2);
  }
  
  void verify() throws JarException, IOException {
    if (this.jarURL == null)
      throw new JarException("Class is on the bootclasspath"); 
    try {
      verifyJars(this.jarURL, null);
    } catch (NoSuchProviderException noSuchProviderException) {
      throw new JarException("Cannot verify " + this.jarURL.toString());
    } catch (CertificateException certificateException) {
      throw new JarException("Cannot verify " + this.jarURL.toString());
    } catch (ParsingException parsingException) {
      throw new JarException("Cannot parse " + this.jarURL.toString());
    } finally {
      if (this.verifiedSignerCache != null)
        this.verifiedSignerCache.clear(); 
    } 
  }
  
  static void verifyPolicySigned(Certificate[] paramArrayOfCertificate) throws Exception {
    List<X509Certificate[]> list = convertCertsToChains(paramArrayOfCertificate);
    boolean bool = false;
    for (X509Certificate[] arrayOfX509Certificate : list) {
      X509Certificate x509Certificate = arrayOfX509Certificate[0];
      if (x509Certificate.equals(frameworkCertificate)) {
        bool = true;
        break;
      } 
    } 
    if (!bool)
      throw new SecurityException("The jurisdiction policy files are not signed by a trusted signer!"); 
  }
  
  CryptoPermissions getPermissions() {
    return this.appPerms;
  }
  
  private void verifyJars(URL paramURL, Vector<String> paramVector) throws NoSuchProviderException, CertificateException, IOException, CryptoPolicyParser.ParsingException {
    String str = paramURL.toString();
    if (paramVector == null || 
      !paramVector.contains(str)) {
      String str1 = verifySingleJar(paramURL);
      if (paramVector != null)
        paramVector.addElement(str); 
      if (str1 != null) {
        if (paramVector == null) {
          paramVector = new Vector<>();
          paramVector.addElement(str);
        } 
        verifyManifestClassPathJars(paramURL, str1, paramVector);
      } 
    } 
  }
  
  private void verifyManifestClassPathJars(URL paramURL, String paramString, Vector<String> paramVector) throws NoSuchProviderException, CertificateException, IOException, CryptoPolicyParser.ParsingException {
    String[] arrayOfString = parseAttrClasspath(paramString);
    try {
      for (byte b = 0; b < arrayOfString.length; b++) {
        URL uRL = new URL(paramURL, arrayOfString[b]);
        verifyJars(uRL, paramVector);
      } 
    } catch (MalformedURLException malformedURLException1) {
      MalformedURLException malformedURLException2 = new MalformedURLException("The JAR file " + paramURL.toString() + " contains invalid URLs in its Class-Path attribute");
      malformedURLException2.initCause(malformedURLException1);
      throw malformedURLException2;
    } 
  }
  
  private String verifySingleJar(URL paramURL) throws NoSuchProviderException, CertificateException, IOException, CryptoPolicyParser.ParsingException {
    final URL url = paramURL.getProtocol().equalsIgnoreCase("jar") ? paramURL : new URL("jar:" + paramURL.toString() + "!/");
    JarHolder jarHolder = null;
    try {
      try {
        jarHolder = AccessController.<JarHolder>doPrivileged(new PrivilegedExceptionAction<JarHolder>() {
              public JarVerifier.JarHolder run() throws Exception {
                boolean bool = false;
                JarURLConnection jarURLConnection = (JarURLConnection)url.openConnection();
                if (jarURLConnection.getClass().getName()
                  .equals("sun.plugin.net.protocol.jar.CachedJarURLConnection"))
                  bool = true; 
                jarURLConnection.setUseCaches(bool);
                JarFile jarFile = jarURLConnection.getJarFile();
                if (jarFile != null)
                  return new JarVerifier.JarHolder(jarFile, bool); 
                return null;
              }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        SecurityException securityException = new SecurityException("Cannot verify " + uRL.toString());
        securityException.initCause(privilegedActionException);
        throw securityException;
      } 
      if (jarHolder != null) {
        JarFile jarFile = jarHolder.file;
        byte[] arrayOfByte = new byte[8192];
        Enumeration<JarEntry> enumeration1 = jarFile.entries();
        while (enumeration1.hasMoreElements()) {
          JarEntry jarEntry = enumeration1.nextElement();
          BufferedInputStream bufferedInputStream = new BufferedInputStream(jarFile.getInputStream(jarEntry));
          try {
            while (bufferedInputStream.read(arrayOfByte, 0, arrayOfByte.length) != -1);
          } finally {
            bufferedInputStream.close();
          } 
        } 
        Manifest manifest = jarFile.getManifest();
        if (manifest == null)
          throw new JarException(paramURL
              .toString() + " is not signed."); 
        Enumeration<JarEntry> enumeration2 = jarFile.entries();
        while (enumeration2.hasMoreElements()) {
          JarEntry jarEntry = enumeration2.nextElement();
          if (jarEntry.isDirectory())
            continue; 
          Certificate[] arrayOfCertificate = jarEntry.getCertificates();
          if (arrayOfCertificate == null || arrayOfCertificate.length == 0) {
            if (!jarEntry.getName().startsWith("META-INF"))
              throw new JarException(paramURL.toString() + " has unsigned entries - " + jarEntry
                  
                  .getName()); 
            continue;
          } 
          int i = 0;
          boolean bool = false;
          X509Certificate[] arrayOfX509Certificate;
          while ((arrayOfX509Certificate = getAChain(arrayOfCertificate, i)) != null) {
            if (this.verifiedSignerCache.contains(arrayOfX509Certificate[0])) {
              bool = true;
              break;
            } 
            if (isTrusted(arrayOfX509Certificate)) {
              bool = true;
              this.verifiedSignerCache.addElement(arrayOfX509Certificate[0]);
              break;
            } 
            i += arrayOfX509Certificate.length;
          } 
          if (!bool)
            throw new JarException(paramURL.toString() + " is not signed by a" + " trusted signer."); 
        } 
        if (this.jarURL.equals(paramURL) && this.savePerms) {
          JarEntry jarEntry = jarHolder.file.getJarEntry("cryptoPerms");
          if (jarEntry == null)
            throw new JarException("Can not find cryptoPerms"); 
          this.appPerms = new CryptoPermissions();
          this.appPerms.load(jarHolder.file.getInputStream(jarEntry));
        } 
        return manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH);
      } 
      return null;
    } finally {
      if (jarHolder != null && !jarHolder.useCaches)
        jarHolder.file.close(); 
    } 
  }
  
  private static String[] parseAttrClasspath(String paramString) throws JarException {
    paramString = paramString.trim();
    int i = paramString.indexOf(' ');
    String str = null;
    Vector<String> vector = new Vector();
    boolean bool = false;
    do {
      if (i > 0) {
        str = paramString.substring(0, i);
        paramString = paramString.substring(i + 1).trim();
        i = paramString.indexOf(' ');
      } else {
        str = paramString;
        bool = true;
      } 
      if (str.endsWith(".jar")) {
        vector.addElement(str);
      } else {
        throw new JarException("The provider contains un-verifiable components");
      } 
    } while (!bool);
    return vector.<String>toArray(new String[0]);
  }
  
  private boolean isTrusted(X509Certificate[] paramArrayOfX509Certificate) {
    try {
      this.validator.validate(paramArrayOfX509Certificate);
      return true;
    } catch (CertificateException certificateException) {
      return false;
    } 
  }
  
  private static X509Certificate[] getAChain(Certificate[] paramArrayOfCertificate, int paramInt) {
    if (paramInt > paramArrayOfCertificate.length - 1)
      return null; 
    int i;
    for (i = paramInt; i < paramArrayOfCertificate.length - 1 && (
      (X509Certificate)paramArrayOfCertificate[i + 1]).getSubjectDN().equals(((X509Certificate)paramArrayOfCertificate[i])
        .getIssuerDN()); i++);
    int j = i - paramInt + 1;
    X509Certificate[] arrayOfX509Certificate = new X509Certificate[j];
    for (byte b = 0; b < j; b++)
      arrayOfX509Certificate[b] = (X509Certificate)paramArrayOfCertificate[paramInt + b]; 
    return arrayOfX509Certificate;
  }
  
  private static List<X509Certificate[]> convertCertsToChains(Certificate[] paramArrayOfCertificate) throws CertificateException {
    if (paramArrayOfCertificate == null)
      return (List)Collections.emptyList(); 
    ArrayList<X509Certificate[]> arrayList = new ArrayList();
    X509Certificate[] arrayOfX509Certificate = null;
    int i = 0;
    while ((arrayOfX509Certificate = getAChain(paramArrayOfCertificate, i)) != null) {
      arrayList.add(arrayOfX509Certificate);
      i += arrayOfX509Certificate.length;
    } 
    return (List<X509Certificate[]>)arrayList;
  }
  
  private static void testSignatures(X509Certificate paramX509Certificate, CertificateFactory paramCertificateFactory) throws Exception {
    String str = "-----BEGIN CERTIFICATE-----\nMIIDLDCCAukCBDf5OeUwCwYHKoZIzjgEAwUAMHsxCzAJBgNVBAYTAlVTMQswCQYDVQQIEwJDQTESMBAGA1UEBxMJQ3VwZXJ0aW5vMRkwFwYDVQQKExBTdW4gTWljcm9zeXN0ZW1zMRYwFAYDVQQLEw1KYXZhIFNvZnR3YXJlMRgwFgYDVQQDEw9KQ0UgRGV2ZWxvcG1lbnQwHhcNOTkxMDA0MjMzNjA1WhcNMDAxMDAzMjMzNjA1WjB7MQswCQYDVQQGEwJVUzELMAkGA1UECBMCQ0ExEjAQBgNVBAcTCUN1cGVydGlubzEZMBcGA1UEChMQU3VuIE1pY3Jvc3lzdGVtczEWMBQGA1UECxMNSmF2YSBTb2Z0d2FyZTEYMBYGA1UEAxMPSkNFIERldmVsb3BtZW50MIIBuDCCASwGByqGSM44BAEwggEfAoGBAP1/U4EddRIpUt9KnC7s5Of2EbdSPO9EAMMeP4C2USZpRV1AIlH7WT2NWPq/xfW6MPbLm1Vs14E7gB00b/JmYLdrmVClpJ+f6AR7ECLCT7up1/63xhv4O1fnxqimFQ8E+4P208UewwI1VBNaFpEy9nXzrith1yrv8iIDGZ3RSAHHAhUAl2BQjxUjC8yykrmCouuEC/BYHPUCgYEA9+GghdabPd7LvKtcNrhXuXmUr7v6OuqC+VdMCz0HgmdRWVeOutRZT+ZxBxCBgLRJFnEj6EwoFhO3zwkyjMim4TwWeotUfI0o4KOuHiuzpnWRbqN/C/ohNWLx+2J6ASQ7zKTxvqhRkImog9/hWuWfBpKLZl6Ae1UlZAFMO/7PSSoDgYUAAoGBAOGsR8waR5aiuOk1yBLemRlVCY+APJv3xqmPRxWAF6nwV2xrFUB8ghSEMFcHywoe4vBDvkGSoAFzeB5jy5wjDiFsN5AFPEVRfveS4NNZ1dgRdHbbh3h5O1dZE4MAKQwQfUoh9Oa3aahlB+orRzKOHLlGDpbNRQLST5BClvohramCMAsGByqGSM44BAMFAAMwADAtAhRF46T3nS+inP9TA1pLd3LIV0NNDQIVAIafi+1/+JKxu0rcoXWMFSxNaRb3\n-----END CERTIFICATE-----";
    byte[] arrayOfByte = getSystemEntropy();
    int i = arrayOfByte[0] & 0xFF | (arrayOfByte[1] & 0xFF) << 8 | (arrayOfByte[2] & 0xFF) << 16 | arrayOfByte[3] << 24;
    X509Certificate[] arrayOfX509Certificate = { paramX509Certificate, parseCertificate(str, paramCertificateFactory), parseCertificate("-----BEGIN CERTIFICATE-----\nMIIB4DCCAYoCAQEwDQYJKoZIhvcNAQEEBQAwezELMAkGA1UEBhMCVVMxCzAJBgNVBAgTAkNBMRIwEAYDVQQHEwlDdXBlcnRpbm8xGTAXBgNVBAoTEFN1biBNaWNyb3N5c3RlbXMxFjAUBgNVBAsTDUphdmEgU29mdHdhcmUxGDAWBgNVBAMTD0pDRSBEZXZlbG9wbWVudDAeFw0wMjEwMzExNTI3NDRaFw0wNzEwMzExNTI3NDRaMHsxCzAJBgNVBAYTAlVTMQswCQYDVQQIEwJDQTESMBAGA1UEBxMJQ3VwZXJ0aW5vMRkwFwYDVQQKExBTdW4gTWljcm9zeXN0ZW1zMRYwFAYDVQQLEw1KYXZhIFNvZnR3YXJlMRgwFgYDVQQDEw9KQ0UgRGV2ZWxvcG1lbnQwXDANBgkqhkiG9w0BAQEFAANLADBIAkEAo/4CddEOa3M6v9JFAhnBYgTq54Y30++F8yzCK9EeYaG3AzvzZqNshDy579647p0cOM/4VO6rU2PgbzgKXPcs8wIDAQABMA0GCSqGSIb3DQEBBAUAA0EACqPlFmVdKdYSCTNltXKQnBqss9GNjbnB+CitvWrwN+oOK8qQpvV+5LB6LruvRy6zCedCV95Z2kXKg/Fnj0gvsg==\n-----END CERTIFICATE-----", paramCertificateFactory), parseCertificate("-----BEGIN CERTIFICATE-----\nMIIB4DCCAYoCAQIwDQYJKoZIhvcNAQEEBQAwezELMAkGA1UEBhMCVVMxCzAJBgNVBAgTAkNBMRIwEAYDVQQHEwlDdXBlcnRpbm8xGTAXBgNVBAoTEFN1biBNaWNyb3N5c3RlbXMxFjAUBgNVBAsTDUphdmEgU29mdHdhcmUxGDAWBgNVBAMTD0pDRSBEZXZlbG9wbWVudDAeFw0wMjEwMzExNTI3NDRaFw0wNzEwMzExNTI3NDRaMHsxCzAJBgNVBAYTAlVTMQswCQYDVQQIEwJDQTESMBAGA1UEBxMJQ3VwZXJ0aW5vMRkwFwYDVQQKExBTdW4gTWljcm9zeXN0ZW1zMRYwFAYDVQQLEw1KYXZhIFNvZnR3YXJlMRgwFgYDVQQDEw9KQ0UgRGV2ZWxvcG1lbnQwXDANBgkqhkiG9w0BAQEFAANLADBIAkEAr1OSXaOzpnVoqL2LqS5+HLy1kVvBwiM/E5iYT9eZaghE8qvF+4fETipWUNTWCQzHR4cDJGJOl9Nm77tELhES4QIDAQABMA0GCSqGSIb3DQEBBAUAA0EAL+WcVFyj+iXlEVNVQbNOOUlWmlmXGiNKKXnIdNcc1ZUyi+JW0zmlfZ7iU/eRYhEEJBwdrUoyiGOGLo7pi6JzAA==\n-----END CERTIFICATE-----", paramCertificateFactory) };
    PublicKey[] arrayOfPublicKey = new PublicKey[4];
    arrayOfPublicKey[0] = paramX509Certificate.getPublicKey();
    arrayOfPublicKey[1] = arrayOfPublicKey[0];
    arrayOfPublicKey[2] = arrayOfX509Certificate[2].getPublicKey();
    arrayOfPublicKey[3] = arrayOfPublicKey[2];
    boolean[] arrayOfBoolean = { true, false, true, false };
    for (byte b = 0; b < 12; b++) {
      boolean bool;
      int j = i & 0x3;
      i >>= 2;
      try {
        arrayOfX509Certificate[j].verify(arrayOfPublicKey[j]);
        bool = true;
      } catch (SignatureException signatureException) {
        bool = false;
      } catch (InvalidKeyException invalidKeyException) {
        bool = false;
      } 
      if (bool != arrayOfBoolean[j])
        throw new SecurityException("Signature classes have been tampered with"); 
    } 
  }
  
  private static byte[] getSystemEntropy() {
    MessageDigest messageDigest;
    try {
      messageDigest = MessageDigest.getInstance("SHA");
    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
      throw new InternalError("internal error: SHA-1 not available.");
    } 
    byte b = (byte)(int)System.currentTimeMillis();
    messageDigest.update(b);
    try {
      Properties properties = System.getProperties();
      for (String str : properties.stringPropertyNames()) {
        messageDigest.update(str.getBytes());
        messageDigest.update(properties.getProperty(str).getBytes());
      } 
      messageDigest
        .update(InetAddress.getLocalHost().toString().getBytes());
      File file = new File(properties.getProperty("java.io.tmpdir"));
      String[] arrayOfString = file.list();
      for (byte b1 = 0; b1 < arrayOfString.length; b1++)
        messageDigest.update(arrayOfString[b1].getBytes()); 
    } catch (Exception exception) {
      messageDigest.update((byte)exception.hashCode());
    } 
    Runtime runtime = Runtime.getRuntime();
    byte[] arrayOfByte = longToByteArray(runtime.totalMemory());
    messageDigest.update(arrayOfByte, 0, arrayOfByte.length);
    arrayOfByte = longToByteArray(runtime.freeMemory());
    messageDigest.update(arrayOfByte, 0, arrayOfByte.length);
    return messageDigest.digest();
  }
  
  private static byte[] longToByteArray(long paramLong) {
    byte[] arrayOfByte = new byte[8];
    for (byte b = 0; b < 8; b++) {
      arrayOfByte[b] = (byte)(int)paramLong;
      paramLong >>= 8L;
    } 
    return arrayOfByte;
  }
}
