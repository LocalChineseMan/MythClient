package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;

public interface XMLDTDSource {
  void setDTDHandler(XMLDTDHandler paramXMLDTDHandler);
  
  XMLDTDHandler getDTDHandler();
}
