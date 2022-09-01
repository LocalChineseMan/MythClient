package javax.crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.lang.reflect.Constructor;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

final class CryptoPolicyParser {
  private Vector<GrantEntry> grantEntries = new Vector<>();
  
  private StreamTokenizer st;
  
  private int lookahead;
  
  void read(Reader paramReader) throws ParsingException, IOException {
    if (!(paramReader instanceof BufferedReader))
      paramReader = new BufferedReader(paramReader); 
    this.st = new StreamTokenizer(paramReader);
    this.st.resetSyntax();
    this.st.wordChars(97, 122);
    this.st.wordChars(65, 90);
    this.st.wordChars(46, 46);
    this.st.wordChars(48, 57);
    this.st.wordChars(95, 95);
    this.st.wordChars(36, 36);
    this.st.wordChars(160, 255);
    this.st.whitespaceChars(0, 32);
    this.st.commentChar(47);
    this.st.quoteChar(39);
    this.st.quoteChar(34);
    this.st.lowerCaseMode(false);
    this.st.ordinaryChar(47);
    this.st.slashSlashComments(true);
    this.st.slashStarComments(true);
    this.st.parseNumbers();
    Hashtable hashtable = null;
    this.lookahead = this.st.nextToken();
    while (this.lookahead != -1) {
      if (peek("grant")) {
        GrantEntry grantEntry = parseGrantEntry(hashtable);
        if (grantEntry != null)
          this.grantEntries.addElement(grantEntry); 
      } else {
        throw new ParsingException(this.st.lineno(), "expected grant statement");
      } 
      match(";");
    } 
  }
  
  private GrantEntry parseGrantEntry(Hashtable<String, Vector<String>> paramHashtable) throws ParsingException, IOException {
    GrantEntry grantEntry = new GrantEntry();
    match("grant");
    match("{");
    while (!peek("}")) {
      if (peek("Permission")) {
        CryptoPermissionEntry cryptoPermissionEntry = parsePermissionEntry(paramHashtable);
        grantEntry.add(cryptoPermissionEntry);
        match(";");
        continue;
      } 
      throw new ParsingException(this.st
          .lineno(), "expected permission entry");
    } 
    match("}");
    return grantEntry;
  }
  
  private CryptoPermissionEntry parsePermissionEntry(Hashtable<String, Vector<String>> paramHashtable) throws ParsingException, IOException {
    CryptoPermissionEntry cryptoPermissionEntry = new CryptoPermissionEntry();
    match("Permission");
    cryptoPermissionEntry.cryptoPermission = match("permission type");
    if (cryptoPermissionEntry.cryptoPermission.equals("javax.crypto.CryptoAllPermission")) {
      cryptoPermissionEntry.alg = "CryptoAllPermission";
      cryptoPermissionEntry.maxKeySize = Integer.MAX_VALUE;
      return cryptoPermissionEntry;
    } 
    if (peek("\"")) {
      cryptoPermissionEntry.alg = match("quoted string").toUpperCase(Locale.ENGLISH);
    } else if (peek("*")) {
      match("*");
      cryptoPermissionEntry.alg = "*";
    } else {
      throw new ParsingException(this.st.lineno(), "Missing the algorithm name");
    } 
    peekAndMatch(",");
    if (peek("\""))
      cryptoPermissionEntry.exemptionMechanism = match("quoted string").toUpperCase(Locale.ENGLISH); 
    peekAndMatch(",");
    if (!isConsistent(cryptoPermissionEntry.alg, cryptoPermissionEntry.exemptionMechanism, paramHashtable))
      throw new ParsingException(this.st.lineno(), "Inconsistent policy"); 
    if (peek("number")) {
      cryptoPermissionEntry.maxKeySize = match();
    } else if (peek("*")) {
      match("*");
      cryptoPermissionEntry.maxKeySize = Integer.MAX_VALUE;
    } else {
      if (!peek(";"))
        throw new ParsingException(this.st.lineno(), "Missing the maximum allowable key size"); 
      cryptoPermissionEntry.maxKeySize = Integer.MAX_VALUE;
    } 
    peekAndMatch(",");
    if (peek("\"")) {
      String str = match("quoted string");
      Vector<Integer> vector = new Vector(1);
      while (peek(",")) {
        match(",");
        if (peek("number")) {
          vector.addElement(new Integer(match()));
          continue;
        } 
        if (peek("*")) {
          match("*");
          vector.addElement(new Integer(2147483647));
          continue;
        } 
        throw new ParsingException(this.st.lineno(), "Expecting an integer");
      } 
      Integer[] arrayOfInteger = new Integer[vector.size()];
      vector.copyInto((Object[])arrayOfInteger);
      cryptoPermissionEntry.checkParam = true;
      cryptoPermissionEntry.algParamSpec = getInstance(str, arrayOfInteger);
    } 
    return cryptoPermissionEntry;
  }
  
