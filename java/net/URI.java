package java.net;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.text.Normalizer;
import sun.nio.cs.ThreadLocalCoders;

public final class URI implements Comparable<URI>, Serializable {
  static final long serialVersionUID = -6052424284110960213L;
  
  private transient String scheme;
  
  private transient String fragment;
  
  private transient String authority;
  
  private transient String userInfo;
  
  private transient String host;
  
  private transient int port = -1;
  
  private transient String path;
  
  private transient String query;
  
  private volatile transient String schemeSpecificPart;
  
  private volatile transient int hash;
  
  private volatile transient String decodedUserInfo = null;
  
  private volatile transient String decodedAuthority = null;
  
  private volatile transient String decodedPath = null;
  
  private volatile transient String decodedQuery = null;
  
  private volatile transient String decodedFragment = null;
  
  private volatile transient String decodedSchemeSpecificPart = null;
  
  private volatile String string;
  
  public URI(String paramString) throws URISyntaxException {
    (new Parser(paramString)).parse(false);
  }
  
  public URI(String paramString1, String paramString2, String paramString3, int paramInt, String paramString4, String paramString5, String paramString6) throws URISyntaxException {
    String str = toString(paramString1, null, null, paramString2, paramString3, paramInt, paramString4, paramString5, paramString6);
    checkPath(str, paramString1, paramString4);
    (new Parser(str)).parse(true);
  }
  
  public URI(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) throws URISyntaxException {
    String str = toString(paramString1, null, paramString2, null, null, -1, paramString3, paramString4, paramString5);
    checkPath(str, paramString1, paramString3);
    (new Parser(str)).parse(false);
  }
  
  public URI(String paramString1, String paramString2, String paramString3, String paramString4) throws URISyntaxException {
    this(paramString1, null, paramString2, -1, paramString3, null, paramString4);
  }
  
  public URI(String paramString1, String paramString2, String paramString3) throws URISyntaxException {
    (new Parser(toString(paramString1, paramString2, null, null, null, -1, null, null, paramString3)))
      
      .parse(false);
  }
  
  public static URI create(String paramString) {
    try {
      return new URI(paramString);
    } catch (URISyntaxException uRISyntaxException) {
      throw new IllegalArgumentException(uRISyntaxException.getMessage(), uRISyntaxException);
    } 
  }
  
  public URI parseServerAuthority() throws URISyntaxException {
    if (this.host != null || this.authority == null)
      return this; 
    defineString();
    (new Parser(this.string)).parse(true);
    return this;
  }
  
  public URI normalize() {
    return normalize(this);
  }
  
  public URI resolve(URI paramURI) {
    return resolve(this, paramURI);
  }
  
  public URI resolve(String paramString) {
    return resolve(create(paramString));
  }
  
  public URI relativize(URI paramURI) {
    return relativize(this, paramURI);
  }
  
  public URL toURL() throws MalformedURLException {
    if (!isAbsolute())
      throw new IllegalArgumentException("URI is not absolute"); 
    return new URL(toString());
  }
  
  public String getScheme() {
    return this.scheme;
  }
  
  public boolean isAbsolute() {
    return (this.scheme != null);
  }
  
  public boolean isOpaque() {
    return (this.path == null);
  }
  
  public String getRawSchemeSpecificPart() {
    defineSchemeSpecificPart();
    return this.schemeSpecificPart;
  }
  
  public String getSchemeSpecificPart() {
    if (this.decodedSchemeSpecificPart == null)
      this.decodedSchemeSpecificPart = decode(getRawSchemeSpecificPart()); 
    return this.decodedSchemeSpecificPart;
  }
  
  public String getRawAuthority() {
    return this.authority;
  }
  
  public String getAuthority() {
    if (this.decodedAuthority == null)
      this.decodedAuthority = decode(this.authority); 
    return this.decodedAuthority;
  }
  
  public String getRawUserInfo() {
    return this.userInfo;
  }
  
  public String getUserInfo() {
    if (this.decodedUserInfo == null && this.userInfo != null)
      this.decodedUserInfo = decode(this.userInfo); 
    return this.decodedUserInfo;
  }
  
  public String getHost() {
    return this.host;
  }
  
  public int getPort() {
    return this.port;
  }
  
  public String getRawPath() {
    return this.path;
  }
  
  public String getPath() {
    if (this.decodedPath == null && this.path != null)
      this.decodedPath = decode(this.path); 
    return this.decodedPath;
  }
  
  public String getRawQuery() {
    return this.query;
  }
  
  public String getQuery() {
    if (this.decodedQuery == null && this.query != null)
      this.decodedQuery = decode(this.query); 
    return this.decodedQuery;
  }
  
  public String getRawFragment() {
    return this.fragment;
  }
  
