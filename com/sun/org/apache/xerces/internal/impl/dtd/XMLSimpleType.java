package com.sun.org.apache.xerces.internal.impl.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;

public class XMLSimpleType {
  public static final short TYPE_CDATA = 0;
  
  public static final short TYPE_ENTITY = 1;
  
  public static final short TYPE_ENUMERATION = 2;
  
  public static final short TYPE_ID = 3;
  
  public static final short TYPE_IDREF = 4;
  
  public static final short TYPE_NMTOKEN = 5;
  
  public static final short TYPE_NOTATION = 6;
  
  public static final short TYPE_NAMED = 7;
  
  public static final short DEFAULT_TYPE_DEFAULT = 3;
  
  public static final short DEFAULT_TYPE_FIXED = 1;
  
  public static final short DEFAULT_TYPE_IMPLIED = 0;
  
  public static final short DEFAULT_TYPE_REQUIRED = 2;
  
  public short type;
  
  public String name;
  
  public String[] enumeration;
  
  public boolean list;
  
  public short defaultType;
  
  public String defaultValue;
  
  public String nonNormalizedDefaultValue;
  
  public DatatypeValidator datatypeValidator;
  
  public void setValues(short type, String name, String[] enumeration, boolean list, short defaultType, String defaultValue, String nonNormalizedDefaultValue, DatatypeValidator datatypeValidator) {
    this.type = type;
    this.name = name;
    if (enumeration != null && enumeration.length > 0) {
      this.enumeration = new String[enumeration.length];
      System.arraycopy(enumeration, 0, this.enumeration, 0, this.enumeration.length);
    } else {
      this.enumeration = null;
    } 
    this.list = list;
    this.defaultType = defaultType;
    this.defaultValue = defaultValue;
    this.nonNormalizedDefaultValue = nonNormalizedDefaultValue;
    this.datatypeValidator = datatypeValidator;
  }
  
  public void setValues(XMLSimpleType simpleType) {
    this.type = simpleType.type;
    this.name = simpleType.name;
    if (simpleType.enumeration != null && simpleType.enumeration.length > 0) {
      this.enumeration = new String[simpleType.enumeration.length];
      System.arraycopy(simpleType.enumeration, 0, this.enumeration, 0, this.enumeration.length);
    } else {
      this.enumeration = null;
    } 
    this.list = simpleType.list;
    this.defaultType = simpleType.defaultType;
    this.defaultValue = simpleType.defaultValue;
    this.nonNormalizedDefaultValue = simpleType.nonNormalizedDefaultValue;
    this.datatypeValidator = simpleType.datatypeValidator;
  }
  
  public void clear() {
    this.type = -1;
    this.name = null;
    this.enumeration = null;
    this.list = false;
    this.defaultType = -1;
    this.defaultValue = null;
    this.nonNormalizedDefaultValue = null;
    this.datatypeValidator = null;
  }
}