  private static final AlgorithmParameterSpec getInstance(String paramString, Integer[] paramArrayOfInteger) throws ParsingException {
    AlgorithmParameterSpec algorithmParameterSpec = null;
    try {
      Class<?> clazz = Class.forName(paramString);
      Class[] arrayOfClass = new Class[paramArrayOfInteger.length];
      for (byte b = 0; b < paramArrayOfInteger.length; b++)
        arrayOfClass[b] = int.class; 
      Constructor<?> constructor = clazz.getConstructor(arrayOfClass);
      algorithmParameterSpec = (AlgorithmParameterSpec)constructor.newInstance((Object[])paramArrayOfInteger);
    } catch (Exception exception) {
      throw new ParsingException("Cannot call the constructor of " + paramString + exception);
    } 
    return algorithmParameterSpec;
  }
  
  private boolean peekAndMatch(String paramString) throws ParsingException, IOException {
    if (peek(paramString)) {
      match(paramString);
      return true;
    } 
    return false;
  }
  
  private boolean peek(String paramString) {
    boolean bool = false;
    switch (this.lookahead) {
      case -3:
        if (paramString.equalsIgnoreCase(this.st.sval))
          bool = true; 
        break;
      case -2:
        if (paramString.equalsIgnoreCase("number"))
          bool = true; 
        break;
      case 44:
        if (paramString.equals(","))
          bool = true; 
        break;
      case 123:
        if (paramString.equals("{"))
          bool = true; 
        break;
      case 125:
        if (paramString.equals("}"))
          bool = true; 
        break;
      case 34:
        if (paramString.equals("\""))
          bool = true; 
        break;
      case 42:
        if (paramString.equals("*"))
          bool = true; 
        break;
      case 59:
        if (paramString.equals(";"))
          bool = true; 
        break;
    } 
    return bool;
  }
  
  private int match() throws ParsingException, IOException {
    int i = -1;
    int j = this.st.lineno();
    String str = null;
    switch (this.lookahead) {
      case -2:
        i = (int)this.st.nval;
        if (i < 0)
          str = String.valueOf(this.st.nval); 
        this.lookahead = this.st.nextToken();
        break;
      default:
        str = this.st.sval;
        break;
    } 
    if (i <= 0)
      throw new ParsingException(j, "a non-negative number", str); 
    return i;
  }
  