  public String getFragment() {
    if (this.decodedFragment == null && this.fragment != null)
      this.decodedFragment = decode(this.fragment); 
    return this.decodedFragment;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof URI))
      return false; 
    URI uRI = (URI)paramObject;
    if (isOpaque() != uRI.isOpaque())
      return false; 
    if (!equalIgnoringCase(this.scheme, uRI.scheme))
      return false; 
    if (!equal(this.fragment, uRI.fragment))
      return false; 
    if (isOpaque())
      return equal(this.schemeSpecificPart, uRI.schemeSpecificPart); 
    if (!equal(this.path, uRI.path))
      return false; 
    if (!equal(this.query, uRI.query))
      return false; 
    if (this.authority == uRI.authority)
      return true; 
    if (this.host != null) {
      if (!equal(this.userInfo, uRI.userInfo))
        return false; 
      if (!equalIgnoringCase(this.host, uRI.host))
        return false; 
      if (this.port != uRI.port)
        return false; 
    } else if (this.authority != null) {
      if (!equal(this.authority, uRI.authority))
        return false; 
    } else if (this.authority != uRI.authority) {
      return false;
    } 
    return true;
  }
  
  public int hashCode() {
    if (this.hash != 0)
      return this.hash; 
    int i = hashIgnoringCase(0, this.scheme);
    i = hash(i, this.fragment);
    if (isOpaque()) {
      i = hash(i, this.schemeSpecificPart);
    } else {
      i = hash(i, this.path);
      i = hash(i, this.query);
      if (this.host != null) {
        i = hash(i, this.userInfo);
        i = hashIgnoringCase(i, this.host);
        i += 1949 * this.port;
      } else {
        i = hash(i, this.authority);
      } 
    } 
    this.hash = i;
    return i;
  }
  
  public int compareTo(URI paramURI) {
    int i;
    if ((i = compareIgnoringCase(this.scheme, paramURI.scheme)) != 0)
      return i; 
    if (isOpaque()) {
      if (paramURI.isOpaque()) {
        if ((i = compare(this.schemeSpecificPart, paramURI.schemeSpecificPart)) != 0)
          return i; 
        return compare(this.fragment, paramURI.fragment);
      } 
      return 1;
    } 
    if (paramURI.isOpaque())
      return -1; 
    if (this.host != null && paramURI.host != null) {
      if ((i = compare(this.userInfo, paramURI.userInfo)) != 0)
        return i; 
      if ((i = compareIgnoringCase(this.host, paramURI.host)) != 0)
        return i; 
      if ((i = this.port - paramURI.port) != 0)
        return i; 
    } else if ((i = compare(this.authority, paramURI.authority)) != 0) {
      return i;
    } 
    if ((i = compare(this.path, paramURI.path)) != 0)
      return i; 
    if ((i = compare(this.query, paramURI.query)) != 0)
      return i; 
    return compare(this.fragment, paramURI.fragment);
  }
  
  public String toString() {
    defineString();
    return this.string;
  }
  
  public String toASCIIString() {
    defineString();
    return encode(this.string);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    defineString();
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    this.port = -1;
    paramObjectInputStream.defaultReadObject();
    try {
      (new Parser(this.string)).parse(false);
    } catch (URISyntaxException uRISyntaxException) {
      InvalidObjectException invalidObjectException = new InvalidObjectException("Invalid URI");
      invalidObjectException.initCause(uRISyntaxException);
      throw invalidObjectException;
    } 
  }
  
  private static int toLower(char paramChar) {
    if (paramChar >= 'A' && paramChar <= 'Z')
      return paramChar + 32; 
    return paramChar;
  }
  
  private static int toUpper(char paramChar) {
    if (paramChar >= 'a' && paramChar <= 'z')
      return paramChar - 32; 
    return paramChar;
  }
  
  private static boolean equal(String paramString1, String paramString2) {
    if (paramString1 == paramString2)
      return true; 
    if (paramString1 != null && paramString2 != null) {
      if (paramString1.length() != paramString2.length())
        return false; 
      if (paramString1.indexOf('%') < 0)
        return paramString1.equals(paramString2); 
      int i = paramString1.length();
      for (byte b = 0; b < i; ) {
        char c1 = paramString1.charAt(b);
        char c2 = paramString2.charAt(b);
        if (c1 != '%') {
          if (c1 != c2)
            return false; 
          b++;
          continue;
        } 
        if (c2 != '%')
          return false; 
        b++;
        if (toLower(paramString1.charAt(b)) != toLower(paramString2.charAt(b)))
          return false; 
        b++;
        if (toLower(paramString1.charAt(b)) != toLower(paramString2.charAt(b)))
          return false; 
        b++;
      } 
      return true;
    } 
    return false;
  }
  
  private static boolean equalIgnoringCase(String paramString1, String paramString2) {
    if (paramString1 == paramString2)
      return true; 
    if (paramString1 != null && paramString2 != null) {
      int i = paramString1.length();
      if (paramString2.length() != i)
        return false; 
      for (byte b = 0; b < i; b++) {
        if (toLower(paramString1.charAt(b)) != toLower(paramString2.charAt(b)))
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  private static int hash(int paramInt, String paramString) {
    if (paramString == null)
      return paramInt; 
    return (paramString.indexOf('%') < 0) ? (paramInt * 127 + paramString.hashCode()) : 
      normalizedHash(paramInt, paramString);
  }
  
  private static int normalizedHash(int paramInt, String paramString) {
    int i = 0;
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      i = 31 * i + c;
      if (c == '%') {
        for (int j = b + 1; j < b + 3; j++)
          i = 31 * i + toUpper(paramString.charAt(j)); 
        b += 2;
      } 
    } 
    return paramInt * 127 + i;
  }
  
  private static int hashIgnoringCase(int paramInt, String paramString) {
    if (paramString == null)
      return paramInt; 
    int i = paramInt;
    int j = paramString.length();
    for (byte b = 0; b < j; b++)
      i = 31 * i + toLower(paramString.charAt(b)); 
    return i;
  }
  
  private static int compare(String paramString1, String paramString2) {
    if (paramString1 == paramString2)
      return 0; 
    if (paramString1 != null) {
      if (paramString2 != null)
        return paramString1.compareTo(paramString2); 
      return 1;
    } 
    return -1;
  }
  
  private static int compareIgnoringCase(String paramString1, String paramString2) {
    if (paramString1 == paramString2)
      return 0; 
    if (paramString1 != null) {
      if (paramString2 != null) {
        int i = paramString1.length();
        int j = paramString2.length();
        int k = (i < j) ? i : j;
        for (byte b = 0; b < k; b++) {
          int m = toLower(paramString1.charAt(b)) - toLower(paramString2.charAt(b));
          if (m != 0)
            return m; 
        } 
        return i - j;
      } 
      return 1;
    } 
    return -1;
  }
  
  private static void checkPath(String paramString1, String paramString2, String paramString3) throws URISyntaxException {
    if (paramString2 != null && 
      paramString3 != null && paramString3
      .length() > 0 && paramString3.charAt(0) != '/')
      throw new URISyntaxException(paramString1, "Relative path in absolute URI"); 
  }
  
  private void appendAuthority(StringBuffer paramStringBuffer, String paramString1, String paramString2, String paramString3, int paramInt) {
    if (paramString3 != null) {
      paramStringBuffer.append("//");
      if (paramString2 != null) {
        paramStringBuffer.append(quote(paramString2, L_USERINFO, H_USERINFO));
        paramStringBuffer.append('@');
      } 
      boolean bool = (paramString3.indexOf(':') >= 0 && !paramString3.startsWith("[") && !paramString3.endsWith("]")) ? true : false;
      if (bool)
        paramStringBuffer.append('['); 
      paramStringBuffer.append(paramString3);
      if (bool)
        paramStringBuffer.append(']'); 
      if (paramInt != -1) {
        paramStringBuffer.append(':');
        paramStringBuffer.append(paramInt);
      } 
    } else if (paramString1 != null) {
      paramStringBuffer.append("//");
      if (paramString1.startsWith("[")) {
        int i = paramString1.indexOf("]");
        String str1 = paramString1, str2 = "";
        if (i != -1 && paramString1.indexOf(":") != -1)
          if (i == paramString1.length()) {
            str2 = paramString1;
            str1 = "";
          } else {
            str2 = paramString1.substring(0, i + 1);
            str1 = paramString1.substring(i + 1);
          }  
        paramStringBuffer.append(str2);
        paramStringBuffer.append(quote(str1, L_REG_NAME | L_SERVER, H_REG_NAME | H_SERVER));
      } else {
        paramStringBuffer.append(quote(paramString1, L_REG_NAME | L_SERVER, H_REG_NAME | H_SERVER));
      } 
    } 
  }
  
  private void appendSchemeSpecificPart(StringBuffer paramStringBuffer, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt, String paramString5, String paramString6) {
    if (paramString1 != null) {
      if (paramString1.startsWith("//[")) {
        int i = paramString1.indexOf("]");
        if (i != -1 && paramString1.indexOf(":") != -1) {
          String str1, str2;
          if (i == paramString1.length()) {
            str2 = paramString1;
            str1 = "";
          } else {
            str2 = paramString1.substring(0, i + 1);
            str1 = paramString1.substring(i + 1);
          } 
          paramStringBuffer.append(str2);
          paramStringBuffer.append(quote(str1, L_URIC, H_URIC));
        } 
      } else {
        paramStringBuffer.append(quote(paramString1, L_URIC, H_URIC));
      } 
    } else {
      appendAuthority(paramStringBuffer, paramString2, paramString3, paramString4, paramInt);
      if (paramString5 != null)
        paramStringBuffer.append(quote(paramString5, L_PATH, H_PATH)); 
      if (paramString6 != null) {
        paramStringBuffer.append('?');
        paramStringBuffer.append(quote(paramString6, L_URIC, H_URIC));
      } 
    } 
  }
  
  private void appendFragment(StringBuffer paramStringBuffer, String paramString) {
    if (paramString != null) {
      paramStringBuffer.append('#');
      paramStringBuffer.append(quote(paramString, L_URIC, H_URIC));
    } 
  }
  
  private String toString(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt, String paramString6, String paramString7, String paramString8) {
    StringBuffer stringBuffer = new StringBuffer();
    if (paramString1 != null) {
      stringBuffer.append(paramString1);
      stringBuffer.append(':');
    } 
    appendSchemeSpecificPart(stringBuffer, paramString2, paramString3, paramString4, paramString5, paramInt, paramString6, paramString7);
    appendFragment(stringBuffer, paramString8);
    return stringBuffer.toString();
  }
  
  private void defineSchemeSpecificPart() {
    if (this.schemeSpecificPart != null)
      return; 
    StringBuffer stringBuffer = new StringBuffer();
    appendSchemeSpecificPart(stringBuffer, null, getAuthority(), getUserInfo(), this.host, this.port, 
        getPath(), getQuery());
    if (stringBuffer.length() == 0)
      return; 
    this.schemeSpecificPart = stringBuffer.toString();
  }
  
  private void defineString() {
    if (this.string != null)
      return; 
    StringBuffer stringBuffer = new StringBuffer();
    if (this.scheme != null) {
      stringBuffer.append(this.scheme);
      stringBuffer.append(':');
    } 
    if (isOpaque()) {
      stringBuffer.append(this.schemeSpecificPart);
    } else {
      if (this.host != null) {
        stringBuffer.append("//");
        if (this.userInfo != null) {
          stringBuffer.append(this.userInfo);
          stringBuffer.append('@');
        } 
        boolean bool = (this.host.indexOf(':') >= 0 && !this.host.startsWith("[") && !this.host.endsWith("]")) ? true : false;
        if (bool)
          stringBuffer.append('['); 
        stringBuffer.append(this.host);
        if (bool)
          stringBuffer.append(']'); 
        if (this.port != -1) {
          stringBuffer.append(':');
          stringBuffer.append(this.port);
        } 
      } else if (this.authority != null) {
        stringBuffer.append("//");
        stringBuffer.append(this.authority);
      } 
      if (this.path != null)
        stringBuffer.append(this.path); 
      if (this.query != null) {
        stringBuffer.append('?');
        stringBuffer.append(this.query);
      } 
    } 
    if (this.fragment != null) {
      stringBuffer.append('#');
      stringBuffer.append(this.fragment);
    } 
    this.string = stringBuffer.toString();
  }
  
  private static String resolvePath(String paramString1, String paramString2, boolean paramBoolean) {
    int i = paramString1.lastIndexOf('/');
    int j = paramString2.length();
    String str = "";
    if (j == 0) {
      if (i >= 0)
        str = paramString1.substring(0, i + 1); 
    } else {
      StringBuffer stringBuffer = new StringBuffer(paramString1.length() + j);
      if (i >= 0)
        stringBuffer.append(paramString1.substring(0, i + 1)); 
      stringBuffer.append(paramString2);
      str = stringBuffer.toString();
    } 
    return normalize(str);
  }
  
  private static URI resolve(URI paramURI1, URI paramURI2) {
    if (paramURI2.isOpaque() || paramURI1.isOpaque())
      return paramURI2; 
    if (paramURI2.scheme == null && paramURI2.authority == null && paramURI2.path
      .equals("") && paramURI2.fragment != null && paramURI2.query == null) {
      if (paramURI1.fragment != null && paramURI2.fragment
        .equals(paramURI1.fragment))
        return paramURI1; 
      URI uRI1 = new URI();
      uRI1.scheme = paramURI1.scheme;
      uRI1.authority = paramURI1.authority;
      uRI1.userInfo = paramURI1.userInfo;
      uRI1.host = paramURI1.host;
      uRI1.port = paramURI1.port;
      uRI1.path = paramURI1.path;
      uRI1.fragment = paramURI2.fragment;
      uRI1.query = paramURI1.query;
      return uRI1;
    } 
    if (paramURI2.scheme != null)
      return paramURI2; 
    URI uRI = new URI();
    uRI.scheme = paramURI1.scheme;
    uRI.query = paramURI2.query;
    uRI.fragment = paramURI2.fragment;
    if (paramURI2.authority == null) {
      uRI.authority = paramURI1.authority;
      uRI.host = paramURI1.host;
      uRI.userInfo = paramURI1.userInfo;
      uRI.port = paramURI1.port;
      String str = (paramURI2.path == null) ? "" : paramURI2.path;
      if (str.length() > 0 && str.charAt(0) == '/') {
        uRI.path = paramURI2.path;
      } else {
        uRI.path = resolvePath(paramURI1.path, str, paramURI1.isAbsolute());
      } 
    } else {
      uRI.authority = paramURI2.authority;
      uRI.host = paramURI2.host;
      uRI.userInfo = paramURI2.userInfo;
      uRI.host = paramURI2.host;
      uRI.port = paramURI2.port;
      uRI.path = paramURI2.path;
    } 
    return uRI;
  }
  
  private static URI normalize(URI paramURI) {
    if (paramURI.isOpaque() || paramURI.path == null || paramURI.path.length() == 0)
      return paramURI; 
    String str = normalize(paramURI.path);
    if (str == paramURI.path)
      return paramURI; 
    URI uRI = new URI();
    uRI.scheme = paramURI.scheme;
    uRI.fragment = paramURI.fragment;
    uRI.authority = paramURI.authority;
    uRI.userInfo = paramURI.userInfo;
    uRI.host = paramURI.host;
    uRI.port = paramURI.port;
    uRI.path = str;
    uRI.query = paramURI.query;
    return uRI;
  }
  
  private static URI relativize(URI paramURI1, URI paramURI2) {
    if (paramURI2.isOpaque() || paramURI1.isOpaque())
      return paramURI2; 
    if (!equalIgnoringCase(paramURI1.scheme, paramURI2.scheme) || 
      !equal(paramURI1.authority, paramURI2.authority))
      return paramURI2; 
    String str1 = normalize(paramURI1.path);
    String str2 = normalize(paramURI2.path);
    if (!str1.equals(str2)) {
      if (!str1.endsWith("/"))
        str1 = str1 + "/"; 
      if (!str2.startsWith(str1))
        return paramURI2; 
    } 
    URI uRI = new URI();
    uRI.path = str2.substring(str1.length());
    uRI.query = paramURI2.query;
    uRI.fragment = paramURI2.fragment;
    return uRI;
  }
  
  private static int needsNormalization(String paramString) {
    boolean bool = true;
    byte b1 = 0;
    int i = paramString.length() - 1;
    byte b2 = 0;
    while (b2 <= i && 
      paramString.charAt(b2) == '/')
      b2++; 
    if (b2 > 1)
      bool = false; 
    label34: while (b2 <= i) {
      if (paramString.charAt(b2) == '.' && (b2 == i || paramString
        
        .charAt(b2 + 1) == '/' || (paramString
        .charAt(b2 + 1) == '.' && (b2 + 1 == i || paramString
        
        .charAt(b2 + 2) == '/'))))
        bool = false; 
      b1++;
      while (b2 <= i) {
        if (paramString.charAt(b2++) != '/')
          continue; 
        continue label34;
      } 
    } 
    return bool ? -1 : b1;
  }
  
  private static void split(char[] paramArrayOfchar, int[] paramArrayOfint) {
    int i = paramArrayOfchar.length - 1;
    byte b1 = 0;
    byte b2 = 0;
    while (b1 <= i && 
      paramArrayOfchar[b1] == '/') {
      paramArrayOfchar[b1] = Character.MIN_VALUE;
      b1++;
    } 
    while (b1 <= i) {
      paramArrayOfint[b2++] = b1++;
      label22: while (b1 <= i) {
        if (paramArrayOfchar[b1++] != '/')
          continue; 
        paramArrayOfchar[b1 - 1] = Character.MIN_VALUE;
        break label22;
      } 
    } 
    if (b2 != paramArrayOfint.length)
      throw new InternalError(); 
  }
  
  private static int join(char[] paramArrayOfchar, int[] paramArrayOfint) {
    int i = paramArrayOfint.length;
    int j = paramArrayOfchar.length - 1;
    byte b1 = 0;
    if (paramArrayOfchar[b1] == '\000')
      paramArrayOfchar[b1++] = '/'; 
    for (byte b2 = 0; b2 < i; b2++) {
      int k = paramArrayOfint[b2];
      if (k != -1)
        if (b1 == k) {
          while (b1 <= j && paramArrayOfchar[b1] != '\000')
            b1++; 
          if (b1 <= j)
            paramArrayOfchar[b1++] = '/'; 
        } else if (b1 < k) {
          while (k <= j && paramArrayOfchar[k] != '\000')
            paramArrayOfchar[b1++] = paramArrayOfchar[k++]; 
          if (k <= j)
            paramArrayOfchar[b1++] = '/'; 
        } else {
          throw new InternalError();
        }  
    } 
    return b1;
  }
  
  private static void removeDots(char[] paramArrayOfchar, int[] paramArrayOfint) {
    int i = paramArrayOfint.length;
    int j = paramArrayOfchar.length - 1;
    for (byte b = 0; b < i; b++) {
      byte b1 = 0;
      do {
        int k = paramArrayOfint[b];
        if (paramArrayOfchar[k] != '.')
          continue; 
        if (k == j) {
          b1 = 1;
          break;
        } 
        if (paramArrayOfchar[k + 1] == '\000') {
          b1 = 1;
          break;
        } 
        if (paramArrayOfchar[k + 1] == '.' && (k + 1 == j || paramArrayOfchar[k + 2] == '\000')) {
          b1 = 2;
          break;
        } 
        ++b;
      } while (b < i);
      if (b > i || b1 == 0)
        break; 
      if (b1 == 1) {
        paramArrayOfint[b] = -1;
      } else {
        int k;
        for (k = b - 1; k >= 0 && 
          paramArrayOfint[k] == -1; k--);
        if (k >= 0) {
          int m = paramArrayOfint[k];
          if (paramArrayOfchar[m] != '.' || paramArrayOfchar[m + 1] != '.' || paramArrayOfchar[m + 2] != '\000') {
            paramArrayOfint[b] = -1;
            paramArrayOfint[k] = -1;
          } 
        } 
      } 
    } 
  }
  
  private static void maybeAddLeadingDot(char[] paramArrayOfchar, int[] paramArrayOfint) {
    if (paramArrayOfchar[0] == '\000')
      return; 
    int i = paramArrayOfint.length;
    byte b = 0;
    while (b < i && 
      paramArrayOfint[b] < 0)
      b++; 
    if (b >= i || b == 0)
      return; 
    int j = paramArrayOfint[b];
    for (; j < paramArrayOfchar.length && paramArrayOfchar[j] != ':' && paramArrayOfchar[j] != '\000'; j++);
    if (j >= paramArrayOfchar.length || paramArrayOfchar[j] == '\000')
      return; 
    paramArrayOfchar[0] = '.';
    paramArrayOfchar[1] = Character.MIN_VALUE;
    paramArrayOfint[0] = 0;
  }
  
  private static String normalize(String paramString) {
    int i = needsNormalization(paramString);
    if (i < 0)
      return paramString; 
    char[] arrayOfChar = paramString.toCharArray();
    int[] arrayOfInt = new int[i];
    split(arrayOfChar, arrayOfInt);
    removeDots(arrayOfChar, arrayOfInt);
    maybeAddLeadingDot(arrayOfChar, arrayOfInt);
    String str = new String(arrayOfChar, 0, join(arrayOfChar, arrayOfInt));
    if (str.equals(paramString))
      return paramString; 
    return str;
  }
  
  private static long lowMask(String paramString) {
    int i = paramString.length();
    long l = 0L;
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c < '@')
        l |= 1L << c; 
    } 
    return l;
  }
  
  private static long highMask(String paramString) {
    int i = paramString.length();
    long l = 0L;
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c >= '@' && c < '')
        l |= 1L << c - 64; 
    } 
    return l;
  }
  
  private static long lowMask(char paramChar1, char paramChar2) {
    long l = 0L;
    int i = Math.max(Math.min(paramChar1, 63), 0);
    int j = Math.max(Math.min(paramChar2, 63), 0);
    for (int k = i; k <= j; k++)
      l |= 1L << k; 
    return l;
  }
  
  private static long highMask(char paramChar1, char paramChar2) {
    long l = 0L;
    int i = Math.max(Math.min(paramChar1, 127), 64) - 64;
    int j = Math.max(Math.min(paramChar2, 127), 64) - 64;
    for (int k = i; k <= j; k++)
      l |= 1L << k; 
    return l;
  }
  
  private static boolean match(char paramChar, long paramLong1, long paramLong2) {
    if (paramChar == '\000')
      return false; 
    if (paramChar < '@')
      return ((1L << paramChar & paramLong1) != 0L); 
    if (paramChar < '')
      return ((1L << paramChar - 64 & paramLong2) != 0L); 
    return false;
  }
  
  private static final long L_DIGIT = lowMask('0', '9');
  
  private static final long H_DIGIT = 0L;
  
  private static final long L_UPALPHA = 0L;
  
  private static final long H_UPALPHA = highMask('A', 'Z');
  
  private static final long L_LOWALPHA = 0L;
  
  private static final long H_LOWALPHA = highMask('a', 'z');
  
  private static final long L_ALPHA = 0L;
  
  private static final long H_ALPHA = H_LOWALPHA | H_UPALPHA;
  
  private static final long L_ALPHANUM = L_DIGIT | 0x0L;
  
  private static final long H_ALPHANUM = 0x0L | H_ALPHA;
  
  private static final long L_HEX = L_DIGIT;
  
  private static final long H_HEX = highMask('A', 'F') | highMask('a', 'f');
  
  private static final long L_MARK = lowMask("-_.!~*'()");
  
  private static final long H_MARK = highMask("-_.!~*'()");
  
  private static final long L_UNRESERVED = L_ALPHANUM | L_MARK;
  
  private static final long H_UNRESERVED = H_ALPHANUM | H_MARK;
  
  private static final long L_RESERVED = lowMask(";/?:@&=+$,[]");
  
  private static final long H_RESERVED = highMask(";/?:@&=+$,[]");
  
  private static final long L_ESCAPED = 1L;
  
  private static final long H_ESCAPED = 0L;
  
  private static final long L_URIC = L_RESERVED | L_UNRESERVED | 0x1L;
  
  private static final long H_URIC = H_RESERVED | H_UNRESERVED | 0x0L;
  
  private static final long L_PCHAR = L_UNRESERVED | 0x1L | 
    lowMask(":@&=+$,");
  
  private static final long H_PCHAR = H_UNRESERVED | 0x0L | 
    highMask(":@&=+$,");
  
  private static final long L_PATH = L_PCHAR | lowMask(";/");
  
  private static final long H_PATH = H_PCHAR | highMask(";/");
  
  private static final long L_DASH = lowMask("-");
  
  private static final long H_DASH = highMask("-");
  
  private static final long L_DOT = lowMask(".");
  
  private static final long H_DOT = highMask(".");
  
  private static final long L_USERINFO = L_UNRESERVED | 0x1L | 
    lowMask(";:&=+$,");
  
  private static final long H_USERINFO = H_UNRESERVED | 0x0L | 
    highMask(";:&=+$,");
  
  private static final long L_REG_NAME = L_UNRESERVED | 0x1L | 
    lowMask("$,;:@&=+");
  
  private static final long H_REG_NAME = H_UNRESERVED | 0x0L | 
    highMask("$,;:@&=+");
  
  private static final long L_SERVER = L_USERINFO | L_ALPHANUM | L_DASH | 
    lowMask(".:@[]");
  
  private static final long H_SERVER = H_USERINFO | H_ALPHANUM | H_DASH | 
    highMask(".:@[]");
  
  private static final long L_SERVER_PERCENT = L_SERVER | 
    lowMask("%");
  
  private static final long H_SERVER_PERCENT = H_SERVER | 
    highMask("%");
  
  private static final long L_LEFT_BRACKET = lowMask("[");
  
  private static final long H_LEFT_BRACKET = highMask("[");
  
  private static final long L_SCHEME = 0x0L | L_DIGIT | lowMask("+-.");
  
  private static final long H_SCHEME = H_ALPHA | 0x0L | highMask("+-.");
  
  private static final long L_URIC_NO_SLASH = L_UNRESERVED | 0x1L | 
    lowMask(";?:@&=+$,");
  
  private static final long H_URIC_NO_SLASH = H_UNRESERVED | 0x0L | 
    highMask(";?:@&=+$,");
  
  private static final char[] hexDigits = new char[] { 
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
      'A', 'B', 'C', 'D', 'E', 'F' };
  
  private static void appendEscape(StringBuffer paramStringBuffer, byte paramByte) {
    paramStringBuffer.append('%');
    paramStringBuffer.append(hexDigits[paramByte >> 4 & 0xF]);
    paramStringBuffer.append(hexDigits[paramByte >> 0 & 0xF]);
  }
  
  private static void appendEncoded(StringBuffer paramStringBuffer, char paramChar) {
    ByteBuffer byteBuffer = null;
    try {
      byteBuffer = ThreadLocalCoders.encoderFor("UTF-8").encode(CharBuffer.wrap("" + paramChar));
    } catch (CharacterCodingException characterCodingException) {
      assert false;
    } 
    while (byteBuffer.hasRemaining()) {
      int i = byteBuffer.get() & 0xFF;
      if (i >= 128) {
        appendEscape(paramStringBuffer, (byte)i);
        continue;
      } 
      paramStringBuffer.append((char)i);
    } 
  }
  
  private static String quote(String paramString, long paramLong1, long paramLong2) {
    int i = paramString.length();
    StringBuffer stringBuffer = null;
    boolean bool = ((paramLong1 & 0x1L) != 0L) ? true : false;
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c < '') {
        if (!match(c, paramLong1, paramLong2)) {
          if (stringBuffer == null) {
            stringBuffer = new StringBuffer();
            stringBuffer.append(paramString.substring(0, b));
          } 
          appendEscape(stringBuffer, (byte)c);
        } else if (stringBuffer != null) {
          stringBuffer.append(c);
        } 
      } else if (bool && (
        Character.isSpaceChar(c) || 
        Character.isISOControl(c))) {
        if (stringBuffer == null) {
          stringBuffer = new StringBuffer();
          stringBuffer.append(paramString.substring(0, b));
        } 
        appendEncoded(stringBuffer, c);
      } else if (stringBuffer != null) {
        stringBuffer.append(c);
      } 
    } 
    return (stringBuffer == null) ? paramString : stringBuffer.toString();
  }
  
  private static String encode(String paramString) {
    int i = paramString.length();
    if (i == 0)
      return paramString; 
    byte b = 0;
    while (paramString.charAt(b) < '') {
      if (++b >= i)
        return paramString; 
    } 
    String str = Normalizer.normalize(paramString, Normalizer.Form.NFC);
    ByteBuffer byteBuffer = null;
    try {
      byteBuffer = ThreadLocalCoders.encoderFor("UTF-8").encode(CharBuffer.wrap(str));
    } catch (CharacterCodingException characterCodingException) {
      assert false;
    } 
    StringBuffer stringBuffer = new StringBuffer();
    while (byteBuffer.hasRemaining()) {
      int j = byteBuffer.get() & 0xFF;
      if (j >= 128) {
        appendEscape(stringBuffer, (byte)j);
        continue;
      } 
      stringBuffer.append((char)j);
    } 
    return stringBuffer.toString();
  }
  
  private static int decode(char paramChar) {
    if (paramChar >= '0' && paramChar <= '9')
      return paramChar - 48; 
    if (paramChar >= 'a' && paramChar <= 'f')
      return paramChar - 97 + 10; 
    if (paramChar >= 'A' && paramChar <= 'F')
      return paramChar - 65 + 10; 
    assert false;
    return -1;
  }
  
  private static byte decode(char paramChar1, char paramChar2) {
    return (byte)((decode(paramChar1) & 0xF) << 4 | (decode(paramChar2) & 0xF) << 0);
  }
  
  private static String decode(String paramString) {
    if (paramString == null)
      return paramString; 
    int i = paramString.length();
    if (i == 0)
      return paramString; 
    if (paramString.indexOf('%') < 0)
      return paramString; 
    StringBuffer stringBuffer = new StringBuffer(i);
    ByteBuffer byteBuffer = ByteBuffer.allocate(i);
    CharBuffer charBuffer = CharBuffer.allocate(i);
    CharsetDecoder charsetDecoder = ThreadLocalCoders.decoderFor("UTF-8").onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
    char c = paramString.charAt(0);
    boolean bool = false;
    for (byte b = 0; b < i; ) {
      assert c == paramString.charAt(b);
      if (c == '[') {
        bool = true;
      } else if (bool && c == ']') {
        bool = false;
      } 
      if (c != '%' || bool) {
        stringBuffer.append(c);
        if (++b >= i)
          break; 
        c = paramString.charAt(b);
        continue;
      } 
      byteBuffer.clear();
      byte b1 = b;
      do {
        assert i - b >= 2;
        byteBuffer.put(decode(paramString.charAt(++b), paramString.charAt(++b)));
        if (++b >= i)
          break; 
        c = paramString.charAt(b);
      } while (c == '%');
      byteBuffer.flip();
      charBuffer.clear();
      charsetDecoder.reset();
      CoderResult coderResult = charsetDecoder.decode(byteBuffer, charBuffer, true);
      assert coderResult.isUnderflow();
      coderResult = charsetDecoder.flush(charBuffer);
      assert coderResult.isUnderflow();
      stringBuffer.append(charBuffer.flip().toString());
    } 
    return stringBuffer.toString();
  }
  
  private URI() {}
  
  private class Parser {
    private String input;
    
    private boolean requireServerAuthority = false;
    
    private int ipv6byteCount;
    
    private void fail(String param1String) throws URISyntaxException {
      throw new URISyntaxException(this.input, param1String);
    }
    
    private void fail(String param1String, int param1Int) throws URISyntaxException {
      throw new URISyntaxException(this.input, param1String, param1Int);
    }
    
    private void failExpecting(String param1String, int param1Int) throws URISyntaxException {
      fail("Expected " + param1String, param1Int);
    }
    
    private void failExpecting(String param1String1, String param1String2, int param1Int) throws URISyntaxException {
      fail("Expected " + param1String1 + " following " + param1String2, param1Int);
    }
    
    private String substring(int param1Int1, int param1Int2) {
      return this.input.substring(param1Int1, param1Int2);
    }
    
    private char charAt(int param1Int) {
      return this.input.charAt(param1Int);
    }
    
    private boolean at(int param1Int1, int param1Int2, char param1Char) {
      return (param1Int1 < param1Int2 && charAt(param1Int1) == param1Char);
    }
    
    private boolean at(int param1Int1, int param1Int2, String param1String) {
      int i = param1Int1;
      int j = param1String.length();
      if (j > param1Int2 - i)
        return false; 
      byte b = 0;
      while (b < j && 
        charAt(i++) == param1String.charAt(b))
        b++; 
      return (b == j);
    }
    
    private int scan(int param1Int1, int param1Int2, char param1Char) {
      if (param1Int1 < param1Int2 && charAt(param1Int1) == param1Char)
        return param1Int1 + 1; 
      return param1Int1;
    }
    
    private int scan(int param1Int1, int param1Int2, String param1String1, String param1String2) {
      int i = param1Int1;
      while (i < param1Int2) {
        char c = charAt(i);
        if (param1String1.indexOf(c) >= 0)
          return -1; 
        if (param1String2.indexOf(c) >= 0)
          break; 
        i++;
      } 
      return i;
    }
    
    private int scanEscape(int param1Int1, int param1Int2, char param1Char) throws URISyntaxException {
      int i = param1Int1;
      char c = param1Char;
      if (c == '%') {
        if (i + 3 <= param1Int2 && URI
          .match(charAt(i + 1), URI.L_HEX, URI.H_HEX) && URI
          .match(charAt(i + 2), URI.L_HEX, URI.H_HEX))
          return i + 3; 
        fail("Malformed escape pair", i);
      } else if (c > '' && 
        !Character.isSpaceChar(c) && 
        !Character.isISOControl(c)) {
        return i + 1;
      } 
      return i;
    }
    
    private int scan(int param1Int1, int param1Int2, long param1Long1, long param1Long2) throws URISyntaxException {
      int i = param1Int1;
      while (i < param1Int2) {
        char c = charAt(i);
        if (URI.match(c, param1Long1, param1Long2)) {
          i++;
          continue;
        } 
        if ((param1Long1 & 0x1L) != 0L) {
          int j = scanEscape(i, param1Int2, c);
          if (j > i)
            i = j; 
        } 
      } 
      return i;
    }
    
    private void checkChars(int param1Int1, int param1Int2, long param1Long1, long param1Long2, String param1String) throws URISyntaxException {
      int i = scan(param1Int1, param1Int2, param1Long1, param1Long2);
      if (i < param1Int2)
        fail("Illegal character in " + param1String, i); 
    }
    
    private void checkChar(int param1Int, long param1Long1, long param1Long2, String param1String) throws URISyntaxException {
      checkChars(param1Int, param1Int + 1, param1Long1, param1Long2, param1String);
    }
    
    void parse(boolean param1Boolean) throws URISyntaxException {
      boolean bool;
      this.requireServerAuthority = param1Boolean;
      int i = this.input.length();
      int j = scan(0, i, "/?#", ":");
      if (j >= 0 && at(j, i, ':')) {
        if (j == 0)
          failExpecting("scheme name", 0); 
        checkChar(0, 0L, URI.H_ALPHA, "scheme name");
        checkChars(1, j, URI.L_SCHEME, URI.H_SCHEME, "scheme name");
        URI.this.scheme = substring(0, j);
        bool = ++j;
        if (at(j, i, '/')) {
          j = parseHierarchical(j, i);
        } else {
          int k = scan(j, i, "", "#");
          if (k <= j)
            failExpecting("scheme-specific part", j); 
          checkChars(j, k, URI.L_URIC, URI.H_URIC, "opaque part");
          j = k;
        } 
      } else {
        bool = false;
        j = parseHierarchical(0, i);
      } 
      URI.this.schemeSpecificPart = substring(bool, j);
      if (at(j, i, '#')) {
        checkChars(j + 1, i, URI.L_URIC, URI.H_URIC, "fragment");
        URI.this.fragment = substring(j + 1, i);
        j = i;
      } 
      if (j < i)
        fail("end of URI", j); 
    }
    
    private int parseHierarchical(int param1Int1, int param1Int2) throws URISyntaxException {
      int i = param1Int1;
      if (at(i, param1Int2, '/') && at(i + 1, param1Int2, '/')) {
        i += 2;
        int k = scan(i, param1Int2, "", "/?#");
        if (k > i) {
          i = parseAuthority(i, k);
        } else if (k >= param1Int2) {
          failExpecting("authority", i);
        } 
      } 
      int j = scan(i, param1Int2, "", "?#");
      checkChars(i, j, URI.L_PATH, URI.H_PATH, "path");
      URI.this.path = substring(i, j);
      i = j;
      if (at(i, param1Int2, '?')) {
        i++;
        j = scan(i, param1Int2, "", "#");
        checkChars(i, j, URI.L_URIC, URI.H_URIC, "query");
        URI.this.query = substring(i, j);
        i = j;
      } 
      return i;
    }
    
    private int parseAuthority(int param1Int1, int param1Int2) throws URISyntaxException {
      boolean bool1;
      int i = param1Int1;
      int j = i;
      URISyntaxException uRISyntaxException = null;
      if (scan(i, param1Int2, "", "]") > i) {
        bool1 = (scan(i, param1Int2, URI.L_SERVER_PERCENT, URI.H_SERVER_PERCENT) == param1Int2) ? true : false;
      } else {
        bool1 = (scan(i, param1Int2, URI.L_SERVER, URI.H_SERVER) == param1Int2) ? true : false;
      } 
      boolean bool2 = (scan(i, param1Int2, URI.L_REG_NAME, URI.H_REG_NAME) == param1Int2) ? true : false;
      if (bool2 && !bool1) {
        URI.this.authority = substring(i, param1Int2);
        return param1Int2;
      } 
      if (bool1)
        try {
          j = parseServer(i, param1Int2);
          if (j < param1Int2)
            failExpecting("end of authority", j); 
          URI.this.authority = substring(i, param1Int2);
        } catch (URISyntaxException uRISyntaxException1) {
          URI.this.userInfo = null;
          URI.this.host = null;
          URI.this.port = -1;
          if (this.requireServerAuthority)
            throw uRISyntaxException1; 
          uRISyntaxException = uRISyntaxException1;
          j = i;
        }  
      if (j < param1Int2)
        if (bool2) {
          URI.this.authority = substring(i, param1Int2);
        } else {
          if (uRISyntaxException != null)
            throw uRISyntaxException; 
          fail("Illegal character in authority", j);
        }  
      return param1Int2;
    }
    
    private int parseServer(int param1Int1, int param1Int2) throws URISyntaxException {
      int i = param1Int1;
      int j = scan(i, param1Int2, "/?#", "@");
      if (j >= i && at(j, param1Int2, '@')) {
        checkChars(i, j, URI.L_USERINFO, URI.H_USERINFO, "user info");
        URI.this.userInfo = substring(i, j);
        i = j + 1;
      } 
      if (at(i, param1Int2, '[')) {
        i++;
        j = scan(i, param1Int2, "/?#", "]");
        if (j > i && at(j, param1Int2, ']')) {
          int k = scan(i, j, "", "%");
          if (k > i) {
            parseIPv6Reference(i, k);
            if (k + 1 == j)
              fail("scope id expected"); 
            checkChars(k + 1, j, URI.L_ALPHANUM, URI.H_ALPHANUM, "scope id");
          } else {
            parseIPv6Reference(i, j);
          } 
          URI.this.host = substring(i - 1, j + 1);
          i = j + 1;
        } else {
          failExpecting("closing bracket for IPv6 address", j);
        } 
      } else {
        j = parseIPv4Address(i, param1Int2);
        if (j <= i)
          j = parseHostname(i, param1Int2); 
        i = j;
      } 
      if (at(i, param1Int2, ':')) {
        i++;
        j = scan(i, param1Int2, "", "/");
        if (j > i) {
          checkChars(i, j, URI.L_DIGIT, 0L, "port number");
          try {
            URI.this.port = Integer.parseInt(substring(i, j));
          } catch (NumberFormatException numberFormatException) {
            fail("Malformed port number", i);
          } 
          i = j;
        } 
      } 
      if (i < param1Int2)
        failExpecting("port number", i); 
      return i;
    }
    
    private int scanByte(int param1Int1, int param1Int2) throws URISyntaxException {
      int i = param1Int1;
      int j = scan(i, param1Int2, URI.L_DIGIT, 0L);
      if (j <= i)
        return j; 
      if (Integer.parseInt(substring(i, j)) > 255)
        return i; 
      return j;
    }
    
    private int scanIPv4Address(int param1Int1, int param1Int2, boolean param1Boolean) throws URISyntaxException {
      int i = param1Int1;
      int k = scan(i, param1Int2, URI.L_DIGIT | URI.L_DOT, 0x0L | URI.H_DOT);
      if (k <= i || (param1Boolean && k != param1Int2))
        return -1; 
      i = j;
      i = j;
      i = j;
      i = j;
      i = j;
      i = j;
      int j;
      if ((j = scanByte(i, k)) > i && (j = scan(i, k, '.')) > i && (j = scanByte(i, k)) > i && (j = scan(i, k, '.')) > i && (j = scanByte(i, k)) > i && (j = scan(i, k, '.')) > i && (
        j = scanByte(i, k)) > i) {
        i = j;
        if (j >= k)
          return j; 
      } 
      fail("Malformed IPv4 address", j);
      return -1;
    }
    
    private int takeIPv4Address(int param1Int1, int param1Int2, String param1String) throws URISyntaxException {
      int i = scanIPv4Address(param1Int1, param1Int2, true);
      if (i <= param1Int1)
        failExpecting(param1String, param1Int1); 
      return i;
    }
    
    private int parseIPv4Address(int param1Int1, int param1Int2) {
      int i;
      try {
        i = scanIPv4Address(param1Int1, param1Int2, false);
      } catch (URISyntaxException uRISyntaxException) {
        return -1;
      } catch (NumberFormatException numberFormatException) {
        return -1;
      } 
      if (i > param1Int1 && i < param1Int2)
        if (charAt(i) != ':')
          i = -1;  
      if (i > param1Int1)
        URI.this.host = substring(param1Int1, i); 
      return i;
    }
    
    private int parseHostname(int param1Int1, int param1Int2) throws URISyntaxException {
      int i = param1Int1;
      int j = -1;
      do {
        int k = scan(i, param1Int2, URI.L_ALPHANUM, URI.H_ALPHANUM);
        if (k <= i)
          break; 
        j = i;
        if (k > i) {
          i = k;
          k = scan(i, param1Int2, URI.L_ALPHANUM | URI.L_DASH, URI.H_ALPHANUM | URI.H_DASH);
          if (k > i) {
            if (charAt(k - 1) == '-')
              fail("Illegal character in hostname", k - 1); 
            i = k;
          } 
        } 
        k = scan(i, param1Int2, '.');
        if (k <= i)
          break; 
        i = k;
      } while (i < param1Int2);
      if (i < param1Int2 && !at(i, param1Int2, ':'))
        fail("Illegal character in hostname", i); 
      if (j < 0)
        failExpecting("hostname", param1Int1); 
      if (j > param1Int1 && !URI.match(charAt(j), 0L, URI.H_ALPHA))
        fail("Illegal character in hostname", j); 
      URI.this.host = substring(param1Int1, i);
      return i;
    }
    
    Parser(String param1String) {
      this.ipv6byteCount = 0;
      this.input = param1String;
      URI.this.string = param1String;
    }
    
    private int parseIPv6Reference(int param1Int1, int param1Int2) throws URISyntaxException {
      int i = param1Int1;
      boolean bool = false;
      int j = scanHexSeq(i, param1Int2);
      if (j > i) {
        i = j;
        if (at(i, param1Int2, "::")) {
          bool = true;
          i = scanHexPost(i + 2, param1Int2);
        } else if (at(i, param1Int2, ':')) {
          i = takeIPv4Address(i + 1, param1Int2, "IPv4 address");
          this.ipv6byteCount += 4;
        } 
      } else if (at(i, param1Int2, "::")) {
        bool = true;
        i = scanHexPost(i + 2, param1Int2);
      } 
      if (i < param1Int2)
        fail("Malformed IPv6 address", param1Int1); 
      if (this.ipv6byteCount > 16)
        fail("IPv6 address too long", param1Int1); 
      if (!bool && this.ipv6byteCount < 16)
        fail("IPv6 address too short", param1Int1); 
      if (bool && this.ipv6byteCount == 16)
        fail("Malformed IPv6 address", param1Int1); 
      return i;
    }
    
    private int scanHexPost(int param1Int1, int param1Int2) throws URISyntaxException {
      int i = param1Int1;
      if (i == param1Int2)
        return i; 
      int j = scanHexSeq(i, param1Int2);
      if (j > i) {
        i = j;
        if (at(i, param1Int2, ':')) {
          i++;
          i = takeIPv4Address(i, param1Int2, "hex digits or IPv4 address");
          this.ipv6byteCount += 4;
        } 
      } else {
        i = takeIPv4Address(i, param1Int2, "hex digits or IPv4 address");
        this.ipv6byteCount += 4;
      } 
      return i;
    }
    
    private int scanHexSeq(int param1Int1, int param1Int2) throws URISyntaxException {
      int i = param1Int1;
      int j = scan(i, param1Int2, URI.L_HEX, URI.H_HEX);
      if (j <= i)
        return -1; 
      if (at(j, param1Int2, '.'))
        return -1; 
      if (j > i + 4)
        fail("IPv6 hexadecimal digit sequence too long", i); 
      this.ipv6byteCount += 2;
      i = j;
      while (i < param1Int2 && 
        at(i, param1Int2, ':')) {
        if (at(i + 1, param1Int2, ':'))
          break; 
        i++;
        j = scan(i, param1Int2, URI.L_HEX, URI.H_HEX);
        if (j <= i)
          failExpecting("digits for an IPv6 address", i); 
        if (at(j, param1Int2, '.')) {
          i--;
          break;
        } 
        if (j > i + 4)
          fail("IPv6 hexadecimal digit sequence too long", i); 
        this.ipv6byteCount += 2;
        i = j;
      } 
      return i;
    }
  }
}
