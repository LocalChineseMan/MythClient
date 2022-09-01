package com.sun.org.apache.xerces.internal.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class URI implements Serializable {
  static final long serialVersionUID = 1601921774685357214L;
  
  public static class MalformedURIException extends IOException {
    static final long serialVersionUID = -6695054834342951930L;
    
    public MalformedURIException() {}
    
    public MalformedURIException(String p_msg) {
      super(p_msg);
    }
  }
  
  private static final byte[] fgLookupTable = new byte[128];
  
  private static final int RESERVED_CHARACTERS = 1;
  
  private static final int MARK_CHARACTERS = 2;
  
  private static final int SCHEME_CHARACTERS = 4;
  
  private static final int USERINFO_CHARACTERS = 8;
  
  private static final int ASCII_ALPHA_CHARACTERS = 16;
  
  private static final int ASCII_DIGIT_CHARACTERS = 32;
  
  private static final int ASCII_HEX_CHARACTERS = 64;
  
  private static final int PATH_CHARACTERS = 128;
  
  private static final int MASK_ALPHA_NUMERIC = 48;
  
  private static final int MASK_UNRESERVED_MASK = 50;
  
  private static final int MASK_URI_CHARACTER = 51;
  
  private static final int MASK_SCHEME_CHARACTER = 52;
  
  private static final int MASK_USERINFO_CHARACTER = 58;
  
  private static final int MASK_PATH_CHARACTER = 178;
  
  static {
    int i;
    for (i = 48; i <= 57; i++)
      fgLookupTable[i] = (byte)(fgLookupTable[i] | 0x60); 
    for (i = 65; i <= 70; i++) {
      fgLookupTable[i] = (byte)(fgLookupTable[i] | 0x50);
      fgLookupTable[i + 32] = (byte)(fgLookupTable[i + 32] | 0x50);
    } 
    for (i = 71; i <= 90; i++) {
      fgLookupTable[i] = (byte)(fgLookupTable[i] | 0x10);
      fgLookupTable[i + 32] = (byte)(fgLookupTable[i + 32] | 0x10);
    } 
    fgLookupTable[59] = (byte)(fgLookupTable[59] | 0x1);
    fgLookupTable[47] = (byte)(fgLookupTable[47] | 0x1);
    fgLookupTable[63] = (byte)(fgLookupTable[63] | 0x1);
    fgLookupTable[58] = (byte)(fgLookupTable[58] | 0x1);
    fgLookupTable[64] = (byte)(fgLookupTable[64] | 0x1);
    fgLookupTable[38] = (byte)(fgLookupTable[38] | 0x1);
    fgLookupTable[61] = (byte)(fgLookupTable[61] | 0x1);
    fgLookupTable[43] = (byte)(fgLookupTable[43] | 0x1);
    fgLookupTable[36] = (byte)(fgLookupTable[36] | 0x1);
    fgLookupTable[44] = (byte)(fgLookupTable[44] | 0x1);
    fgLookupTable[91] = (byte)(fgLookupTable[91] | 0x1);
    fgLookupTable[93] = (byte)(fgLookupTable[93] | 0x1);
    fgLookupTable[45] = (byte)(fgLookupTable[45] | 0x2);
    fgLookupTable[95] = (byte)(fgLookupTable[95] | 0x2);
    fgLookupTable[46] = (byte)(fgLookupTable[46] | 0x2);
    fgLookupTable[33] = (byte)(fgLookupTable[33] | 0x2);
    fgLookupTable[126] = (byte)(fgLookupTable[126] | 0x2);
    fgLookupTable[42] = (byte)(fgLookupTable[42] | 0x2);
    fgLookupTable[39] = (byte)(fgLookupTable[39] | 0x2);
    fgLookupTable[40] = (byte)(fgLookupTable[40] | 0x2);
    fgLookupTable[41] = (byte)(fgLookupTable[41] | 0x2);
    fgLookupTable[43] = (byte)(fgLookupTable[43] | 0x4);
    fgLookupTable[45] = (byte)(fgLookupTable[45] | 0x4);
    fgLookupTable[46] = (byte)(fgLookupTable[46] | 0x4);
    fgLookupTable[59] = (byte)(fgLookupTable[59] | 0x8);
    fgLookupTable[58] = (byte)(fgLookupTable[58] | 0x8);
    fgLookupTable[38] = (byte)(fgLookupTable[38] | 0x8);
    fgLookupTable[61] = (byte)(fgLookupTable[61] | 0x8);
    fgLookupTable[43] = (byte)(fgLookupTable[43] | 0x8);
    fgLookupTable[36] = (byte)(fgLookupTable[36] | 0x8);
    fgLookupTable[44] = (byte)(fgLookupTable[44] | 0x8);
    fgLookupTable[59] = (byte)(fgLookupTable[59] | 0x80);
    fgLookupTable[47] = (byte)(fgLookupTable[47] | 0x80);
    fgLookupTable[58] = (byte)(fgLookupTable[58] | 0x80);
    fgLookupTable[64] = (byte)(fgLookupTable[64] | 0x80);
    fgLookupTable[38] = (byte)(fgLookupTable[38] | 0x80);
    fgLookupTable[61] = (byte)(fgLookupTable[61] | 0x80);
    fgLookupTable[43] = (byte)(fgLookupTable[43] | 0x80);
    fgLookupTable[36] = (byte)(fgLookupTable[36] | 0x80);
    fgLookupTable[44] = (byte)(fgLookupTable[44] | 0x80);
  }
  
  private String m_scheme = null;
  
  private String m_userinfo = null;
  
  private String m_host = null;
  
  private int m_port = -1;
  
  private String m_regAuthority = null;
  
  private String m_path = null;
  
  private String m_queryString = null;
  
  private String m_fragment = null;
  
  private static boolean DEBUG = false;
  
  public URI(URI p_other) {
    initialize(p_other);
  }
  
  public URI(String p_uriSpec) throws MalformedURIException {
    this((URI)null, p_uriSpec);
  }
  
  public URI(String p_uriSpec, boolean allowNonAbsoluteURI) throws MalformedURIException {
    this((URI)null, p_uriSpec, allowNonAbsoluteURI);
  }
  
  public URI(URI p_base, String p_uriSpec) throws MalformedURIException {
    initialize(p_base, p_uriSpec);
  }
  
  public URI(URI p_base, String p_uriSpec, boolean allowNonAbsoluteURI) throws MalformedURIException {
    initialize(p_base, p_uriSpec, allowNonAbsoluteURI);
  }
  
  public URI(String p_scheme, String p_schemeSpecificPart) throws MalformedURIException {
    if (p_scheme == null || p_scheme.trim().length() == 0)
      throw new MalformedURIException("Cannot construct URI with null/empty scheme!"); 
    if (p_schemeSpecificPart == null || p_schemeSpecificPart
      .trim().length() == 0)
      throw new MalformedURIException("Cannot construct URI with null/empty scheme-specific part!"); 
    setScheme(p_scheme);
    setPath(p_schemeSpecificPart);
  }
  
  public URI(String p_scheme, String p_host, String p_path, String p_queryString, String p_fragment) throws MalformedURIException {
    this(p_scheme, null, p_host, -1, p_path, p_queryString, p_fragment);
  }
  
  public URI(String p_scheme, String p_userinfo, String p_host, int p_port, String p_path, String p_queryString, String p_fragment) throws MalformedURIException {
    if (p_scheme == null || p_scheme.trim().length() == 0)
      throw new MalformedURIException("Scheme is required!"); 
    if (p_host == null) {
      if (p_userinfo != null)
        throw new MalformedURIException("Userinfo may not be specified if host is not specified!"); 
      if (p_port != -1)
        throw new MalformedURIException("Port may not be specified if host is not specified!"); 
    } 
    if (p_path != null) {
      if (p_path.indexOf('?') != -1 && p_queryString != null)
        throw new MalformedURIException("Query string cannot be specified in path and query string!"); 
      if (p_path.indexOf('#') != -1 && p_fragment != null)
        throw new MalformedURIException("Fragment cannot be specified in both the path and fragment!"); 
    } 
    setScheme(p_scheme);
    setHost(p_host);
    setPort(p_port);
    setUserinfo(p_userinfo);
    setPath(p_path);
    setQueryString(p_queryString);
    setFragment(p_fragment);
  }
  
  private void initialize(URI p_other) {
    this.m_scheme = p_other.getScheme();
    this.m_userinfo = p_other.getUserinfo();
    this.m_host = p_other.getHost();
    this.m_port = p_other.getPort();
    this.m_regAuthority = p_other.getRegBasedAuthority();
    this.m_path = p_other.getPath();
    this.m_queryString = p_other.getQueryString();
    this.m_fragment = p_other.getFragment();
  }
  
  private void initialize(URI p_base, String p_uriSpec, boolean allowNonAbsoluteURI) throws MalformedURIException {
    String uriSpec = p_uriSpec;
    int uriSpecLen = (uriSpec != null) ? uriSpec.length() : 0;
    if (p_base == null && uriSpecLen == 0) {
      if (allowNonAbsoluteURI) {
        this.m_path = "";
        return;
      } 
      throw new MalformedURIException("Cannot initialize URI with empty parameters.");
    } 
    if (uriSpecLen == 0) {
      initialize(p_base);
      return;
    } 
    int index = 0;
    int colonIdx = uriSpec.indexOf(':');
    if (colonIdx != -1) {
      int searchFrom = colonIdx - 1;
      int slashIdx = uriSpec.lastIndexOf('/', searchFrom);
      int queryIdx = uriSpec.lastIndexOf('?', searchFrom);
      int fragmentIdx = uriSpec.lastIndexOf('#', searchFrom);
      if (colonIdx == 0 || slashIdx != -1 || queryIdx != -1 || fragmentIdx != -1) {
        if (colonIdx == 0 || (p_base == null && fragmentIdx != 0 && !allowNonAbsoluteURI))
          throw new MalformedURIException("No scheme found in URI."); 
      } else {
        initializeScheme(uriSpec);
        index = this.m_scheme.length() + 1;
        if (colonIdx == uriSpecLen - 1 || uriSpec.charAt(colonIdx + 1) == '#')
          throw new MalformedURIException("Scheme specific part cannot be empty."); 
      } 
    } else if (p_base == null && uriSpec.indexOf('#') != 0 && !allowNonAbsoluteURI) {
      throw new MalformedURIException("No scheme found in URI.");
    } 
    if (index + 1 < uriSpecLen && uriSpec
      .charAt(index) == '/' && uriSpec.charAt(index + 1) == '/') {
      index += 2;
      int startPos = index;
      char testChar = Character.MIN_VALUE;
      while (index < uriSpecLen) {
        testChar = uriSpec.charAt(index);
        if (testChar == '/' || testChar == '?' || testChar == '#')
          break; 
        index++;
      } 
      if (index > startPos) {
        if (!initializeAuthority(uriSpec.substring(startPos, index)))
          index = startPos - 2; 
      } else {
        this.m_host = "";
      } 
    } 
    initializePath(uriSpec, index);
    if (p_base != null)
      absolutize(p_base); 
  }
  
  private void initialize(URI p_base, String p_uriSpec) throws MalformedURIException {
    String uriSpec = p_uriSpec;
    int uriSpecLen = (uriSpec != null) ? uriSpec.length() : 0;
    if (p_base == null && uriSpecLen == 0)
      throw new MalformedURIException("Cannot initialize URI with empty parameters."); 
    if (uriSpecLen == 0) {
      initialize(p_base);
      return;
    } 
    int index = 0;
    int colonIdx = uriSpec.indexOf(':');
    if (colonIdx != -1) {
      int searchFrom = colonIdx - 1;
      int slashIdx = uriSpec.lastIndexOf('/', searchFrom);
      int queryIdx = uriSpec.lastIndexOf('?', searchFrom);
      int fragmentIdx = uriSpec.lastIndexOf('#', searchFrom);
      if (colonIdx == 0 || slashIdx != -1 || queryIdx != -1 || fragmentIdx != -1) {
        if (colonIdx == 0 || (p_base == null && fragmentIdx != 0))
          throw new MalformedURIException("No scheme found in URI."); 
      } else {
        initializeScheme(uriSpec);
        index = this.m_scheme.length() + 1;
        if (colonIdx == uriSpecLen - 1 || uriSpec.charAt(colonIdx + 1) == '#')
          throw new MalformedURIException("Scheme specific part cannot be empty."); 
      } 
    } else if (p_base == null && uriSpec.indexOf('#') != 0) {
      throw new MalformedURIException("No scheme found in URI.");
    } 
    if (index + 1 < uriSpecLen && uriSpec
      .charAt(index) == '/' && uriSpec.charAt(index + 1) == '/') {
      index += 2;
      int startPos = index;
      char testChar = Character.MIN_VALUE;
      while (index < uriSpecLen) {
        testChar = uriSpec.charAt(index);
        if (testChar == '/' || testChar == '?' || testChar == '#')
          break; 
        index++;
      } 
      if (index > startPos) {
        if (!initializeAuthority(uriSpec.substring(startPos, index)))
          index = startPos - 2; 
      } else if (index < uriSpecLen) {
        this.m_host = "";
      } else {
        throw new MalformedURIException("Expected authority.");
      } 
    } 
    initializePath(uriSpec, index);
    if (p_base != null)
      absolutize(p_base); 
  }
  
  public void absolutize(URI p_base) {
    if (this.m_path.length() == 0 && this.m_scheme == null && this.m_host == null && this.m_regAuthority == null) {
      this.m_scheme = p_base.getScheme();
      this.m_userinfo = p_base.getUserinfo();
      this.m_host = p_base.getHost();
      this.m_port = p_base.getPort();
      this.m_regAuthority = p_base.getRegBasedAuthority();
      this.m_path = p_base.getPath();
      if (this.m_queryString == null) {
        this.m_queryString = p_base.getQueryString();
        if (this.m_fragment == null)
          this.m_fragment = p_base.getFragment(); 
      } 
      return;
    } 
    if (this.m_scheme == null) {
      this.m_scheme = p_base.getScheme();
    } else {
      return;
    } 
    if (this.m_host == null && this.m_regAuthority == null) {
      this.m_userinfo = p_base.getUserinfo();
      this.m_host = p_base.getHost();
      this.m_port = p_base.getPort();
      this.m_regAuthority = p_base.getRegBasedAuthority();
    } else {
      return;
    } 
    if (this.m_path.length() > 0 && this.m_path
      .startsWith("/"))
      return; 
    String path = "";
    String basePath = p_base.getPath();
    if (basePath != null && basePath.length() > 0) {
      int lastSlash = basePath.lastIndexOf('/');
      if (lastSlash != -1)
        path = basePath.substring(0, lastSlash + 1); 
    } else if (this.m_path.length() > 0) {
      path = "/";
    } 
    path = path.concat(this.m_path);
    int index = -1;
    while ((index = path.indexOf("/./")) != -1)
      path = path.substring(0, index + 1).concat(path.substring(index + 3)); 
    if (path.endsWith("/."))
      path = path.substring(0, path.length() - 1); 
    index = 1;
    int segIndex = -1;
    String tempString = null;
    while ((index = path.indexOf("/../", index)) > 0) {
      tempString = path.substring(0, path.indexOf("/../"));
      segIndex = tempString.lastIndexOf('/');
      if (segIndex != -1) {
        if (!tempString.substring(segIndex).equals("..")) {
          path = path.substring(0, segIndex + 1).concat(path.substring(index + 4));
          index = segIndex;
          continue;
        } 
        index += 4;
        continue;
      } 
      index += 4;
    } 
    if (path.endsWith("/..")) {
      tempString = path.substring(0, path.length() - 3);
      segIndex = tempString.lastIndexOf('/');
      if (segIndex != -1)
        path = path.substring(0, segIndex + 1); 
    } 
    this.m_path = path;
  }
  
  private void initializeScheme(String p_uriSpec) throws MalformedURIException {
    int uriSpecLen = p_uriSpec.length();
    int index = 0;
    String scheme = null;
    char testChar = Character.MIN_VALUE;
    while (index < uriSpecLen) {
      testChar = p_uriSpec.charAt(index);
      if (testChar == ':' || testChar == '/' || testChar == '?' || testChar == '#')
        break; 
      index++;
    } 
    scheme = p_uriSpec.substring(0, index);
    if (scheme.length() == 0)
      throw new MalformedURIException("No scheme found in URI."); 
    setScheme(scheme);
  }
  
  private boolean initializeAuthority(String p_uriSpec) {
    int index = 0;
    int start = 0;
    int end = p_uriSpec.length();
    char testChar = Character.MIN_VALUE;
    String userinfo = null;
    if (p_uriSpec.indexOf('@', start) != -1) {
      while (index < end) {
        testChar = p_uriSpec.charAt(index);
        if (testChar == '@')
          break; 
        index++;
      } 
      userinfo = p_uriSpec.substring(start, index);
      index++;
    } 
    String host = null;
    start = index;
    boolean hasPort = false;
    if (index < end)
      if (p_uriSpec.charAt(start) == '[') {
        int bracketIndex = p_uriSpec.indexOf(']', start);
        index = (bracketIndex != -1) ? bracketIndex : end;
        if (index + 1 < end && p_uriSpec.charAt(index + 1) == ':') {
          index++;
          hasPort = true;
        } else {
          index = end;
        } 
      } else {
        int colonIndex = p_uriSpec.lastIndexOf(':', end);
        index = (colonIndex > start) ? colonIndex : end;
        hasPort = (index != end);
      }  
    host = p_uriSpec.substring(start, index);
    int port = -1;
    if (host.length() > 0)
      if (hasPort) {
        start = ++index;
        while (index < end)
          index++; 
        String portStr = p_uriSpec.substring(start, index);
        if (portStr.length() > 0)
          try {
            port = Integer.parseInt(portStr);
            if (port == -1)
              port--; 
          } catch (NumberFormatException nfe) {
            port = -2;
          }  
      }  
    if (isValidServerBasedAuthority(host, port, userinfo)) {
      this.m_host = host;
      this.m_port = port;
      this.m_userinfo = userinfo;
      return true;
    } 
    if (isValidRegistryBasedAuthority(p_uriSpec)) {
      this.m_regAuthority = p_uriSpec;
      return true;
    } 
    return false;
  }
  
  private boolean isValidServerBasedAuthority(String host, int port, String userinfo) {
    if (!isWellFormedAddress(host))
      return false; 
    if (port < -1 || port > 65535)
      return false; 
    if (userinfo != null) {
      int index = 0;
      int end = userinfo.length();
      char testChar = Character.MIN_VALUE;
      while (index < end) {
        testChar = userinfo.charAt(index);
        if (testChar == '%') {
          if (index + 2 >= end || 
            !isHex(userinfo.charAt(index + 1)) || 
            !isHex(userinfo.charAt(index + 2)))
            return false; 
          index += 2;
        } else if (!isUserinfoCharacter(testChar)) {
          return false;
        } 
        index++;
      } 
    } 
    return true;
  }
  
  private boolean isValidRegistryBasedAuthority(String authority) {
    int index = 0;
    int end = authority.length();
    while (index < end) {
      char testChar = authority.charAt(index);
      if (testChar == '%') {
        if (index + 2 >= end || 
          !isHex(authority.charAt(index + 1)) || 
          !isHex(authority.charAt(index + 2)))
          return false; 
        index += 2;
      } else if (!isPathCharacter(testChar)) {
        return false;
      } 
      index++;
    } 
    return true;
  }
  
  private void initializePath(String p_uriSpec, int p_nStartIndex) throws MalformedURIException {
    if (p_uriSpec == null)
      throw new MalformedURIException("Cannot initialize path from null string!"); 
    int index = p_nStartIndex;
    int start = p_nStartIndex;
    int end = p_uriSpec.length();
    char testChar = Character.MIN_VALUE;
    if (start < end)
      if (getScheme() == null || p_uriSpec.charAt(start) == '/') {
        while (index < end) {
          testChar = p_uriSpec.charAt(index);
          if (testChar == '%') {
            if (index + 2 >= end || 
              !isHex(p_uriSpec.charAt(index + 1)) || 
              !isHex(p_uriSpec.charAt(index + 2)))
              throw new MalformedURIException("Path contains invalid escape sequence!"); 
            index += 2;
          } else if (!isPathCharacter(testChar)) {
            if (testChar == '?' || testChar == '#')
              break; 
            throw new MalformedURIException("Path contains invalid character: " + testChar);
          } 
          index++;
        } 
      } else {
        while (index < end) {
          testChar = p_uriSpec.charAt(index);
          if (testChar == '?' || testChar == '#')
            break; 
          if (testChar == '%') {
            if (index + 2 >= end || 
              !isHex(p_uriSpec.charAt(index + 1)) || 
              !isHex(p_uriSpec.charAt(index + 2)))
              throw new MalformedURIException("Opaque part contains invalid escape sequence!"); 
            index += 2;
          } else if (!isURICharacter(testChar)) {
            throw new MalformedURIException("Opaque part contains invalid character: " + testChar);
          } 
          index++;
        } 
      }  
    this.m_path = p_uriSpec.substring(start, index);
    if (testChar == '?') {
      start = ++index;
      while (index < end) {
        testChar = p_uriSpec.charAt(index);
        if (testChar == '#')
          break; 
        if (testChar == '%') {
          if (index + 2 >= end || 
            !isHex(p_uriSpec.charAt(index + 1)) || 
            !isHex(p_uriSpec.charAt(index + 2)))
            throw new MalformedURIException("Query string contains invalid escape sequence!"); 
          index += 2;
        } else if (!isURICharacter(testChar)) {
          throw new MalformedURIException("Query string contains invalid character: " + testChar);
        } 
        index++;
      } 
      this.m_queryString = p_uriSpec.substring(start, index);
    } 
    if (testChar == '#') {
      start = ++index;
      while (index < end) {
        testChar = p_uriSpec.charAt(index);
        if (testChar == '%') {
          if (index + 2 >= end || 
            !isHex(p_uriSpec.charAt(index + 1)) || 
            !isHex(p_uriSpec.charAt(index + 2)))
            throw new MalformedURIException("Fragment contains invalid escape sequence!"); 
          index += 2;
        } else if (!isURICharacter(testChar)) {
          throw new MalformedURIException("Fragment contains invalid character: " + testChar);
        } 
        index++;
      } 
      this.m_fragment = p_uriSpec.substring(start, index);
    } 
  }
  
  public String getScheme() {
    return this.m_scheme;
  }
  
  public String getSchemeSpecificPart() {
    StringBuilder schemespec = new StringBuilder();
    if (this.m_host != null || this.m_regAuthority != null) {
      schemespec.append("//");
      if (this.m_host != null) {
        if (this.m_userinfo != null) {
          schemespec.append(this.m_userinfo);
          schemespec.append('@');
        } 
        schemespec.append(this.m_host);
        if (this.m_port != -1) {
          schemespec.append(':');
          schemespec.append(this.m_port);
        } 
      } else {
        schemespec.append(this.m_regAuthority);
      } 
    } 
    if (this.m_path != null)
      schemespec.append(this.m_path); 
    if (this.m_queryString != null) {
      schemespec.append('?');
      schemespec.append(this.m_queryString);
    } 
    if (this.m_fragment != null) {
      schemespec.append('#');
      schemespec.append(this.m_fragment);
    } 
    return schemespec.toString();
  }
  
  public String getUserinfo() {
    return this.m_userinfo;
  }
  
  public String getHost() {
    return this.m_host;
  }
  
  public int getPort() {
    return this.m_port;
  }
  
  public String getRegBasedAuthority() {
    return this.m_regAuthority;
  }
  
  public String getAuthority() {
    StringBuilder authority = new StringBuilder();
    if (this.m_host != null || this.m_regAuthority != null) {
      authority.append("//");
      if (this.m_host != null) {
        if (this.m_userinfo != null) {
          authority.append(this.m_userinfo);
          authority.append('@');
        } 
        authority.append(this.m_host);
        if (this.m_port != -1) {
          authority.append(':');
          authority.append(this.m_port);
        } 
      } else {
        authority.append(this.m_regAuthority);
      } 
    } 
    return authority.toString();
  }
  
  public String getPath(boolean p_includeQueryString, boolean p_includeFragment) {
    StringBuilder pathString = new StringBuilder(this.m_path);
    if (p_includeQueryString && this.m_queryString != null) {
      pathString.append('?');
      pathString.append(this.m_queryString);
    } 
    if (p_includeFragment && this.m_fragment != null) {
      pathString.append('#');
      pathString.append(this.m_fragment);
    } 
    return pathString.toString();
  }
  
  public String getPath() {
    return this.m_path;
  }
  
  public String getQueryString() {
    return this.m_queryString;
  }
  
  public String getFragment() {
    return this.m_fragment;
  }
  
  public void setScheme(String p_scheme) throws MalformedURIException {
    if (p_scheme == null)
      throw new MalformedURIException("Cannot set scheme from null string!"); 
    if (!isConformantSchemeName(p_scheme))
      throw new MalformedURIException("The scheme is not conformant."); 
    this.m_scheme = p_scheme.toLowerCase();
  }
  
  public void setUserinfo(String p_userinfo) throws MalformedURIException {
    if (p_userinfo == null) {
      this.m_userinfo = null;
      return;
    } 
    if (this.m_host == null)
      throw new MalformedURIException("Userinfo cannot be set when host is null!"); 
    int index = 0;
    int end = p_userinfo.length();
    char testChar = Character.MIN_VALUE;
    while (index < end) {
      testChar = p_userinfo.charAt(index);
      if (testChar == '%') {
        if (index + 2 >= end || 
          !isHex(p_userinfo.charAt(index + 1)) || 
          !isHex(p_userinfo.charAt(index + 2)))
          throw new MalformedURIException("Userinfo contains invalid escape sequence!"); 
      } else if (!isUserinfoCharacter(testChar)) {
        throw new MalformedURIException("Userinfo contains invalid character:" + testChar);
      } 
      index++;
    } 
    this.m_userinfo = p_userinfo;
  }
  
  public void setHost(String p_host) throws MalformedURIException {
    if (p_host == null || p_host.length() == 0) {
      if (p_host != null)
        this.m_regAuthority = null; 
      this.m_host = p_host;
      this.m_userinfo = null;
      this.m_port = -1;
      return;
    } 
    if (!isWellFormedAddress(p_host))
      throw new MalformedURIException("Host is not a well formed address!"); 
    this.m_host = p_host;
    this.m_regAuthority = null;
  }
  
  public void setPort(int p_port) throws MalformedURIException {
    if (p_port >= 0 && p_port <= 65535) {
      if (this.m_host == null)
        throw new MalformedURIException("Port cannot be set when host is null!"); 
    } else if (p_port != -1) {
      throw new MalformedURIException("Invalid port number!");
    } 
    this.m_port = p_port;
  }
  
  public void setRegBasedAuthority(String authority) throws MalformedURIException {
    if (authority == null) {
      this.m_regAuthority = null;
      return;
    } 
    if (authority.length() < 1 || 
      !isValidRegistryBasedAuthority(authority) || authority
      .indexOf('/') != -1)
      throw new MalformedURIException("Registry based authority is not well formed."); 
    this.m_regAuthority = authority;
    this.m_host = null;
    this.m_userinfo = null;
    this.m_port = -1;
  }
  
  public void setPath(String p_path) throws MalformedURIException {
    if (p_path == null) {
      this.m_path = null;
      this.m_queryString = null;
      this.m_fragment = null;
    } else {
      initializePath(p_path, 0);
    } 
  }
  
  public void appendPath(String p_addToPath) throws MalformedURIException {
    if (p_addToPath == null || p_addToPath.trim().length() == 0)
      return; 
    if (!isURIString(p_addToPath))
      throw new MalformedURIException("Path contains invalid character!"); 
    if (this.m_path == null || this.m_path.trim().length() == 0) {
      if (p_addToPath.startsWith("/")) {
        this.m_path = p_addToPath;
      } else {
        this.m_path = "/" + p_addToPath;
      } 
    } else if (this.m_path.endsWith("/")) {
      if (p_addToPath.startsWith("/")) {
        this.m_path = this.m_path.concat(p_addToPath.substring(1));
      } else {
        this.m_path = this.m_path.concat(p_addToPath);
      } 
    } else if (p_addToPath.startsWith("/")) {
      this.m_path = this.m_path.concat(p_addToPath);
    } else {
      this.m_path = this.m_path.concat("/" + p_addToPath);
    } 
  }
  
  public void setQueryString(String p_queryString) throws MalformedURIException {
    if (p_queryString == null) {
      this.m_queryString = null;
    } else {
      if (!isGenericURI())
        throw new MalformedURIException("Query string can only be set for a generic URI!"); 
      if (getPath() == null)
        throw new MalformedURIException("Query string cannot be set when path is null!"); 
      if (!isURIString(p_queryString))
        throw new MalformedURIException("Query string contains invalid character!"); 
      this.m_queryString = p_queryString;
    } 
  }
  
  public void setFragment(String p_fragment) throws MalformedURIException {
    if (p_fragment == null) {
      this.m_fragment = null;
    } else {
      if (!isGenericURI())
        throw new MalformedURIException("Fragment can only be set for a generic URI!"); 
      if (getPath() == null)
        throw new MalformedURIException("Fragment cannot be set when path is null!"); 
      if (!isURIString(p_fragment))
        throw new MalformedURIException("Fragment contains invalid character!"); 
      this.m_fragment = p_fragment;
    } 
  }
  
  public boolean equals(Object p_test) {
    if (p_test instanceof URI) {
      URI testURI = (URI)p_test;
      if (((this.m_scheme == null && testURI.m_scheme == null) || (this.m_scheme != null && testURI.m_scheme != null && this.m_scheme
        
        .equals(testURI.m_scheme))) && ((this.m_userinfo == null && testURI.m_userinfo == null) || (this.m_userinfo != null && testURI.m_userinfo != null && this.m_userinfo
        
        .equals(testURI.m_userinfo))) && ((this.m_host == null && testURI.m_host == null) || (this.m_host != null && testURI.m_host != null && this.m_host
        
        .equals(testURI.m_host))) && this.m_port == testURI.m_port && ((this.m_path == null && testURI.m_path == null) || (this.m_path != null && testURI.m_path != null && this.m_path
        
        .equals(testURI.m_path))) && ((this.m_queryString == null && testURI.m_queryString == null) || (this.m_queryString != null && testURI.m_queryString != null && this.m_queryString
        
        .equals(testURI.m_queryString))) && ((this.m_fragment == null && testURI.m_fragment == null) || (this.m_fragment != null && testURI.m_fragment != null && this.m_fragment
        
        .equals(testURI.m_fragment))))
        return true; 
    } 
    return false;
  }
  
  public int hashCode() {
    int hash = 5;
    hash = 47 * hash + Objects.hashCode(this.m_scheme);
    hash = 47 * hash + Objects.hashCode(this.m_userinfo);
    hash = 47 * hash + Objects.hashCode(this.m_host);
    hash = 47 * hash + this.m_port;
    hash = 47 * hash + Objects.hashCode(this.m_path);
    hash = 47 * hash + Objects.hashCode(this.m_queryString);
    hash = 47 * hash + Objects.hashCode(this.m_fragment);
    return hash;
  }
  
  public String toString() {
    StringBuilder uriSpecString = new StringBuilder();
    if (this.m_scheme != null) {
      uriSpecString.append(this.m_scheme);
      uriSpecString.append(':');
    } 
    uriSpecString.append(getSchemeSpecificPart());
    return uriSpecString.toString();
  }
  
  public boolean isGenericURI() {
    return (this.m_host != null);
  }
  
  public boolean isAbsoluteURI() {
    return (this.m_scheme != null);
  }
  
  public static boolean isConformantSchemeName(String p_scheme) {
    if (p_scheme == null || p_scheme.trim().length() == 0)
      return false; 
    if (!isAlpha(p_scheme.charAt(0)))
      return false; 
    int schemeLength = p_scheme.length();
    for (int i = 1; i < schemeLength; i++) {
      char testChar = p_scheme.charAt(i);
      if (!isSchemeCharacter(testChar))
        return false; 
    } 
    return true;
  }
  
  public static boolean isWellFormedAddress(String address) {
    if (address == null)
      return false; 
    int addrLength = address.length();
    if (addrLength == 0)
      return false; 
    if (address.startsWith("["))
      return isWellFormedIPv6Reference(address); 
    if (address.startsWith(".") || address
      .startsWith("-") || address
      .endsWith("-"))
      return false; 
    int index = address.lastIndexOf('.');
    if (address.endsWith("."))
      index = address.substring(0, index).lastIndexOf('.'); 
    if (index + 1 < addrLength && isDigit(address.charAt(index + 1)))
      return isWellFormedIPv4Address(address); 
    if (addrLength > 255)
      return false; 
    int labelCharCount = 0;
    for (int i = 0; i < addrLength; i++) {
      char testChar = address.charAt(i);
      if (testChar == '.') {
        if (!isAlphanum(address.charAt(i - 1)))
          return false; 
        if (i + 1 < addrLength && !isAlphanum(address.charAt(i + 1)))
          return false; 
        labelCharCount = 0;
      } else {
        if (!isAlphanum(testChar) && testChar != '-')
          return false; 
        if (++labelCharCount > 63)
          return false; 
      } 
    } 
    return true;
  }
  
  public static boolean isWellFormedIPv4Address(String address) {
    int addrLength = address.length();
    int numDots = 0;
    int numDigits = 0;
    for (int i = 0; i < addrLength; i++) {
      char testChar = address.charAt(i);
      if (testChar == '.') {
        if ((i > 0 && !isDigit(address.charAt(i - 1))) || (i + 1 < addrLength && 
          !isDigit(address.charAt(i + 1))))
          return false; 
        numDigits = 0;
        if (++numDots > 3)
          return false; 
      } else {
        if (!isDigit(testChar))
          return false; 
        if (++numDigits > 3)
          return false; 
        if (numDigits == 3) {
          char first = address.charAt(i - 2);
          char second = address.charAt(i - 1);
          if (first >= '2' && (first != '2' || (second >= '5' && (second != '5' || testChar > '5'))))
            return false; 
        } 
      } 
    } 
    return (numDots == 3);
  }
  
  public static boolean isWellFormedIPv6Reference(String address) {
    int addrLength = address.length();
    int index = 1;
    int end = addrLength - 1;
    if (addrLength <= 2 || address.charAt(0) != '[' || address
      .charAt(end) != ']')
      return false; 
    int[] counter = new int[1];
    index = scanHexSequence(address, index, end, counter);
    if (index == -1)
      return false; 
    if (index == end)
      return (counter[0] == 8); 
    if (index + 1 < end && address.charAt(index) == ':') {
      if (address.charAt(index + 1) == ':') {
        counter[0] = counter[0] + 1;
        if (counter[0] + 1 > 8)
          return false; 
        index += 2;
        if (index == end)
          return true; 
      } else {
        return (counter[0] == 6 && 
          isWellFormedIPv4Address(address.substring(index + 1, end)));
      } 
    } else {
      return false;
    } 
    int prevCount = counter[0];
    index = scanHexSequence(address, index, end, counter);
    if (index != end) {
      if (index != -1)
        if (isWellFormedIPv4Address(address
            .substring((counter[0] > prevCount) ? (index + 1) : index, end))); 
      return false;
    } 
  }
  
  private static int scanHexSequence(String address, int index, int end, int[] counter) {
    int numDigits = 0;
    int start = index;
    for (; index < end; index++) {
      char testChar = address.charAt(index);
      if (testChar == ':') {
        counter[0] = counter[0] + 1;
        if (numDigits > 0 && counter[0] + 1 > 8)
          return -1; 
        if (numDigits == 0 || (index + 1 < end && address.charAt(index + 1) == ':'))
          return index; 
        numDigits = 0;
      } else {
        if (!isHex(testChar)) {
          if (testChar == '.' && numDigits < 4 && numDigits > 0 && counter[0] <= 6) {
            int back = index - numDigits - 1;
            return (back >= start) ? back : (back + 1);
          } 
          return -1;
        } 
        if (++numDigits > 4)
          return -1; 
      } 
    } 
    counter[0] = counter[0] + 1;
    return (numDigits > 0 && counter[0] + 1 <= 8) ? end : -1;
  }
  
  private static boolean isDigit(char p_char) {
    return (p_char >= '0' && p_char <= '9');
  }
  
  private static boolean isHex(char p_char) {
    return (p_char <= 'f' && (fgLookupTable[p_char] & 0x40) != 0);
  }
  
  private static boolean isAlpha(char p_char) {
    return ((p_char >= 'a' && p_char <= 'z') || (p_char >= 'A' && p_char <= 'Z'));
  }
  
  private static boolean isAlphanum(char p_char) {
    return (p_char <= 'z' && (fgLookupTable[p_char] & 0x30) != 0);
  }
  
  private static boolean isReservedCharacter(char p_char) {
    return (p_char <= ']' && (fgLookupTable[p_char] & 0x1) != 0);
  }
  
  private static boolean isUnreservedCharacter(char p_char) {
    return (p_char <= '~' && (fgLookupTable[p_char] & 0x32) != 0);
  }
  
  private static boolean isURICharacter(char p_char) {
    return (p_char <= '~' && (fgLookupTable[p_char] & 0x33) != 0);
  }
  
  private static boolean isSchemeCharacter(char p_char) {
    return (p_char <= 'z' && (fgLookupTable[p_char] & 0x34) != 0);
  }
  
  private static boolean isUserinfoCharacter(char p_char) {
    return (p_char <= 'z' && (fgLookupTable[p_char] & 0x3A) != 0);
  }
  
  private static boolean isPathCharacter(char p_char) {
    return (p_char <= '~' && (fgLookupTable[p_char] & 0xB2) != 0);
  }
  
  private static boolean isURIString(String p_uric) {
    if (p_uric == null)
      return false; 
    int end = p_uric.length();
    char testChar = Character.MIN_VALUE;
    for (int i = 0; i < end; i++) {
      testChar = p_uric.charAt(i);
      if (testChar == '%') {
        if (i + 2 >= end || 
          !isHex(p_uric.charAt(i + 1)) || 
          !isHex(p_uric.charAt(i + 2)))
          return false; 
        i += 2;
      } else if (!isURICharacter(testChar)) {
        return false;
      } 
    } 
    return true;
  }
  
  public URI() {}
}
