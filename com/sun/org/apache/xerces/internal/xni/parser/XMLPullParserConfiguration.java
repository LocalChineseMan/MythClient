package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import java.io.IOException;

public interface XMLPullParserConfiguration extends XMLParserConfiguration {
  void setInputSource(XMLInputSource paramXMLInputSource) throws XMLConfigurationException, IOException;
  
  boolean parse(boolean paramBoolean) throws XNIException, IOException;
  
  void cleanup();
}
