package com.sun.org.apache.xerces.internal.xni.parser;

import com.sun.org.apache.xerces.internal.util.Status;
import com.sun.org.apache.xerces.internal.xni.XNIException;

public class XMLConfigurationException extends XNIException {
  static final long serialVersionUID = -5437427404547669188L;
  
  protected Status fType;
  
  protected String fIdentifier;
  
  public XMLConfigurationException(Status type, String identifier) {
    super(identifier);
    this.fType = type;
    this.fIdentifier = identifier;
  }
  
  public XMLConfigurationException(Status type, String identifier, String message) {
    super(message);
    this.fType = type;
    this.fIdentifier = identifier;
  }
  
  public Status getType() {
    return this.fType;
  }
  
  public String getIdentifier() {
    return this.fIdentifier;
  }
}
