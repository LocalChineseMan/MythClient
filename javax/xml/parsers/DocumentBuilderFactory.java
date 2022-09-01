package javax.xml.parsers;

import javax.xml.validation.Schema;

public abstract class DocumentBuilderFactory {
  private boolean validating = false;
  
  private boolean namespaceAware = false;
  
  private boolean whitespace = false;
  
  private boolean expandEntityRef = true;
  
  private boolean ignoreComments = false;
  
  private boolean coalescing = false;
  
  public static DocumentBuilderFactory newInstance() {
    return FactoryFinder.<DocumentBuilderFactory>find(DocumentBuilderFactory.class, "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
  }
  
  public static DocumentBuilderFactory newInstance(String factoryClassName, ClassLoader classLoader) {
    return FactoryFinder.<DocumentBuilderFactory>newInstance(DocumentBuilderFactory.class, factoryClassName, classLoader, false);
  }
  
  public abstract DocumentBuilder newDocumentBuilder() throws ParserConfigurationException;
  
  public void setNamespaceAware(boolean awareness) {
    this.namespaceAware = awareness;
  }
  
  public void setValidating(boolean validating) {
    this.validating = validating;
  }
  
  public void setIgnoringElementContentWhitespace(boolean whitespace) {
    this.whitespace = whitespace;
  }
  
  public void setExpandEntityReferences(boolean expandEntityRef) {
    this.expandEntityRef = expandEntityRef;
  }
  
  public void setIgnoringComments(boolean ignoreComments) {
    this.ignoreComments = ignoreComments;
  }
  
  public void setCoalescing(boolean coalescing) {
    this.coalescing = coalescing;
  }
  
  public boolean isNamespaceAware() {
    return this.namespaceAware;
  }
  
  public boolean isValidating() {
    return this.validating;
  }
  
  public boolean isIgnoringElementContentWhitespace() {
    return this.whitespace;
  }
  
  public boolean isExpandEntityReferences() {
    return this.expandEntityRef;
  }
  
  public boolean isIgnoringComments() {
    return this.ignoreComments;
  }
  
  public boolean isCoalescing() {
    return this.coalescing;
  }
  
  public abstract void setAttribute(String paramString, Object paramObject) throws IllegalArgumentException;
  
  public abstract Object getAttribute(String paramString) throws IllegalArgumentException;
  
  public abstract void setFeature(String paramString, boolean paramBoolean) throws ParserConfigurationException;
  
  public abstract boolean getFeature(String paramString) throws ParserConfigurationException;
  
  public Schema getSchema() {
    throw new UnsupportedOperationException("This parser does not support specification \"" + 
        
        getClass().getPackage().getSpecificationTitle() + "\" version \"" + 
        
        getClass().getPackage().getSpecificationVersion() + "\"");
  }
  
  public void setSchema(Schema schema) {
    throw new UnsupportedOperationException("This parser does not support specification \"" + 
        
        getClass().getPackage().getSpecificationTitle() + "\" version \"" + 
        
        getClass().getPackage().getSpecificationVersion() + "\"");
  }
  
  public void setXIncludeAware(boolean state) {
    if (state)
      throw new UnsupportedOperationException(" setXIncludeAware is not supported on this JAXP implementation or earlier: " + 
          
          getClass()); 
  }
  
  public boolean isXIncludeAware() {
    throw new UnsupportedOperationException("This parser does not support specification \"" + 
        
        getClass().getPackage().getSpecificationTitle() + "\" version \"" + 
        
        getClass().getPackage().getSpecificationVersion() + "\"");
  }
}
