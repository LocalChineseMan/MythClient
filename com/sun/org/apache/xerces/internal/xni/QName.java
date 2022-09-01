package com.sun.org.apache.xerces.internal.xni;

public class QName implements Cloneable {
  public String prefix;
  
  public String localpart;
  
  public String rawname;
  
  public String uri;
  
  public QName() {
    clear();
  }
  
  public QName(String prefix, String localpart, String rawname, String uri) {
    setValues(prefix, localpart, rawname, uri);
  }
  
  public QName(QName qname) {
    setValues(qname);
  }
  
  public void setValues(QName qname) {
    this.prefix = qname.prefix;
    this.localpart = qname.localpart;
    this.rawname = qname.rawname;
    this.uri = qname.uri;
  }
  
  public void setValues(String prefix, String localpart, String rawname, String uri) {
    this.prefix = prefix;
    this.localpart = localpart;
    this.rawname = rawname;
    this.uri = uri;
  }
  
  public void clear() {
    this.prefix = null;
    this.localpart = null;
    this.rawname = null;
    this.uri = null;
  }
  
  public Object clone() {
    return new QName(this);
  }
  
  public int hashCode() {
    if (this.uri != null)
      return this.uri.hashCode() + ((this.localpart != null) ? this.localpart.hashCode() : 0); 
    return (this.rawname != null) ? this.rawname.hashCode() : 0;
  }
  
  public boolean equals(Object object) {
    if (object == this)
      return true; 
    if (object != null && object instanceof QName) {
      QName qname = (QName)object;
      if (qname.uri != null)
        return (qname.localpart.equals(this.localpart) && qname.uri.equals(this.uri)); 
      if (this.uri == null)
        return this.rawname.equals(qname.rawname); 
    } 
    return false;
  }
  
  public String toString() {
    StringBuffer str = new StringBuffer();
    boolean comma = false;
    if (this.prefix != null) {
      str.append("prefix=\"" + this.prefix + '"');
      comma = true;
    } 
    if (this.localpart != null) {
      if (comma)
        str.append(','); 
      str.append("localpart=\"" + this.localpart + '"');
      comma = true;
    } 
    if (this.rawname != null) {
      if (comma)
        str.append(','); 
      str.append("rawname=\"" + this.rawname + '"');
      comma = true;
    } 
    if (this.uri != null) {
      if (comma)
        str.append(','); 
      str.append("uri=\"" + this.uri + '"');
    } 
    return str.toString();
  }
}
