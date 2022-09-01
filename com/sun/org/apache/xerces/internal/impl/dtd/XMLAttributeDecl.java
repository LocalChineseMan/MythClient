package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.xni.QName;

public class XMLAttributeDecl {
  public final QName name = new QName();
  
  public final XMLSimpleType simpleType = new XMLSimpleType();
  
  public boolean optional;
  
  public void setValues(QName name, XMLSimpleType simpleType, boolean optional) {
    this.name.setValues(name);
    this.simpleType.setValues(simpleType);
    this.optional = optional;
  }
  
  public void clear() {
    this.name.clear();
    this.simpleType.clear();
    this.optional = false;
  }
}
