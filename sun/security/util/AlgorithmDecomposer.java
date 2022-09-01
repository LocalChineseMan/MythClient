package sun.security.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class AlgorithmDecomposer {
  private static final Pattern transPattern = Pattern.compile("/");
  
  private static final Pattern pattern = Pattern.compile("with|and", 2);
  
  public Set<String> decompose(String paramString) {
    if (paramString == null || paramString.length() == 0)
      return new HashSet<>(); 
    String[] arrayOfString = transPattern.split(paramString);
    HashSet<String> hashSet = new HashSet();
    for (String str : arrayOfString) {
      if (str != null && str.length() != 0) {
        String[] arrayOfString1 = pattern.split(str);
        for (String str1 : arrayOfString1) {
          if (str1 != null && str1.length() != 0)
            hashSet.add(str1); 
        } 
      } 
    } 
    if (hashSet.contains("SHA1") && !hashSet.contains("SHA-1"))
      hashSet.add("SHA-1"); 
    if (hashSet.contains("SHA-1") && !hashSet.contains("SHA1"))
      hashSet.add("SHA1"); 
    if (hashSet.contains("SHA224") && !hashSet.contains("SHA-224"))
      hashSet.add("SHA-224"); 
    if (hashSet.contains("SHA-224") && !hashSet.contains("SHA224"))
      hashSet.add("SHA224"); 
    if (hashSet.contains("SHA256") && !hashSet.contains("SHA-256"))
      hashSet.add("SHA-256"); 
    if (hashSet.contains("SHA-256") && !hashSet.contains("SHA256"))
      hashSet.add("SHA256"); 
    if (hashSet.contains("SHA384") && !hashSet.contains("SHA-384"))
      hashSet.add("SHA-384"); 
    if (hashSet.contains("SHA-384") && !hashSet.contains("SHA384"))
      hashSet.add("SHA384"); 
    if (hashSet.contains("SHA512") && !hashSet.contains("SHA-512"))
      hashSet.add("SHA-512"); 
    if (hashSet.contains("SHA-512") && !hashSet.contains("SHA512"))
      hashSet.add("SHA512"); 
    return hashSet;
  }
}
