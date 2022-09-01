package com.sun.org.apache.xerces.internal.xni.grammars;

import com.sun.org.apache.xerces.internal.xni.XMLResourceIdentifier;

public interface XMLGrammarDescription extends XMLResourceIdentifier {
  public static final String XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  
  public static final String XML_DTD = "http://www.w3.org/TR/REC-xml";
  
  String getGrammarType();
}
