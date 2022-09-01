package java.awt.datatransfer;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class MimeTypeParameterList implements Cloneable {
  private Hashtable<String, String> parameters;
  
  private static final String TSPECIALS = "()<>@,;:\\\"/[]?=";
  
  public MimeTypeParameterList() {
    this.parameters = new Hashtable<>();
  }
  
  public MimeTypeParameterList(String paramString) throws MimeTypeParseException {
    this.parameters = new Hashtable<>();
    parse(paramString);
  }
  
  public int hashCode() {
    int i = 47721858;
    String str = null;
    Enumeration<String> enumeration = getNames();
    while (enumeration.hasMoreElements()) {
      str = enumeration.nextElement();
      i += str.hashCode();
      i += get(str).hashCode();
    } 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof MimeTypeParameterList))
      return false; 
    MimeTypeParameterList mimeTypeParameterList = (MimeTypeParameterList)paramObject;
    if (size() != mimeTypeParameterList.size())
      return false; 
    String str1 = null;
    String str2 = null;
    String str3 = null;
    Set<Map.Entry<String, String>> set = this.parameters.entrySet();
    Iterator<Map.Entry<String, String>> iterator = set.iterator();
    Map.Entry entry = null;
    while (iterator.hasNext()) {
      entry = iterator.next();
      str1 = (String)entry.getKey();
      str2 = (String)entry.getValue();
      str3 = mimeTypeParameterList.parameters.get(str1);
      if (str2 == null || str3 == null) {
        if (str2 != str3)
          return false; 
        continue;
      } 
      if (!str2.equals(str3))
        return false; 
    } 
    return true;
  }
  
  protected void parse(String paramString) throws MimeTypeParseException {
    int i = paramString.length();
    if (i > 0) {
      int j = skipWhiteSpace(paramString, 0);
      int k = 0;
      if (j < i) {
        char c = paramString.charAt(j);
        while (j < i && c == ';') {
          j++;
          j = skipWhiteSpace(paramString, j);
          if (j < i) {
            k = j;
            c = paramString.charAt(j);
            while (j < i && isTokenChar(c)) {
              j++;
              c = paramString.charAt(j);
            } 
            String str = paramString.substring(k, j).toLowerCase();
            j = skipWhiteSpace(paramString, j);
            if (j < i && paramString.charAt(j) == '=') {
              j++;
              j = skipWhiteSpace(paramString, j);
              if (j < i) {
                String str1;
                c = paramString.charAt(j);
                if (c == '"') {
                  k = ++j;
                  if (j < i) {
                    boolean bool = false;
                    while (j < i && !bool) {
                      c = paramString.charAt(j);
                      if (c == '\\') {
                        j += 2;
                        continue;
                      } 
                      if (c == '"') {
                        bool = true;
                        continue;
                      } 
                      j++;
                    } 
                    if (c == '"') {
                      str1 = unquote(paramString.substring(k, j));
                      j++;
                    } else {
                      throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
                    } 
                  } else {
                    throw new MimeTypeParseException("Encountered unterminated quoted parameter value.");
                  } 
                } else if (isTokenChar(c)) {
                  k = j;
                  boolean bool = false;
                  while (j < i && !bool) {
                    c = paramString.charAt(j);
                    if (isTokenChar(c)) {
                      j++;
                      continue;
                    } 
                    bool = true;
                  } 
                  str1 = paramString.substring(k, j);
                } else {
                  throw new MimeTypeParseException("Unexpected character encountered at index " + j);
                } 
                this.parameters.put(str, str1);
              } else {
                throw new MimeTypeParseException("Couldn't find a value for parameter named " + str);
              } 
            } else {
              throw new MimeTypeParseException("Couldn't find the '=' that separates a parameter name from its value.");
            } 
          } else {
            throw new MimeTypeParseException("Couldn't find parameter name");
          } 
          j = skipWhiteSpace(paramString, j);
          if (j < i)
            c = paramString.charAt(j); 
        } 
        if (j < i)
          throw new MimeTypeParseException("More characters encountered in input than expected."); 
      } 
    } 
  }
  
  public int size() {
    return this.parameters.size();
  }
  
  public boolean isEmpty() {
    return this.parameters.isEmpty();
  }
  
  public String get(String paramString) {
    return this.parameters.get(paramString.trim().toLowerCase());
  }
  
  public void set(String paramString1, String paramString2) {
    this.parameters.put(paramString1.trim().toLowerCase(), paramString2);
  }
  
  public void remove(String paramString) {
    this.parameters.remove(paramString.trim().toLowerCase());
  }
  
  public Enumeration<String> getNames() {
    return this.parameters.keys();
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(this.parameters.size() * 16);
    Enumeration<String> enumeration = this.parameters.keys();
    while (enumeration.hasMoreElements()) {
      stringBuilder.append("; ");
      String str = enumeration.nextElement();
      stringBuilder.append(str);
      stringBuilder.append('=');
      stringBuilder.append(quote(this.parameters.get(str)));
    } 
    return stringBuilder.toString();
  }
  
  public Object clone() {
    MimeTypeParameterList mimeTypeParameterList = null;
    try {
      mimeTypeParameterList = (MimeTypeParameterList)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {}
    mimeTypeParameterList.parameters = (Hashtable<String, String>)this.parameters.clone();
    return mimeTypeParameterList;
  }
  
  private static boolean isTokenChar(char paramChar) {
    return (paramChar > ' ' && paramChar < '' && "()<>@,;:\\\"/[]?=".indexOf(paramChar) < 0);
  }
  
  private static int skipWhiteSpace(String paramString, int paramInt) {
    int i = paramString.length();
    if (paramInt < i) {
      char c = paramString.charAt(paramInt);
      while (paramInt < i && Character.isWhitespace(c)) {
        paramInt++;
        c = paramString.charAt(paramInt);
      } 
    } 
    return paramInt;
  }
  
  private static String quote(String paramString) {
    boolean bool = false;
    int i = paramString.length();
    for (byte b = 0; b < i && !bool; b++)
      bool = !isTokenChar(paramString.charAt(b)) ? true : false; 
    if (bool) {
      StringBuilder stringBuilder = new StringBuilder((int)(i * 1.5D));
      stringBuilder.append('"');
      for (byte b1 = 0; b1 < i; b1++) {
        char c = paramString.charAt(b1);
        if (c == '\\' || c == '"')
          stringBuilder.append('\\'); 
        stringBuilder.append(c);
      } 
      stringBuilder.append('"');
      return stringBuilder.toString();
    } 
    return paramString;
  }
  
  private static String unquote(String paramString) {
    int i = paramString.length();
    StringBuilder stringBuilder = new StringBuilder(i);
    boolean bool = false;
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (!bool && c != '\\') {
        stringBuilder.append(c);
      } else if (bool) {
        stringBuilder.append(c);
        bool = false;
      } else {
        bool = true;
      } 
    } 
    return stringBuilder.toString();
  }
}
