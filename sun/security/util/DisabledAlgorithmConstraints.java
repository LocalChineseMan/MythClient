package sun.security.util;

import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.Key;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DisabledAlgorithmConstraints extends AbstractAlgorithmConstraints {
  public static final String PROPERTY_CERTPATH_DISABLED_ALGS = "jdk.certpath.disabledAlgorithms";
  
  public static final String PROPERTY_TLS_DISABLED_ALGS = "jdk.tls.disabledAlgorithms";
  
  private static final Map<String, String[]> disabledAlgorithmsMap = (Map)new HashMap<>();
  
  private static final Map<String, KeySizeConstraints> keySizeConstraintsMap = new HashMap<>();
  
  private final String[] disabledAlgorithms;
  
  private final KeySizeConstraints keySizeConstraints;
  
  public DisabledAlgorithmConstraints(String paramString) {
    this(paramString, new AlgorithmDecomposer());
  }
  
  public DisabledAlgorithmConstraints(String paramString, AlgorithmDecomposer paramAlgorithmDecomposer) {
    super(paramAlgorithmDecomposer);
    this.disabledAlgorithms = getAlgorithms(disabledAlgorithmsMap, paramString);
    this.keySizeConstraints = getKeySizeConstraints(this.disabledAlgorithms, paramString);
  }
  
  public final boolean permits(Set<CryptoPrimitive> paramSet, String paramString, AlgorithmParameters paramAlgorithmParameters) {
    if (paramSet == null || paramSet.isEmpty())
      throw new IllegalArgumentException("No cryptographic primitive specified"); 
    return checkAlgorithm(this.disabledAlgorithms, paramString, this.decomposer);
  }
  
  public final boolean permits(Set<CryptoPrimitive> paramSet, Key paramKey) {
    return checkConstraints(paramSet, "", paramKey, (AlgorithmParameters)null);
  }
  
  public final boolean permits(Set<CryptoPrimitive> paramSet, String paramString, Key paramKey, AlgorithmParameters paramAlgorithmParameters) {
    if (paramString == null || paramString.length() == 0)
      throw new IllegalArgumentException("No algorithm name specified"); 
    return checkConstraints(paramSet, paramString, paramKey, paramAlgorithmParameters);
  }
  
  private boolean checkConstraints(Set<CryptoPrimitive> paramSet, String paramString, Key paramKey, AlgorithmParameters paramAlgorithmParameters) {
    if (paramKey == null)
      throw new IllegalArgumentException("The key cannot be null"); 
    if (paramString != null && paramString.length() != 0 && 
      !permits(paramSet, paramString, paramAlgorithmParameters))
      return false; 
    if (!permits(paramSet, paramKey.getAlgorithm(), (AlgorithmParameters)null))
      return false; 
    if (this.keySizeConstraints.disables(paramKey))
      return false; 
    return true;
  }
  
  private static KeySizeConstraints getKeySizeConstraints(String[] paramArrayOfString, String paramString) {
    synchronized (keySizeConstraintsMap) {
      if (!keySizeConstraintsMap.containsKey(paramString)) {
        KeySizeConstraints keySizeConstraints = new KeySizeConstraints(paramArrayOfString);
        keySizeConstraintsMap.put(paramString, keySizeConstraints);
      } 
      return keySizeConstraintsMap.get(paramString);
    } 
  }
  
  private static class KeySizeConstraints {
    private static final Pattern pattern = Pattern.compile("(\\S+)\\s+keySize\\s*(<=|<|==|!=|>|>=)\\s*(\\d+)");
    
    private Map<String, Set<DisabledAlgorithmConstraints.KeySizeConstraint>> constraintsMap = Collections.synchronizedMap(new HashMap<>());
    
    public KeySizeConstraints(String[] param1ArrayOfString) {
      for (String str : param1ArrayOfString) {
        if (str != null && !str.isEmpty()) {
          Matcher matcher = pattern.matcher(str);
          if (matcher.matches()) {
            String str1 = matcher.group(1);
            DisabledAlgorithmConstraints.KeySizeConstraint.Operator operator = DisabledAlgorithmConstraints.KeySizeConstraint.Operator.of(matcher.group(2));
            int i = Integer.parseInt(matcher.group(3));
            str1 = str1.toLowerCase(Locale.ENGLISH);
            synchronized (this.constraintsMap) {
              if (!this.constraintsMap.containsKey(str1))
                this.constraintsMap.put(str1, new HashSet<>()); 
              Set<DisabledAlgorithmConstraints.KeySizeConstraint> set = this.constraintsMap.get(str1);
              DisabledAlgorithmConstraints.KeySizeConstraint keySizeConstraint = new DisabledAlgorithmConstraints.KeySizeConstraint(operator, i);
              set.add(keySizeConstraint);
            } 
          } 
        } 
      } 
    }
    
    public boolean disables(Key param1Key) {
      String str = param1Key.getAlgorithm().toLowerCase(Locale.ENGLISH);
      synchronized (this.constraintsMap) {
        if (this.constraintsMap.containsKey(str)) {
          Set set = this.constraintsMap.get(str);
          for (DisabledAlgorithmConstraints.KeySizeConstraint keySizeConstraint : set) {
            if (keySizeConstraint.disables(param1Key))
              return true; 
          } 
        } 
      } 
      return false;
    }
  }
  
  private static class KeySizeConstraint {
    private int minSize;
    
    private int maxSize;
    
    enum Operator {
      EQ, NE, LT, LE, GT, GE;
      
      static Operator of(String param2String) {
        switch (param2String) {
          case "==":
            return EQ;
          case "!=":
            return NE;
          case "<":
            return LT;
          case "<=":
            return LE;
          case ">":
            return GT;
          case ">=":
            return GE;
        } 
        throw new IllegalArgumentException(param2String + " is not a legal Operator");
      }
    }
    
    private int prohibitedSize = -1;
    
    public KeySizeConstraint(Operator param1Operator, int param1Int) {
      switch (param1Operator) {
        case EQ:
          this.minSize = 0;
          this.maxSize = Integer.MAX_VALUE;
          this.prohibitedSize = param1Int;
          return;
        case NE:
          this.minSize = param1Int;
          this.maxSize = param1Int;
          return;
        case LT:
          this.minSize = param1Int;
          this.maxSize = Integer.MAX_VALUE;
          return;
        case LE:
          this.minSize = param1Int + 1;
          this.maxSize = Integer.MAX_VALUE;
          return;
        case GT:
          this.minSize = 0;
          this.maxSize = param1Int;
          return;
        case GE:
          this.minSize = 0;
          this.maxSize = (param1Int > 1) ? (param1Int - 1) : 0;
          return;
      } 
      this.minSize = Integer.MAX_VALUE;
      this.maxSize = -1;
    }
    
    public boolean disables(Key param1Key) {
      int i = KeyUtil.getKeySize(param1Key);
      if (i == 0)
        return true; 
      if (i > 0)
        return (i < this.minSize || i > this.maxSize || this.prohibitedSize == i); 
      return false;
    }
  }
}
