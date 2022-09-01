package com.sun.org.apache.xerces.internal.jaxp;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import java.util.Hashtable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class DocumentBuilderFactoryImpl extends DocumentBuilderFactory {
  private Hashtable attributes;
  
  private Hashtable features;
  
  private Schema grammar;
  
  private boolean isXIncludeAware;
  
  private boolean fSecureProcess = true;
  
  public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
    if (this.grammar != null && this.attributes != null) {
      if (this.attributes.containsKey("http://java.sun.com/xml/jaxp/properties/schemaLanguage"))
        throw new ParserConfigurationException(
            SAXMessageFormatter.formatMessage(null, "schema-already-specified", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaLanguage" })); 
      if (this.attributes.containsKey("http://java.sun.com/xml/jaxp/properties/schemaSource"))
        throw new ParserConfigurationException(
            SAXMessageFormatter.formatMessage(null, "schema-already-specified", new Object[] { "http://java.sun.com/xml/jaxp/properties/schemaSource" })); 
    } 
    try {
      return new DocumentBuilderImpl(this, this.attributes, this.features, this.fSecureProcess);
    } catch (SAXException se) {
      throw new ParserConfigurationException(se.getMessage());
    } 
  }
  
  public void setAttribute(String name, Object value) throws IllegalArgumentException {
    if (value == null) {
      if (this.attributes != null)
        this.attributes.remove(name); 
      return;
    } 
    if (this.attributes == null)
      this.attributes = new Hashtable<>(); 
    this.attributes.put(name, value);
    try {
      new DocumentBuilderImpl(this, this.attributes, this.features);
    } catch (Exception e) {
      this.attributes.remove(name);
      throw new IllegalArgumentException(e.getMessage());
    } 
  }
  
  public Object getAttribute(String name) throws IllegalArgumentException {
    if (this.attributes != null) {
      Object val = this.attributes.get(name);
      if (val != null)
        return val; 
    } 
    DOMParser domParser = null;
    try {
      domParser = (new DocumentBuilderImpl(this, this.attributes, this.features)).getDOMParser();
      return domParser.getProperty(name);
    } catch (SAXException se1) {
      try {
        boolean result = domParser.getFeature(name);
        return result ? Boolean.TRUE : Boolean.FALSE;
      } catch (SAXException se2) {
        throw new IllegalArgumentException(se1.getMessage());
      } 
    } 
  }
  
  public Schema getSchema() {
    return this.grammar;
  }
  
  public void setSchema(Schema grammar) {
    this.grammar = grammar;
  }
  
  public boolean isXIncludeAware() {
    return this.isXIncludeAware;
  }
  
  public void setXIncludeAware(boolean state) {
    this.isXIncludeAware = state;
  }
  
  public boolean getFeature(String name) throws ParserConfigurationException {
    if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing"))
      return this.fSecureProcess; 
    if (this.features != null) {
      Object val = this.features.get(name);
      if (val != null)
        return ((Boolean)val).booleanValue(); 
    } 
    try {
      DOMParser domParser = (new DocumentBuilderImpl(this, this.attributes, this.features)).getDOMParser();
      return domParser.getFeature(name);
    } catch (SAXException e) {
      throw new ParserConfigurationException(e.getMessage());
    } 
  }
  
  public void setFeature(String name, boolean value) throws ParserConfigurationException {
    if (this.features == null)
      this.features = new Hashtable<>(); 
    if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
      if (System.getSecurityManager() != null && !value)
        throw new ParserConfigurationException(
            SAXMessageFormatter.formatMessage(null, "jaxp-secureprocessing-feature", null)); 
      this.fSecureProcess = value;
      this.features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
      return;
    } 
    this.features.put(name, value ? Boolean.TRUE : Boolean.FALSE);
    try {
      new DocumentBuilderImpl(this, this.attributes, this.features);
    } catch (SAXNotSupportedException e) {
      this.features.remove(name);
      throw new ParserConfigurationException(e.getMessage());
    } catch (SAXNotRecognizedException e) {
      this.features.remove(name);
      throw new ParserConfigurationException(e.getMessage());
    } 
  }
}
