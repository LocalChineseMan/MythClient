package sun.security.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.security.auth.x500.X500Principal;
import sun.net.util.IPAddressUtil;
import sun.security.ssl.Krb5Helper;
import sun.security.x509.X500Name;

public class HostnameChecker {
  public static final byte TYPE_TLS = 1;
  
  private static final HostnameChecker INSTANCE_TLS = new HostnameChecker((byte)1);
  
  public static final byte TYPE_LDAP = 2;
  
  private static final HostnameChecker INSTANCE_LDAP = new HostnameChecker((byte)2);
  
  private static final int ALTNAME_DNS = 2;
  
  private static final int ALTNAME_IP = 7;
  
  private final byte checkType;
  
  private HostnameChecker(byte paramByte) {
    this.checkType = paramByte;
  }
  
  public static HostnameChecker getInstance(byte paramByte) {
    if (paramByte == 1)
      return INSTANCE_TLS; 
    if (paramByte == 2)
      return INSTANCE_LDAP; 
    throw new IllegalArgumentException("Unknown check type: " + paramByte);
  }
  
  public void match(String paramString, X509Certificate paramX509Certificate) throws CertificateException {
    if (isIpAddress(paramString)) {
      matchIP(paramString, paramX509Certificate);
    } else {
      matchDNS(paramString, paramX509Certificate);
    } 
  }
  
  public static boolean match(String paramString, Principal paramPrincipal) {
    String str = getServerName(paramPrincipal);
    return paramString.equalsIgnoreCase(str);
  }
  
  public static String getServerName(Principal paramPrincipal) {
    return Krb5Helper.getPrincipalHostName(paramPrincipal);
  }
  
  private static boolean isIpAddress(String paramString) {
    if (IPAddressUtil.isIPv4LiteralAddress(paramString) || 
      IPAddressUtil.isIPv6LiteralAddress(paramString))
      return true; 
    return false;
  }
  
  private static void matchIP(String paramString, X509Certificate paramX509Certificate) throws CertificateException {
    Collection<List<?>> collection = paramX509Certificate.getSubjectAlternativeNames();
    if (collection == null)
      throw new CertificateException("No subject alternative names present"); 
    for (List<Integer> list : collection) {
      if (((Integer)list.get(0)).intValue() == 7) {
        String str = (String)list.get(1);
        if (paramString.equalsIgnoreCase(str))
          return; 
        try {
          if (InetAddress.getByName(paramString).equals(
              InetAddress.getByName(str)))
            return; 
        } catch (UnknownHostException unknownHostException) {
        
        } catch (SecurityException securityException) {}
      } 
    } 
    throw new CertificateException("No subject alternative names matching IP address " + paramString + " found");
  }
  
  private void matchDNS(String paramString, X509Certificate paramX509Certificate) throws CertificateException {
    Collection<List<?>> collection = paramX509Certificate.getSubjectAlternativeNames();
    if (collection != null) {
      boolean bool = false;
      for (List<Integer> list : collection) {
        if (((Integer)list.get(0)).intValue() == 2) {
          bool = true;
          String str1 = (String)list.get(1);
          if (isMatched(paramString, str1))
            return; 
        } 
      } 
      if (bool)
        throw new CertificateException("No subject alternative DNS name matching " + paramString + " found."); 
    } 
    X500Name x500Name = getSubjectX500Name(paramX509Certificate);
    DerValue derValue = x500Name.findMostSpecificAttribute(X500Name.commonName_oid);
    if (derValue != null)
      try {
        if (isMatched(paramString, derValue.getAsString()))
          return; 
      } catch (IOException iOException) {} 
    String str = "No name matching " + paramString + " found";
    throw new CertificateException(str);
  }
  
  public static X500Name getSubjectX500Name(X509Certificate paramX509Certificate) throws CertificateParsingException {
    try {
      Principal principal = paramX509Certificate.getSubjectDN();
      if (principal instanceof X500Name)
        return (X500Name)principal; 
      X500Principal x500Principal = paramX509Certificate.getSubjectX500Principal();
      return new X500Name(x500Principal.getEncoded());
    } catch (IOException iOException) {
      throw (CertificateParsingException)(new CertificateParsingException())
        .initCause(iOException);
    } 
  }
  
  private boolean isMatched(String paramString1, String paramString2) {
    if (this.checkType == 1)
      return matchAllWildcards(paramString1, paramString2); 
    if (this.checkType == 2)
      return matchLeftmostWildcard(paramString1, paramString2); 
    return false;
  }
  
  private static boolean matchAllWildcards(String paramString1, String paramString2) {
    paramString1 = paramString1.toLowerCase(Locale.ENGLISH);
    paramString2 = paramString2.toLowerCase(Locale.ENGLISH);
    StringTokenizer stringTokenizer1 = new StringTokenizer(paramString1, ".");
    StringTokenizer stringTokenizer2 = new StringTokenizer(paramString2, ".");
    if (stringTokenizer1.countTokens() != stringTokenizer2.countTokens())
      return false; 
    while (stringTokenizer1.hasMoreTokens()) {
      if (!matchWildCards(stringTokenizer1.nextToken(), stringTokenizer2
          .nextToken()))
        return false; 
    } 
    return true;
  }
  
  private static boolean matchLeftmostWildcard(String paramString1, String paramString2) {
    paramString1 = paramString1.toLowerCase(Locale.ENGLISH);
    paramString2 = paramString2.toLowerCase(Locale.ENGLISH);
    int i = paramString2.indexOf(".");
    int j = paramString1.indexOf(".");
    if (i == -1)
      i = paramString2.length(); 
    if (j == -1)
      j = paramString1.length(); 
    if (matchWildCards(paramString1.substring(0, j), paramString2
        .substring(0, i)))
      return paramString2.substring(i).equals(paramString1
          .substring(j)); 
    return false;
  }
  
  private static boolean matchWildCards(String paramString1, String paramString2) {
    int i = paramString2.indexOf("*");
    if (i == -1)
      return paramString1.equals(paramString2); 
    boolean bool = true;
    String str1 = "";
    String str2 = paramString2;
    while (i != -1) {
      str1 = str2.substring(0, i);
      str2 = str2.substring(i + 1);
      int j = paramString1.indexOf(str1);
      if (j == -1 || (bool && j != 0))
        return false; 
      bool = false;
      paramString1 = paramString1.substring(j + str1.length());
      i = str2.indexOf("*");
    } 
    return paramString1.endsWith(str2);
  }
}
