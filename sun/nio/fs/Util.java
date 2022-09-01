package sun.nio.fs;

import java.nio.charset.Charset;
import java.nio.file.LinkOption;
import java.security.AccessController;
import java.util.HashSet;
import java.util.Set;
import sun.security.action.GetPropertyAction;

class Util {
  private static final Charset jnuEncoding = Charset.forName(
      AccessController.<String>doPrivileged(new GetPropertyAction("sun.jnu.encoding")));
  
  static Charset jnuEncoding() {
    return jnuEncoding;
  }
  
  static byte[] toBytes(String paramString) {
    return paramString.getBytes(jnuEncoding);
  }
  
  static String toString(byte[] paramArrayOfbyte) {
    return new String(paramArrayOfbyte, jnuEncoding);
  }
  
  static String[] split(String paramString, char paramChar) {
    byte b1 = 0;
    for (byte b2 = 0; b2 < paramString.length(); b2++) {
      if (paramString.charAt(b2) == paramChar)
        b1++; 
    } 
    String[] arrayOfString = new String[b1 + 1];
    byte b3 = 0;
    int i = 0;
    for (byte b4 = 0; b4 < paramString.length(); b4++) {
      if (paramString.charAt(b4) == paramChar) {
        arrayOfString[b3++] = paramString.substring(i, b4);
        i = b4 + 1;
      } 
    } 
    arrayOfString[b3] = paramString.substring(i, paramString.length());
    return arrayOfString;
  }
  
  @SafeVarargs
  static <E> Set<E> newSet(E... paramVarArgs) {
    HashSet<E> hashSet = new HashSet();
    for (E e : paramVarArgs)
      hashSet.add(e); 
    return hashSet;
  }
  
  @SafeVarargs
  static <E> Set<E> newSet(Set<E> paramSet, E... paramVarArgs) {
    HashSet<E> hashSet = new HashSet<>(paramSet);
    for (E e : paramVarArgs)
      hashSet.add(e); 
    return hashSet;
  }
  
  static boolean followLinks(LinkOption... paramVarArgs) {
    boolean bool = true;
    for (LinkOption linkOption : paramVarArgs) {
      if (linkOption == LinkOption.NOFOLLOW_LINKS) {
        bool = false;
      } else {
        if (linkOption == null)
          throw new NullPointerException(); 
        throw new AssertionError("Should not get here");
      } 
    } 
    return bool;
  }
}
