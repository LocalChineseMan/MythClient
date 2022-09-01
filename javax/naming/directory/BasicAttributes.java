package javax.naming.directory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

public class BasicAttributes implements Attributes {
  private boolean ignoreCase = false;
  
  transient Hashtable<String, Attribute> attrs = new Hashtable<>(11);
  
  private static final long serialVersionUID = 4980164073184639448L;
  
  public BasicAttributes() {}
  
  public BasicAttributes(boolean paramBoolean) {
    this.ignoreCase = paramBoolean;
  }
  
  public BasicAttributes(String paramString, Object paramObject) {
    this();
    put(new BasicAttribute(paramString, paramObject));
  }
  
  public BasicAttributes(String paramString, Object paramObject, boolean paramBoolean) {
    this(paramBoolean);
    put(new BasicAttribute(paramString, paramObject));
  }
  
  public Object clone() {
    BasicAttributes basicAttributes;
    try {
      basicAttributes = (BasicAttributes)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      basicAttributes = new BasicAttributes(this.ignoreCase);
    } 
    basicAttributes.attrs = (Hashtable<String, Attribute>)this.attrs.clone();
    return basicAttributes;
  }
  
  public boolean isCaseIgnored() {
    return this.ignoreCase;
  }
  
  public int size() {
    return this.attrs.size();
  }
  
  public Attribute get(String paramString) {
    return this.attrs.get(this.ignoreCase ? paramString
        .toLowerCase(Locale.ENGLISH) : paramString);
  }
  
  public NamingEnumeration<Attribute> getAll() {
    return new AttrEnumImpl(this);
  }
  
  public NamingEnumeration<String> getIDs() {
    return new IDEnumImpl(this);
  }
  
  public Attribute put(String paramString, Object paramObject) {
    return put(new BasicAttribute(paramString, paramObject));
  }
  
  public Attribute put(Attribute paramAttribute) {
    String str = paramAttribute.getID();
    if (this.ignoreCase)
      str = str.toLowerCase(Locale.ENGLISH); 
    return this.attrs.put(str, paramAttribute);
  }
  
  public Attribute remove(String paramString) {
    String str = this.ignoreCase ? paramString.toLowerCase(Locale.ENGLISH) : paramString;
    return this.attrs.remove(str);
  }
  
  public String toString() {
    if (this.attrs.size() == 0)
      return "No attributes"; 
    return this.attrs.toString();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null && paramObject instanceof Attributes) {
      Attributes attributes = (Attributes)paramObject;
      if (this.ignoreCase != attributes.isCaseIgnored())
        return false; 
      if (size() == attributes.size()) {
        try {
          NamingEnumeration<? extends Attribute> namingEnumeration = attributes.getAll();
          while (namingEnumeration.hasMore()) {
            Attribute attribute1 = namingEnumeration.next();
            Attribute attribute2 = get(attribute1.getID());
            if (!attribute1.equals(attribute2))
              return false; 
          } 
        } catch (NamingException namingException) {
          return false;
        } 
        return true;
      } 
    } 
    return false;
  }
  
  public int hashCode() {
    int i = this.ignoreCase ? 1 : 0;
    try {
      NamingEnumeration<Attribute> namingEnumeration = getAll();
      while (namingEnumeration.hasMore())
        i += namingEnumeration.next().hashCode(); 
    } catch (NamingException namingException) {}
    return i;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeInt(this.attrs.size());
    Enumeration<Attribute> enumeration = this.attrs.elements();
    while (enumeration.hasMoreElements())
      paramObjectOutputStream.writeObject(enumeration.nextElement()); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    int i = paramObjectInputStream.readInt();
    this.attrs = (i >= 1) ? new Hashtable<>(i * 2) : new Hashtable<>(2);
    while (--i >= 0)
      put((Attribute)paramObjectInputStream.readObject()); 
  }
  
  class BasicAttributes {}
  
  class BasicAttributes {}
}
