package javax.naming.directory;

import java.io.Serializable;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public interface Attribute extends Cloneable, Serializable {
  public static final long serialVersionUID = 8707690322213556804L;
  
  NamingEnumeration<?> getAll() throws NamingException;
  
  Object get() throws NamingException;
  
  int size();
  
  String getID();
  
  boolean contains(Object paramObject);
  
  boolean add(Object paramObject);
  
  boolean remove(Object paramObject);
  
  void clear();
  
  DirContext getAttributeSyntaxDefinition() throws NamingException;
  
  DirContext getAttributeDefinition() throws NamingException;
  
  Object clone();
  
  boolean isOrdered();
  
  Object get(int paramInt) throws NamingException;
  
  Object remove(int paramInt);
  
  void add(int paramInt, Object paramObject);
  
  Object set(int paramInt, Object paramObject);
}
