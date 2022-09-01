package sun.security.util;

import java.security.AccessController;
import java.security.AlgorithmConstraints;
import java.security.Security;
import java.util.Map;
import java.util.Set;

public abstract class AbstractAlgorithmConstraints implements AlgorithmConstraints {
  protected final AlgorithmDecomposer decomposer;
  
  protected AbstractAlgorithmConstraints(AlgorithmDecomposer paramAlgorithmDecomposer) {
    this.decomposer = paramAlgorithmDecomposer;
  }
  
  private static void loadAlgorithmsMap(Map<String, String[]> paramMap, String paramString) {
    String str = AccessController.<String>doPrivileged(() -> Security.getProperty(paramString));
    String[] arrayOfString = null;
    if (str != null && !str.isEmpty()) {
      if (str.charAt(0) == '"' && str
        .charAt(str.length() - 1) == '"')
        str = str.substring(1, str.length() - 1); 
      arrayOfString = str.split(",");
      for (byte b = 0; b < arrayOfString.length; 
        b++)
        arrayOfString[b] = arrayOfString[b].trim(); 
    } 
    if (arrayOfString == null)
      arrayOfString = new String[0]; 
    paramMap.put(paramString, arrayOfString);
  }
  
  static String[] getAlgorithms(Map<String, String[]> paramMap, String paramString) {
    synchronized (paramMap) {
      if (!paramMap.containsKey(paramString))
        loadAlgorithmsMap(paramMap, paramString); 
      return paramMap.get(paramString);
    } 
  }
  
  static boolean checkAlgorithm(String[] paramArrayOfString, String paramString, AlgorithmDecomposer paramAlgorithmDecomposer) {
    if (paramString == null || paramString.length() == 0)
      throw new IllegalArgumentException("No algorithm name specified"); 
    Set<String> set = null;
    for (String str : paramArrayOfString) {
      if (str != null && !str.isEmpty()) {
        if (str.equalsIgnoreCase(paramString))
          return false; 
        if (set == null)
          set = paramAlgorithmDecomposer.decompose(paramString); 
        for (String str1 : set) {
          if (str.equalsIgnoreCase(str1))
            return false; 
        } 
      } 
    } 
    return true;
  }
}
