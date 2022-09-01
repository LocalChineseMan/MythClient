package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.dtd.models.ContentModelValidator;
import com.sun.org.apache.xerces.internal.xni.QName;

public class XMLElementDecl {
  public static final short TYPE_ANY = 0;
  
  public static final short TYPE_EMPTY = 1;
  
  public static final short TYPE_MIXED = 2;
  
  public static final short TYPE_CHILDREN = 3;
  
  public static final short TYPE_SIMPLE = 4;
  
  public final QName name = new QName();
  
  public int scope = -1;
  
  public short type = -1;
  
  public ContentModelValidator contentModelValidator;
  
  public final XMLSimpleType simpleType = new XMLSimpleType();
  
  public void setValues(QName name, int scope, short type, ContentModelValidator contentModelValidator, XMLSimpleType simpleType) {
    this.name.setValues(name);
    this.scope = scope;
    this.type = type;
    this.contentModelValidator = contentModelValidator;
    this.simpleType.setValues(simpleType);
  }
  
  public void clear() {
    this.name.clear();
    this.type = -1;
    this.scope = -1;
    this.contentModelValidator = null;
    this.simpleType.clear();
  }
}
