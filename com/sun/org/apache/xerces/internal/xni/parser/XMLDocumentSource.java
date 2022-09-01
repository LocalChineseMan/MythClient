package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XMLDocumentHandler;

public interface XMLDocumentSource {
  void setDocumentHandler(XMLDocumentHandler paramXMLDocumentHandler);
  
  XMLDocumentHandler getDocumentHandler();
}
