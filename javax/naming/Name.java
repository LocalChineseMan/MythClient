package javax.naming;

import java.io.Serializable;
import java.util.Enumeration;

public interface Name extends Cloneable, Serializable, Comparable<Object> {
  public static final long serialVersionUID = -3617482732056931635L;
  
  Object clone();
  
  int compareTo(Object paramObject);
  
  int size();
  
  boolean isEmpty();
  
  Enumeration<String> getAll();
  
  String get(int paramInt);
  
  Name getPrefix(int paramInt);
  
  Name getSuffix(int paramInt);
  
  boolean startsWith(Name paramName);
  
  boolean endsWith(Name paramName);
  
  Name addAll(Name paramName) throws InvalidNameException;
  
  Name addAll(int paramInt, Name paramName) throws InvalidNameException;
  
  Name add(String paramString) throws InvalidNameException;
  
  Name add(int paramInt, String paramString) throws InvalidNameException;
  
  Object remove(int paramInt) throws InvalidNameException;
}