  private String match(String paramString) throws ParsingException, IOException {
    String str = null;
    switch (this.lookahead) {
      case -2:
        throw new ParsingException(this.st.lineno(), paramString, "number " + 
            String.valueOf(this.st.nval));
      case -1:
        throw new ParsingException("expected " + paramString + ", read end of file");
      case -3:
        if (paramString.equalsIgnoreCase(this.st.sval)) {
          this.lookahead = this.st.nextToken();
        } else if (paramString.equalsIgnoreCase("permission type")) {
          str = this.st.sval;
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, this.st.sval);
        } 
        return str;
      case 34:
        if (paramString.equalsIgnoreCase("quoted string")) {
          str = this.st.sval;
          this.lookahead = this.st.nextToken();
        } else if (paramString.equalsIgnoreCase("permission type")) {
          str = this.st.sval;
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, this.st.sval);
        } 
        return str;
      case 44:
        if (paramString.equals(",")) {
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, ",");
        } 
        return str;
      case 123:
        if (paramString.equals("{")) {
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, "{");
        } 
        return str;
      case 125:
        if (paramString.equals("}")) {
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, "}");
        } 
        return str;
      case 59:
        if (paramString.equals(";")) {
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, ";");
        } 
        return str;
      case 42:
        if (paramString.equals("*")) {
          this.lookahead = this.st.nextToken();
        } else {
          throw new ParsingException(this.st.lineno(), paramString, "*");
        } 
        return str;
    } 
    throw new ParsingException(this.st.lineno(), paramString, new String(new char[] { (char)this.lookahead }));
  }
  
  CryptoPermission[] getPermissions() {
    Vector<CryptoAllPermission> vector = new Vector();
    Enumeration<GrantEntry> enumeration = this.grantEntries.elements();
    while (enumeration.hasMoreElements()) {
      GrantEntry grantEntry = enumeration.nextElement();
      Enumeration<CryptoPermissionEntry> enumeration1 = grantEntry.permissionElements();
      while (enumeration1.hasMoreElements()) {
        CryptoPermissionEntry cryptoPermissionEntry = enumeration1.nextElement();
        if (cryptoPermissionEntry.cryptoPermission.equals("javax.crypto.CryptoAllPermission")) {
          vector.addElement(CryptoAllPermission.INSTANCE);
          continue;
        } 
        if (cryptoPermissionEntry.checkParam) {
          vector.addElement(new CryptoPermission(cryptoPermissionEntry.alg, cryptoPermissionEntry.maxKeySize, cryptoPermissionEntry.algParamSpec, cryptoPermissionEntry.exemptionMechanism));
          continue;
        } 
        vector.addElement(new CryptoPermission(cryptoPermissionEntry.alg, cryptoPermissionEntry.maxKeySize, cryptoPermissionEntry.exemptionMechanism));
      } 
    } 
    CryptoPermission[] arrayOfCryptoPermission = new CryptoPermission[vector.size()];
    vector.copyInto((Object[])arrayOfCryptoPermission);
    return arrayOfCryptoPermission;
  }
  
  private boolean isConsistent(String paramString1, String paramString2, Hashtable<String, Vector<String>> paramHashtable) {
    Vector<String> vector;
    String str = (paramString2 == null) ? "none" : paramString2;
    if (paramHashtable == null) {
      paramHashtable = new Hashtable<>();
      vector = new Vector(1);
      vector.addElement(str);
      paramHashtable.put(paramString1, vector);
      return true;
    } 
    if (paramHashtable.containsKey("CryptoAllPermission"))
      return false; 
    if (paramHashtable.containsKey(paramString1)) {
      vector = paramHashtable.get(paramString1);
      if (vector.contains(str))
        return false; 
    } else {
      vector = new Vector(1);
    } 
    vector.addElement(str);
    paramHashtable.put(paramString1, vector);
    return true;
  }
  
  private static class GrantEntry {
    private Vector<CryptoPolicyParser.CryptoPermissionEntry> permissionEntries = new Vector<>();
    
    void add(CryptoPolicyParser.CryptoPermissionEntry param1CryptoPermissionEntry) {
      this.permissionEntries.addElement(param1CryptoPermissionEntry);
    }
    
    boolean remove(CryptoPolicyParser.CryptoPermissionEntry param1CryptoPermissionEntry) {
      return this.permissionEntries.removeElement(param1CryptoPermissionEntry);
    }
    
    boolean contains(CryptoPolicyParser.CryptoPermissionEntry param1CryptoPermissionEntry) {
      return this.permissionEntries.contains(param1CryptoPermissionEntry);
    }
    
    Enumeration<CryptoPolicyParser.CryptoPermissionEntry> permissionElements() {
      return this.permissionEntries.elements();
    }
  }
  
  private static class CryptoPermissionEntry {
    int maxKeySize = 0;
    
    String alg = null;
    
    String exemptionMechanism = null;
    
    boolean checkParam = false;
    
    AlgorithmParameterSpec algParamSpec = null;
    
    String cryptoPermission;
    
    public int hashCode() {
      int i = this.cryptoPermission.hashCode();
      if (this.alg != null)
        i ^= this.alg.hashCode(); 
      if (this.exemptionMechanism != null)
        i ^= this.exemptionMechanism.hashCode(); 
      i ^= this.maxKeySize;
      if (this.checkParam)
        i ^= 0x64; 
      if (this.algParamSpec != null)
        i ^= this.algParamSpec.hashCode(); 
      return i;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof CryptoPermissionEntry))
        return false; 
      CryptoPermissionEntry cryptoPermissionEntry = (CryptoPermissionEntry)param1Object;
      if (this.cryptoPermission == null) {
        if (cryptoPermissionEntry.cryptoPermission != null)
          return false; 
      } else if (!this.cryptoPermission.equals(cryptoPermissionEntry.cryptoPermission)) {
        return false;
      } 
      if (this.alg == null) {
        if (cryptoPermissionEntry.alg != null)
          return false; 
      } else if (!this.alg.equalsIgnoreCase(cryptoPermissionEntry.alg)) {
        return false;
      } 
      if (this.maxKeySize != cryptoPermissionEntry.maxKeySize)
        return false; 
      if (this.checkParam != cryptoPermissionEntry.checkParam)
        return false; 
      if (this.algParamSpec == null) {
        if (cryptoPermissionEntry.algParamSpec != null)
          return false; 
      } else if (!this.algParamSpec.equals(cryptoPermissionEntry.algParamSpec)) {
        return false;
      } 
      return true;
    }
  }
  
  static final class CryptoPolicyParser {}
}
